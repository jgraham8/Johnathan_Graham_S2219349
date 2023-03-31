//
// Name                 Johnathan Graham
// Student ID           S2219349
// Programme of Study   Computing
//

package org.me.gcu.johnathan_graham_s2219349;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ArrayList<Earthquake> earthquakeCollection = new ArrayList<>();
    private ListView listEarthquakes;
    private ArrayAdapter adapter;
    private Spinner spinDate;
    private TextInputEditText txtinSearch;
    private boolean mapZoomMoveRequired = true, glasgowSearch = false;

    private static float GetColour(Double magnitude) {
        if (magnitude <= 1) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (magnitude <= 2) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else if (magnitude <= 3) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        } else if (magnitude <= 4) {
            return BitmapDescriptorFactory.HUE_RED;
        } else {
            return BitmapDescriptorFactory.HUE_VIOLET;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        if (!connected) {
            Intent intent = new Intent(getBaseContext(), NoInternetActivity.class);
            startActivity(intent);
            return;
        }

        listEarthquakes = (ListView) findViewById(R.id.listEarthquakes);
        adapter = new EarthquakeAdapter(getApplicationContext(), R.layout.item_earthquake, earthquakeCollection);
        listEarthquakes.setAdapter(adapter);

        startProgress();

        //region $DropDown Code
        spinDate = (Spinner) findViewById(R.id.spinDate);
        txtinSearch = (TextInputEditText) findViewById(R.id.txtinSearch);

        ArrayList<String> crustList = new ArrayList<>();

        TreeSet<String> datesSet = new TreeSet<>();
        for (Earthquake e : earthquakeCollection) {
            datesSet.add(e.getPublishDate().getDate());
        }

        crustList.add("All");
        crustList.add("Nearest Glasgow");
        crustList.add("Largest Magnitude");
        crustList.add("Depth");
        crustList.addAll(datesSet.descendingSet());

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crustList);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDate.setAdapter(spinAdapter);
        //endregion

    }

    //region $Map Code
    private void loadMap() {

        SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mf).getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap map = googleMap;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
        map.setMapStyle(style);
        map.clear();

        ArrayList<Earthquake> filteredList = getFilteredEarthquakes();

        Iterator<Earthquake> myItr = filteredList.iterator();

        while (myItr.hasNext()) {
            Earthquake e = myItr.next();
            if (e.getDescription().equals("Not Found")) {
                myItr.remove();
            }
        }

        adapter = new EarthquakeAdapter(getApplicationContext(), R.layout.item_earthquake, filteredList);
        listEarthquakes.setAdapter(adapter);

        for (Earthquake earthquake : filteredList) {
            if (earthquake.getDescription().equals("Not Found")) {
                continue;
            }

            LatLng latlong = new LatLng(earthquake.getLocation().getLatitude(), earthquake.getLocation().getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(latlong)
                    .title(earthquake.toString())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(GetColour(earthquake.getMagnitude()))));
        }
        if (mapZoomMoveRequired) {
            LatLng ukCentred = new LatLng(54.27808937314388, -4.5356791187887175);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ukCentred, 5));

            mapZoomMoveRequired = !mapZoomMoveRequired;
        }
        if (glasgowSearch) {
            LatLng glasgow = new LatLng(55.860916, -4.251433);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 7));

            glasgowSearch = false;
        }
    }

    private ArrayList<Earthquake> getFilteredEarthquakes() {

        ArrayList<Earthquake> sortedList = earthquakeCollection;
        String date = spinDate.getSelectedItem().toString().toLowerCase();
        glasgowSearch = false;

        switch (date) {
            case "all":
                //sortedList = earthquakeCollection;
                break;
            case "nearest glasgow":
                glasgowSearch = true;

                sortedList = new ArrayList<>();

                String[] directions = new String[]{"North", "East", "South", "West"};

                for (String direction : directions) {
                    Earthquake e = getClosestEarthquake(earthquakeCollection, direction);

                    if (!e.getDescription().equals("Not Found")) {
                        sortedList.add(e);
                    }
                }

                break;
            case "largest magnitude":
                Earthquake largestMag = sortedList.get(0);
                for (Earthquake e : sortedList) {
                    if (e.getMagnitude() > largestMag.getMagnitude()) {
                        largestMag = e;
                    }
                }

                sortedList = new ArrayList<>();
                sortedList.add(largestMag);
                break;
            case "depth":
                Earthquake deepest = sortedList.get(0);
                for (Earthquake e : sortedList) {
                    if (e.getDepth() > deepest.getDepth()) {
                        deepest = e;
                    }
                }

                Earthquake shallowest = sortedList.get(0);

                for (Earthquake e : sortedList) {
                    if (e.getDepth() < shallowest.getDepth()) {
                        shallowest = e;
                    }
                }

                sortedList = new ArrayList<>();
                sortedList.add(deepest);
                sortedList.add(shallowest);
                break;
            default:
                sortedList = new ArrayList<>();

                for (Earthquake e : earthquakeCollection) {
                    if (e.getPublishDate().getDate().equals(date)) {
                        sortedList.add(e);
                    }
                }
        }

        String searchText = Objects.requireNonNull(txtinSearch.getText()).toString().toLowerCase();

        ArrayList<Earthquake> filteredList = new ArrayList<>();

        if (searchText.length() >= 1) {
            for (Earthquake e : sortedList) {
                if (e.getTitle().toLowerCase().contains(searchText) || e.getPublishDate().toString().contains(searchText)) {
                    filteredList.add(e);
                }
            }
        } else {
            filteredList = sortedList;
        }

        return filteredList;
    }

    private Earthquake getClosestEarthquake(ArrayList<Earthquake> earthquakes, String direction) {
        if (earthquakes.isEmpty()) {
            return new Earthquake();
        }

        Iterator<Earthquake> myItr = earthquakes.iterator();

        ArrayList<Earthquake> temp = new ArrayList<>();

        switch (direction) {
            case "North":
                while (myItr.hasNext()) {
                    Earthquake e = myItr.next();
                    if (e.getLocation().getDirectionFromGlasgow().equals("North of Glasgow")) {
                        temp.add(e);
                    }
                }
                break;
            case "South":
                while (myItr.hasNext()) {
                    Earthquake e = myItr.next();
                    if (e.getLocation().getDirectionFromGlasgow().equals("South of Glasgow")) {
                        temp.add(e);
                    }
                }
                break;
            case "East":
                while (myItr.hasNext()) {
                    Earthquake e = myItr.next();
                    if (e.getLocation().getDirectionFromGlasgow().equals("East of Glasgow")) {
                        temp.add(e);
                    }
                }
                break;
            case "West":
                while (myItr.hasNext()) {
                    Earthquake e = myItr.next();
                    if (e.getLocation().getDirectionFromGlasgow().equals("West of Glasgow")) {
                        temp.add(e);
                    }
                }
                break;
            default:
        }

        if (temp.isEmpty()) {
            return new Earthquake();
        }

        Earthquake closest = temp.get(0);

        for (Earthquake e : temp) {
            if (e.getLocation().getDistanceFromGlasgowInKM() < closest.getLocation().getDistanceFromGlasgowInKM()) {
                closest = e;
            }
        }
        return closest;
    }
    //endregion

    public void startProgress() {
        // Run network access on a separate thread;
        String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
        Thread t = new Thread(new Task(urlSource));
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Start Thread Try", "in try " + e);
        }
    }

    private class Task implements Runnable {
        private String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("Task", "in run");

            String result = "";

            try {
                Log.e("Task Try", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("BufferedReader", inputLine);
                }
                in.close();
            } catch (IOException ae) {
                Log.e("Task Try", "ioexception");
            }

            parseData(result);

            MainActivity.this.runOnUiThread(() -> {
                Log.d("UI thread", "I am the UI thread");

                loadMap();

                spinDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String date = parent.getItemAtPosition(position).toString();
                        Toast.makeText(parent.getContext(), "Selected: " + date, Toast.LENGTH_LONG).show();

                        mapZoomMoveRequired = true;
                        loadMap();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                txtinSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence chr, int i, int i1, int i2) {
                        //Check char sequence is empty or not
                        if (chr.length() >= 1) {
                            loadMap();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                listEarthquakes.setOnItemClickListener((parent, view, position, id) -> {

                    String selectedItem = parent.getItemAtPosition(position).toString();
                    Earthquake selectedEarthquake = (Earthquake) parent.getItemAtPosition(position);
                    Intent intent = new Intent(getBaseContext(), DetailsActivity.class);

                    Gson gson = new Gson();
                    String earthquakeJSON = gson.toJson(selectedEarthquake);

                    intent.putExtra("EarthquakeJSON", earthquakeJSON);
                    startActivity(intent);

                    Log.e("Selected Item", "in try " + selectedItem);
                });
            });
        }

        private void parseData(String dataToParse) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(new StringReader(dataToParse));

                int eventType = xpp.getEventType();

                Location locationTemp = new Location();
                Earthquake earthquakeTemp = new Earthquake();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // Found a start tag
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = xpp.getName().toLowerCase();
                        switch (tagName) {
                            case "title":
                                earthquakeTemp = new Earthquake();
                                earthquakeTemp.setTitle(xpp.nextText());
                                break;
                            case "description":
                                earthquakeTemp.setDescription(xpp.nextText());
                                break;
                            case "pubdate":
                                earthquakeTemp.setPublishDate(xpp.nextText());
                                break;
                            case "category":
                                earthquakeTemp.setCategory(xpp.nextText());
                                break;
                            case "lat":
                                locationTemp = new Location();
                                locationTemp.setLatitude(Double.parseDouble(xpp.nextText()));
                                locationTemp.setName(earthquakeTemp.getDescription());
                                break;
                            case "long":
                                locationTemp.setLongitude(Double.parseDouble(xpp.nextText()));
                                earthquakeTemp.setLocation(locationTemp);

                                earthquakeCollection.add(earthquakeTemp);

                                //Log.e("ParseData", earthquakeTemp.toString());
                                break;
                            default:
                        }
                    }
                    // Get the next event
                    eventType = xpp.next();
                }
            } catch (XmlPullParserException ae1) {
                Log.e("ae1", "Parsing error" + ae1);
            } catch (IOException ae1) {
                Log.e("ae1", "IO error during parsing " + ae1);
            }
            //Log.e("MyTag","End document");
        } // End of parseData
    }
}


//
// Name                 Johnathan Graham
// Student ID           S2219349
// Programme of Study   Computing
//

package org.me.gcu.johnathan_graham_s2219349;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter {
    public EarthquakeAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public EarthquakeAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public EarthquakeAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

    public EarthquakeAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public EarthquakeAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    public EarthquakeAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        View row = convertView;
        Earthquake Earthquake = (Earthquake) getItem(position);

        view.setBackgroundColor(GetColour(Earthquake.getMagnitude()));

        return view;
    }

    private static int GetColour(Double magnitude){
        int purple = Color.rgb(144, 53, 234);
        int red = Color.rgb(234, 53, 53);
        int orange = Color.rgb(234, 144, 53);
        int yellow = Color.rgb(255, 194, 148);
        int green = Color.rgb(112, 247, 186);

        if (magnitude <= 1) {
            return green;
        } else if (magnitude <= 2) {
            return yellow;
        }else if (magnitude <= 3) {
            return orange;
        }else if (magnitude <= 4) {
            return red;
        } else {
            return purple;
        }
    }
}

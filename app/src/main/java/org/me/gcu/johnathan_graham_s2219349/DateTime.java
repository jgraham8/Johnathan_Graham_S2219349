//
// Name                 Johnathan Graham
// Student ID           S2219349
// Programme of Study   Computing
//

package org.me.gcu.johnathan_graham_s2219349;

import android.os.Build;
import android.util.Log;

import com.google.android.material.timepicker.TimeFormat;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTime {
    private String date;
    private String time;

    public DateTime() {
            date = "Wed, 04 Feb 2023";
            time = "11:59:59";
    }

    public DateTime(String publishDate) {
        String[] dateStringArray = publishDate.substring(5).split(" ");

        try {
            this.date = String.format("%s-%s-%s", dateStringArray[2], getNumberFromMonthName(dateStringArray[1], Locale.UK), dateStringArray[0]);
        } catch (ParseException e) {
            Log.e("DateTime","parsing date" + e.toString());
        }

        this.time = dateStringArray[dateStringArray.length - 1];
    }


    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString(){
        return String.format("%s %s", date, time);
    }

    public int getNumberFromMonthName(String monthName, Locale locale) throws ParseException {

//            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("MMM")
//                    .withLocale(locale);
//            TemporalAccessor temporalAccessor = dtFormatter.parse(monthName);
//            return temporalAccessor.get(ChronoField.MONTH_OF_YEAR);

        Date date = new SimpleDateFormat("MMM", locale).parse(monthName);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int monthNumber = cal.get(Calendar.MONTH) + 1;
        System.out.println(monthNumber);

        return monthNumber;
    }
}

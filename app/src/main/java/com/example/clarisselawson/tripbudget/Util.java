package com.example.clarisselawson.tripbudget;

import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by clarisselawson on 12/06/16.
 */
public class Util {
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);

    public static String formatDate(Date date) {
        return dateFormatter.format(date.getTime());
    }

    public static Date displayDateOnButton(Button button, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        button.setText(formatDate(date));
        return date;
    }

    public static String getDefault(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    public static String floatToEditTextValue(float value) {
        // supprimer les chiffres après la virgule s'ils sont nuls
        return String.valueOf(value).replaceAll(".0+$", "");
    }
}

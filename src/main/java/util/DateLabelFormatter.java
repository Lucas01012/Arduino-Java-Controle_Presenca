package util;

import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateLabelFormatter extends DateFormatter {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private SimpleDateFormat sdf;

    public DateLabelFormatter() {
        sdf = new SimpleDateFormat(DATE_PATTERN);
        setFormat(sdf);
    }

    @Override
    public Object stringToValue(String text) {
        try {
            Date parsedDate = sdf.parse(text);
            if (parsedDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedDate);
                return calendar;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String valueToString(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            Date date = calendar.getTime();
            return sdf.format(date);
        }

        return "";
    }
}

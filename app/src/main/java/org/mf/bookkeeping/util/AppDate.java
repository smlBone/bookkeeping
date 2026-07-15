package org.mf.bookkeeping.util;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppDate implements java.io.Serializable, Comparable<AppDate> {
    private static final long serialVersionUID = 3543841154413L;

    private final Calendar calendar;
    private static final Locale china = Locale.CHINA;

    public AppDate() {
        this.calendar = Calendar.getInstance(china);
    }

    public AppDate(Date date) {
        this.calendar = Calendar.getInstance(china);
        this.calendar.setTime(date);
    }

    public AppDate(@NonNull Calendar calendar) {
        this.calendar = (Calendar) calendar.clone();
    }

    public AppDate(int year, int month, int dayOfMonth) {
        this.calendar = Calendar.getInstance(china);
        this.calendar.set(year, month - 1, dayOfMonth, 0, 0, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);
    }

    @NonNull
    public static AppDate now() {
        return new AppDate();
    }

    @NonNull
    public static AppDate yesterday() {
        AppDate date = new AppDate();
        date.calendar.add(Calendar.DAY_OF_MONTH, -1);
        return date;
    }

    @NonNull
    public static AppDate fromMillis(long millis) {
        Calendar cal = Calendar.getInstance(china);
        cal.setTimeInMillis(millis);
        return new AppDate(cal);
    }

    public AppDate addDays(int days) {
        AppDate newDate = new AppDate(this.calendar);
        newDate.calendar.add(Calendar.DAY_OF_MONTH, days);
        return newDate;
    }

    public AppDate addMonths(int months) {
        AppDate newDate = new AppDate(this.calendar);
        newDate.calendar.add(Calendar.MONTH, months);
        return newDate;
    }

    public boolean isSameDay(@NonNull AppDate other) {
        return this.calendar.get(Calendar.YEAR) == other.calendar.get(Calendar.YEAR) &&
                this.calendar.get(Calendar.MONTH) == other.calendar.get(Calendar.MONTH) &&
                this.calendar.get(Calendar.DAY_OF_MONTH) == other.calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String format(String pattern) {
        return new SimpleDateFormat(pattern, china).format(this.calendar.getTime());
    }

    public String getDateString() {
        String dateStr = format("MM-dd");

        if (isSameDay(now())) {
            return dateStr + " 今天";
        } else if (isSameDay(yesterday())) {
            return dateStr + " 昨天";
        } else {
            return dateStr + " " + format("EEE");
        }
    }

    public Date toDate() {
        return this.calendar.getTime();
    }

    public long toMillis() {
        return this.calendar.getTimeInMillis();
    }

    public Calendar toCalendar() {
        return (Calendar) this.calendar.clone();
    }

    public int getYear() {
        return this.calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return this.calendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return this.calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek() {
        return this.calendar.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppDate) {
            return isSameDay((AppDate) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return calendar.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return format("yyyy-MM-dd EEE");
    }

    public String simpleString() {
        return format("yyyy-MM-dd");
    }

    @Override
    public int compareTo(@NonNull AppDate o) {
        int y = this.calendar.get(Calendar.YEAR) - o.calendar.get(Calendar.YEAR);
        int m = this.calendar.get(Calendar.MONTH) - o.calendar.get(Calendar.MONTH);
        if (y == 0) {
            if (m == 0) {
                return this.calendar.get(Calendar.DAY_OF_MONTH) - o.calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                return m;
            }
        } else {
            return y;
        }
    }
}

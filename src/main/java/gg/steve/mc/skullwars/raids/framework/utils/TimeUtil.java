package gg.steve.mc.skullwars.raids.framework.utils;

import java.util.concurrent.TimeUnit;

/**
 * Class that handles converting seconds into days, hours, minutes and seconds
 */
public class TimeUtil {
    //Store the number of days
    private int days;
    //Store the number of hours
    private long hours;
    //String the number of minutes
    private long minutes;
    //Store the number of seconds
    private long seconds;

    /**
     * Converts the parameter into days, hours, minutes and seconds.
     *
     * @param secondsToConvert Integer, the number of seconds to convert
     */
    public TimeUtil(int secondsToConvert) {
        this.days = (int) TimeUnit.SECONDS.toDays(secondsToConvert);
        this.hours = TimeUnit.SECONDS.toHours(secondsToConvert) - (days * 24);
        this.minutes = TimeUnit.SECONDS.toMinutes(secondsToConvert) - (TimeUnit.SECONDS.toHours(secondsToConvert) * 60);
        this.seconds = TimeUnit.SECONDS.toSeconds(secondsToConvert) - (TimeUnit.SECONDS.toMinutes(secondsToConvert) * 60);
    }

    public String getTimeAsString() {
        StringBuilder builder = new StringBuilder();
        if (!getDays().equalsIgnoreCase("0")) {
            builder.append(getDays() + "d");
            if (!getHours().equalsIgnoreCase("0")) builder.append(" ");
        }
        if (!getHours().equalsIgnoreCase("0")) {
            builder.append(getHours() + "h");
            if (!getMinutes().equalsIgnoreCase("0")) builder.append(" ");
        }
        if (!getMinutes().equalsIgnoreCase("0")) {
            builder.append(getMinutes() + "m");
            if (!getSeconds().equalsIgnoreCase("0")) builder.append(" ");
        }
        if (!getSeconds().equalsIgnoreCase("0")) {
            builder.append(getSeconds() + "s");
        }
        return builder.toString();
    }

    /**
     * Getter for the number of days
     *
     * @return String
     */
    public String getDays() {
        return String.valueOf(days);
    }

    /**
     * Getter for the number of hours
     *
     * @return String
     */
    public String getHours() {
        return String.valueOf(hours);
    }

    /**
     * Getter for the number of minutes
     *
     * @return String
     */
    public String getMinutes() {
        return String.valueOf(minutes);
    }

    /**
     * Getter for the number of seconds
     *
     * @return String
     */
    public String getSeconds() {
        return String.valueOf(seconds);
    }
}
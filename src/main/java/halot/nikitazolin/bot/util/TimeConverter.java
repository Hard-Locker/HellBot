package halot.nikitazolin.bot.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class TimeConverter {

//Convert a time string "HH:mm:ss.SSS" to int milliseconds
  public int convertTimeToMillisecond(String timeString) {
    int amountTime = 0;

    String[] firstParts = timeString.split(":");
    String[] secondsParts = firstParts[2].split("\\.");
    int hours = Integer.parseInt(firstParts[0]);
    int minutes = Integer.parseInt(firstParts[1]);
    int seconds = Integer.parseInt(secondsParts[0]);
    int milliseconds = Integer.parseInt(secondsParts[1]);

    amountTime = (hours * 3600 + minutes * 60 + seconds) * 1000 + milliseconds;

    return amountTime;
  }

//Convert a time int milliseconds to string "DD:HH:mm:ss.SSS"
  public String convertIntTimeToSimpleFormatWithMillisecond(int millis) {
    String extendedTime;

    int days = millis / (24 * 60 * 60 * 1000);
    int hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
    int minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
    int seconds = (millis % (60 * 1000)) / 1000;
    int milliseconds = millis % 1000;

    if (days > 0) {
      extendedTime = String.format("%d days %02d:%02d:%02d.%03d", days, hours, minutes, seconds, milliseconds);
    } else if (hours > 0) {
      extendedTime = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    } else {
      extendedTime = String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    return extendedTime;
  }

//Convert a time int milliseconds to string "HH:mm:ss"
  public String convertIntTimeToSimpleFormat(int millis) {
    String extendedTime;

    int hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
    int minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
    int seconds = (millis % (60 * 1000)) / 1000;

    if (hours > 0) {
      extendedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    } else {
      extendedTime = String.format("%02d:%02d", minutes, seconds);
    }

    return extendedTime;
  }

//Convert a time Long milliseconds to string "HH:mm:ss"
  public String convertLongTimeToSimpleFormat(Long millis) {
    String extendedTime;

    long hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
    long minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
    long seconds = (millis % (60 * 1000)) / 1000;

    if (hours > 0) {
      extendedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    } else {
      extendedTime = String.format("%02d:%02d", minutes, seconds);
    }

    return extendedTime;
  }

//Convert a time int milliseconds to string "DD:HH:mm:ss"
  public String convertIntTimeToExtFormat(int millis) {
    String extendedTime;

    int days = millis / (24 * 60 * 60 * 1000);
    int hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
    int minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
    int seconds = (millis % (60 * 1000)) / 1000;

    if (days > 0) {
      extendedTime = String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
    } else if (hours > 0) {
      extendedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    } else {
      extendedTime = String.format("%02d:%02d", minutes, seconds);
    }

    return extendedTime;
  }

//Convert a time Long milliseconds to string "DD:HH:mm:ss"
  public String convertLongTimeToExtFormat(Long millis) {
    String extendedTime;

    long days = millis / (24 * 60 * 60 * 1000);
    long hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
    long minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
    long seconds = (millis % (60 * 1000)) / 1000;

    if (days > 0) {
      extendedTime = String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
    } else if (hours > 0) {
      extendedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    } else {
      extendedTime = String.format("%02d:%02d", minutes, seconds);
    }

    return extendedTime;
  }

// Convert a time in milliseconds to string "yyyy:mm:dd HH:mm:ss"
  public String convertIntTimeToExtendedFormat(int millis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(millis);
    Date date = calendar.getTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    return sdf.format(date);
  }

// Convert a time in milliseconds to string "yyyy:mm:dd HH:mm:ss"
  public String convertLongTimeToExtendedFormat(Long millis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(millis);
    Date date = calendar.getTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    return sdf.format(date);
  }

  // Convert a time in milliseconds to detailed string "YY years MM months DD days
  // HH:mm:ss"
  public String convertTimeToDetailedFormat(long millis) {
    long seconds = millis / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long days = hours / 24;
    long years = days / 365;
    days = days % 365;
    long months = days / 30;
    days = days % 30;

    hours = hours % 24;
    minutes = minutes % 60;
    seconds = seconds % 60;

    return String.format("%d years %d months %d days %02d:%02d:%02d", years, months, days, hours, minutes, seconds);
  }
}

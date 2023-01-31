package dev;

import org.apache.hadoop.io.Text;

public class NcdcRecordParser_v2 {

    private static final int MISSING_TEMPERATURE = 9999;

    private String year;
    private int airTemperature;
    private boolean airTemperatureMalformed;
    private String quality;

    public void parse(String record) {
        year = record.substring(15,19);
        airTemperatureMalformed = false;
        String airTemperatureString;
        if(record.charAt(87) == '+'){
            airTemperatureString = record.substring(88,92);
            airTemperature = Integer.parseInt(airTemperatureString);
        } else if (record.charAt(87) == '-') {
            airTemperatureString = record.substring(87, 92);
            airTemperature = Integer.parseInt(airTemperatureString);
        } else {
            airTemperatureMalformed = true;
        }

        quality = record.substring(92, 93);

    }

    public void parse(Text record) {
        parse(record.toString());
    }

    public boolean isValidTemperature() {
        return !airTemperatureMalformed && airTemperature != MISSING_TEMPERATURE && quality.matches("[01459]");
    }

    public boolean isMalformedTemperature() {
        return airTemperatureMalformed;
    }

    public String getYear() {
        return year;
    }

    public int getAirTemperature() {
        return airTemperature;
    }

}


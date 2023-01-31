package dev;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MaxTemperatureMapperTest {
    @Test
    public void processesValidRecord() throws IOException, InterruptedException {
        // year + temperature
        Text value = new Text("0043011990999991950051518004+68750+023550FM-12+0382" + "99999V0203201N00261220001CN9999999N9-00111+99999999999");

        new MapDriver<LongWritable, Text, Text, IntWritable>()
                .withMapper(new MaxTemperatureMapper_v1())
                .withInput(new LongWritable(0), value)
                .withOutput(new Text("1950"), new IntWritable(-11))
                .runTest();
    }

    // 결측값 테스트
    @Test
    public void ignoresMissingTemperatureRecord() throws IOException, InterruptedException {
        // year + temperature
        Text value = new Text("0043011990999991950051518004+68750+023550FM-12+0382" + "99999V0203201N00261220001CN9999999N9+99991+99999999999");

        new MapDriver<LongWritable, Text, Text, IntWritable>()
                .withMapper(new MaxTemperatureMapper_v1())
                .withInput(new LongWritable(0), value)
                .runTest();
    }

    @Test
    public void processesValidRecord2() throws IOException, InterruptedException {
        // year + temperature
        Text value = new Text("0043011990999991950051518004+68750+023550FM-12+0382" + "99999V0203201N00261220001CN9999999N9-00111+99999999999");

        new MapDriver<LongWritable, Text, Text, IntWritable>()
                .withMapper(new MaxTemperatureMapper_v2())
                .withInput(new LongWritable(0), value)
                .withOutput(new Text("1950"), new IntWritable(-11))
                .runTest();
    }

    // 결측값 테스트
    @Test
    public void ignoresMissingTemperatureRecord2() throws IOException, InterruptedException {
        // year + temperature
        Text value = new Text("0043011990999991950051518004+68750+023550FM-12+0382" + "99999V0203201N00261220001CN9999999N9+99991+99999999999");

        new MapDriver<LongWritable, Text, Text, IntWritable>()
                .withMapper(new MaxTemperatureMapper_v2())
                .withInput(new LongWritable(0), value)
                .runTest();
    }


    @Test
    public void parsesMalformedTemperature() throws IOException, InterruptedException {
        Text value = new Text("0335999999433181957042302005+37950+139117SAO  +0004" +
                // Year
                "RJSN V02011359003150070356999999433201957010100005+353");
                // Temperature
        Counters counters = new Counters();
        new MapDriver<LongWritable, Text, Text, IntWritable>()
                .withMapper(new MaxTemperatureMapper_v4())
                .withInput(new LongWritable(0), value)
                .withCounters(counters)
                .runTest();

        Counter c = counters.findCounter(MaxTemperatureMapper_v4.Temperature.MALFORMED);
        assertThat(c.getValue(), is(1L));



    }
}
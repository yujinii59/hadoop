package dev;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {
    @Test
    public void configTest() {
        Configuration conf = new Configuration();
        conf.addResource("config/configuration-1.xml");
        assertThat(conf.get("color"), is("yellow"));
        assertThat(conf.getInt("size", 0), is(10));
        assertThat(conf.get("breadth", "wide"), is("wide"));
    }

    @Test
    public void configTest2() {
        Configuration conf = new Configuration();
        conf.addResource("config/configuration-1.xml");
        conf.addResource("config/configuration-2.xml");

        assertThat(conf.getInt("size", 0), is(12));
        assertThat(conf.get("weight"), is("heavy"));
        assertThat(conf.get("size-weight"), is("12,heavy"));
        System.setProperty("size", "14");
        assertThat(conf.get("size-weight"), is("14,heavy"));
        System.setProperty("length", "2");
        assertThat(conf.get("length"), is((String) null));
    }
}

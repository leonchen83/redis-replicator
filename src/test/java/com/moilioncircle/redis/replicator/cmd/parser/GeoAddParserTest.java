package com.moilioncircle.redis.replicator.cmd.parser;

import com.moilioncircle.redis.replicator.cmd.impl.GeoAddCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by leon on 2/8/17.
 */
public class GeoAddParserTest {
    @Test
    public void parse() throws Exception {
        GeoAddParser parser = new GeoAddParser();
        GeoAddCommand cmd = parser.parse("GEOADD Sicily 13.361389 38.115556 Palermo 15.087269 37.502669 Catania".split(" "));
        assertEquals("Sicily", cmd.getKey());
        assertEquals(13.361389, cmd.getGeos()[0].getLongitude(), 0.000001);
        assertEquals(38.115556, cmd.getGeos()[0].getLatitude(), 0.000001);
        assertEquals("Palermo", cmd.getGeos()[0].getMember());

        assertEquals(15.087269, cmd.getGeos()[1].getLongitude(), 0.000001);
        assertEquals(37.502669, cmd.getGeos()[1].getLatitude(), 0.000001);
        assertEquals("Catania", cmd.getGeos()[1].getMember());
        System.out.println(cmd);
    }

}
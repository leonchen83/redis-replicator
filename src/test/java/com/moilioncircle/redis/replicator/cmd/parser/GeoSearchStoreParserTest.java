/*
 * Copyright 2016-2018 Leon Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moilioncircle.redis.replicator.cmd.parser;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.moilioncircle.redis.replicator.cmd.impl.GeoSearchStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.OrderType;
import com.moilioncircle.redis.replicator.cmd.impl.UnitType;

/**
 * @author Leon Chen
 * @since 3.5.0
 */
public class GeoSearchStoreParserTest extends AbstractParserTest {
    @Test
    public void parse() {
        {
            GeoSearchStoreParser parser = new GeoSearchStoreParser();
            GeoSearchStoreCommand cmd = parser.parse(toObjectArray("GEOSEARCHSTORE aaa bbb FROMMEMBER ccc FROMLONLAT 100.32 200.45 BYRADIUS 123.5 mi BYBOX 100 200 km desc count 10 WITHCOORD WITHDIST WITHHASH STOREDIST".split(" ")));
            assertEquals("aaa", cmd.getDestination());
            assertEquals("bbb", cmd.getSource());
            assertEquals("ccc", cmd.getFromMember().getMember());
            Assertions.assertEquals(100.32d, cmd.getFromLonLat().getLongitude());
            Assertions.assertEquals(200.45d, cmd.getFromLonLat().getLatitude());
            
            Assertions.assertEquals(123.5d, cmd.getByRadius().getRadius());
            Assertions.assertEquals(UnitType.MI, cmd.getByRadius().getUnitType());
            
            Assertions.assertEquals(100d, cmd.getByBox().getWidth());
            Assertions.assertEquals(200d, cmd.getByBox().getHeight());
            Assertions.assertEquals(UnitType.KM, cmd.getByBox().getUnitType());
            
            Assertions.assertEquals(OrderType.DESC, cmd.getOrderType());
            Assertions.assertEquals(10, cmd.getCount().getCount());
            Assertions.assertTrue(cmd.isStoreDist());
            Assertions.assertTrue(cmd.isWithCoord());
            Assertions.assertTrue(cmd.isWithDist());
            Assertions.assertTrue(cmd.isWithHash());
        }
        
        {
            GeoSearchStoreParser parser = new GeoSearchStoreParser();
            GeoSearchStoreCommand cmd = parser.parse(toObjectArray("GEOSEARCHSTORE aaa bbb FROMLONLAT 100.32 200.45 BYBOX 100 200 km desc count 10 WITHHASH STOREDIST".split(" ")));
            assertEquals("aaa", cmd.getDestination());
            assertEquals("bbb", cmd.getSource());
            assertNull(cmd.getFromMember());
            Assertions.assertEquals(100.32d, cmd.getFromLonLat().getLongitude());
            Assertions.assertEquals(200.45d, cmd.getFromLonLat().getLatitude());
            
            assertNull(cmd.getByRadius());
            
            Assertions.assertEquals(100d, cmd.getByBox().getWidth());
            Assertions.assertEquals(200d, cmd.getByBox().getHeight());
            Assertions.assertEquals(UnitType.KM, cmd.getByBox().getUnitType());
            
            Assertions.assertEquals(OrderType.DESC, cmd.getOrderType());
            Assertions.assertEquals(10, cmd.getCount().getCount());
            Assertions.assertTrue(cmd.isStoreDist());
            Assertions.assertFalse(cmd.isWithCoord());
            Assertions.assertFalse(cmd.isWithDist());
            Assertions.assertTrue(cmd.isWithHash());
        }
        
        {
            GeoSearchStoreParser parser = new GeoSearchStoreParser();
            GeoSearchStoreCommand cmd = parser.parse(toObjectArray("GEOSEARCHSTORE aaa bbb".split(" ")));
            assertEquals("aaa", cmd.getDestination());
            assertEquals("bbb", cmd.getSource());
            assertNull(cmd.getFromMember());
            assertNull(cmd.getFromLonLat());
            assertNull(cmd.getByRadius());
            assertNull(cmd.getByBox());
            
            Assertions.assertEquals(OrderType.NONE, cmd.getOrderType());
            assertNull(cmd.getCount());
            Assertions.assertFalse(cmd.isStoreDist());
            Assertions.assertFalse(cmd.isWithCoord());
            Assertions.assertFalse(cmd.isWithDist());
            Assertions.assertFalse(cmd.isWithHash());
        }
        
        {
            GeoSearchStoreParser parser = new GeoSearchStoreParser();
            GeoSearchStoreCommand cmd = parser.parse(toObjectArray("GEOSEARCHSTORE aaa bbb ASC count 10 STOREDIST".split(" ")));
            assertEquals("aaa", cmd.getDestination());
            assertEquals("bbb", cmd.getSource());
            assertNull(cmd.getFromMember());
            assertNull(cmd.getFromLonLat());
            assertNull(cmd.getByRadius());
            assertNull(cmd.getByBox());
            
            Assertions.assertEquals(OrderType.ASC, cmd.getOrderType());
            assertEquals(10, cmd.getCount().getCount());
            Assertions.assertTrue(cmd.isStoreDist());
            Assertions.assertFalse(cmd.isWithCoord());
            Assertions.assertFalse(cmd.isWithDist());
            Assertions.assertFalse(cmd.isWithHash());
        }
    }
}
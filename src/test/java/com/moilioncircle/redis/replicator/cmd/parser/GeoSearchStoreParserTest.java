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

import static junit.framework.TestCase.assertNull;

import org.junit.Test;

import com.moilioncircle.redis.replicator.cmd.impl.GeoSearchStoreCommand;
import com.moilioncircle.redis.replicator.cmd.impl.OrderType;
import com.moilioncircle.redis.replicator.cmd.impl.UnitType;

import junit.framework.TestCase;

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
            TestCase.assertEquals(100.32d, cmd.getFromLonLat().getLongitude());
            TestCase.assertEquals(200.45d, cmd.getFromLonLat().getLatitude());
            
            TestCase.assertEquals(123.5d, cmd.getByRadius().getRadius());
            TestCase.assertEquals(UnitType.MI, cmd.getByRadius().getUnitType());
            
            TestCase.assertEquals(100d, cmd.getByBox().getWidth());
            TestCase.assertEquals(200d, cmd.getByBox().getHeight());
            TestCase.assertEquals(UnitType.KM, cmd.getByBox().getUnitType());
            
            TestCase.assertEquals(OrderType.DESC, cmd.getOrderType());
            TestCase.assertEquals(10, cmd.getCount().getCount());
            TestCase.assertTrue(cmd.isStoreDist());
            TestCase.assertTrue(cmd.isWithCoord());
            TestCase.assertTrue(cmd.isWithDist());
            TestCase.assertTrue(cmd.isWithHash());
        }
        
        {
            GeoSearchStoreParser parser = new GeoSearchStoreParser();
            GeoSearchStoreCommand cmd = parser.parse(toObjectArray("GEOSEARCHSTORE aaa bbb FROMLONLAT 100.32 200.45 BYBOX 100 200 km desc count 10 WITHHASH STOREDIST".split(" ")));
            assertEquals("aaa", cmd.getDestination());
            assertEquals("bbb", cmd.getSource());
            assertNull(cmd.getFromMember());
            TestCase.assertEquals(100.32d, cmd.getFromLonLat().getLongitude());
            TestCase.assertEquals(200.45d, cmd.getFromLonLat().getLatitude());
            
            assertNull(cmd.getByRadius());
            
            TestCase.assertEquals(100d, cmd.getByBox().getWidth());
            TestCase.assertEquals(200d, cmd.getByBox().getHeight());
            TestCase.assertEquals(UnitType.KM, cmd.getByBox().getUnitType());
            
            TestCase.assertEquals(OrderType.DESC, cmd.getOrderType());
            TestCase.assertEquals(10, cmd.getCount().getCount());
            TestCase.assertTrue(cmd.isStoreDist());
            TestCase.assertFalse(cmd.isWithCoord());
            TestCase.assertFalse(cmd.isWithDist());
            TestCase.assertTrue(cmd.isWithHash());
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
            
            TestCase.assertEquals(OrderType.NONE, cmd.getOrderType());
            assertNull(cmd.getCount());
            TestCase.assertFalse(cmd.isStoreDist());
            TestCase.assertFalse(cmd.isWithCoord());
            TestCase.assertFalse(cmd.isWithDist());
            TestCase.assertFalse(cmd.isWithHash());
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
            
            TestCase.assertEquals(OrderType.ASC, cmd.getOrderType());
            assertEquals(10, cmd.getCount().getCount());
            TestCase.assertTrue(cmd.isStoreDist());
            TestCase.assertFalse(cmd.isWithCoord());
            TestCase.assertFalse(cmd.isWithDist());
            TestCase.assertFalse(cmd.isWithHash());
        }
    }
}
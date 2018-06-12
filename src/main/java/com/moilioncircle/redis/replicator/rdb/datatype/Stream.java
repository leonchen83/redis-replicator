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

package com.moilioncircle.redis.replicator.rdb.datatype;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;

/**
 * @author Leon Chen
 * @since 2.6.0
 */
public class Stream implements Serializable {
    private static final long serialVersionUID = 1L;
    private ID lastId;
    private NavigableMap<ID, Entry> entries;
    private long length;
    private List<Group> groups;
    
    public Stream() {
    
    }
    
    public Stream(ID lastId, NavigableMap<ID, Entry> entries, long length, List<Group> groups) {
        this.lastId = lastId;
        this.entries = entries;
        this.length = length;
        this.groups = groups;
    }
    
    public ID getLastId() {
        return lastId;
    }
    
    public void setLastId(ID lastId) {
        this.lastId = lastId;
    }
    
    public NavigableMap<ID, Entry> getEntries() {
        return entries;
    }
    
    public void setEntries(NavigableMap<ID, Entry> entries) {
        this.entries = entries;
    }
    
    public long getLength() {
        return length;
    }
    
    public void setLength(long length) {
        this.length = length;
    }
    
    public List<Group> getGroups() {
        return groups;
    }
    
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
    
    @Override
    public String toString() {
        return "Stream{" +
                "lastId=" + lastId +
                ", length=" + length +
                ", groups=" + groups +
                '}';
    }
    
    public static class Entry implements Serializable {
        private static final long serialVersionUID = 1L;
        private ID id;
        private boolean deleted;
        private Map<String, String> fields;
        private Map<byte[], byte[]> rawFields;
        
        public Entry() {
        
        }
        
        public Entry(ID id, boolean deleted, Map<String, String> fields) {
            this(id, deleted, fields, null);
        }
        
        public Entry(ID id, boolean deleted, Map<String, String> fields, Map<byte[], byte[]> rawFields) {
            this.id = id;
            this.deleted = deleted;
            this.fields = fields;
            this.rawFields = rawFields;
        }
        
        public ID getId() {
            return id;
        }
        
        public void setId(ID id) {
            this.id = id;
        }
        
        public boolean isDeleted() {
            return deleted;
        }
        
        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }
        
        public Map<String, String> getFields() {
            return fields;
        }
        
        public void setFields(Map<String, String> fields) {
            this.fields = fields;
        }
        
        public Map<byte[], byte[]> getRawFields() {
            return rawFields;
        }
        
        public void setRawFields(Map<byte[], byte[]> rawFields) {
            this.rawFields = rawFields;
        }
        
        @Override
        public String toString() {
            return "Entry{" +
                    "id=" + id +
                    ", deleted=" + deleted +
                    ", fields=" + fields +
                    '}';
        }
    }
    
    public static class Group implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private ID lastId;
        private NavigableMap<ID, Nack> pendingEntries;
        private List<Consumer> consumers;
        private byte[] rawName;
        
        public Group() {
        
        }
    
        public Group(String name, ID lastId, NavigableMap<ID, Nack> pendingEntries, List<Consumer> consumers) {
            this(name, lastId, pendingEntries, consumers, null);
        }
    
        public Group(String name, ID lastId, NavigableMap<ID, Nack> pendingEntries, List<Consumer> consumers, byte[] rawName) {
            this.name = name;
            this.lastId = lastId;
            this.pendingEntries = pendingEntries;
            this.consumers = consumers;
            this.rawName = rawName;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    
        public ID getLastId() {
            return lastId;
        }
    
        public void setLastId(ID lastId) {
            this.lastId = lastId;
        }
        
        public NavigableMap<ID, Nack> getPendingEntries() {
            return pendingEntries;
        }
        
        public void setPendingEntries(NavigableMap<ID, Nack> pendingEntries) {
            this.pendingEntries = pendingEntries;
        }
        
        public List<Consumer> getConsumers() {
            return consumers;
        }
        
        public void setConsumers(List<Consumer> consumers) {
            this.consumers = consumers;
        }
        
        public byte[] getRawName() {
            return rawName;
        }
        
        public void setRawName(byte[] rawName) {
            this.rawName = rawName;
        }
        
        @Override
        public String toString() {
            return "Group{" +
                    "name='" + name + '\'' +
                    ", lastId=" + lastId +
                    ", consumers=" + consumers +
                    '}';
        }
    }
    
    public static class Consumer implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private long seenTime;
        private NavigableMap<ID, Nack> pendingEntries;
        private byte[] rawName;
        
        public Consumer() {
        
        }
        
        public Consumer(String name, long seenTime, NavigableMap<ID, Nack> pendingEntries) {
            this(name, seenTime, pendingEntries, null);
        }
        
        public Consumer(String name, long seenTime, NavigableMap<ID, Nack> pendingEntries, byte[] rawName) {
            this.name = name;
            this.seenTime = seenTime;
            this.pendingEntries = pendingEntries;
            this.rawName = rawName;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public long getSeenTime() {
            return seenTime;
        }
        
        public void setSeenTime(long seenTime) {
            this.seenTime = seenTime;
        }
        
        public NavigableMap<ID, Nack> getPendingEntries() {
            return pendingEntries;
        }
        
        public void setPendingEntries(NavigableMap<ID, Nack> pendingEntries) {
            this.pendingEntries = pendingEntries;
        }
        
        public byte[] getRawName() {
            return rawName;
        }
        
        public void setRawName(byte[] rawName) {
            this.rawName = rawName;
        }
        
        @Override
        public String toString() {
            return "Consumer{" +
                    "name='" + name + '\'' +
                    ", seenTime=" + seenTime +
                    '}';
        }
    }
    
    public static class Nack implements Serializable {
        private static final long serialVersionUID = 1L;
        private ID id;
        private Consumer consumer;
        private long deliveryTime;
        private long deliveryCount;
        
        public Nack() {
        
        }
        
        public Nack(ID id, Consumer consumer, long deliveryTime, long deliveryCount) {
            this.id = id;
            this.consumer = consumer;
            this.deliveryTime = deliveryTime;
            this.deliveryCount = deliveryCount;
        }
        
        public ID getId() {
            return id;
        }
        
        public void setId(ID id) {
            this.id = id;
        }
        
        public Consumer getConsumer() {
            return consumer;
        }
        
        public void setConsumer(Consumer consumer) {
            this.consumer = consumer;
        }
        
        public long getDeliveryTime() {
            return deliveryTime;
        }
        
        public void setDeliveryTime(long deliveryTime) {
            this.deliveryTime = deliveryTime;
        }
        
        public long getDeliveryCount() {
            return deliveryCount;
        }
        
        public void setDeliveryCount(long deliveryCount) {
            this.deliveryCount = deliveryCount;
        }
        
        @Override
        public String toString() {
            return "Nack{" +
                    "id=" + id +
                    ", consumer=" + consumer +
                    ", deliveryTime=" + deliveryTime +
                    ", deliveryCount=" + deliveryCount +
                    '}';
        }
    }
    
    public static class ID implements Serializable, Comparable<ID> {
        private static final long serialVersionUID = 1L;
        private long ms;
        private long seq;
        
        public ID() {
        
        }
        
        public ID(long ms, long seq) {
            this.ms = ms;
            this.seq = seq;
        }
        
        public long getMs() {
            return ms;
        }
        
        public void setMs(long ms) {
            this.ms = ms;
        }
        
        public long getSeq() {
            return seq;
        }
        
        public void setSeq(long seq) {
            this.seq = seq;
        }
        
        public ID delta(long ms, long seq) {
            return new ID(this.ms + ms, this.seq + seq);
        }
        
        @Override
        public String toString() {
            return ms + "-" + seq;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id = (ID) o;
            return ms == id.ms && seq == id.seq;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(ms, seq);
        }
        
        @Override
        public int compareTo(ID that) {
            int r = Long.compare(this.ms, that.ms);
            if (r == 0) return Long.compare(this.seq, that.seq);
            return r;
        }
        
        public static ID valueOf(String id) {
            int idx = id.indexOf('-');
            long ms = Long.parseLong(id.substring(0, idx));
            long seq = Long.parseLong(id.substring(idx + 1, id.length()));
            return new ID(ms, seq);
        }
    
        public static ID valueOf(String strMs, String strSeq) {
            long ms = Long.parseLong(strMs);
            long seq = Long.parseLong(strSeq);
            return new ID(ms, seq);
        }
        
        public static Comparator<ID> comparator() {
            return new Comparator<ID>() {
                @Override
                public int compare(ID o1, ID o2) {
                    return o1.compareTo(o2);
                }
            };
        }
    }
}

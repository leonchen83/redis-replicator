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

import com.moilioncircle.redis.replicator.util.Strings;

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
        String r = "Stream{" + "lastId=" + lastId + ", length=" + length;
        if (groups != null && !groups.isEmpty()) r += ", groups=" + groups;
        if (entries != null && !entries.isEmpty()) r += ", entries=" + entries.size();
        return r + '}';
    }

    public static class Entry implements Serializable {
        private static final long serialVersionUID = 1L;
        private ID id;
        private boolean deleted;
        private Map<byte[], byte[]> fields;

        public Entry() {

        }

        public Entry(ID id, boolean deleted, Map<byte[], byte[]> fields) {
            this.id = id;
            this.deleted = deleted;
            this.fields = fields;
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

        public Map<byte[], byte[]> getFields() {
            return fields;
        }

        public void setFields(Map<byte[], byte[]> fields) {
            this.fields = fields;
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
        private byte[] name;
        private ID lastId;
        private NavigableMap<ID, Nack> pendingEntries;
        private List<Consumer> consumers;

        public Group() {

        }

        public Group(byte[] name, ID lastId, NavigableMap<ID, Nack> pendingEntries, List<Consumer> consumers) {
            this.name = name;
            this.lastId = lastId;
            this.pendingEntries = pendingEntries;
            this.consumers = consumers;
        }

        public byte[] getName() {
            return name;
        }

        public void setName(byte[] name) {
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

        @Override
        public String toString() {
            String r = "Group{" + "name='" + Strings.toString(name) + '\'' + ", lastId=" + lastId;
            if (consumers != null && !consumers.isEmpty()) r += ", consumers=" + consumers;
            if (pendingEntries != null && !pendingEntries.isEmpty()) r += ", gpel=" + pendingEntries.size();
            return r + '}';
        }
    }

    public static class Consumer implements Serializable {
        private static final long serialVersionUID = 1L;
        private byte[] name;
        private long seenTime;
        private NavigableMap<ID, Nack> pendingEntries;

        public Consumer() {

        }

        public Consumer(byte[] name, long seenTime, NavigableMap<ID, Nack> pendingEntries) {
            this.name = name;
            this.seenTime = seenTime;
            this.pendingEntries = pendingEntries;
        }

        public byte[] getName() {
            return name;
        }

        public void setName(byte[] name) {
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

        @Override
        public String toString() {
            String r = "Consumer{" + "name='" + Strings.toString(name) + '\'' + ", seenTime=" + seenTime;
            if (pendingEntries != null && !pendingEntries.isEmpty()) r += ", cpel=" + pendingEntries.size();
            return r + '}';
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
        public static Comparator<ID> COMPARATOR = comparator();

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

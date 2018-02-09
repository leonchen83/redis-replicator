Table of Contents([中文说明](./README.zh_CN.md))  
=================

   * [1. Redis-replicator](#1-redis-replicator)
      * [1.1. Brief introduction](#11-brief-introduction)
      * [1.2. QQ group](#12-qq-group)
      * [1.3. Contract author](#13-contract-author)
   * [2. Install](#2-install)
      * [2.1. Requirements](#21-requirements)
      * [2.2. Maven dependency](#22-maven-dependency)
      * [2.3. Install from source code](#23-install-from-source-code)
      * [2.4. Select a version](#24-select-a-version)
   * [3. Simple usage](#3-simple-usage)
      * [3.1. Replication via socket](#31-replication-via-socket)
      * [3.2. Read rdb file](#32-read-rdb-file)
      * [3.3. Read aof file](#33-read-aof-file)
      * [3.4. Read mixed file](#34-read-mixed-file)
         * [3.4.1. Mixed file format](#341-mixed-file-format)
         * [3.4.2. Mixed file redis configuration](#342-mixed-file-redis-configuration)
         * [3.4.3. Using replicator read mixed file](#343-using-replicator-read-mixed-file)
      * [3.5. Backup remote rdb snapshot](#35-backup-remote-rdb-snapshot)
      * [3.6. Backup remote commands](#36-backup-remote-commands)
      * [3.7. Other examples](#37-other-examples)
   * [4. Advanced topics](#4-advanced-topics)
      * [4.1. Command extension](#41-command-extension)
         * [4.1.1. Write a command](#411-write-a-command)
         * [4.1.2. Write a command parser](#412-write-a-command-parser)
         * [4.1.3. Register this parser](#413-register-this-parser)
         * [4.1.4. Handle command event](#414-handle-command-event)
         * [4.1.5. Put them together](#415-put-them-together)
      * [4.2. Module extension](#42-module-extension)
         * [4.2.1. Compile redis test modules](#421-compile-redis-test-modules)
         * [4.2.2. Open comment in redis.conf](#422-open-comment-in-redisconf)
         * [4.2.3. Write a module parser](#423-write-a-module-parser)
         * [4.2.4. Write a command parser](#424-write-a-command-parser)
         * [4.2.5. Register this module parser and command parser and handle event](#425-register-this-module-parser-and-command-parser-and-handle-event)
         * [4.2.6. Put them together](#426-put-them-together)
      * [4.3. Write your own rdb parser](#43-write-your-own-rdb-parser)
      * [4.4. Event timeline](#44-event-timeline)
      * [4.5. Redis URI](#45-redis-uri)
   * [5. Other topics](#5-other-topics)
      * [5.1. Built-in command parser](#51-built-in-command-parser)
      * [5.2. EOFException](#52-eofexception)
      * [5.3. Trace event log](#53-trace-event-log)
      * [5.4. SSL connection](#54-ssl-connection)
      * [5.5. Auth](#55-auth)
      * [5.6. Avoid full sync](#56-avoid-full-sync)
      * [5.7. FullSyncEvent](#57-fullsyncevent)
      * [5.8. Handle raw bytes](#58-handle-raw-bytes)
      * [5.9. Handle huge key value pair](#59-handle-huge-key-value-pair)
   * [6. Contributors](#6-contributors)
   * [7. References](#7-references)
   * [8. Supported by](#8-supported-by)
      * [8.1. YourKit](#81-yourkit)
      * [8.2. IntelliJ IDEA](#82-intellij-idea)
      * [8.3. Redisson](#83-redisson)
  
# 1. Redis-replicator  

## 1.1. Brief introduction
[![Join the chat at https://gitter.im/leonchen83/redis-replicator](https://badges.gitter.im/leonchen83/redis-replicator.svg)](https://gitter.im/leonchen83/redis-replicator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/leonchen83/redis-replicator.svg?branch=master)](https://travis-ci.org/leonchen83/redis-replicator)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadocs](http://www.javadoc.io/badge/com.moilioncircle/redis-replicator.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)  
  
Redis Replicator implement Redis Replication protocol written in java. It can parse, filter, broadcast the RDB and AOF events in a real time manner. It also can synchronize redis data to your local cache or to database. The following I mentioned `Command` which means `Writable Command`(e.g. `set`,`hmset`) in Redis and excludes the `Readable Command`(e.g. `get`,`hmget`)  

## 1.2. QQ group  
  
**479688557**  

## 1.3. Contract author

**chen.bao.yi@gmail.com**
  
# 2. Install  
## 2.1. Requirements  
jdk 1.7+  
maven-3.2.3+  
redis 2.6 - 4.0.x  

## 2.2. Maven dependency  
```xml  
    <dependency>
        <groupId>com.moilioncircle</groupId>
        <artifactId>redis-replicator</artifactId>
        <version>2.5.0</version>
    </dependency>
```

## 2.3. Install from source code  
  
```
    $mvn clean install package -Dmaven.test.skip=true
```  

## 2.4. Select a version

|     **redis version**        |**redis-replicator version**  |  
| ---------------------------- | ---------------------------- |  
|  \[2.6, 4.0.x\]              |           \[2.3.0, \]        |  
|  \[2.6, 4.0-RC3\]            |       \[2.1.0, 2.2.0\]       |  
|  \[2.6, 3.2.x\]              |  \[1.0.18\](not supported)   |  


# 3. Simple usage  
  
## 3.1. Replication via socket  
  
```java  
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        replicator.open();
```

## 3.2. Read rdb file  

```java  
        Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb");
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });

        replicator.open();
```  

## 3.3. Read aof file  

```java  
        Replicator replicator = new RedisReplicator("redis:///path/to/appendonly.aof");
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        replicator.open();
```  

## 3.4. Read mixed file  
### 3.4.1. Mixed file format  
```java  
    [RDB file][AOF tail]
```
### 3.4.2. Mixed file redis configuration  
```java  
    aof-use-rdb-preamble yes
```
### 3.4.3. Using replicator read mixed file 
```java  
        final Replicator replicator = new RedisReplicator("redis:///path/to/appendonly.aof");
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });

        replicator.open();
```

## 3.5. Backup remote rdb snapshot  

See [RdbBackupExample.java](./examples/com/moilioncircle/examples/backup/RdbBackupExample.java)  

## 3.6. Backup remote commands  

See [CommandBackupExample.java](./examples/com/moilioncircle/examples/backup/CommandBackupExample.java)  

## 3.7. Other examples  

See [examples](./examples/com/moilioncircle/examples/README.md)  

# 4. Advanced topics  

## 4.1. Command extension  
  
### 4.1.1. Write a command  
```java  
    public static class YourAppendCommand implements Command {
        private final String key;
        private final String value;
    
        public YourAppendCommand(String key, String value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
    
        @Override
        public String toString() {
            return "YourAppendCommand{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
            }
        }
    }
```

### 4.1.2. Write a command parser  
```java  

    public class YourAppendParser implements CommandParser<YourAppendCommand> {

        @Override
        public YourAppendCommand parse(Object[] command) {
            return new YourAppendCommand(new String((byte[]) command[1], UTF_8), new String((byte[]) command[2], UTF_8));
        }
    }

```
  
### 4.1.3. Register this parser  
```java  
    Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
    replicator.addCommandParser(CommandName.name("APPEND"),new YourAppendParser());
```
  
### 4.1.4. Handle command event  
```java  
    replicator.addCommandListener(new CommandListener() {
        @Override
        public void handle(Replicator replicator, Command command) {
            if(command instanceof YourAppendCommand){
                YourAppendCommand appendCommand = (YourAppendCommand)command;
                // your code goes here
            }
        }
    });
```  

### 4.1.5. Put them together  

See [CommandExtensionExample.java](./examples/com/moilioncircle/examples/extension/CommandExtensionExample.java)  

## 4.2. Module extension  
### 4.2.1. Compile redis test modules  
```java  
    $cd /path/to/redis-4.0-rc2/src/modules
    $make
```
### 4.2.2. Open comment in redis.conf  

```java  
    loadmodule /path/to/redis-4.0-rc2/src/modules/hellotype.so
```
### 4.2.3. Write a module parser  
```java  
    public class HelloTypeModuleParser implements ModuleParser<HelloTypeModule> {

        @Override
        public HelloTypeModule parse(RedisInputStream in, int version) throws IOException {
            DefaultRdbModuleParser parser = new DefaultRdbModuleParser(in);
            int elements = parser.loadUnsigned(version).intValue();
            long[] ary = new long[elements];
            int i = 0;
            while (elements-- > 0) {
                ary[i++] = parser.loadSigned(version);
            }
            return new HelloTypeModule(ary);
        }
    }

    public class HelloTypeModule implements Module {
        private final long[] value;

        public HelloTypeModule(long[] value) {
            this.value = value;
        }

        public long[] getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "HelloTypeModule{" +
                    "value=" + Arrays.toString(value) +
                    '}';
        }
    }
```
### 4.2.4. Write a command parser  
```java  
    public class HelloTypeParser implements CommandParser<HelloTypeCommand> {
        @Override
        public HelloTypeCommand parse(Object[] command) {
            String key = new String((byte[]) command[1], Constants.UTF_8);
            long value = Long.parseLong(new String((byte[]) command[2], Constants.UTF_8));
            return new HelloTypeCommand(key, value);
        }
    }

    public class HelloTypeCommand implements Command {
        private final String key;
        private final long value;

        public long getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }

        public HelloTypeCommand(String key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "HelloTypeCommand{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }

    }
```
### 4.2.5. Register this module parser and command parser and handle event  

```java  
    public static void main(String[] args) throws IOException {
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        replicator.addCommandParser(CommandName.name("hellotype.insert"), new HelloTypeParser());
        replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv instanceof KeyStringValueModule) {
                    System.out.println(kv);
                }
            }
        });

        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (command instanceof HelloTypeCommand) {
                    System.out.println(command);
                }
            }
        });

        replicator.open();
    }
```

### 4.2.6. Put them together

See [ModuleExtensionExample.java](./examples/com/moilioncircle/examples/extension/ModuleExtensionExample.java)  

## 4.3. Write your own rdb parser  

* Extends `RdbVisitor`  
* Register your `RdbVisitor` to `Replicator` using `setRdbVisitor` method.  

## 4.4. Event timeline  

```java  
     |                     full resynchronization              |  partial resynchronization  |
     ↓-----------<--------------<-------------<----------<-----↓--------------<--------------↑
     ↓                                                         ↓                             ↑ <-reconnect    
 connect->------->-------------->------------->---------->-------------------->--------------x <-disconnect
               ↓              ↓          ↓            ↓                   ↓
          prefullsync    auxfields...  rdbs...   postfullsync            cmds...       
```

## 4.5. Redis URI

Before redis-replicator-2.4.0, We construct `RedisReplicator` like the following:  

```java  
Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/dump.rdb", FileType.RDB, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.AOF, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.MIXED, Configuration.defaultSetting());
```

After redis-replicator-2.4.0, We introduced a new concept(Redis URI) which simplify the constructor of `RedisReplicator`.  

```java  
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb");
Replicator replicator = new RedisReplicator("redis:///path/to/appendonly.aof");

// configuration setting example
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379?authPassword=foobared&readTimeout=10000&ssl=yes");
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb?rateLimit=1000000");
```

# 5. Other topics  
  
## 5.1. Built-in command parser  

|**commands**|**commands**  |  **commands**  |**commands**|**commands**  | **commands**       |
| ---------- | ------------ | ---------------| ---------- | ------------ | ------------------ |    
|  **PING**  |  **APPEND**  |  **SET**       |  **SETEX** |  **MSET**    |  **DEL**           |  
|  **SADD**  |  **HMSET**   |  **HSET**      |  **LSET**  |  **EXPIRE**  |  **EXPIREAT**      |  
| **GETSET** | **HSETNX**   |  **MSETNX**    | **PSETEX** | **SETNX**    |  **SETRANGE**      |  
| **HDEL**   | **UNLINK**   |  **SREM**      | **LPOP**   |  **LPUSH**   | **LPUSHX**         |  
| **LRem**   | **RPOP**     |  **RPUSH**     | **RPUSHX** |  **ZREM**    |  **ZINTERSTORE**   |  
| **INCR**   |  **DECR**    |  **INCRBY**    |**PERSIST** |  **SELECT**  | **FLUSHALL**       |  
|**FLUSHDB** |  **HINCRBY** | **ZINCRBY**    | **MOVE**   |  **SMOVE**   |**BRPOPLPUSH**      |  
|**PFCOUNT** |  **PFMERGE** | **SDIFFSTORE** |**RENAMENX**| **PEXPIREAT**|**SINTERSTORE**     |  
|**ZADD**    | **BITFIELD** |**SUNIONSTORE** |**RESTORE** | **LINSERT**  |**ZREMRANGEBYLEX**  |  
|**GEOADD**  | **PEXPIRE**  |**ZUNIONSTORE** |**EVAL**    |  **SCRIPT**  |**ZREMRANGEBYRANK** |  
|**PUBLISH** |  **BITOP**   |**SETBIT**      | **SWAPDB** | **PFADD**    |**ZREMRANGEBYSCORE**|  
|**RENAME**  |  **MULTI**   |  **EXEC**      | **LTRIM**  |**RPOPLPUSH** |     **SORT**       |  
|**EVALSHA** |              |                |            |              |                    |  
  
## 5.2. EOFException
  
* Adjust redis server setting like the following. more details please refer to [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf)  
  
```java  
    client-output-buffer-limit slave 0 0 0
```  
**WARNNING: this setting may run out of memory of redis server in some cases.**  
  
## 5.3. Trace event log  
  
* Set log level to **debug**
* If you are using log4j2, add logger like the following:

```xml  
    <Logger name="com.moilioncircle" level="debug">
        <AppenderRef ref="YourAppender"/>
    </Logger>
```
  
```java  
    Configuration.defaultSetting().setVerbose(true);
    // redis uri
    "redis://127.0.0.1:6379?verbose=yes"
```
  
## 5.4. SSL connection  
  
```java  
    System.setProperty("javax.net.ssl.trustStore", "/path/to/truststore");
    System.setProperty("javax.net.ssl.trustStorePassword", "password");
    System.setProperty("javax.net.ssl.trustStoreType", "your_type");
    Configuration.defaultSetting().setSsl(true);
    //optional setting
    Configuration.defaultSetting().setSslSocketFactory(sslSocketFactory);
    Configuration.defaultSetting().setSslParameters(sslParameters);
    Configuration.defaultSetting().setHostnameVerifier(hostnameVerifier);
```
  
## 5.5. Auth  
  
```java  
    Configuration.defaultSetting().setAuthPassword("foobared");
    // redis uri
    "redis://127.0.0.1:6379?authPassword=foobared"
```  

## 5.6. Avoid full sync  
  
* Adjust redis server setting like the following  
  
```java  
    repl-backlog-size
    repl-backlog-ttl
    repl-ping-slave-periods
```
`repl-ping-slave-period` **MUST** less than `Configuration.getReadTimeout()`, default `Configuration.getReadTimeout()` is 30 seconds
  
## 5.7. FullSyncEvent  
  
```java  
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        final long start = System.currentTimeMillis();
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
                System.out.println("pre full sync");
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                acc.incrementAndGet();
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                long end = System.currentTimeMillis();
                System.out.println("time elapsed:" + (end - start));
                System.out.println("rdb event count:" + acc.get());
            }
        });
        replicator.open();
```  
  
## 5.8. Handle raw bytes  
  
* For any `KeyValuePair` type except `KeyStringValueModule`, we can get the raw bytes. In some cases(e.g. HyperLogLog),this is very useful.  

  
```java  
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv instanceof KeyStringValueString) {
                    KeyStringValueString ksvs = (KeyStringValueString) kv;
                    byte[] rawValue = ksvs.getRawValue();
                    // handle raw bytes value
                } else if (kv instanceof KeyStringValueHash) {
                    KeyStringValueHash ksvh = (KeyStringValueHash) kv;
                    Map<byte[], byte[]> rawValue = ksvh.getRawValue();
                    // handle raw bytes value
                } else {
                    ...
                }
            }
        });
        replicator.open();
```  
  
For easy operation, the key of return type `Map<byte[], byte[]>` of `KeyStringValueHash.getRawValue`, we can `get` and `put` the key as [value type](https://en.wikipedia.org/wiki/Value_type)  

```java  
KeyStringValueHash ksvh = (KeyStringValueHash) kv;
Map<byte[], byte[]> rawValue = ksvh.getRawValue();
byte[] value = new byte[]{2};
rawValue.put(new byte[]{1}, value);
System.out.println(rawValue.get(new byte[]{1}) == value) //will print true 
```

Commands also support raw bytes.  

```java  
SetCommand set = (SetCommand) command;
byte[] rawKey = set.getRawKey();
byte[] rawValue = set.getRawValue();

```

## 5.9. Handle huge key value pair  

According to [4.3. Write your own rdb parser](#43-write-your-own-rdb-parser), This tool built in an [Iterable Rdb Parser](./src/main/java/com/moilioncircle/redis/replicator/rdb/iterable/ValueIterableRdbVisitor.java) so that handle huge key value pair.  
More details please refer to:  
[1] [HugeKVFileExample.java](./examples/com/moilioncircle/examples/huge/HugeKVFileExample.java)  
[2] [HugeKVSocketExample.java](./examples/com/moilioncircle/examples/huge/HugeKVSocketExample.java)  
  
# 6. Contributors  
* [Leon Chen](https://github.com/leonchen83)  
* [Adrian Yao](https://github.com/adrianyao89)  
* [Trydofor](https://github.com/trydofor)  
* [Argun](https://github.com/Argun)  
* [Sean Pan](https://github.com/XinYang-Pan)  
* Special thanks to [Kevin Zheng](https://github.com/KevinZheng001)  
  
# 7. References  
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  
  * [Redis RDB File Format](https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format)  
  * [Redis Protocol specification](http://redis.io/topics/protocol)
  * [Redis Replication](http://redis.io/topics/replication)

# 8. Supported by  

## 8.1. YourKit  

![YourKit](https://www.yourkit.com/images/yklogo.png)  
YourKit is kindly supporting this open source project with its full-featured Java Profiler.  
YourKit, LLC is the creator of innovative and intelligent tools for profiling  
Java and .NET applications. Take a look at YourKit's leading software products:  
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.  

## 8.2. IntelliJ IDEA  

IntelliJ IDEA is a Java integrated development environment (IDE) for developing computer software.  
It is developed by JetBrains (formerly known as IntelliJ), and is available as an Apache 2 Licensed community edition,  
and in a proprietary commercial edition. Both can be used for commercial development.  

## 8.3. Redisson

Redisson is Redis based In-Memory Data Grid for Java offers distributed objects and services (`BitSet`, `Set`, `Multimap`, `SortedSet`, `Map`, `List`, `Queue`, `BlockingQueue`, `Deque`, `BlockingDeque`, `Semaphore`, `Lock`, `AtomicLong`, `CountDownLatch`, `Publish / Subscribe`, `Bloom filter`, `Remote service`, `Spring cache`, `Executor service`, `Live Object service`, `Scheduler service`) backed by Redis server. Redisson provides more convenient and easiest way to work with Redis. Redisson objects provides a separation of concern, which allows you to keep focus on the data modeling and application logic.

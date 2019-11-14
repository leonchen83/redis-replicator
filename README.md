Table of Contents([中文说明](./README.zh_CN.md))  
=================

   * [1. Redis-replicator](#1-redis-replicator)
      * [1.1. Brief introduction](#11-brief-introduction)
      * [1.2. Chat with author](#12-chat-with-author)
      * [1.3. Contract the author](#13-contract-the-author)
   * [2. Install](#2-install)
      * [2.1. Requirements](#21-requirements)
      * [2.2. Maven dependency](#22-maven-dependency)
      * [2.3. Install from source code](#23-install-from-source-code)
      * [2.4. Select a version](#24-select-a-version)
   * [3. Simple usage](#3-simple-usage)
      * [3.1. Usage](#31-usage)
      * [3.2. Backup remote rdb snapshot](#32-backup-remote-rdb-snapshot)
      * [3.3. Backup remote commands](#33-backup-remote-commands)
      * [3.4. Convert rdb to dump format](#34-convert-rdb-to-dump-format)
      * [3.5. Rdb check](#35-rdb-check)
      * [3.6. Other examples](#36-other-examples)
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
      * [4.3. Stream](#43-stream)
      * [4.4. Write your own rdb parser](#44-write-your-own-rdb-parser)
      * [4.5. Redis URI](#45-redis-uri)
   * [5. Other topics](#5-other-topics)
      * [5.1. Built-in command parser](#51-built-in-command-parser)
      * [5.2. EOFException](#52-eofexception)
      * [5.3. Trace event log](#53-trace-event-log)
      * [5.4. SSL connection](#54-ssl-connection)
      * [5.5. Auth](#55-auth)
      * [5.6. Avoid full sync](#56-avoid-full-sync)
      * [5.7. Lifecycle event](#57-lifecycle-event)
      * [5.8. Handle huge key value pair](#58-handle-huge-key-value-pair)
   * [6. Contributors](#6-contributors)
   * [7. References](#7-references)
   * [8. Supported by](#8-supported-by)
      * [8.1. YourKit](#81-yourkit)
      * [8.2. IntelliJ IDEA](#82-intellij-idea)
      * [8.3. Redisson](#83-redisson)
  
# 1. Redis-replicator  

## 1.1. Brief introduction
[![Build Status](https://travis-ci.org/leonchen83/redis-replicator.svg?branch=master)](https://travis-ci.org/leonchen83/redis-replicator)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadocs](http://www.javadoc.io/badge/com.moilioncircle/redis-replicator.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](./ANTI-996-LICENSE)  
  
Redis Replicator implement Redis Replication protocol written in java. It can parse, filter, broadcast the RDB and AOF events in a real time manner. It also can synchronize redis data to your local cache or to database. The following I mentioned `Command` which means `Writable Command`(e.g. `set`,`hmset`) in Redis and excludes the `Readable Command`(e.g. `get`,`hmget`), Supported redis-5.0 and former redis versions.  

## 1.2. Chat with author  
  
[![Join the chat at https://gitter.im/leonchen83/redis-replicator](https://badges.gitter.im/leonchen83/redis-replicator.svg)](https://gitter.im/leonchen83/redis-replicator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)  

## 1.3. Contract the author

**chen.bao.yi@gmail.com**
  
# 2. Install  
## 2.1. Requirements  
jdk 1.8+  
maven-3.3.1+(support [toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html))  
redis 2.6 - 5.0  

## 2.2. Maven dependency  
```xml  
    <dependency>
        <groupId>com.moilioncircle</groupId>
        <artifactId>redis-replicator</artifactId>
        <version>3.3.1</version>
    </dependency>
```

## 2.3. Install from source code  
  
```
    step 1: install jdk-1.8.x for compile
    step 2: install jdk-9.0.x for compile(or jdk-11.0.x)
    step 3: git clone https://github.com/leonchen83/redis-replicator.git
    step 4: $cd ./redis-replicator 
            replace jdk path in toolchains.xml and save it.
    step 5: $mvn clean install package -Dmaven.test.skip=true --global-toolchains ./toolchains.xml
```  

## 2.4. Select a version

|     **redis version**        |**redis-replicator version**  |  
| ---------------------------- | ---------------------------- |  
|  \[2.6, 5.0.x\]              |       \[2.6.1, \]            |  
|  \[2.6, 4.0.x\]              |       \[2.3.0, 2.5.0\]       |  
|  \[2.6, 4.0-RC3\]            |       \[2.1.0, 2.2.0\]       |  
|  \[2.6, 3.2.x\]              |  \[1.0.18\](not supported)   |  


# 3. Simple usage  

## 3.1. Usage  
  
```java  
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueString) {
                    KeyStringValueString kv = (KeyStringValueString) event;
                    System.out.println(new String(kv.getKey()));
                    System.out.println(new String(kv.getValue()));
                } else {
                    ....
                }
            }
        });
        replicator.open();
```

## 3.2. Backup remote rdb snapshot  

See [RdbBackupExample.java](./examples/com/moilioncircle/examples/backup/RdbBackupExample.java)  

## 3.3. Backup remote commands  

See [CommandBackupExample.java](./examples/com/moilioncircle/examples/backup/CommandBackupExample.java)  

## 3.4. Convert rdb to dump format

We can use `DumpRdbVisitor` to convert rdb to redis [DUMP](https://redis.io/commands/dump) format.  
  
```java  

        Replicator r = new RedisReplicator("redis:///path/to/dump.rdb");
        r.setRdbVisitor(new DumpRdbVisitor(r));
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (!(event instanceof DumpKeyValuePair)) return;
                DumpKeyValuePair dkv = (DumpKeyValuePair) event;
                byte[] serialized = dkv.getValue();
                // we can use redis RESTORE command to migrate this serialized value to another redis.
            }
        });
        r.open();

```

## 3.5. Rdb check

We can use `SkipRdbVisitor` to check rdb's correctness.  

```java  

        Replicator r = new RedisReplicator("redis:///path/to/dump.rdb");
        r.setRdbVisitor(new SkipRdbVisitor(r));
        r.open();

```

## 3.6. Other examples  

See [examples](./examples/com/moilioncircle/examples/README.md)  

# 4. Advanced topics  

## 4.1. Command extension  
  
### 4.1.1. Write a command  
```java  
    public static class YourAppendCommand extends AbstractCommand {
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
    replicator.addEventListener(new EventListener() {
        @Override
        public void onEvent(Replicator replicator, Event event) {
            if(event instanceof YourAppendCommand){
                YourAppendCommand appendCommand = (YourAppendCommand)event;
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

    public class HelloTypeCommand extends AbstractCommand {
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
    }
```
### 4.2.5. Register this module parser and command parser and handle event  

```java  
    public static void main(String[] args) throws IOException {
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        replicator.addCommandParser(CommandName.name("hellotype.insert"), new HelloTypeParser());
        replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueModule) {
                    System.out.println(event);
                }
                
                if (event instanceof HelloTypeCommand) {
                    System.out.println(event);
                }
            }
        });
        replicator.open();
    }
```

### 4.2.6. Put them together

See [ModuleExtensionExample.java](./examples/com/moilioncircle/examples/extension/ModuleExtensionExample.java)  

## 4.3. Stream
  
Since Redis 5.0+, Redis add a new data structure `STREAM`. Redis-replicator parse the `STREAM` like the following:  
  
```java  

        Replicator r = new RedisReplicator("redis://127.0.0.1:6379");
        r.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if (event instanceof KeyStringValueStream) {
                    KeyStringValueStream kv = (KeyStringValueStream)event;
                    // key
                    String key = kv.getKey();
                    
                    // stream
                    Stream stream = kv.getValueAsStream();
                    // last stream id
                    stream.getLastId();
                    
                    // entries
                    NavigableMap<Stream.ID, Stream.Entry> entries = stream.getEntries();
                    
                    // optional : group
                    for (Stream.Group group : stream.getGroups()) {
                        // group PEL(pending entries list)
                        NavigableMap<Stream.ID, Stream.Nack> gpel = group.getPendingEntries();
                        
                        // consumer
                        for (Stream.Consumer consumer : group.getConsumers()) {
                            // consumer PEL(pending entries list)
                            NavigableMap<Stream.ID, Stream.Nack> cpel = consumer.getPendingEntries();
                        }
                    }
                }
            }
        });
        r.open();

```

## 4.4. Write your own rdb parser  

* Write `YourRdbVisitor` extends `RdbVisitor`  
* Register your `RdbVisitor` to `Replicator` using `setRdbVisitor` method.  

## 4.5. Redis URI

Before redis-replicator-2.4.0, We construct `RedisReplicator` like the following:  

```java  
Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/dump.rdb", FileType.RDB, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.AOF, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.MIXED, Configuration.defaultSetting());
```

After redis-replicator-2.4.0, We introduced a new concept(Redis URI) which simplify the construction process of `RedisReplicator`.  

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
|**EVALSHA** | **ZPOPMAX**  | **ZPOPMIN**    | **XACK**   | **XADD**     |  **XCLAIM**        |  
|**XDEL**    | **XGROUP**   | **XTRIM**      | **XSETID** |              |                    |  
  
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
  
## 5.7. Lifecycle event  
  
```java  
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        final long start = System.currentTimeMillis();
        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addEventListener(new EventListener() {
            @Override
            public void onEvent(Replicator replicator, Event event) {
                if(event instanceof PreRdbSyncEvent) {
                    System.out.println("pre rdb sync");
                } else if(event instanceof PostRdbSyncEvent) {
                    long end = System.currentTimeMillis();
                    System.out.println("time elapsed:" + (end - start));
                    System.out.println("rdb event count:" + acc.get());
                } else {
                    acc.incrementAndGet();
                }
            }
        });
        replicator.open();
```  

## 5.8. Handle huge key value pair  

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
  * [Redis RDB File Format](https://github.com/leonchen83/redis-replicator/wiki/RDB-dump-data-format)  
  * [Redis Protocol specification](http://redis.io/topics/protocol)
  * [Redis Replication](http://redis.io/topics/replication)
  * [Redis-replicator Design and Implementation](https://github.com/leonchen83/mycode/blob/master/redis/redis-share/Redis-replicator%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%AE%9E%E7%8E%B0.md)

# 8. Supported by  

## 8.1. YourKit  

![YourKit](https://www.yourkit.com/images/yklogo.png)  
YourKit is kindly supporting this open source project with its full-featured Java Profiler.  
YourKit, LLC is the creator of innovative and intelligent tools for profiling  
Java and .NET applications. Take a look at YourKit's leading software products:  
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.  

## 8.2. IntelliJ IDEA  

[IntelliJ IDEA](https://www.jetbrains.com/?from=redis-replicator) is a Java integrated development environment (IDE) for developing computer software.  
It is developed by JetBrains (formerly known as IntelliJ), and is available as an Apache 2 Licensed community edition,  
and in a proprietary commercial edition. Both can be used for commercial development.  

## 8.3. Redisson

[Redisson](https://github.com/redisson/redisson) is Redis based In-Memory Data Grid for Java offers distributed objects and services (`BitSet`, `Set`, `Multimap`, `SortedSet`, `Map`, `List`, `Queue`, `BlockingQueue`, `Deque`, `BlockingDeque`, `Semaphore`, `Lock`, `AtomicLong`, `CountDownLatch`, `Publish / Subscribe`, `Bloom filter`, `Remote service`, `Spring cache`, `Executor service`, `Live Object service`, `Scheduler service`) backed by Redis server. Redisson provides more convenient and easiest way to work with Redis. Redisson objects provides a separation of concern, which allows you to keep focus on the data modeling and application logic.

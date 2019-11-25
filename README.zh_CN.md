内容索引([Table of Contents](./README.md))  
=================

   * [1. Redis-replicator](#1-redis-replicator)
      * [1.1. 简介](#11-简介)
      * [1.2. QQ讨论组](#12-qq讨论组)
      * [1.3. 联系作者](#13-联系作者)
      * [1.4. 兼容性声明](#14-兼容性声明)
   * [2. 安装](#2-安装)
      * [2.1. 安装前置条件](#21-安装前置条件)
      * [2.2. Maven依赖](#22-maven依赖)
      * [2.3. 安装源码到本地maven仓库](#23-安装源码到本地maven仓库)
      * [2.4. 选择一个版本](#24-选择一个版本)
   * [3. 简要用法](#3-简要用法)
      * [3.1. 用法](#31-用法)
      * [3.2. 备份远程redis的rdb文件](#32-备份远程redis的rdb文件)
      * [3.3. 备份远程redis的实时命令](#33-备份远程redis的实时命令)
      * [3.4. 将rdb转换成dump格式](#34-将rdb转换成dump格式)
      * [3.5. 检查Rdb的正确性](#35-检查rdb的正确性)
      * [3.6. 其他示例](#36-其他示例)
   * [4. 高级主题](#4-高级主题)
      * [4.1. 命令扩展](#41-命令扩展)
         * [4.1.1. 首先写一个command类](#411-首先写一个command类)
         * [4.1.2. 然后写一个command parser](#412-然后写一个command-parser)
         * [4.1.3. 注册这个command parser到replicator](#413-注册这个command-parser到replicator)
         * [4.1.4. 处理这个注册的command事件](#414-处理这个注册的command事件)
         * [4.1.5. 结合到一起](#415-结合到一起)
      * [4.2. Module扩展(redis-4.0及以上)](#42-module扩展redis-40及以上)
         * [4.2.1. 编译redis源码中的测试modules](#421-编译redis源码中的测试modules)
         * [4.2.2. 打开redis配置文件redis.conf中相关注释](#422-打开redis配置文件redisconf中相关注释)
         * [4.2.3. 写一个module parser](#423-写一个module-parser)
         * [4.2.4. 再写一个command parser](#424-再写一个command-parser)
         * [4.2.5. 注册module parser和command parser并处理相关事件](#425-注册module-parser和command-parser并处理相关事件)
         * [4.2.6. 结合到一起](#426-结合到一起)
      * [4.3. Stream](#43-stream)
      * [4.4. 编写你自己的rdb解析器](#44-编写你自己的rdb解析器)
      * [4.5. Redis URI](#45-redis-uri)
   * [5. 其他主题](#5-其他主题)
      * [5.1. 内置的Command Parser](#51-内置的command-parser)
      * [5.2. 当出现EOFException](#52-当出现eofexception)
      * [5.3. 跟踪事件日志log](#53-跟踪事件日志log)
      * [5.4. SSL安全链接](#54-ssl安全链接)
      * [5.5. redis认证](#55-redis认证)
      * [5.6. 避免全量同步](#56-避免全量同步)
      * [5.7. 生命周期事件](#57-生命周期事件)
      * [5.8. 处理巨大的KV](#58-处理巨大的kv)
   * [6. 贡献者](#6-贡献者)
   * [7. 相关引用](#7-相关引用)
   * [8. 致谢](#8-致谢)
      * [8.1. YourKit](#81-yourkit)
      * [8.2. IntelliJ IDEA](#82-intellij-idea)
      * [8.3. Redisson](#83-redisson)
  
# 1. Redis-replicator  

## 1.1. 简介
[![Build Status](https://travis-ci.org/leonchen83/redis-replicator.svg?branch=master)](https://travis-ci.org/leonchen83/redis-replicator)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadocs](http://www.javadoc.io/badge/com.moilioncircle/redis-replicator.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](./ANTI-996-LICENSE_CN)  
  
Redis Replicator是一款RDB解析以及AOF解析的工具. 此工具完整实现了Redis Replication协议. 支持SYNC, PSYNC, PSYNC2等三种同步命令. 还支持远程RDB文件备份以及数据同步等功能. 此文中提到的 `命令` 特指Redis中的写(比如 `set`,`hmset`)命令，不包括读命令(比如 `get`,`hmget`), 支持的redis版本范围从2.6到5.0  

## 1.2. QQ讨论组  
  
**479688557**  

## 1.3. 联系作者  

**chen.bao.yi@qq.com**

## 1.4. 兼容性声明  

**com.moilioncircle.redis.replicator.cmd.impl包下的文件;**  
**由于要兼容redis变化,可能API会根据不同版本有不兼容的调整.**  
  
# 2. 安装  
## 2.1. 安装前置条件  
jdk 1.8+  
maven-3.3.1+(支持 [toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html))  
redis 2.6 - 5.0  

## 2.2. Maven依赖  
```xml  
    <dependency>
        <groupId>com.moilioncircle</groupId>
        <artifactId>redis-replicator</artifactId>
        <version>3.3.3</version>
    </dependency>
```

## 2.3. 安装源码到本地maven仓库  
  
```
    step 1: 安装 jdk-1.8.x
    step 2: 安装 jdk-9.0.x(或jdk-11.0.x)
    step 3: git clone https://github.com/leonchen83/redis-replicator.git
    step 4: cd ./redis-replicator 
            替换toolchains.xml中相应的jdk路径并保存
    step 5: $mvn clean install package -Dmaven.test.skip=true --global-toolchains ./toolchains.xml
```  

## 2.4. 选择一个版本

|     **redis 版本**        |**redis-replicator 版本**  |  
| ------------------------- | ------------------------- |  
|  \[2.6, 5.0.x\]           |       \[2.6.1, \]         |  
|  \[2.6, 4.0.x\]           |       \[2.3.0, 2.5.0\]    |  
|  \[2.6, 4.0-RC3\]         |       \[2.1.0, 2.2.0\]    |  
|  \[2.6, 3.2.x\]           |  \[1.0.18\](不再提供支持)   |  


# 3. 简要用法  
  
## 3.1. 用法  
  
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

## 3.2. 备份远程redis的rdb文件  

参阅 [RdbBackupExample.java](./examples/com/moilioncircle/examples/backup/RdbBackupExample.java)  

## 3.3. 备份远程redis的实时命令  

参阅 [CommandBackupExample.java](./examples/com/moilioncircle/examples/backup/CommandBackupExample.java)  

## 3.4. 将rdb转换成dump格式

我们可以用 `DumpRdbVisitor` 来将 rdb 转换成 redis [DUMP](https://redis.io/commands/dump) 格式。  
  
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

## 3.5. 检查Rdb的正确性

我们可以用 `SkipRdbVisitor` 来检查 rdb 的正确性.  

```java  

        Replicator r = new RedisReplicator("redis:///path/to/dump.rdb");
        r.setRdbVisitor(new SkipRdbVisitor(r));
        r.open();

```

## 3.6. 其他示例  

参阅 [examples](./examples/com/moilioncircle/examples/README.md)  

# 4. 高级主题  

## 4.1. 命令扩展  
  
### 4.1.1. 首先写一个command类  
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

### 4.1.2. 然后写一个command parser  
```java  

    public class YourAppendParser implements CommandParser<YourAppendCommand> {

        @Override
        public YourAppendCommand parse(Object[] command) {
            return new YourAppendCommand(new String((byte[]) command[1], UTF_8), new String((byte[]) command[2], UTF_8));
        }
    }

```
  
### 4.1.3. 注册这个command parser到replicator  
```java  
    Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
    replicator.addCommandParser(CommandName.name("APPEND"),new YourAppendParser());
```
  
### 4.1.4. 处理这个注册的command事件  
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

### 4.1.5. 结合到一起  

参阅 [CommandExtensionExample.java](./examples/com/moilioncircle/examples/extension/CommandExtensionExample.java)  

## 4.2. Module扩展(redis-4.0及以上)  
### 4.2.1. 编译redis源码中的测试modules  
```java  
    $cd /path/to/redis-4.0-rc2/src/modules
    $make
```
### 4.2.2. 打开redis配置文件redis.conf中相关注释  

```java  
    loadmodule /path/to/redis-4.0-rc2/src/modules/hellotype.so
```
### 4.2.3. 写一个module parser  
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
### 4.2.4. 再写一个command parser  
```java  
    public class HelloTypeParser implements CommandParser<HelloTypeCommand> {
        @Override
        public HelloTypeCommand parse(Object[] command) {
            String key = new String((byte[])command[1],Constants.UTF_8);
            long value = Long.parseLong(new String((byte[])command[2],Constants.UTF_8));
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
### 4.2.5. 注册module parser和command parser并处理相关事件  

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

### 4.2.6. 结合到一起  

参阅 [ModuleExtensionExample.java](./examples/com/moilioncircle/examples/extension/ModuleExtensionExample.java)  

## 4.3. Stream
  
Redis-5.0+ 增加了一个新的数据结构 `STREAM`. Redis-replicator 用下述代码解析 `STREAM`  
  
  
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

## 4.4. 编写你自己的rdb解析器  

* 写一个类继承 `RdbVisitor` 抽象类  
* 通过 `Replicator` 的 `setRdbVisitor` 方法注册你自己的 `RdbVisitor`.  
  
## 4.5. Redis URI

在 redis-replicator-2.4.0 版之前, 我们按如下方式构造 `RedisReplicator` :  

```java  
Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/dump.rdb", FileType.RDB, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.AOF, Configuration.defaultSetting());
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.MIXED, Configuration.defaultSetting());
```

在 redis-replicator-2.4.0 版之后, 我们引入了一个新的概念(Redis URI) 来简化 `RedisReplicator` 的构造, 以便提供一致的API.  

```java  
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb");
Replicator replicator = new RedisReplicator("redis:///path/to/appendonly.aof");

// 配置的例子
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379?authPassword=foobared&readTimeout=10000&ssl=yes");
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb?rateLimit=1000000");
```


# 5. 其他主题  
  
## 5.1. 内置的Command Parser  

|  **命令**  |**命令**  |  **命令**  |**命令**|**命令**  | **命令**   |
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
  
## 5.2. 当出现EOFException
  
* 调整redis server中的以下配置. 相关配置请参考 [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf)  
  
```java  
    client-output-buffer-limit slave 0 0 0
```  
**警告: 这个配置可能会使redis-server中的内存溢出**  
  
## 5.3. 跟踪事件日志log  
  
* 日志级别调整成 **debug**
* 如果你项目中使用log4j2,请加入如下Logger到配置文件:

```xml  
    <Logger name="com.moilioncircle" level="debug">
        <AppenderRef ref="YourAppender"/>
    </Logger>
```
  
```java  
    Configuration.defaultSetting().setVerbose(true);
    // redis uri
    "redis://127.0.0.1?verbose=yes"
```
  
## 5.4. SSL安全链接  
  
```java  
    System.setProperty("javax.net.ssl.trustStore", "/path/to/truststore");
    System.setProperty("javax.net.ssl.trustStorePassword", "password");
    System.setProperty("javax.net.ssl.trustStoreType", "your_type");
    Configuration.defaultSetting().setSsl(true);
    //可选设置
    Configuration.defaultSetting().setSslSocketFactory(sslSocketFactory);
    Configuration.defaultSetting().setSslParameters(sslParameters);
    Configuration.defaultSetting().setHostnameVerifier(hostnameVerifier);
```
  
## 5.5. redis认证  
  
```java  
    Configuration.defaultSetting().setAuthPassword("foobared");
    // redis uri
    "redis://127.0.0.1:6379?authPassword=foobared"
```  

## 5.6. 避免全量同步  
  
* 调整redis-server中的如下配置  
  
```java  
    repl-backlog-size
    repl-backlog-ttl
    repl-ping-slave-period
```
`repl-ping-slave-period` **必须** 小于 `Configuration.getReadTimeout()`, 默认的 `Configuration.getReadTimeout()` 是30秒.
  
## 5.7. 生命周期事件  
  
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
  
## 5.8. 处理巨大的KV  

根据 [4.3. 编写你自己的rdb解析器](#43-编写你自己的rdb解析器), 这个工具内嵌了一个[迭代方式的rdb解析器](./src/main/java/com/moilioncircle/redis/replicator/rdb/iterable/ValueIterableRdbVisitor.java), 以便处理巨大的KV.  
详细的例子参阅:  
[1] [HugeKVFileExample.java](./examples/com/moilioncircle/examples/huge/HugeKVFileExample.java)  
[2] [HugeKVSocketExample.java](./examples/com/moilioncircle/examples/huge/HugeKVSocketExample.java)  
  
# 6. 贡献者  

* [Leon Chen](https://github.com/leonchen83)  
* [Adrian Yao](https://github.com/adrianyao89)  
* [Trydofor](https://github.com/trydofor)  
* [Argun](https://github.com/Argun)  
* [Sean Pan](https://github.com/XinYang-Pan)  
* 特别感谢 [Kevin Zheng](https://github.com/KevinZheng001)  
  
# 7. 相关引用  
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  
  * [Redis RDB文件格式](https://github.com/leonchen83/redis-replicator/wiki/RDB-dump-data-format)  
  * [Redis 协议指南](http://redis.io/topics/protocol)
  * [Redis 同步协议](http://redis.io/topics/replication)
  * [Redis-replicator 设计与实现](https://github.com/leonchen83/mycode/blob/master/redis/redis-share/Redis-replicator%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%AE%9E%E7%8E%B0.md)

# 8. 致谢  

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

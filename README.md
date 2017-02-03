# Redis-replicator  

[![Join the chat at https://gitter.im/leonchen83/redis-replicator](https://badges.gitter.im/leonchen83/redis-replicator.svg)](https://gitter.im/leonchen83/redis-replicator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/leonchen83/redis-replicator.svg?branch=master)](https://travis-ci.org/leonchen83/redis-replicator)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.moilioncircle/redis-replicator/badge.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)  
  
Redis Replicator is a redis RDB and Command parser written in java.  
It can parse,filter,broadcast the RDB and Command events in a real time manner.  
It also can sync redis data to your local cache or to database.  
  
##Online Coding  
[![Livecoding viewers](https://tools.livecoding.tv/badge/viewersSmall/921/leonchen83)](https://www.livecoding.tv/leonchen83)
[![Livecoding fans](https://tools.livecoding.tv/badge/followersSmall/106/leonchen83)](https://www.livecoding.tv/leonchen83)
[![Livecoding.scheduled](https://tools.livecoding.tv/badge/nextStreamSmall/no/leonchen83)](https://www.livecoding.tv/leonchen83)  
  
##QQ Group 
  
**479688557**  
  
#Requirements  
jdk 1.7+  
redis 2.4 - 4.0-rc2  

#Install from source code  
  
```
clean install package -Dmaven.test.skip=true
```  

#Usage  
  
##Socket  
  
```java  
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
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

##Rdb file  

```java  
        Replicator replicator = new RedisReplicator(new File("dump.rdb"), Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });

        replicator.open();
```  

##Aof file  

```java  
        Replicator replicator = new RedisReplicator(new File("appendonly.aof"), Configuration.defaultSetting(), false);
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        replicator.open();
```  

#Command Extension  
  
* **write a command**  
```java  
    public class AppendCommand implements Command {
        private final String key;
        private final String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
        
        public AppendCommand(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "AppendCommand{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
```

* **write a command parser.**  
```java
    public class AppendParser implements CommandParser<AppendCommand> {

        @Override
        public AppendCommand parse(CommandName cmdName, Object[] params) {
            return new AppendCommand((String) params[0], (String) params[1]);
        }
    }

```
  
* **register this parser.**  
```java  
    Replicator replicator = new RedisReplicator("127.0.0.1",6379,Configuration.defaultSetting());
    replicator.addCommandParser(CommandName.name("APPEND"),new AppendParser());
```
  
* **handle event about this command.**  
```java
    replicator.addCommandListener(new CommandListener() {
        @Override
        public void handle(Replicator replicator, Command command) {
            if(command instanceof AppendCommand){
                AppendCommand appendCommand = (AppendCommand)command;
                //your code here
            }
        }
    });
```  

#Built-in Parser  

|**commands**|**commands**  |  **commands**  |**commands**|**commands**  | **commands**   |
| ---------- | ------------ | ---------------| ---------- | ------------ | ---------------|    
|  **PING**  |  **APPEND**  |  **SET**       |  **SETEX** |  **MSET**    |  **DEL**       |  
|  **SADD**  |  **HMSET**   |  **HSET**      |  **LSET**  |  **EXPIRE**  |  **EXPIREAT**  |  
| **GETSET** | **HSETNX**   |  **MSETNX**    | **PSETEX** | **SETNX**    |  **SETRANGE**  |  
| **HDEL**   | **HKEYS**    |  **HVALS**     | **LPOP**   |  **LPUSH**   | **LPUSHX**     |  
| **LRem**   | **RPOP**     |  **RPUSH**     | **RPUSHX** |  **ZREM**    |  **RENAME**    |  
| **INCR**   |  **DECR**    |  **INCRBY**    |**PERSIST** |  **SELECT**  | **FLUSHALL**   |  
|**FLUSHDB** |  **HINCRBY** | **ZINCRBY**    | **MOVE**   |  **SMOVE**   |  **PFADD**     |  
|**PFCOUNT** |  **PFMERGE** | **SDIFFSTORE** |**RENAMENX**| **PEXPIREAT**|**SINTERSTORE** |  
|**ZADD**    | **BITFIELD** |**SUNIONSTORE** |**RESTORE** | **LINSERT**  |**ZINTERSTORE** |  
|**GEOADD**  | **PEXPIRE**  |**ZUNIONSTORE** |**EVAL**    |  **SCRIPT**  |**BRPOPLPUSH**  |  
|**PUBLISH** |  **BITOP**   |**SETBIT**      |**SREM**    |  **UNLINK**  |                |  
  
##EOFException
  
* adjust redis server setting below.more details please refer to [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf)  
  
```java
    client-output-buffer-limit slave 0 0 0
```  
**WARNNING: this setting may run out of memory of redis server in some cases.**  
  
##Trace Event log  
  
* set log level to **debug**
* if you are using log4j2,add logger below:

```xml
    <Logger name="com.moilioncircle" level="debug">
        <AppenderRef ref="YourAppender"/>
    </Logger>
```
  
```java
    Configuration.defaultSetting().setVerbose(true);
```
  
##SSL connection  
  
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
  
##Auth  
  
```java
    Configuration.defaultSetting().setAuthPassword("foobared");
```  

##Avoid Full Sync  
  
* adjust redis server setting below  
  
```java
    repl-backlog-size
    repl-backlog-ttl
    repl-ping-slave-period
```
`repl-ping-slave-period` **MUST** less than `Configuration.getReadTimeout()`  
default `Configuration.getReadTimeout()` is 30 seconds
  
##FullSyncEvent  
  
```java
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
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
  
##Handle Raw Bytes  
  
* when kv.getValueRdbType() == 0, you can get the raw bytes of value. In some cases(eg. HyperLogLog),this is very useful.  
  
```java
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                if (kv.getValueRdbType() == 0) {
                    KeyStringValueString ksvs = (KeyStringValueString) kv;
                    System.out.println(Arrays.toString(ksvs.getRawBytes()));
                }
            }
        });
        replicator.open();
```  
  
#References  
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  
  * [Redis RDB File Format](https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format)  
  * [Redis Protocol specification](http://redis.io/topics/protocol)
  * [Redis Replication](http://redis.io/topics/replication)


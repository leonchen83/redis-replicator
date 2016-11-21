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

##Current Stable Version  
  
```java
<dependency>
    <groupId>com.moilioncircle</groupId>
    <artifactId>redis-replicator</artifactId>
    <version>1.0.14</version>
</dependency>
```  
  
#Requirements  
jdk 1.7+  
redis 2.4+  

#Install from source code  
  
```
clean install package -Dmaven.test.skip=true
```  
  
#Flow Chart  
![Alt text](https://raw.githubusercontent.com/leonchen83/redis-replicator/master/docs/redis-replicator-flow-chart.png)  
  
#Class Chart  
![Alt text](https://raw.githubusercontent.com/leonchen83/redis-replicator/master/docs/redis-replicator-class-chart.png)  
  
#All Classes Chart
![Alt text](https://raw.githubusercontent.com/leonchen83/redis-replicator/master/docs/redis-replicator-all-classes-chart.png)  
  
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
        replicator.addRdbFilter(new RdbFilter() {
            @Override
            public boolean accept(KeyValuePair<?> kv) {
                return kv.getKey().startsWith("SESSION");
            }
        });
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
        replicator.addCommandFilter(new CommandFilter() {
                    @Override
                    public boolean accept(Command command) {
                        return command instanceof SetParser.SetCommand && ((SetParser.SetCommand)command).getKey().startsWith("test_");
                    }
                });
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                acc.incrementAndGet();
            }
        });
        replicator.open();
```  

#Command Extension  
  
* **write a command parser.**  
```java  
public class AppendParser implements CommandParser<AppendParser.AppendCommand> {

    @Override
    public AppendCommand parse(CommandName cmdName, Object[] params) {
        return new AppendCommand((String) params[0], (String) params[1]);
    }

    public static class AppendCommand implements Command {
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

        @Override
        public CommandName name() {
            return CommandName.name("APPEND");
        }
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
                if(command instanceof AppendParser.AppendCommand){
                    AppendParser.AppendCommand appendCommand = (AppendParser.AppendCommand)command;
                    //your code here
                }
            }
        });
```  

#Built-in Parser  
  
**PING**  
**APPEND**  
**SET**  
**SETEX**  
**MSET**  
**DEL**  
**SADD**  
**HMSET**  
**HSET**  
**LSET**  
**EXPIRE**  
**EXPIREAT**  
**GETSET**  
**HSETNX**  
**MSETNX**  
**PSETEX**  
**SETNX**  
**SETRANGE**  
**HDEL**  
**HKEYS**  
**HVALS**  
**LPOP**  
**LPUSH**  
**LPUSHX**  
**LRem**  
**RPOP**  
**RPUSH**  
**RPUSHX**  
**ZREM**  
**RENAME**  
**INCR**  
**DECR**  
**INCRBY**  
**PERSIST**  
**SELECT**  
**FLUSHALL**  
**FLUSHDB**  
**HINCRBY**  
**ZINCRBY**  
**MOVE**  
**SMOVE**  
**PFADD**  
**PFCOUNT**  
**PFMERGE**  
**SDIFFSTORE**  
**SINTERSTORE**  
**SUNIONSTORE**  
**ZADD**  
**ZINTERSTORE**  
**ZUNIONSTORE**  
**BRPOPLPUSH**  
**LINSERT**  
**RENAMENX**  
**RESTORE**  
**PEXPIRE**  
**PEXPIREAT**  
**GEOADD**  
**EVAL**  
**SCRIPT**  
**PUBLISH**  
**BITOP**  
**BITFIELD**  
**SETBIT**  
  
##EOFException
  
* adjust redis server setting below.more details please refer to [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf)  
  
```java
    client-output-buffer-limit slave 0 0 0
```  
**WARNNING: this setting may run out of memory of redis server in some cases.**  
  
##Trace Event log  
  
* set log level to **debug**
* if you are using log4j2,add logger below:

```java
    <Logger name="com.moilioncircle" level="debug">
        <AppenderRef ref="YourAppender"/>
    </Logger>
```

  
```java
    Configuration.defaultSetting().setVerbose(true);
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
```
  
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
```  
  
##Handle Raw Bytes  
  
* when kv.getValueRdbType() == 0, you can get the raw bytes of value. In some cases(eg. HyperLogLog),this is very useful.  
  
```java
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        replicator.addRdbFilter(new RdbFilter() {
            @Override
            public boolean accept(KeyValuePair<?> kv) {
                return kv.getValueRdbType() == 0;
            }
        });
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


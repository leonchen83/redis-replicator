
Table of Contents([中文说明](https://github.com/leonchen83/redis-replicator/blob/master/README.zh_CN.md))  
=================

   * [Redis-replicator](#redis-replicator)
      * [Brief introduction](#brief-introduction)
      * [QQ group](#qq-group)
      * [Contract author](#contract-author)
   * [Install](#install)
      * [Requirements](#requirements)
      * [Maven dependency](#maven-dependency)
      * [Install from source code](#install-from-source-code)
   * [Simple usage](#simple-usage)
      * [Replication via socket](#replication-via-socket)
      * [Read rdb file](#read-rdb-file)
      * [Read aof file](#read-aof-file)
      * [Read mixed file](#read-mixed-file)
         * [Mixed file format](#mixed-file-format)
         * [Mixed file redis configuration](#mixed-file-redis-configuration)
         * [Using replicator read mixed file](#using-replicator-read-mixed-file)
      * [Backup remote rdb snapshot](#backup-remote-rdb-snapshot)
      * [Backup remote commands](#backup-remote-commands)
   * [Advanced topics](#advanced-topics)
      * [Command extension](#command-extension)
         * [Write a command](#write-a-command)
         * [Write a command parser](#write-a-command-parser)
         * [Register this parser](#register-this-parser)
         * [Handle command event](#handle-command-event)
      * [Module extension](#module-extension)
         * [Compile redis test modules](#compile-redis-test-modules)
         * [Open comment in redis.conf](#open-comment-in-redisconf)
         * [Write a module parser](#write-a-module-parser)
         * [Write a command parser](#write-a-command-parser-1)
         * [Register this module parser and command parser and handle event](#register-this-module-parser-and-command-parser-and-handle-event)
      * [Write your own rdb parser](#write-your-own-rdb-parser)
      * [Built-in command parser](#built-in-command-parser)
      * [EOFException](#eofexception)
      * [Trace event log](#trace-event-log)
      * [SSL connection](#ssl-connection)
      * [Auth](#auth)
      * [Avoid full sync](#avoid-full-sync)
      * [FullSyncEvent](#fullsyncevent)
      * [Handle raw bytes](#handle-raw-bytes)
   * [Contributors](#contributors)
   * [References](#references)

#Redis-replicator  

##Brief introduction
[![Join the chat at https://gitter.im/leonchen83/redis-replicator](https://badges.gitter.im/leonchen83/redis-replicator.svg)](https://gitter.im/leonchen83/redis-replicator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/leonchen83/redis-replicator.svg?branch=master)](https://travis-ci.org/leonchen83/redis-replicator)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.moilioncircle/redis-replicator/badge.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)  
  
Redis Replicator is a redis rdb and command parser written in java.  
It can parse,filter,broadcast the rdb and command events in a real time manner.  
It also can sync redis data to your local cache or to database.  
The below I mentioned `Command` is only means `Write Command` in redis which excludes `Read Command`(e.g. `get`,`hmget`)  

##QQ group  
  
**479688557**  

##Contract author

**chen.bao.yi@qq.com**
  
#Install  
##Requirements  
jdk 1.7+  
redis 2.4 - 4.0-rc2  
maven-3.2.3 or newer  

##Maven dependency  
```java  
    <dependency>
        <groupId>com.moilioncircle</groupId>
        <artifactId>redis-replicator</artifactId>
        <version>2.0.0-rc2</version>
    </dependency>
```

##Install from source code  
  
```
    $mvn clean install package -Dmaven.test.skip=true
```  

#Simple usage  
  
##Replication via socket  
  
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

##Read rdb file  

```java  
        Replicator replicator = new RedisReplicator(new File("dump.rdb"), FileType.RDB, Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });

        replicator.open();
```  

##Read aof file  

```java  
        Replicator replicator = new RedisReplicator(new File("appendonly.aof"), FileType.AOF, Configuration.defaultSetting());
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        replicator.open();
```  

##Read mixed file  
###Mixed file format  
```java  
    [RDB file][AOF tail]
```
###Mixed file redis configuration  
```java  
    aof-use-rdb-preamble yes
```
###Using replicator read mixed file 
```java  
        final Replicator replicator = new RedisReplicator(new File("appendonly.aof"), FileType.MIXED,
                Configuration.defaultSetting());
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

##Backup remote rdb snapshot  

```java  

        final FileOutputStream out = new FileOutputStream(new File("./dump.rdb"));
        final RawByteListener rawByteListener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                try {
                    out.write(rawBytes);
                } catch (IOException ignore) {
                }
            }
        };

        //save rdb from remote server
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
                replicator.addRdbRawByteListener(rawByteListener);
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                replicator.removeRdbRawByteListener(rawByteListener);
                try {
                    out.close();
                    replicator.close();
                } catch (IOException ignore) {
                }
            }
        });
        replicator.open();

        //check rdb file
        replicator = new RedisReplicator(new File("./dump.rdb"), FileType.RDB, Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });
        replicator.open();
```

##Backup remote commands  

```java  

        final FileOutputStream out = new FileOutputStream(new File("./appendonly.aof"));
        final RawByteListener rawByteListener = new RawByteListener() {
            @Override
            public void handle(byte... rawBytes) {
                try {
                    out.write(rawBytes);
                } catch (IOException ignore) {
                }
            }
        };

        //save 1000 records commands
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                replicator.addRdbRawByteListener(rawByteListener);
            }
        });

        final AtomicInteger acc = new AtomicInteger(0);
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if (acc.incrementAndGet() == 1000) {
                    try {
                        out.close();
                        replicator.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        replicator.open();

        //check aof file
        replicator = new RedisReplicator(new File("./appendonly.aof"), FileType.AOF, Configuration.defaultSetting());
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        replicator.open();
```

#Advanced topics  

##Command extension  
  
###Write a command  
```java  
    public static class YourAppendCommand implements Command {
        public final String key;
        public final String value;
    
        public YourAppendCommand(String key, String value) {
            this.key = key;
            this.value = value;
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

###Write a command parser  
```java  

    public class YourAppendParser implements CommandParser<YourAppendCommand> {

        @Override
        public YourAppendCommand parse(Object[] command) {
            return new YourAppendCommand((String) command[1], (String) command[2]);
        }
    }

```
  
###Register this parser  
```java  
    Replicator replicator = new RedisReplicator("127.0.0.1",6379,Configuration.defaultSetting());
    replicator.addCommandParser(CommandName.name("APPEND"),new YourAppendParser());
```
  
###Handle command event  
```java  
    replicator.addCommandListener(new CommandListener() {
        @Override
        public void handle(Replicator replicator, Command command) {
            if(command instanceof YourAppendCommand){
                YourAppendCommand appendCommand = (YourAppendCommand)command;
                //your code gots here
            }
        }
    });
```  

##Module extension  
###Compile redis test modules  
```java  
    $cd /path/to/redis-4.0-rc2/src/modules
    $make
```
###Open comment in redis.conf  

```java  
    loadmodule /path/to/redis-4.0-rc2/src/modules/hellotype.so
```
###Write a module parser  
```java  
    public class HelloTypeModuleParser implements ModuleParser<HelloTypeModule> {

        @Override
        public HelloTypeModule parse(RedisInputStream in) throws IOException {
            DefaultRdbModuleParser parser = new DefaultRdbModuleParser(in);
            int elements = (int) parser.loadUnSigned();
            long[] ary = new long[elements];
            int i = 0;
            while (elements-- > 0) {
                ary[i++] = parser.loadSigned();
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
###Write a command parser  
```java  
    public class HelloTypeParser implements CommandParser<HelloTypeCommand> {
        @Override
        public HelloTypeCommand parse(Object[] command) {
            String key = (String) command[1];
            long value = Long.parseLong((String) command[2]);
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
###Register this module parser and command parser and handle event  

```java  
    public static void main(String[] args) throws IOException {
        RedisReplicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
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
##Write your own rdb parser  
* implements `RdbVisitor`  
* register your `RdbVisitor` to `Replicator` using `setRdbVisitor` method.  

##Built-in command parser  

|**commands**|**commands**  |  **commands**  |**commands**|**commands**  | **commands**   |
| ---------- | ------------ | ---------------| ---------- | ------------ | ---------------|    
|  **PING**  |  **APPEND**  |  **SET**       |  **SETEX** |  **MSET**    |  **DEL**       |  
|  **SADD**  |  **HMSET**   |  **HSET**      |  **LSET**  |  **EXPIRE**  |  **EXPIREAT**  |  
| **GETSET** | **HSETNX**   |  **MSETNX**    | **PSETEX** | **SETNX**    |  **SETRANGE**  |  
| **HDEL**   | **UNLINK**   |  **SREM**      | **LPOP**   |  **LPUSH**   | **LPUSHX**     |  
| **LRem**   | **RPOP**     |  **RPUSH**     | **RPUSHX** |  **ZREM**    |  **RENAME**    |  
| **INCR**   |  **DECR**    |  **INCRBY**    |**PERSIST** |  **SELECT**  | **FLUSHALL**   |  
|**FLUSHDB** |  **HINCRBY** | **ZINCRBY**    | **MOVE**   |  **SMOVE**   |  **PFADD**     |  
|**PFCOUNT** |  **PFMERGE** | **SDIFFSTORE** |**RENAMENX**| **PEXPIREAT**|**SINTERSTORE** |  
|**ZADD**    | **BITFIELD** |**SUNIONSTORE** |**RESTORE** | **LINSERT**  |**ZINTERSTORE** |  
|**GEOADD**  | **PEXPIRE**  |**ZUNIONSTORE** |**EVAL**    |  **SCRIPT**  |**BRPOPLPUSH**  |  
|**PUBLISH** |  **BITOP**   |**SETBIT**      |            |              |                |  
  
##EOFException
  
* adjust redis server setting below.more details please refer to [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf)  
  
```java  
    client-output-buffer-limit slave 0 0 0
```  
**WARNNING: this setting may run out of memory of redis server in some cases.**  
  
##Trace event log  
  
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

##Avoid full sync  
  
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
  
##Handle raw bytes  
  
* when kv.getValueRdbType() == 0, you can get the raw bytes of value. In some cases(e.g. HyperLogLog),this is very useful.  
  
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

#Contributors  
* Leon Chen  
* Adrian Yao  
  
#References  
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  
  * [Redis RDB File Format](https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format)  
  * [Redis Protocol specification](http://redis.io/topics/protocol)
  * [Redis Replication](http://redis.io/topics/replication)


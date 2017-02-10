内容索引
=================

   * [Redis-replicator](#redis-replicator)
      * [简介](#简介)
      * [QQ讨论组](#qq讨论组)
      * [联系作者](#联系作者)
   * [安装](#安装)
      * [安装前置条件](#安装前置条件)
      * [Maven依赖](#maven依赖)
      * [安装源码到本地maven仓库](#安装源码到本地maven仓库)
   * [简要用法](#简要用法)
      * [通过socket同步](#通过socket同步)
      * [读取并解析rdb文件](#读取并解析rdb文件)
      * [读取并解析aof文件](#读取并解析aof文件)
      * [读取混合格式文件](#读取混合格式文件)
         * [redis混合文件格式](#redis混合文件格式)
         * [redis混合文件格式配置](#redis混合文件格式配置)
         * [应用Replicator读取混合格式文件](#应用replicator读取混合格式文件)
      * [备份远程redis的rdb文件](#备份远程redis的rdb文件)
      * [备份远程redis的实时命令](#备份远程redis的实时命令)
   * [高级主题](#高级主题)
      * [命令扩展](#命令扩展)
         * [首先写一个command类](#首先写一个command类)
         * [然后写一个command parser](#然后写一个command-parser)
         * [注册这个command parser到replicator](#注册这个command-parser到replicator)
         * [处理这个注册的command事件](#处理这个注册的command事件)
      * [Module扩展(redis-4.0及以上)](#module扩展redis-40及以上)
         * [编译redis源码中的测试modules](#编译redis源码中的测试modules)
         * [打开redis配置文件redis.conf中相关注释](#打开redis配置文件redisconf中相关注释)
         * [写一个module parser](#写一个module-parser)
         * [再写一个command parser](#再写一个command-parser)
         * [注册module parser和command parser并处理相关事件](#注册module-parser和command-parser并处理相关事件)
      * [编写你自己的rdb解析器](#编写你自己的rdb解析器)
      * [内置的Command Parser](#内置的command-parser)
      * [当出现EOFException](#当出现eofexception)
      * [跟踪事件日志log](#跟踪事件日志log)
      * [SSL安全链接](#ssl安全链接)
      * [redis认证](#redis认证)
      * [避免全量同步](#避免全量同步)
      * [FullSyncEvent事件](#fullsyncevent事件)
      * [处理原始字节数组](#处理原始字节数组)
   * [贡献者](#贡献者)
   * [相关引用](#相关引用)

#Redis-replicator  

##简介
[![Join the chat at https://gitter.im/leonchen83/redis-replicator](https://badges.gitter.im/leonchen83/redis-replicator.svg)](https://gitter.im/leonchen83/redis-replicator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/leonchen83/redis-replicator.svg?branch=master)](https://travis-ci.org/leonchen83/redis-replicator)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.moilioncircle/redis-replicator/badge.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)  
  
Redis Replicator是一款rdb解析以及命令解析的工具. 此工具完整实现了redis replication协议.  
支持sync,psync,psync2等三种同步命令. 还支持远程rdb文件备份以及数据同步等功能.  
此文中提到的 `命令` 特指redis中的写命令，不包括读命令(比如 `get`,`hmget`)  

##QQ讨论组  
  
**479688557**  

##联系作者

**chen.bao.yi@qq.com**
  
#安装  
##安装前置条件  
jdk 1.7+  
redis 2.4 - 4.0-rc2  
maven-3.2.3以上  

##Maven依赖  
```java  
    <dependency>
        <groupId>com.moilioncircle</groupId>
        <artifactId>redis-replicator</artifactId>
        <version>2.0.0-rc2</version>
    </dependency>
```

##安装源码到本地maven仓库  
  
```
    $mvn clean install package -Dmaven.test.skip=true
```  

#简要用法  
  
##通过socket同步  
  
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

##读取并解析rdb文件  

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

##读取并解析aof文件  

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

##读取混合格式文件  
###redis混合文件格式  
```java  
    [RDB file][AOF tail]
```
###redis混合文件格式配置  
```java  
    aof-use-rdb-preamble yes
```
###应用Replicator读取混合格式文件 
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


##备份远程redis的rdb文件  

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

##备份远程redis的实时命令  

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

#高级主题  

##命令扩展  
  
###首先写一个command类  
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

###然后写一个command parser  
```java  

    public class YourAppendParser implements CommandParser<YourAppendCommand> {

        @Override
        public YourAppendCommand parse(Object[] command) {
            return new YourAppendCommand((String) command[1], (String) command[2]);
        }
    }

```
  
###注册这个command parser到replicator  
```java  
    Replicator replicator = new RedisReplicator("127.0.0.1",6379,Configuration.defaultSetting());
    replicator.addCommandParser(CommandName.name("APPEND"),new YourAppendParser());
```
  
###处理这个注册的command事件  
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

##Module扩展(redis-4.0及以上)  
###编译redis源码中的测试modules  
```java  
    $cd /path/to/redis-4.0-rc2/src/modules
    $make
```
###打开redis配置文件redis.conf中相关注释  

```java  
    loadmodule /path/to/redis-4.0-rc2/src/modules/hellotype.so
```
###写一个module parser  
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
###再写一个command parser  
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
###注册module parser和command parser并处理相关事件  

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
##编写你自己的rdb解析器  
* 写一个类实现 `RdbVisitor`接口  
* 通过`Replicator`的`setRdbVisitor`方法注册你自己的 `RdbVisitor`.  

##内置的Command Parser  

|  **命令**  |**命令**  |  **命令**  |**命令**|**命令**  | **命令**   |
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
  
##当出现EOFException
  
* 调整redis server中的以下配置. 相关配置请参考 [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf)  
  
```java  
    client-output-buffer-limit slave 0 0 0
```  
**警告: 这个配置可能会使redis-server中的内存溢出**  
  
##跟踪事件日志log  
  
* 日志级别调整成 **debug**
* 如果你项目中使用log4j2,请加入如下Logger到配置文件:

```xml  
    <Logger name="com.moilioncircle" level="debug">
        <AppenderRef ref="YourAppender"/>
    </Logger>
```
  
```java  
    Configuration.defaultSetting().setVerbose(true);
```
  
##SSL安全链接  
  
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
  
##redis认证  
  
```java  
    Configuration.defaultSetting().setAuthPassword("foobared");
```  

##避免全量同步  
  
* 调整redis-server中的如下配置  
  
```java  
    repl-backlog-size
    repl-backlog-ttl
    repl-ping-slave-period
```
`repl-ping-slave-period` **必须** 小于 `Configuration.getReadTimeout()`  
默认的 `Configuration.getReadTimeout()` 是30秒.
  
##FullSyncEvent事件  
  
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
  
##处理原始字节数组  
  
* 当kv.getValueRdbType() == 0时, 可以得到原始的字节数组. 在某些情况(比如HyperLogLog)下会很有用.  
  
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
  
#贡献者  
* Leon Chen  
* Adrian Yao  
  
#相关引用  
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  
  * [Redis RDB File Format](https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format)  
  * [Redis Protocol specification](http://redis.io/topics/protocol)
  * [Redis Replication](http://redis.io/topics/replication)


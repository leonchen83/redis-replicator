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
      * [3.1. 通过socket同步](#31-通过socket同步)
      * [3.2. 读取并解析rdb文件](#32-读取并解析rdb文件)
      * [3.3. 读取并解析aof文件](#33-读取并解析aof文件)
      * [3.4. 读取混合格式文件](#34-读取混合格式文件)
         * [3.4.1. redis混合文件格式](#341-redis混合文件格式)
         * [3.4.2. redis混合文件格式配置](#342-redis混合文件格式配置)
         * [3.4.3. 应用Replicator读取混合格式文件](#343-应用replicator读取混合格式文件)
      * [3.5. 备份远程redis的rdb文件](#35-备份远程redis的rdb文件)
      * [3.6. 备份远程redis的实时命令](#36-备份远程redis的实时命令)
   * [4. 高级主题](#4-高级主题)
      * [4.1. 命令扩展](#41-命令扩展)
         * [4.1.1. 首先写一个command类](#411-首先写一个command类)
         * [4.1.2. 然后写一个command parser](#412-然后写一个command-parser)
         * [4.1.3. 注册这个command parser到replicator](#413-注册这个command-parser到replicator)
         * [4.1.4. 处理这个注册的command事件](#414-处理这个注册的command事件)
      * [4.2. Module扩展(redis-4.0及以上)](#42-module扩展redis-40及以上)
         * [4.2.1. 编译redis源码中的测试modules](#421-编译redis源码中的测试modules)
         * [4.2.2. 打开redis配置文件redis.conf中相关注释](#422-打开redis配置文件redisconf中相关注释)
         * [4.2.3. 写一个module parser](#423-写一个module-parser)
         * [4.2.4. 再写一个command parser](#424-再写一个command-parser)
         * [4.2.5. 注册module parser和command parser并处理相关事件](#425-注册module-parser和command-parser并处理相关事件)
      * [4.3. 编写你自己的rdb解析器](#43-编写你自己的rdb解析器)
      * [4.4. 事件时间线](#44-事件时间线)
   * [5. 其他主题](#5-其他主题)
      * [5.1. 内置的Command Parser](#51-内置的command-parser)
      * [5.2. 当出现EOFException](#52-当出现eofexception)
      * [5.3. 跟踪事件日志log](#53-跟踪事件日志log)
      * [5.4. SSL安全链接](#54-ssl安全链接)
      * [5.5. redis认证](#55-redis认证)
      * [5.6. 避免全量同步](#56-避免全量同步)
      * [5.7. FullSyncEvent事件](#57-fullsyncevent事件)
      * [5.8. 处理原始字节数组](#58-处理原始字节数组)
   * [6. 贡献者](#6-贡献者)
   * [7. 相关引用](#7-相关引用)
   * [8. 致谢](#8-致谢)
      * [8.1. YourKit](#81-yourkit)
      * [8.2. IntelliJ IDEA](#82-intellij-idea)
      * [8.3. Redisson](#83-redisson)
  
# 1. Redis-replicator  

## 1.1. 简介
[![Join the chat at https://gitter.im/leonchen83/redis-replicator](https://badges.gitter.im/leonchen83/redis-replicator.svg)](https://gitter.im/leonchen83/redis-replicator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/leonchen83/redis-replicator.svg?branch=master)](https://travis-ci.org/leonchen83/redis-replicator)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.moilioncircle/redis-replicator/badge.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)  
  
Redis Replicator是一款rdb解析以及命令解析的工具. 此工具完整实现了redis replication协议.  
支持sync,psync,psync2等三种同步命令. 还支持远程rdb文件备份以及数据同步等功能.  
此文中提到的 `命令` 特指redis中的写命令，不包括读命令(比如 `get`,`hmget`)  

## 1.2. QQ讨论组  
  
**479688557**  

## 1.3. 联系作者  

**chen.bao.yi@qq.com**

## 1.4. 兼容性声明  

**com.moilioncircle.redis.replicator.cmd.impl包下的文件;**  
**由于要兼容redis变化,可能API会根据不同版本有不兼容的调整.**  
  
# 2. 安装  
## 2.1. 安装前置条件  
jdk 1.7+  
maven-3.2.3+  
redis 2.6 - 4.0  

## 2.2. Maven依赖  
```java  
    <dependency>
        <groupId>com.moilioncircle</groupId>
        <artifactId>redis-replicator</artifactId>
        <version>2.4.1</version>
    </dependency>
```

## 2.3. 安装源码到本地maven仓库  
  
```
    $mvn clean install package -Dmaven.test.skip=true
```  

## 2.4. 选择一个版本

|     **redis 版本**        |**redis-replicator 版本**  |  
| ------------------------- | ------------------------- |  
|  \[2.6, 4.0.0\]           |           \[2.3.0, \]     |  
|  \[2.6, 4.0-RC3\]         |       \[2.1.0, 2.2.0\]    |  
|  \[2.6, 3.2.x\]           |  \[1.0.18\](不再提供支持)   |  


# 3. 简要用法  
  
## 3.1. 通过socket同步  
  
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

## 3.2. 读取并解析rdb文件  

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

## 3.3. 读取并解析aof文件  

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

## 3.4. 读取混合格式文件  
### 3.4.1. redis混合文件格式  
```java  
    [RDB file][AOF tail]
```
### 3.4.2. redis混合文件格式配置  
```java  
    aof-use-rdb-preamble yes
```
### 3.4.3. 应用Replicator读取混合格式文件 
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


## 3.5. 备份远程redis的rdb文件  

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

        //保存server端传来的rdb文件
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void preFullSync(Replicator replicator) {
                replicator.addRawByteListener(rawByteListener);
            }

            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
            }

            @Override
            public void postFullSync(Replicator replicator, long checksum) {
                replicator.removeRawByteListener(rawByteListener);
                try {
                    out.close();
                    replicator.close();
                } catch (IOException ignore) {
                }
            }
        });
        replicator.open();

        //检查写入的rdb文件
        replicator = new RedisReplicator(new File("./dump.rdb"), FileType.RDB, Configuration.defaultSetting());
        replicator.addRdbListener(new RdbListener.Adaptor() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });
        replicator.open();
```

## 3.6. 备份远程redis的实时命令  

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

        //保存1000条命令
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
                replicator.addRawByteListener(rawByteListener);
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

        //检查写入的aof文件
        replicator = new RedisReplicator(new File("./appendonly.aof"), FileType.AOF, Configuration.defaultSetting());
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                System.out.println(command);
            }
        });
        replicator.open();
```

# 4. 高级主题  

## 4.1. 命令扩展  
  
### 4.1.1. 首先写一个command类  
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
    Replicator replicator = new RedisReplicator("127.0.0.1",6379,Configuration.defaultSetting());
    replicator.addCommandParser(CommandName.name("APPEND"),new YourAppendParser());
```
  
### 4.1.4. 处理这个注册的command事件  
```java  
    replicator.addCommandListener(new CommandListener() {
        @Override
        public void handle(Replicator replicator, Command command) {
            if(command instanceof YourAppendCommand){
                YourAppendCommand appendCommand = (YourAppendCommand)command;
                //你的业务代码写在这
            }
        }
    });
```  

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

        @Override
        public String toString() {
            return "HelloTypeModule{" +
                    "value=" + Arrays.toString(value) +
                    '}';
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
### 4.2.5. 注册module parser和command parser并处理相关事件  

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
## 4.3. 编写你自己的rdb解析器  
* 写一个类继承 `RdbVisitor`抽象类  
* 通过`Replicator`的`setRdbVisitor`方法注册你自己的 `RdbVisitor`.  

## 4.4. 事件时间线  

```java  
     |                         全量同步                         |             部分同步          |
     ↓-----------<--------------<-------------<----------<-----↓--------------<--------------↑
     ↓                                                         ↓                             ↑ <-重连    
    连接->------->-------------->------------->---------->-------------------->---------------x <-断线
               ↓              ↓          ↓            ↓                   ↓
          prefullsync    auxfields...  rdbs...   postfullsync            cmds...       
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
```  

## 5.6. 避免全量同步  
  
* 调整redis-server中的如下配置  
  
```java  
    repl-backlog-size
    repl-backlog-ttl
    repl-ping-slave-period
```
`repl-ping-slave-period` **必须** 小于 `Configuration.getReadTimeout()`  
默认的 `Configuration.getReadTimeout()` 是30秒.
  
## 5.7. FullSyncEvent事件  
  
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
  
## 5.8. 处理原始字节数组  
  
* 除`KeyStringValueModule`以外的kv类型, 都可以得到原始的字节数组. 在某些情况(比如HyperLogLog)下会很有用.  
  
```java  
        Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
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
  
为了操作简便`KeyStringValueHash.getRawValue`返回的`Map<byte[], byte[]>`中的key可以当做值类型存取  

```java  
KeyStringValueHash ksvh = (KeyStringValueHash) kv;
Map<byte[], byte[]> rawValue = ksvh.getRawValue();
byte[] value = new byte[]{2};
rawValue.put(new byte[]{1}, value);
System.out.println(rawValue.get(new byte[]{1}) == value) // 会打印true
```

命令解析同样支持原始字节数组.    

```java  
SetCommand set = (SetCommand) command;
byte[] rawKey = set.getRawKey();
byte[] rawValue = set.getRawValue();

```
  
# 6. 贡献者  
* Leon Chen  
* Adrian Yao  
* Trydofor  
  
# 7. 相关引用  
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  
  * [Redis RDB File Format](https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format)  
  * [Redis Protocol specification](http://redis.io/topics/protocol)
  * [Redis Replication](http://redis.io/topics/replication)

# 8. 致谢  

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
<!--@nrg.languages=en,zh-->
<!--@nrg.defaultLanguage=en-->
<!--@nrg.fileNamePattern.zh=README.zh_CN.md-->

Table of Contents ([中文说明](./README.zh_CN.md))<!--en-->
内容索引([Table of Contents](./README.md))  <!--zh-->
=================

* [1. Redis-replicator](#1-redis-replicator)<!--en-->
   * [1. Redis-replicator](#1-redis-replicator)<!--zh-->
    * [1.1. Brief Introduction](#11-brief-introduction)<!--en-->
      * [1.1. 简介](#11-简介)<!--zh-->
    * [1.2. Chat with Author](#12-chat-with-author)<!--en-->
      * [1.2. QQ讨论组](#12-qq讨论组)<!--zh-->
    * [1.3. Contact the Author](#13-contact-the-author)<!--en-->
      * [1.3. 联系作者](#13-联系作者)<!--zh-->
* [2. Installation](#2-installation)<!--en-->
   * [2. 安装](#2-安装)<!--zh-->
    * [2.1. Requirements](#21-requirements)<!--en-->
      * [2.1. 安装前置条件](#21-安装前置条件)<!--zh-->
    * [2.2. Maven Dependency](#22-maven-dependency)<!--en-->
      * [2.2. Maven依赖](#22-maven依赖)<!--zh-->
    * [2.3. Install from Source Code](#23-install-from-source-code)<!--en-->
      * [2.3. 安装源码到本地maven仓库](#23-安装源码到本地maven仓库)<!--zh-->
    * [2.4. Select a Version](#24-select-a-version)<!--en-->
      * [2.4. 选择一个版本](#24-选择一个版本)<!--zh-->
* [3. Simple Usage](#3-simple-usage)<!--en-->
   * [3. 简要用法](#3-简要用法)<!--zh-->
    * [3.1. Basic Usage](#31-basic-usage)<!--en-->
      * [3.1. 用法](#31-用法)<!--zh-->
    * [3.2. Backup Remote RDB Snapshot](#32-backup-remote-rdb-snapshot)<!--en-->
      * [3.2. 备份远程redis的rdb文件](#32-备份远程redis的rdb文件)<!--zh-->
    * [3.3. Backup Remote Commands](#33-backup-remote-commands)<!--en-->
      * [3.3. 备份远程redis的实时命令](#33-备份远程redis的实时命令)<!--zh-->
    * [3.4. Convert RDB to Dump Format](#34-convert-rdb-to-dump-format)<!--en-->
      * [3.4. 将rdb转换成dump格式](#34-将rdb转换成dump格式)<!--zh-->
    * [3.5. RDB Check](#35-rdb-check)<!--en-->
      * [3.5. 检查Rdb的正确性](#35-检查rdb的正确性)<!--zh-->
    * [3.6. Scan and PSYNC](#36-scan-and-psync)<!--en-->
      * [3.6. Scan与PSYNC](#36-scan与psync)<!--zh-->
    * [3.7. Other Examples](#37-other-examples)<!--en-->
      * [3.7. 其他示例](#37-其他示例)<!--zh-->
* [4. Advanced Topics](#4-advanced-topics)<!--en-->
   * [4. 高级主题](#4-高级主题)<!--zh-->
    * [4.1. Command Extension](#41-command-extension)<!--en-->
      * [4.1. 命令扩展](#41-命令扩展)<!--zh-->
        * [4.1.1. Write a Command](#411-write-a-command)<!--en-->
         * [4.1.1. 首先写一个command类](#411-首先写一个command类)<!--zh-->
        * [4.1.2. Write a Command Parser](#412-write-a-command-parser)<!--en-->
         * [4.1.2. 然后写一个command parser](#412-然后写一个command-parser)<!--zh-->
        * [4.1.3. Register the Parser](#413-register-the-parser)<!--en-->
         * [4.1.3. 注册这个command parser到replicator](#413-注册这个command-parser到replicator)<!--zh-->
        * [4.1.4. Handle Command Event](#414-handle-command-event)<!--en-->
         * [4.1.4. 处理这个注册的command事件](#414-处理这个注册的command事件)<!--zh-->
        * [4.1.5. Putting It All Together](#415-putting-it-all-together)<!--en-->
         * [4.1.5. 结合到一起](#415-结合到一起)<!--zh-->
    * [4.2. Module Extension](#42-module-extension)<!--en-->
      * [4.2. Module扩展(redis-4.0及以上)](#42-module扩展redis-40及以上)<!--zh-->
        * [4.2.1. Compile Redis Test Modules](#421-compile-redis-test-modules)<!--en-->
         * [4.2.1. 编译redis源码中的测试modules](#421-编译redis源码中的测试modules)<!--zh-->
        * [4.2.2. Uncomment in redis.conf](#422-uncomment-in-redisconf)<!--en-->
         * [4.2.2. 打开redis配置文件redis.conf中相关注释](#422-打开redis配置文件redisconf中相关注释)<!--zh-->
        * [4.2.3. Write a Module Parser](#423-write-a-module-parser)<!--en-->
         * [4.2.3. 写一个module parser](#423-写一个module-parser)<!--zh-->
        * [4.2.4. Write a Command Parser](#424-write-a-command-parser)<!--en-->
         * [4.2.4. 再写一个command parser](#424-再写一个command-parser)<!--zh-->
        * [4.2.5. Register Parsers and Handle Events](#425-register-parsers-and-handle-events)<!--en-->
         * [4.2.5. 注册module parser和command parser并处理相关事件](#425-注册module-parser和command-parser并处理相关事件)<!--zh-->
        * [4.2.6. Putting It All Together](#426-putting-it-all-together)<!--en-->
         * [4.2.6. 结合到一起](#426-结合到一起)<!--zh-->
    * [4.3. Stream](#43-stream)<!--en-->
      * [4.3. Stream](#43-stream)<!--zh-->
    * [4.4. Write Your Own RDB Parser](#44-write-your-own-rdb-parser)<!--en-->
      * [4.4. 编写你自己的rdb解析器](#44-编写你自己的rdb解析器)<!--zh-->
    * [4.5. Redis URI](#45-redis-uri)<!--en-->
      * [4.5. Redis URI](#45-redis-uri)<!--zh-->
* [5. Other Topics](#5-other-topics)<!--en-->
   * [5. 其他主题](#5-其他主题)<!--zh-->
    * [5.1. Built-in Command Parsers](#51-built-in-command-parsers)<!--en-->
      * [5.1. 内置的Command Parser](#51-内置的command-parser)<!--zh-->
    * [5.2. EOFException](#52-eofexception)<!--en-->
      * [5.2. 当出现EOFException](#52-当出现eofexception)<!--zh-->
    * [5.3. Trace Event Log](#53-trace-event-log)<!--en-->
      * [5.3. 跟踪事件日志log](#53-跟踪事件日志log)<!--zh-->
    * [5.4. SSL Connection](#54-ssl-connection)<!--en-->
      * [5.4. SSL安全链接](#54-ssl安全链接)<!--zh-->
    * [5.5. Authentication](#55-authentication)<!--en-->
      * [5.5. redis认证](#55-redis认证)<!--zh-->
    * [5.6. Avoid Full Sync](#56-avoid-full-sync)<!--en-->
      * [5.6. 避免全量同步](#56-避免全量同步)<!--zh-->
    * [5.7. Lifecycle Events](#57-lifecycle-events)<!--en-->
      * [5.7. 生命周期事件](#57-生命周期事件)<!--zh-->
    * [5.8. Handle Huge Key-Value Pairs](#58-handle-huge-key-value-pairs)<!--en-->
      * [5.8. 处理巨大的KV](#58-处理巨大的kv)<!--zh-->
    * [5.9. Redis 6 Support](#59-redis-6-support)<!--en-->
      * [5.9. Redis6支持](#59-redis6支持)<!--zh-->
        * [5.9.1. SSL Support](#591-ssl-support)<!--en-->
         * [5.9.1. SSL支持](#591-ssl支持)<!--zh-->
        * [5.9.2. ACL Support](#592-acl-support)<!--en-->
         * [5.9.2. ACL支持](#592-acl支持)<!--zh-->
    * [5.10. Redis 7 Support](#510-redis-7-support)<!--en-->
      * [5.10. Redis7支持](#510-redis7支持)<!--zh-->
        * [5.10.1. Function](#5101-function)
    * [5.11. Redis 7.4 Support](#511-redis-74-support)<!--en-->
      * [5.11. Redis7.4支持](#511-redis74支持)<!--zh-->
        * [5.11.1. TTL Hash](#5111-ttl-hash)
* [6. Contributors](#6-contributors)<!--en-->
   * [6. 贡献者](#6-贡献者)<!--zh-->
* [7. Consulting](#7-consulting)<!--en-->
   * [7. 商业咨询](#7-商业咨询)<!--zh-->
* [8. References](#8-references)<!--en-->
   * [8. 相关引用](#8-相关引用)<!--zh-->
* [9. Supported By](#9-supported-by)<!--en-->
   * [9. 致谢](#9-致谢)<!--zh-->
    * [9.1. 宁文君](#91-宁文君)<!--en-->
      * [9.1. 宁文君](#91-宁文君)<!--zh-->
    * [9.2. YourKit](#92-yourkit)<!--en-->
      * [9.2. YourKit](#92-yourkit)<!--zh-->
    * [9.3. IntelliJ IDEA](#93-intellij-idea)<!--en-->
      * [9.3. IntelliJ IDEA](#93-intellij-idea)<!--zh-->
    * [9.4. Redisson](#94-redisson)<!--en-->
      * [9.4. Redisson](#94-redisson)<!--zh-->
<!--en-->
  <!--zh-->
# 1. Redis-replicator<!--en-->
# 1. Redis-replicator  <!--zh-->

<a href="https://www.paypal.com/paypalme/leonchen83" target="_blank"><img src="https://github.com/leonchen83/share/blob/master/other/buymeacoffee.jpg?raw=true" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a><!--en-->
<a href="https://raw.githubusercontent.com/leonchen83/share/master/other/wechat_payment.png" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a><!--zh-->

## 1.1. Brief Introduction<!--en-->
## 1.1. 简介<!--zh-->
[![Java CI with Maven](https://github.com/leonchen83/redis-replicator/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/leonchen83/redis-replicator/actions/workflows/maven.yml)
[![Coverage Status](https://coveralls.io/repos/github/leonchen83/redis-replicator/badge.svg?branch=master)](https://coveralls.io/github/leonchen83/redis-replicator?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.moilioncircle/redis-replicator)
[![Javadocs](http://www.javadoc.io/badge/com.moilioncircle/redis-replicator.svg)](http://www.javadoc.io/doc/com.moilioncircle/redis-replicator)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/leonchen83/redis-replicator/blob/master/LICENSE)
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](./ANTI-996-LICENSE)<!--en-->
[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg?style=flat-square)](./ANTI-996-LICENSE_CN)  <!--zh-->
<!--en-->
  <!--zh-->
Redis Replicator is an implementation of the Redis Replication protocol written in Java. It can parse, filter, and broadcast RDB and AOF events in real-time. It can also synchronize Redis data to a local cache or a database. In this document, `Command` refers to writable commands (e.g., `set`, `hmset`) and excludes readable commands (e.g., `get`, `hmget`). Supports Redis 8.4.x and older versions.<!--en-->
Redis Replicator是一款RDB解析以及AOF解析的工具. 此工具完整实现了Redis Replication协议. 支持SYNC, PSYNC, PSYNC2等三种同步命令. 还支持远程RDB文件备份以及数据同步等功能. 此文中提到的 `命令` 特指Redis中的写(比如 `set`,`hmset`)命令，不包括读命令(比如 `get`,`hmget`), 支持的redis版本范围从2.6到8.4.x  <!--zh-->

## 1.2. Chat with Author<!--en-->
## 1.2. QQ讨论组  <!--zh-->
<!--en-->
  <!--zh-->
[![Join the chat at https://gitter.im/leonchen83/redis-replicator](https://badges.gitter.im/leonchen83/redis-replicator.svg)](https://gitter.im/leonchen83/redis-replicator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)<!--en-->
**479688557**  <!--zh-->

## 1.3. Contact the Author<!--en-->
## 1.3. 联系作者  <!--zh-->

**chen.bao.yi@gmail.com**<!--en-->
**chen.bao.yi@qq.com**<!--zh-->

# 2. Installation<!--en-->
# 2. 安装  <!--zh-->
## 2.1. Requirements<!--en-->
## 2.1. 安装前置条件  <!--zh-->
- **Compile:** JDK 9+<!--en-->
编译最低jdk版本 jdk9+  <!--zh-->
- **Runtime:** JDK 8+<!--en-->
运行最低jdk版本 jdk8+  <!--zh-->
- **Maven:** 3.3.1+<!--en-->
maven-3.3.1+  <!--zh-->
- **Redis:** 2.6 - 8.4<!--en-->
redis 2.6 - 8.4  <!--zh-->

## 2.2. Maven Dependency<!--en-->
## 2.2. Maven依赖  <!--zh-->
```xml<!--en-->
```xml  <!--zh-->
<dependency><!--en-->
    <dependency><!--zh-->
    <groupId>com.moilioncircle</groupId><!--en-->
        <groupId>com.moilioncircle</groupId><!--zh-->
    <artifactId>redis-replicator</artifactId><!--en-->
        <artifactId>redis-replicator</artifactId><!--zh-->
    <version>3.11.0</version><!--en-->
        <version>3.11.0</version><!--zh-->
</dependency><!--en-->
    </dependency><!--zh-->
```

## 2.3. Install from Source Code<!--en-->
## 2.3. 安装源码到本地maven仓库  <!--zh-->
<!--en-->
  <!--zh-->
```bash<!--en-->
```<!--zh-->
# Step 1: Install JDK 11+ for compilation<!--en-->
    step 1: 安装 jdk-11.0.x<!--zh-->
# Step 2: Clone the repository<!--en-->
    step 2: git clone https://github.com/leonchen83/redis-replicator.git<!--zh-->
git clone https://github.com/leonchen83/redis-replicator.git<!--en-->
    step 3: $cd ./redis-replicator <!--zh-->
# Step 3: Navigate to the project directory<!--en-->
    step 4: $mvn clean install package -DskipTests<!--zh-->
cd redis-replicator<!--en-->
```  <!--zh-->
# Step 4: Build the project<!--en-->
<!--zh-->
mvn clean install package -DskipTests<!--en-->
## 2.4. 选择一个版本<!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
| **Redis Version** | **redis-replicator Version** |<!--zh-->
## 2.4. Select a Version<!--en-->
|-------------------|------------------------------|<!--zh-->
<!--en-->
| [2.6, 8.4.x]      | [3.11.0,     ]               |<!--zh-->
| **Redis Version** | **redis-replicator Version** |<!--en-->
| [2.6, 8.2.x]      | [3.10.0,3.10.0]              |<!--zh-->
|-------------------|------------------------------|<!--en-->
| [2.6, 8.0.x]      | [3.9.0, 3.9.0]               |<!--zh-->
| [2.6, 8.4.x]      | [3.11.0,     ]               |<!--en-->
| [2.6, 7.2.x]      | [3.8.0, 3.8.1]               |<!--zh-->
| [2.6, 8.2.x]      | [3.10.0,3.10.0]              |<!--en-->
| [2.6, 7.0.x]      | [3.6.4, 3.7.0]               |<!--zh-->
| [2.6, 8.0.x]      | [3.9.0, 3.9.0]               |<!--en-->
| [2.6, 7.0.x-RC2]  | [3.6.2, 3.6.3]               |<!--zh-->
| [2.6, 7.2.x]      | [3.8.0, 3.8.1]               |<!--en-->
| [2.6, 7.0.0-RC1]  | [3.6.0, 3.6.1]               |<!--zh-->
| [2.6, 7.0.x]      | [3.6.4, 3.7.0]               |<!--en-->
| [2.6, 6.2.x]      | [3.5.2, 3.5.5]               |<!--zh-->
| [2.6, 7.0.x-RC2]  | [3.6.2, 3.6.3]               |<!--en-->
| [2.6, 6.2.0-RC1]  | [3.5.0, 3.5.1]               |<!--zh-->
| [2.6, 7.0.0-RC1]  | [3.6.0, 3.6.1]               |<!--en-->
| [2.6, 6.0.x]      | [3.4.0, 3.4.4]               |<!--zh-->
| [2.6, 6.2.x]      | [3.5.2, 3.5.5]               |<!--en-->
| [2.6, 5.0.x]      | [2.6.1, 3.3.3]               |<!--zh-->
| [2.6, 6.2.0-RC1]  | [3.5.0, 3.5.1]               |<!--en-->
| [2.6, 4.0.x]      | [2.3.0, 2.5.0]               |<!--zh-->
| [2.6, 6.0.x]      | [3.4.0, 3.4.4]               |<!--en-->
| [2.6, 4.0-RC3]    | [2.1.0, 2.2.0]               |<!--zh-->
| [2.6, 5.0.x]      | [2.6.1, 3.3.3]               |<!--en-->
| [2.6, 3.2.x]      | [1.0.18] (not supported)     |<!--zh-->
| [2.6, 4.0.x]      | [2.3.0, 2.5.0]               |<!--en-->
<!--zh-->
| [2.6, 4.0-RC3]    | [2.1.0, 2.2.0]               |<!--en-->
<!--zh-->
| [2.6, 3.2.x]      | [1.0.18] (not supported)     |<!--en-->
# 3. 简要用法  <!--zh-->
<!--en-->
  <!--zh-->
<!--en-->
## 3.1. 用法  <!--zh-->
# 3. Simple Usage<!--en-->
  <!--zh-->
<!--en-->
```java  <!--zh-->
## 3.1. Basic Usage<!--en-->
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
<!--en-->
        replicator.addEventListener(new EventListener() {<!--zh-->
```java<!--en-->
            @Override<!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
            public void onEvent(Replicator replicator, Event event) {<!--zh-->
replicator.addEventListener(new EventListener() {<!--en-->
                if (event instanceof KeyStringValueString) {<!--zh-->
    @Override<!--en-->
                    KeyStringValueString kv = (KeyStringValueString) event;<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
                    System.out.println(new String(kv.getKey()));<!--zh-->
        if (event instanceof KeyStringValueString) {<!--en-->
                    System.out.println(new String(kv.getValue()));<!--zh-->
            KeyStringValueString kv = (KeyStringValueString) event;<!--en-->
                } else {<!--zh-->
            System.out.println(new String(kv.getKey()));<!--en-->
                    ....<!--zh-->
            System.out.println(new String(kv.getValue()));<!--en-->
                }<!--zh-->
        } else {<!--en-->
            }<!--zh-->
            // ...<!--en-->
        });<!--zh-->
        }<!--en-->
        replicator.open();<!--zh-->
    }<!--en-->
```<!--zh-->
});<!--en-->
<!--zh-->
replicator.open();<!--en-->
## 3.2. 备份远程redis的rdb文件  <!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
参阅 [RdbBackupExample.java](./examples/com/moilioncircle/examples/backup/RdbBackupExample.java)  <!--zh-->
## 3.2. Backup Remote RDB Snapshot<!--en-->
<!--zh-->
<!--en-->
## 3.3. 备份远程redis的实时命令  <!--zh-->
See [RdbBackupExample.java](./examples/com/moilioncircle/examples/backup/RdbBackupExample.java)<!--en-->
<!--zh-->
<!--en-->
参阅 [CommandBackupExample.java](./examples/com/moilioncircle/examples/backup/CommandBackupExample.java)  <!--zh-->
## 3.3. Backup Remote Commands<!--en-->
<!--zh-->
<!--en-->
## 3.4. 将rdb转换成dump格式<!--zh-->
See [CommandBackupExample.java](./examples/com/moilioncircle/examples/backup/CommandBackupExample.java)<!--en-->
<!--zh-->
<!--en-->
我们可以用 `DumpRdbVisitor` 来将 rdb 转换成 redis [DUMP](https://redis.io/commands/dump) 格式。  <!--zh-->
## 3.4. Convert RDB to Dump Format<!--en-->
  <!--zh-->
<!--en-->
```java  <!--zh-->
You can use `DumpRdbVisitor` to convert an RDB file to the Redis [DUMP](https://redis.io/commands/dump) format.<!--en-->
<!--zh-->
<!--en-->
        Replicator r = new RedisReplicator("redis:///path/to/dump.rdb");<!--zh-->
```java<!--en-->
        r.setRdbVisitor(new DumpRdbVisitor(r));<!--zh-->
Replicator r = new RedisReplicator("redis:///path/to/dump.rdb");<!--en-->
        r.addEventListener(new EventListener() {<!--zh-->
r.setRdbVisitor(new DumpRdbVisitor(r));<!--en-->
            @Override<!--zh-->
r.addEventListener(new EventListener() {<!--en-->
            public void onEvent(Replicator replicator, Event event) {<!--zh-->
    @Override<!--en-->
                if (!(event instanceof DumpKeyValuePair)) return;<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
                DumpKeyValuePair dkv = (DumpKeyValuePair) event;<!--zh-->
        if (!(event instanceof DumpKeyValuePair)) return;<!--en-->
                byte[] serialized = dkv.getValue();<!--zh-->
        DumpKeyValuePair dkv = (DumpKeyValuePair) event;<!--en-->
                // we can use redis RESTORE command to migrate this serialized value to another redis.<!--zh-->
        byte[] serialized = dkv.getValue();<!--en-->
            }<!--zh-->
        // We can use the Redis RESTORE command to migrate this serialized value to another Redis instance.<!--en-->
        });<!--zh-->
    }<!--en-->
        r.open();<!--zh-->
});<!--en-->
<!--zh-->
r.open();<!--en-->
```<!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
## 3.5. 检查Rdb的正确性<!--zh-->
## 3.5. RDB Check<!--en-->
<!--zh-->
<!--en-->
我们可以用 `SkipRdbVisitor` 来检查 rdb 的正确性.  <!--zh-->
You can use `SkipRdbVisitor` to check the correctness of an RDB file.<!--en-->
<!--zh-->
<!--en-->
```java  <!--zh-->
```java<!--en-->
<!--zh-->
Replicator r = new RedisReplicator("redis:///path/to/dump.rdb");<!--en-->
        Replicator r = new RedisReplicator("redis:///path/to/dump.rdb");<!--zh-->
r.setRdbVisitor(new SkipRdbVisitor(r));<!--en-->
        r.setRdbVisitor(new SkipRdbVisitor(r));<!--zh-->
r.open();<!--en-->
        r.open();<!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
```<!--zh-->
## 3.6. Scan and PSYNC<!--en-->
<!--zh-->
<!--en-->
## 3.6. Scan与PSYNC<!--zh-->
By default, redis-replicator uses the `PSYNC` command, pretending to be a replica, to receive commands. An example is as follows:<!--en-->
<!--zh-->
```java<!--en-->
默认情况下, redis-replicator 使用 PSYNC 命令伪装成slave接收命令, 如下所示<!--zh-->
Replicator r = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
```java<!--zh-->
r.addEventListener(new EventListener() {<!--en-->
        Replicator r = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
    @Override<!--en-->
        r.addEventListener(new EventListener() {<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
            @Override<!--zh-->
        System.out.println(event);<!--en-->
            public void onEvent(Replicator replicator, Event event) {<!--zh-->
    }<!--en-->
                System.out.println(event);<!--zh-->
});<!--en-->
            }<!--zh-->
<!--en-->
        });<!--zh-->
r.open();<!--en-->
        <!--zh-->
```<!--en-->
        r.open();<!--zh-->

However, on some cloud services, the `PSYNC` command is prohibited. In such cases, you can use the `SCAN` command instead:<!--en-->
```<!--zh-->
```java<!--en-->
<!--zh-->
Replicator r = new RedisReplicator("redis://127.0.0.1:6379?enableScan=yes&scanStep=256");<!--en-->
然而, 在某些云服务中, PSYNC 是被禁止使用的, 因此我们使用 Scan 命令来替换PSYNC命令扫描全库, 如下所示<!--zh-->
r.addEventListener(new EventListener() {<!--en-->
```java<!--zh-->
    @Override<!--en-->
<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
        Replicator r = new RedisReplicator("redis://127.0.0.1:6379?enableScan=yes&scanStep=256");<!--zh-->
        System.out.println(event);<!--en-->
        r.addEventListener(new EventListener() {<!--zh-->
    }<!--en-->
            @Override<!--zh-->
});<!--en-->
            public void onEvent(Replicator replicator, Event event) {<!--zh-->
<!--en-->
                System.out.println(event);<!--zh-->
r.open();<!--en-->
            }<!--zh-->
```<!--en-->
        });<!--zh-->
<!--en-->
        <!--zh-->
## 3.7. Other Examples<!--en-->
        r.open();<!--zh-->

See [examples](./examples/com/moilioncircle/examples/README.md)<!--en-->
```<!--zh-->

# 4. Advanced Topics<!--en-->
## 3.7. 其他示例  <!--zh-->

## 4.1. Command Extension<!--en-->
参阅 [examples](./examples/com/moilioncircle/examples/README.md)  <!--zh-->

### 4.1.1. Write a Command<!--en-->
# 4. 高级主题  <!--zh-->
```java<!--en-->
<!--zh-->
@CommandSpec(command = "APPEND")<!--en-->
## 4.1. 命令扩展  <!--zh-->
public static class YourAppendCommand extends AbstractCommand {<!--en-->
  <!--zh-->
    private final String key;<!--en-->
### 4.1.1. 首先写一个command类  <!--zh-->
    private final String value;<!--en-->
```java  <!--zh-->
<!--en-->
    @CommandSpec(command = "APPEND")<!--zh-->
    public YourAppendCommand(String key, String value) {<!--en-->
    public static class YourAppendCommand extends AbstractCommand {<!--zh-->
        this.key = key;<!--en-->
        private final String key;<!--zh-->
        this.value = value;<!--en-->
        private final String value;<!--zh-->
    }<!--en-->
    <!--zh-->
    <!--en-->
        public YourAppendCommand(String key, String value) {<!--zh-->
    public String getKey() {<!--en-->
            this.key = key;<!--zh-->
        return key;<!--en-->
            this.value = value;<!--zh-->
    }<!--en-->
        }<!--zh-->
    <!--en-->
                <!--zh-->
    public String getValue() {<!--en-->
        public String getKey() {<!--zh-->
        return value;<!--en-->
            return key;<!--zh-->
    }<!--en-->
        }<!--zh-->
}<!--en-->
        <!--zh-->
```<!--en-->
        public String getValue() {<!--zh-->
<!--en-->
            return value;<!--zh-->
### 4.1.2. Write a Command Parser<!--en-->
        }<!--zh-->
```java<!--en-->
    }<!--zh-->
public class YourAppendParser implements CommandParser<YourAppendCommand> {<!--en-->
```<!--zh-->
    @Override<!--en-->
<!--zh-->
    public YourAppendCommand parse(Object[] command) {<!--en-->
### 4.1.2. 然后写一个command parser  <!--zh-->
        return new YourAppendCommand(new String((byte[]) command[1], UTF_8), new String((byte[]) command[2], UTF_8));<!--en-->
```java<!--zh-->
    }<!--en-->
    public class YourAppendParser implements CommandParser<YourAppendCommand> {<!--zh-->
}<!--en-->
<!--zh-->
```<!--en-->
        @Override<!--zh-->
<!--en-->
        public YourAppendCommand parse(Object[] command) {<!--zh-->
### 4.1.3. Register the Parser<!--en-->
            return new YourAppendCommand(new String((byte[]) command[1], UTF_8), new String((byte[]) command[2], UTF_8));<!--zh-->
```java<!--en-->
        }<!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
    }<!--zh-->
replicator.addCommandParser(CommandName.name("APPEND"), new YourAppendParser());<!--en-->
<!--zh-->
```
<!--en-->
  <!--zh-->
### 4.1.4. Handle Command Event<!--en-->
### 4.1.3. 注册这个command parser到replicator  <!--zh-->
```java<!--en-->
```java  <!--zh-->
replicator.addEventListener(new EventListener() {<!--en-->
    Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
    @Override<!--en-->
    replicator.addCommandParser(CommandName.name("APPEND"),new YourAppendParser());<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
```<!--zh-->
        if(event instanceof YourAppendCommand){<!--en-->
  <!--zh-->
            YourAppendCommand appendCommand = (YourAppendCommand)event;<!--en-->
### 4.1.4. 处理这个注册的command事件  <!--zh-->
            // Your code goes here<!--en-->
```java  <!--zh-->
        }<!--en-->
    replicator.addEventListener(new EventListener() {<!--zh-->
    }<!--en-->
        @Override<!--zh-->
});<!--en-->
        public void onEvent(Replicator replicator, Event event) {<!--zh-->
```<!--en-->
            if(event instanceof YourAppendCommand){<!--zh-->
<!--en-->
                YourAppendCommand appendCommand = (YourAppendCommand)event;<!--zh-->
### 4.1.5. Putting It All Together<!--en-->
                // your code goes here<!--zh-->
<!--en-->
            }<!--zh-->
See [CommandExtensionExample.java](./examples/com/moilioncircle/examples/extension/CommandExtensionExample.java)<!--en-->
        }<!--zh-->
<!--en-->
    });<!--zh-->
## 4.2. Module Extension<!--en-->
```  <!--zh-->
### 4.2.1. Compile Redis Test Modules<!--en-->
<!--zh-->
```bash<!--en-->
### 4.1.5. 结合到一起  <!--zh-->
cd /path/to/redis-4.0-rc2/src/modules<!--en-->
<!--zh-->
make<!--en-->
参阅 [CommandExtensionExample.java](./examples/com/moilioncircle/examples/extension/CommandExtensionExample.java)  <!--zh-->
```<!--en-->
<!--zh-->
### 4.2.2. Uncomment in redis.conf<!--en-->
## 4.2. Module扩展(redis-4.0及以上)  <!--zh-->
<!--en-->
### 4.2.1. 编译redis源码中的测试modules  <!--zh-->
```<!--en-->
```java  <!--zh-->
loadmodule /path/to/redis-4.0-rc2/src/modules/hellotype.so<!--en-->
    $cd /path/to/redis-4.0-rc2/src/modules<!--zh-->
```<!--en-->
    $make<!--zh-->
### 4.2.3. Write a Module Parser<!--en-->
```<!--zh-->
```java<!--en-->
### 4.2.2. 打开redis配置文件redis.conf中相关注释  <!--zh-->
public class HelloTypeModuleParser implements ModuleParser<HelloTypeModule> {<!--en-->
<!--zh-->
    @Override<!--en-->
```java  <!--zh-->
    public HelloTypeModule parse(RedisInputStream in, int version) throws IOException {<!--en-->
    loadmodule /path/to/redis-4.0-rc2/src/modules/hellotype.so<!--zh-->
        DefaultRdbModuleParser parser = new DefaultRdbModuleParser(in);<!--en-->
```<!--zh-->
        int elements = parser.loadUnsigned(version).intValue();<!--en-->
### 4.2.3. 写一个module parser  <!--zh-->
        long[] ary = new long[elements];<!--en-->
```java  <!--zh-->
        int i = 0;<!--en-->
    public class HelloTypeModuleParser implements ModuleParser<HelloTypeModule> {<!--zh-->
        while (elements-- > 0) {<!--en-->
<!--zh-->
            ary[i++] = parser.loadSigned(version);<!--en-->
        @Override<!--zh-->
        }<!--en-->
        public HelloTypeModule parse(RedisInputStream in, int version) throws IOException {<!--zh-->
        return new HelloTypeModule(ary);<!--en-->
            DefaultRdbModuleParser parser = new DefaultRdbModuleParser(in);<!--zh-->
    }<!--en-->
            int elements = parser.loadUnsigned(version).intValue();<!--zh-->
}<!--en-->
            long[] ary = new long[elements];<!--zh-->
<!--en-->
            int i = 0;<!--zh-->
public class HelloTypeModule implements Module {<!--en-->
            while (elements-- > 0) {<!--zh-->
    private final long[] value;<!--en-->
                ary[i++] = parser.loadSigned(version);<!--zh-->
<!--en-->
            }<!--zh-->
    public HelloTypeModule(long[] value) {<!--en-->
            return new HelloTypeModule(ary);<!--zh-->
        this.value = value;<!--en-->
        }<!--zh-->
    }

    public long[] getValue() {<!--en-->
    public class HelloTypeModule implements Module {<!--zh-->
        return value;<!--en-->
        private final long[] value;<!--zh-->
    }<!--en-->
<!--zh-->
}<!--en-->
        public HelloTypeModule(long[] value) {<!--zh-->
```<!--en-->
            this.value = value;<!--zh-->
### 4.2.4. Write a Command Parser<!--en-->
        }<!--zh-->
```java<!--en-->
<!--zh-->
public class HelloTypeParser implements CommandParser<HelloTypeCommand> {<!--en-->
        public long[] getValue() {<!--zh-->
    @Override<!--en-->
            return value;<!--zh-->
    public HelloTypeCommand parse(Object[] command) {<!--en-->
        }<!--zh-->
        String key = new String((byte[]) command[1], Constants.UTF_8);<!--en-->
    }<!--zh-->
        long value = Long.parseLong(new String((byte[]) command[2], Constants.UTF_8));<!--en-->
```<!--zh-->
        return new HelloTypeCommand(key, value);<!--en-->
### 4.2.4. 再写一个command parser  <!--zh-->
    }<!--en-->
```java  <!--zh-->
}<!--en-->
    public class HelloTypeParser implements CommandParser<HelloTypeCommand> {<!--zh-->
<!--en-->
        @Override<!--zh-->
@CommandSpec(command = "hellotype.insert")<!--en-->
        public HelloTypeCommand parse(Object[] command) {<!--zh-->
public class HelloTypeCommand extends AbstractCommand {<!--en-->
            String key = new String((byte[])command[1],Constants.UTF_8);<!--zh-->
    private final String key;<!--en-->
            long value = Long.parseLong(new String((byte[])command[2],Constants.UTF_8));<!--zh-->
    private final long value;<!--en-->
            return new HelloTypeCommand(key, value);<!--zh-->
<!--en-->
        }<!--zh-->
    public long getValue() {<!--en-->
    }<!--zh-->
        return value;<!--en-->
    <!--zh-->
    }<!--en-->
    @CommandSpec(command = "hellotype.insert")<!--zh-->
<!--en-->
    public class HelloTypeCommand extends AbstractCommand {<!--zh-->
    public String getKey() {<!--en-->
        private final String key;<!--zh-->
        return key;<!--en-->
        private final long value;<!--zh-->
    }<!--en-->
<!--zh-->
<!--en-->
        public long getValue() {<!--zh-->
    public HelloTypeCommand(String key, long value) {<!--en-->
            return value;<!--zh-->
        this.key = key;<!--en-->
        }<!--zh-->
        this.value = value;<!--en-->
<!--zh-->
    }<!--en-->
        public String getKey() {<!--zh-->
}<!--en-->
            return key;<!--zh-->
```<!--en-->
        }<!--zh-->
### 4.2.5. Register Parsers and Handle Events<!--en-->
<!--zh-->
<!--en-->
        public HelloTypeCommand(String key, long value) {<!--zh-->
```java<!--en-->
            this.key = key;<!--zh-->
public static void main(String[] args) throws IOException {<!--en-->
            this.value = value;<!--zh-->
    Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
        }<!--zh-->
    replicator.addCommandParser(CommandName.name("hellotype.insert"), new HelloTypeParser());<!--en-->
    }<!--zh-->
    replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());<!--en-->
```<!--zh-->
    replicator.addEventListener(new EventListener() {<!--en-->
### 4.2.5. 注册module parser和command parser并处理相关事件  <!--zh-->
        @Override<!--en-->
<!--zh-->
        public void onEvent(Replicator replicator, Event event) {<!--en-->
```java  <!--zh-->
            if (event instanceof KeyStringValueModule) {<!--en-->
    public static void main(String[] args) throws IOException {<!--zh-->
                System.out.println(event);<!--en-->
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
            }<!--en-->
        replicator.addCommandParser(CommandName.name("hellotype.insert"), new HelloTypeParser());<!--zh-->
            <!--en-->
        replicator.addModuleParser("hellotype", 0, new HelloTypeModuleParser());<!--zh-->
            if (event instanceof HelloTypeCommand) {<!--en-->
        replicator.addEventListener(new EventListener() {<!--zh-->
                System.out.println(event);<!--en-->
            @Override<!--zh-->
            }<!--en-->
            public void onEvent(Replicator replicator, Event event) {<!--zh-->
        }<!--en-->
                if (event instanceof KeyStringValueModule) {<!--zh-->
    });<!--en-->
                    System.out.println(event);<!--zh-->
    replicator.open();<!--en-->
                }<!--zh-->
}<!--en-->
                <!--zh-->
```<!--en-->
                if (event instanceof HelloTypeCommand) {<!--zh-->
<!--en-->
                    System.out.println(event);<!--zh-->
### 4.2.6. Putting It All Together<!--en-->
                }<!--zh-->
<!--en-->
            }<!--zh-->
See [ModuleExtensionExample.java](./examples/com/moilioncircle/examples/extension/ModuleExtensionExample.java)<!--en-->
        });<!--zh-->
<!--en-->
        replicator.open();<!--zh-->
## 4.3. Stream<!--en-->
    }<!--zh-->
<!--en-->
```<!--zh-->
Since Redis 5.0, a new data structure called `STREAM` has been added. Redis-replicator parses `STREAM` data as follows:<!--en-->
<!--zh-->
<!--en-->
### 4.2.6. 结合到一起  <!--zh-->
```java<!--en-->
<!--zh-->
Replicator r = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
参阅 [ModuleExtensionExample.java](./examples/com/moilioncircle/examples/extension/ModuleExtensionExample.java)  <!--zh-->
r.addEventListener(new EventListener() {<!--en-->
<!--zh-->
    @Override<!--en-->
## 4.3. Stream<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
  <!--zh-->
        if (event instanceof KeyStringValueStream) {<!--en-->
Redis-5.0+ 增加了一个新的数据结构 `STREAM`. Redis-replicator 用下述代码解析 `STREAM`  <!--zh-->
            KeyStringValueStream kv = (KeyStringValueStream)event;<!--en-->
  <!--zh-->
            // Key<!--en-->
  <!--zh-->
            String key = kv.getKey();<!--en-->
```java  <!--zh-->
            <!--en-->
<!--zh-->
            // Stream<!--en-->
        Replicator r = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
            Stream stream = kv.getValueAsStream();<!--en-->
        r.addEventListener(new EventListener() {<!--zh-->
            // Last stream ID<!--en-->
            @Override<!--zh-->
            stream.getLastId();<!--en-->
            public void onEvent(Replicator replicator, Event event) {<!--zh-->
            <!--en-->
                if (event instanceof KeyStringValueStream) {<!--zh-->
            // Entries<!--en-->
                    KeyStringValueStream kv = (KeyStringValueStream)event;<!--zh-->
            NavigableMap<Stream.ID, Stream.Entry> entries = stream.getEntries();<!--en-->
                    // key<!--zh-->
            <!--en-->
                    String key = kv.getKey();<!--zh-->
            // Optional: Groups<!--en-->
                    <!--zh-->
            for (Stream.Group group : stream.getGroups()) {<!--en-->
                    // stream<!--zh-->
                // Group PEL (Pending Entries List)<!--en-->
                    Stream stream = kv.getValueAsStream();<!--zh-->
                NavigableMap<Stream.ID, Stream.Nack> gpel = group.getPendingEntries();<!--en-->
                    // last stream id<!--zh-->
                <!--en-->
                    stream.getLastId();<!--zh-->
                // Consumers<!--en-->
                    <!--zh-->
                for (Stream.Consumer consumer : group.getConsumers()) {<!--en-->
                    // entries<!--zh-->
                    // Consumer PEL (Pending Entries List)<!--en-->
                    NavigableMap<Stream.ID, Stream.Entry> entries = stream.getEntries();<!--zh-->
                    NavigableMap<Stream.ID, Stream.Nack> cpel = consumer.getPendingEntries();<!--en-->
                    <!--zh-->
                }<!--en-->
                    // optional : group<!--zh-->
            }<!--en-->
                    for (Stream.Group group : stream.getGroups()) {<!--zh-->
        }<!--en-->
                        // group PEL(pending entries list)<!--zh-->
    }<!--en-->
                        NavigableMap<Stream.ID, Stream.Nack> gpel = group.getPendingEntries();<!--zh-->
});<!--en-->
                        <!--zh-->
r.open();<!--en-->
                        // consumer<!--zh-->
```<!--en-->
                        for (Stream.Consumer consumer : group.getConsumers()) {<!--zh-->
<!--en-->
                            // consumer PEL(pending entries list)<!--zh-->
## 4.4. Write Your Own RDB Parser<!--en-->
                            NavigableMap<Stream.ID, Stream.Nack> cpel = consumer.getPendingEntries();<!--zh-->
<!--en-->
                        }<!--zh-->
*   Write a `YourRdbVisitor` that extends `RdbVisitor`.<!--en-->
                    }<!--zh-->
*   Register your `RdbVisitor` with the `Replicator` using the `setRdbVisitor` method.<!--en-->
                }<!--zh-->
<!--en-->
            }<!--zh-->
## 4.5. Redis URI<!--en-->
        });<!--zh-->
<!--en-->
        r.open();<!--zh-->
Before version 2.4.0, `RedisReplicator` was constructed as follows:<!--en-->
<!--zh-->
<!--en-->
```<!--zh-->
```java<!--en-->
<!--zh-->
Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());<!--en-->
## 4.4. 编写你自己的rdb解析器  <!--zh-->
Replicator replicator = new RedisReplicator(new File("/path/to/dump.rdb"), FileType.RDB, Configuration.defaultSetting());<!--en-->
<!--zh-->
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof"), FileType.AOF, Configuration.defaultSetting());<!--en-->
* 写一个类继承 `RdbVisitor` 抽象类  <!--zh-->
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof"), FileType.MIXED, Configuration.defaultSetting());<!--en-->
* 通过 `Replicator` 的 `setRdbVisitor` 方法注册你自己的 `RdbVisitor`.  <!--zh-->
```<!--en-->
  <!--zh-->
<!--en-->
## 4.5. Redis URI<!--zh-->
Since version 2.4.0, we have introduced the Redis URI concept to simplify the `RedisReplicator` construction process:<!--en-->
<!--zh-->
<!--en-->
在 redis-replicator-2.4.0 版之前, 我们按如下方式构造 `RedisReplicator` :  <!--zh-->
```java<!--en-->
<!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
```java  <!--zh-->
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb");<!--en-->
Replicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());<!--zh-->
Replicator replicator = new RedisReplicator("redis:///path/to/appendonly.aof");<!--en-->
Replicator replicator = new RedisReplicator(new File("/path/to/dump.rdb", FileType.RDB, Configuration.defaultSetting());<!--zh-->
<!--en-->
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.AOF, Configuration.defaultSetting());<!--zh-->
// Configuration setting example<!--en-->
Replicator replicator = new RedisReplicator(new File("/path/to/appendonly.aof", FileType.MIXED, Configuration.defaultSetting());<!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379?authPassword=foobared&readTimeout=10000&ssl=yes");<!--en-->
```<!--zh-->
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb?rateLimit=1000000");<!--en-->
<!--zh-->
Replicator replicator = new RedisReplicator("rediss://user:pass@127.0.0.1:6379?rateLimit=1000000");<!--en-->
在 redis-replicator-2.4.0 版之后, 我们引入了一个新的概念(Redis URI) 来简化 `RedisReplicator` 的构造, 以便提供一致的API.  <!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
```java  <!--zh-->
# 5. Other Topics<!--en-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
<!--en-->
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb");<!--zh-->
## 5.1. Built-in Command Parsers<!--en-->
Replicator replicator = new RedisReplicator("redis:///path/to/appendonly.aof");<!--zh-->

| **Command**  | **Command**    | **Command**        | **Command**  | **Command**   | **Command**          |<!--en-->
// 配置的例子<!--zh-->
|--------------|----------------|--------------------|--------------|---------------|----------------------|<!--en-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379?authPassword=foobared&readTimeout=10000&ssl=yes");<!--zh-->
| **PING**     | **APPEND**     | **SET**            | **SETEX**    | **MSET**      | **DEL**              |<!--en-->
Replicator replicator = new RedisReplicator("redis:///path/to/dump.rdb?rateLimit=1000000");<!--zh-->
| **SADD**     | **HMSET**      | **HSET**           | **LSET**     | **EXPIRE**    | **EXPIREAT**         |<!--en-->
Replicator replicator = new RedisReplicator("rediss://user:pass@127.0.0.1:6379?rateLimit=1000000");<!--zh-->
| **GETSET**   | **HSETNX**     | **MSETNX**         | **PSETEX**   | **SETNX**     | **SETRANGE**         |<!--en-->
```<!--zh-->
| **HDEL**     | **UNLINK**     | **SREM**           | **LPOP**     | **LPUSH**     | **LPUSHX**           |<!--en-->
<!--zh-->
| **LREM**     | **RPOP**       | **RPUSH**          | **RPUSHX**   | **ZREM**      | **ZINTERSTORE**      |<!--en-->
<!--zh-->
| **INCR**     | **DECR**       | **INCRBY**         | **PERSIST**  | **SELECT**    | **FLUSHALL**         |<!--en-->
# 5. 其他主题  <!--zh-->
| **FLUSHDB**  | **HINCRBY**    | **ZINCRBY**        | **MOVE**     | **SMOVE**     | **BRPOPLPUSH**       |<!--en-->
  <!--zh-->
| **PFCOUNT**  | **PFMERGE**    | **SDIFFSTORE**     | **RENAMENX** | **PEXPIREAT** | **SINTERSTORE**      |<!--en-->
## 5.1. 内置的Command Parser  <!--zh-->
| **ZADD**     | **BITFIELD**   | **SUNIONSTORE**    | **RESTORE**  | **LINSERT**   | **ZREMRANGEBYLEX**   |<!--en-->
<!--zh-->
| **GEOADD**   | **PEXPIRE**    | **ZUNIONSTORE**    | **EVAL**     | **SCRIPT**    | **ZREMRANGEBYRANK**  |<!--en-->
| **命令**       |**命令**        | **命令**             |**命令**       |**命令**      | **命令**            |  <!--zh-->
| **PUBLISH**  | **BITOP**      | **SETBIT**         | **SWAPDB**   | **PFADD**     | **ZREMRANGEBYSCORE** |<!--en-->
|--------------| -------------- |--------------------| ------------ | ------------ | ------------------ |  <!--zh-->
| **RENAME**   | **MULTI**      | **EXEC**           | **LTRIM**    | **RPOPLPUSH** | **SORT**             |<!--en-->
| **PING**     |  **APPEND**    | **SET**            |  **SETEX**   |  **MSET**    |  **DEL**           |  <!--zh-->
| **EVALSHA**  | **ZPOPMAX**    | **ZPOPMIN**        | **XACK**     | **XADD**      | **XCLAIM**           |<!--en-->
| **SADD**     |  **HMSET**     | **HSET**           |  **LSET**    |  **EXPIRE**  |  **EXPIREAT**      |  <!--zh-->
| **XDEL**     | **XGROUP**     | **XTRIM**          | **XSETID**   | **COPY**      | **LMOVE**            |<!--en-->
| **GETSET**   | **HSETNX**     | **MSETNX**         | **PSETEX**   | **SETNX**    |  **SETRANGE**      |  <!--zh-->
| **BLMOVE**   | **ZDIFFSTORE** | **GEOSEARCHSTORE** | **FUNCTION** | **SPUBLISH**  | **HPERSIST**         |<!--en-->
| **HDEL**     | **UNLINK**     | **SREM**           | **LPOP**     |  **LPUSH**   | **LPUSHX**         |  <!--zh-->
| **HSETEX**   | **HPEXPIREAT** | **XACKDEL**        | **XDELEX**   | **MSETEX**    |                      |<!--en-->
| **LRem**     | **RPOP**       | **RPUSH**          | **RPUSHX**   |  **ZREM**    |  **ZINTERSTORE**   |  <!--zh-->
<!--en-->
| **INCR**     |  **DECR**      | **INCRBY**         |**PERSIST**   |  **SELECT**  | **FLUSHALL**       |  <!--zh-->
## 5.2. EOFException<!--en-->
| **FLUSHDB**  |  **HINCRBY**   | **ZINCRBY**        | **MOVE**     |  **SMOVE**   |**BRPOPLPUSH**      |  <!--zh-->
<!--en-->
| **PFCOUNT**  |  **PFMERGE**   | **SDIFFSTORE**     |**RENAMENX**  | **PEXPIREAT**|**SINTERSTORE**     |  <!--zh-->
When event consumption is too slow and the backlog of events exceeds the Redis backlog limit, Redis will actively disconnect from the slave. When Redis-replicator reconnects, it will perform a full synchronization. To avoid this situation, you need to set the parameter `client-output-buffer-limit slave 0 0 0`.<!--en-->
| **ZADD**     | **BITFIELD**   | **SUNIONSTORE**    |**RESTORE**   | **LINSERT**  |**ZREMRANGEBYLEX**  |  <!--zh-->
<!--en-->
| **GEOADD**   | **PEXPIRE**    | **ZUNIONSTORE**    |**EVAL**      |  **SCRIPT**  |**ZREMRANGEBYRANK** |  <!--zh-->
For more details, please refer to [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf).<!--en-->
| **PUBLISH**  |  **BITOP**     | **SETBIT**         | **SWAPDB**   | **PFADD**    |**ZREMRANGEBYSCORE**|  <!--zh-->
<!--en-->
| **RENAME**   |  **MULTI**     | **EXEC**           | **LTRIM**    |**RPOPLPUSH** |     **SORT**       |  <!--zh-->
```<!--en-->
| **EVALSHA**  | **ZPOPMAX**    | **ZPOPMIN**        | **XACK**     | **XADD**     |  **XCLAIM**        |  <!--zh-->
client-output-buffer-limit slave 0 0 0<!--en-->
| **XDEL**     | **XGROUP**     | **XTRIM**          | **XSETID**   | **COPY**     |  **LMOVE**         |  <!--zh-->
```<!--en-->
| **BLMOVE**   | **ZDIFFSTORE** | **GEOSEARCHSTORE** | **FUNCTION** | **SPUBLISH** | **HPERSIST**       |  <!--zh-->
**WARNING: This setting may cause the Redis server to run out of memory in some cases.**<!--en-->
| **HSETEX**   | **HPEXPIREAT** | **XACKDEL**        | **XDELEX**   | **MSETEX**   |                    |  <!--zh-->
<!--en-->
  <!--zh-->
## 5.3. Trace Event Log<!--en-->
## 5.2. 当出现EOFException<!--zh-->

*   Set the log level to **debug**.<!--en-->
当消费事件过慢积压事件超过redis backlog限制时，redis会主动断开与slave的连接，Redis-replicator再重连时会走全量同步，如果想避免这一情况，需要设置参数`client-output-buffer-limit slave 0 0 0`<!--zh-->
*   If you are using Log4j2, add a logger as shown below:<!--en-->
  <!--zh-->
<!--en-->
相关配置请参考 [redis.conf](https://raw.githubusercontent.com/antirez/redis/3.0/redis.conf)  <!--zh-->
```xml<!--en-->
  <!--zh-->
<Logger name="com.moilioncircle" level="debug"><!--en-->
```java  <!--zh-->
    <AppenderRef ref="YourAppender"/><!--en-->
client-output-buffer-limit slave 0 0 0<!--zh-->
</Logger><!--en-->
```  <!--zh-->
```<!--en-->
**警告: 这个配置可能会使redis-server中的内存溢出**  <!--zh-->
<!--en-->
  <!--zh-->
```java<!--en-->
## 5.3. 跟踪事件日志log  <!--zh-->
Configuration.defaultSetting().setVerbose(true);<!--en-->
  <!--zh-->
// As a Redis URI parameter<!--en-->
* 日志级别调整成 **debug**<!--zh-->
"redis://127.0.0.1:6379?verbose=yes"<!--en-->
* 如果你项目中使用log4j2,请加入如下Logger到配置文件:<!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
```xml  <!--zh-->
## 5.4. SSL Connection<!--en-->
    <Logger name="com.moilioncircle" level="debug"><!--zh-->
<!--en-->
        <AppenderRef ref="YourAppender"/><!--zh-->
```java<!--en-->
    </Logger><!--zh-->
System.setProperty("javax.net.ssl.keyStore", "/path/to/keystore");<!--en-->
```<!--zh-->
System.setProperty("javax.net.ssl.keyStorePassword", "password");<!--en-->
  <!--zh-->
System.setProperty("javax.net.ssl.keyStoreType", "your_type");<!--en-->
```java  <!--zh-->
<!--en-->
    Configuration.defaultSetting().setVerbose(true);<!--zh-->
System.setProperty("javax.net.ssl.trustStore", "/path/to/truststore");<!--en-->
    // redis uri<!--zh-->
System.setProperty("javax.net.ssl.trustStorePassword", "password");<!--en-->
    "redis://127.0.0.1?verbose=yes"<!--zh-->
System.setProperty("javax.net.ssl.trustStoreType", "your_type");<!--en-->
```<!--zh-->
<!--en-->
  <!--zh-->
Configuration.defaultSetting().setSsl(true);<!--en-->
## 5.4. SSL安全链接  <!--zh-->
<!--en-->
  <!--zh-->
// Optional settings<!--en-->
```java  <!--zh-->
Configuration.defaultSetting().setSslSocketFactory(sslSocketFactory);<!--en-->
    System.setProperty("javax.net.ssl.keyStore", "/path/to/keystore");<!--zh-->
Configuration.defaultSetting().setSslParameters(sslParameters);<!--en-->
    System.setProperty("javax.net.ssl.keyStorePassword", "password");<!--zh-->
Configuration.defaultSetting().setHostnameVerifier(hostnameVerifier);<!--en-->
    System.setProperty("javax.net.ssl.keyStoreType", "your_type");<!--zh-->

// As a Redis URI parameter<!--en-->
    System.setProperty("javax.net.ssl.trustStore", "/path/to/truststore");<!--zh-->
"redis://127.0.0.1:6379?ssl=yes"<!--en-->
    System.setProperty("javax.net.ssl.trustStorePassword", "password");<!--zh-->
"rediss://127.0.0.1:6379"<!--en-->
    System.setProperty("javax.net.ssl.trustStoreType", "your_type");<!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
    Configuration.defaultSetting().setSsl(true);<!--zh-->
If you prefer not to use `System.setProperty`, you can configure it programmatically as follows:<!--en-->
<!--zh-->
<!--en-->
    // 可选设置<!--zh-->
```java<!--en-->
    Configuration.defaultSetting().setSslSocketFactory(sslSocketFactory);<!--zh-->
RedisSslContextFactory factory = new RedisSslContextFactory();<!--en-->
    Configuration.defaultSetting().setSslParameters(sslParameters);<!--zh-->
factory.setKeyStorePath("/path/to/redis/tests/tls/redis.p12");<!--en-->
    Configuration.defaultSetting().setHostnameVerifier(hostnameVerifier);<!--zh-->
factory.setKeyStoreType("pkcs12");<!--en-->
    // redis uri<!--zh-->
factory.setKeyStorePassword("password");<!--en-->
    "redis://127.0.0.1:6379?ssl=yes"<!--zh-->
<!--en-->
    "rediss://127.0.0.1:6379"<!--zh-->
factory.setTrustStorePath("/path/to/redis/tests/tls/redis.p12");<!--en-->
```<!--zh-->
factory.setTrustStoreType("pkcs12");<!--en-->
  <!--zh-->
factory.setTrustStorePassword("password");<!--en-->
## 5.5. redis认证  <!--zh-->
<!--en-->
  <!--zh-->
SslConfiguration ssl = SslConfiguration.defaultSetting().setSslContextFactory(factory);<!--en-->
```java  <!--zh-->
Replicator replicator = new RedisReplicator("rediss://127.0.0.1:6379", ssl);<!--en-->
    Configuration.defaultSetting().setAuthUser("default");<!--zh-->
```<!--en-->
    Configuration.defaultSetting().setAuthPassword("foobared");<!--zh-->
<!--en-->
    // redis uri<!--zh-->
## 5.5. Authentication<!--en-->
    "redis://127.0.0.1:6379?authPassword=foobared&authUser=default"<!--zh-->
<!--en-->
    "redis://default:foobared@127.0.0.1:6379"<!--zh-->
```java<!--en-->
```  <!--zh-->
Configuration.defaultSetting().setAuthUser("default");<!--en-->
<!--zh-->
Configuration.defaultSetting().setAuthPassword("foobared");<!--en-->
## 5.6. 避免全量同步  <!--zh-->
<!--en-->
  <!--zh-->
// As a Redis URI parameter<!--en-->
* 调整redis-server中的如下配置  <!--zh-->
"redis://127.0.0.1:6379?authPassword=foobared&authUser=default"<!--en-->
  <!--zh-->
"redis://default:foobared@127.0.0.1:6379"<!--en-->
```java  <!--zh-->
```<!--en-->
    repl-backlog-size<!--zh-->
<!--en-->
    repl-backlog-ttl<!--zh-->
## 5.6. Avoid Full Sync<!--en-->
    repl-ping-slave-period<!--zh-->
<!--en-->
```<!--zh-->
Adjust the Redis server settings as follows:<!--en-->
`repl-ping-slave-period` **必须** 小于 `Configuration.getReadTimeout()`, 默认的 `Configuration.getReadTimeout()` 是60秒.<!--zh-->
<!--en-->
  <!--zh-->
```<!--en-->
## 5.7. 生命周期事件  <!--zh-->
repl-backlog-size<!--en-->
  <!--zh-->
repl-backlog-ttl<!--en-->
```java  <!--zh-->
repl-ping-slave-period<!--en-->
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
```<!--en-->
        final long start = System.currentTimeMillis();<!--zh-->
The `repl-ping-slave-period` **MUST** be less than `Configuration.getReadTimeout()`. The default `Configuration.getReadTimeout()` is 60 seconds.<!--en-->
        final AtomicInteger acc = new AtomicInteger(0);<!--zh-->
<!--en-->
        replicator.addEventListener(new EventListener() {<!--zh-->
## 5.7. Lifecycle Events<!--en-->
            @Override<!--zh-->
<!--en-->
            public void onEvent(Replicator replicator, Event event) {<!--zh-->
```java<!--en-->
                if(event instanceof PreRdbSyncEvent) {<!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
                    System.out.println("pre rdb sync");<!--zh-->
final long start = System.currentTimeMillis();<!--en-->
                } else if(event instanceof PostRdbSyncEvent) {<!--zh-->
final AtomicInteger acc = new AtomicInteger(0);<!--en-->
                    long end = System.currentTimeMillis();<!--zh-->
replicator.addEventListener(new EventListener() {<!--en-->
                    System.out.println("time elapsed:" + (end - start));<!--zh-->
    @Override<!--en-->
                    System.out.println("rdb event count:" + acc.get());<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
                } else {<!--zh-->
        if(event instanceof PreRdbSyncEvent) {<!--en-->
                    acc.incrementAndGet();<!--zh-->
            System.out.println("pre rdb sync");<!--en-->
                }<!--zh-->
        } else if(event instanceof PostRdbSyncEvent) {<!--en-->
            }<!--zh-->
            long end = System.currentTimeMillis();<!--en-->
        });<!--zh-->
            System.out.println("time elapsed:" + (end - start));<!--en-->
        replicator.open();<!--zh-->
            System.out.println("rdb event count:" + acc.get());<!--en-->
```  <!--zh-->
        } else {<!--en-->
  <!--zh-->
            acc.incrementAndGet();<!--en-->
## 5.8. 处理巨大的KV  <!--zh-->
        }<!--en-->
<!--zh-->
    }<!--en-->
根据 [4.3. 编写你自己的rdb解析器](#43-编写你自己的rdb解析器), 这个工具内嵌了一个[迭代方式的rdb解析器](./src/main/java/com/moilioncircle/redis/replicator/rdb/iterable/ValueIterableRdbVisitor.java), 以便处理巨大的KV.  <!--zh-->
});<!--en-->
详细的例子参阅:  <!--zh-->
replicator.open();<!--en-->
[1] [HugeKVFileExample.java](./examples/com/moilioncircle/examples/huge/HugeKVFileExample.java)  <!--zh-->
```<!--en-->
[2] [HugeKVSocketExample.java](./examples/com/moilioncircle/examples/huge/HugeKVSocketExample.java)  <!--zh-->
<!--en-->
  <!--zh-->
## 5.8. Handle Huge Key-Value Pairs<!--en-->
## 5.9. Redis6支持<!--zh-->

As mentioned in [4.4. Write Your Own RDB Parser](#44-write-your-own-rdb-parser), this tool has a built-in [Iterable Rdb Parser](./src/main/java/com/moilioncircle/redis/replicator/rdb/iterable/ValueIterableRdbVisitor.java) to handle huge key-value pairs.<!--en-->
### 5.9.1. SSL支持<!--zh-->
For more details, please refer to:<!--en-->
<!--zh-->
[1] [HugeKVFileExample.java](./examples/com/moilioncircle/examples/huge/HugeKVFileExample.java)<!--en-->
```<!--zh-->
[2] [HugeKVSocketExample.java](./examples/com/moilioncircle/examples/huge/HugeKVSocketExample.java)<!--en-->
    $cd /path/to/redis<!--zh-->
<!--en-->
    $./utils/gen-test-certs.sh<!--zh-->
## 5.9. Redis 6 Support<!--en-->
    $cd tests/tls<!--zh-->
<!--en-->
    $openssl pkcs12 -export -CAfile ca.crt -in redis.crt -inkey redis.key -out redis.p12<!--zh-->
### 5.9.1. SSL Support<!--en-->
    $cd /path/to/redis<!--zh-->
<!--en-->
    $./src/redis-server --tls-port 6379 --port 0 --tls-cert-file ./tests/tls/redis.crt \<!--zh-->
```bash<!--en-->
         --tls-key-file ./tests/tls/redis.key --tls-ca-cert-file ./tests/tls/ca.crt \<!--zh-->
cd /path/to/redis<!--en-->
         --tls-replication yes --bind 0.0.0.0 --protected-mode no<!--zh-->
./utils/gen-test-certs.sh<!--en-->
<!--zh-->
cd tests/tls<!--en-->
    System.setProperty("javax.net.ssl.keyStore", "/path/to/redis/tests/tls/redis.p12");<!--zh-->
openssl pkcs12 -export -CAfile ca.crt -in redis.crt -inkey redis.key -out redis.p12<!--en-->
    System.setProperty("javax.net.ssl.keyStorePassword", "password");<!--zh-->
cd /path/to/redis<!--en-->
    System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");<!--zh-->
./src/redis-server --tls-port 6379 --port 0 --tls-cert-file ./tests/tls/redis.crt \<!--en-->
<!--zh-->
     --tls-key-file ./tests/tls/redis.key --tls-ca-cert-file ./tests/tls/ca.crt \<!--en-->
    System.setProperty("javax.net.ssl.trustStore", "/path/to/redis/tests/tls/redis.p12");<!--zh-->
     --tls-replication yes --bind 0.0.0.0 --protected-mode no<!--en-->
    System.setProperty("javax.net.ssl.trustStorePassword", "password");<!--zh-->
```<!--en-->
    System.setProperty("javax.net.ssl.trustStoreType", "pkcs12");<!--zh-->

```java<!--en-->
    Replicator replicator = new RedisReplicator("rediss://127.0.0.1:6379");<!--zh-->
System.setProperty("javax.net.ssl.keyStore", "/path/to/redis/tests/tls/redis.p12");<!--en-->
<!--zh-->
System.setProperty("javax.net.ssl.keyStorePassword", "password");<!--en-->
```<!--zh-->
System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");<!--en-->
  <!--zh-->
<!--en-->
如果你不想设置 `System.setProperty` 可以使用下面的方式  <!--zh-->
System.setProperty("javax.net.ssl.trustStore", "/path/to/redis/tests/tls/redis.p12");<!--en-->
  <!--zh-->
System.setProperty("javax.net.ssl.trustStorePassword", "password");<!--en-->
```java  <!--zh-->
System.setProperty("javax.net.ssl.trustStoreType", "pkcs12");<!--en-->
<!--zh-->
<!--en-->
    RedisSslContextFactory factory = new RedisSslContextFactory();<!--zh-->
Replicator replicator = new RedisReplicator("rediss://127.0.0.1:6379");<!--en-->
    factory.setKeyStorePath("/path/to/redis/tests/tls/redis.p12");<!--zh-->
```<!--en-->
    factory.setKeyStoreType("pkcs12");<!--zh-->
<!--en-->
    factory.setKeyStorePassword("password");<!--zh-->
### 5.9.2. ACL Support<!--en-->
<!--zh-->
<!--en-->
    factory.setTrustStorePath("/path/to/redis/tests/tls/redis.p12");<!--zh-->
```java<!--en-->
    factory.setTrustStoreType("pkcs12");<!--zh-->
Replicator replicator = new RedisReplicator("redis://user:pass@127.0.0.1:6379");<!--en-->
    factory.setTrustStorePassword("password");<!--zh-->
```<!--en-->
<!--zh-->
<!--en-->
    SslConfiguration ssl = SslConfiguration.defaultSetting().setSslContextFactory(factory);<!--zh-->
## 5.10. Redis 7 Support<!--en-->
    Replicator replicator = new RedisReplicator("rediss://127.0.0.1:6379", ssl);<!--zh-->

### 5.10.1. Function<!--en-->
``` <!--zh-->

Since Redis 7.0, `FUNCTION` is supported, and its structure is stored in the RDB file. You can use the following method to parse a `FUNCTION`.<!--en-->
### 5.9.2. ACL支持<!--zh-->

```java<!--en-->
```java  <!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
<!--zh-->
replicator.addEventListener(new EventListener() {<!--en-->
    Replicator replicator = new RedisReplicator("redis://user:pass@127.0.0.1:6379");<!--zh-->
    @Override<!--en-->
<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
```<!--zh-->
        if (event instanceof Function) {<!--en-->
<!--zh-->
            Function function = (Function) event;<!--en-->
## 5.10. Redis7支持<!--zh-->
            function.getCode();<!--en-->
<!--zh-->
                <!--en-->
### 5.10.1. Function<!--zh-->
            // Your code goes here<!--en-->
<!--zh-->
        }<!--en-->
Redis 7.0 添加了 `function` 的支持. `function` 的结构存储在rdb文件中. 因此我们能用如下方式解析`function`.<!--zh-->
    }<!--en-->
<!--zh-->
});<!--en-->
```java  <!--zh-->
replicator.open();<!--en-->
<!--zh-->
```<!--en-->
    Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
<!--en-->
    replicator.addEventListener(new EventListener() {<!--zh-->
You can also parse a `FUNCTION` into serialized data and use `FUNCTION RESTORE` to restore it to a target Redis instance.<!--en-->
        @Override<!--zh-->
<!--en-->
        public void onEvent(Replicator replicator, Event event) {<!--zh-->
```java<!--en-->
            if (event instanceof Function) {<!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
                Function function = (Function) event;<!--zh-->
replicator.setRdbVisitor(new DumpRdbVisitor(replicator));<!--en-->
                function.getCode();<!--zh-->
replicator.addEventListener(new EventListener() {<!--en-->
                    <!--zh-->
    @Override<!--en-->
                // your code goes here<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
            }<!--zh-->
        if (event instanceof DumpFunction) {<!--en-->
        }<!--zh-->
            DumpFunction function = (DumpFunction) event;<!--en-->
    });<!--zh-->
            byte[] serialized = function.getSerialized();<!--en-->
    replicator.open();<!--zh-->
            // Your code goes here<!--en-->
```<!--zh-->
            // You can use FUNCTION RESTORE to restore the serialized data to a target Redis instance<!--en-->
<!--zh-->
        }<!--en-->
也可以把 `function` 解析成 `serialized` 格式. 这样接下来我们可以用 `FUNCTION RESTORE` 命令把 `serialized` 数据迁移到目标redis<!--zh-->
    }<!--en-->
<!--zh-->
});<!--en-->
```java  <!--zh-->
replicator.open();<!--en-->
<!--zh-->
```<!--en-->
    Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
<!--en-->
    replicator.setRdbVisitor(new DumpRdbVisitor(replicator));<!--zh-->
## 5.11. Redis 7.4 Support<!--en-->
    replicator.addEventListener(new EventListener() {<!--zh-->
<!--en-->
        @Override<!--zh-->
### 5.11.1. TTL Hash<!--en-->
        public void onEvent(Replicator replicator, Event event) {<!--zh-->
<!--en-->
            if (event instanceof DumpFunction) {<!--zh-->
Since Redis 7.4, `TTL HASH` is supported, and its structure is stored in the RDB file. You can use the following method to parse a `TTL HASH`.<!--en-->
                DumpFunction function = (DumpFunction) event;<!--zh-->
<!--en-->
                byte[] serialized = function.getSerialized();<!--zh-->
```java<!--en-->
                // your code goes here<!--zh-->
Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--en-->
                // you can use FUNCTION RESTORE to restore above serialized data to target redis<!--zh-->
replicator.addEventListener(new EventListener() {<!--en-->
            }<!--zh-->
    @Override<!--en-->
        }<!--zh-->
    public void onEvent(Replicator replicator, Event event) {<!--en-->
    });<!--zh-->
        if (event instanceof KeyStringValueTTLHash) {<!--en-->
    replicator.open();<!--zh-->
            KeyStringValueTTLHash skv = (KeyStringValueTTLHash) event;<!--en-->
```<!--zh-->
            // Key<!--en-->
<!--zh-->
            byte[] key = skv.getKey();<!--en-->
## 5.11. Redis7.4支持<!--zh-->
            <!--en-->
<!--zh-->
            // TTL Hash<!--en-->
### 5.11.1. TTL Hash<!--zh-->
            Map<byte[], TTLValue> ttlHash = skv.getValue();<!--en-->
<!--zh-->
            for (Map.Entry<byte[], TTLValue> entry : ttlHash.entrySet()) {<!--en-->
Redis 7.4 添加了 `ttl hash` 的支持. `ttl hash` 的结构存储在rdb文件中. 因此我们能用如下方式解析`ttl hash`.<!--zh-->
                System.out.println("field: " + Strings.toString(entry.getKey()));<!--en-->
<!--zh-->
                System.out.println("value: " + Strings.toString(entry.getValue().getValue()));<!--en-->
```java  <!--zh-->
                System.out.println("field ttl: " + entry.getValue().getExpires());<!--en-->
<!--zh-->
            }<!--en-->
    Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");<!--zh-->
        }<!--en-->
    replicator.addEventListener(new EventListener() {<!--zh-->
    }<!--en-->
        @Override<!--zh-->
});<!--en-->
        public void onEvent(Replicator replicator, Event event) {<!--zh-->
replicator.open();<!--en-->
            if (event instanceof KeyStringValueTTLHash) {<!--zh-->
```<!--en-->
                KeyStringValueTTLHash skv = (KeyStringValueTTLHash) event;<!--zh-->
<!--en-->
                // key<!--zh-->
# 6. Contributors<!--en-->
                byte[] key = skv.getKey();<!--zh-->
* [Leon Chen](https://github.com/leonchen83)<!--en-->
                <!--zh-->
* [Adrian Yao](https://github.com/adrianyao89)<!--en-->
                // ttl hash<!--zh-->
* [Trydofor](https://github.com/trydofor)<!--en-->
                Map<byte[], TTLValue> ttlHash = skv.getValue();<!--zh-->
* [Argun](https://github.com/Argun)<!--en-->
                for (Map.Entry<byte[], TTLValue> entry : ttlHash.entrySet()) {<!--zh-->
* [Sean Pan](https://github.com/XinYang-Pan)<!--en-->
                    System.out.println("field:" + Strings.toString(entry.getKey()));<!--zh-->
* [René Kerner](https://github.com/rk3rn3r)<!--en-->
                    System.out.println("value:" + Strings.toString(entry.getValue().getValue()));<!--zh-->
* [Maplestoria](https://github.com/maplestoria)<!--en-->
                    System.out.println("field ttl:" + entry.getValue().getExpires());<!--zh-->
* Special thanks to [Kevin Zheng](https://github.com/KevinZheng001)<!--en-->
                }<!--zh-->
<!--en-->
            }<!--zh-->
# 7. Consulting<!--en-->
        }<!--zh-->
<!--en-->
    });<!--zh-->
Commercial support for `redis-replicator` is available. The following services are currently offered:<!--en-->
    replicator.open();<!--zh-->
*   Onsite consulting: $10,000 per day<!--en-->
```<!--zh-->
*   Onsite training: $10,000 per day<!--en-->
<!--zh-->
<!--en-->
# 6. 贡献者  <!--zh-->
You may also contact Baoyi Chen directly at [chen.bao.yi@gmail.com](mailto:chen.bao.yi@gmail.com).<!--en-->
<!--zh-->
<!--en-->
* [Leon Chen](https://github.com/leonchen83)  <!--zh-->
# 8. References<!--en-->
* [Adrian Yao](https://github.com/adrianyao89)  <!--zh-->
* [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)<!--en-->
* [Trydofor](https://github.com/trydofor)  <!--zh-->
* [Redis RDB File Format](https://github.com/leonchen83/redis-replicator/wiki/RDB-dump-data-format)<!--en-->
* [Argun](https://github.com/Argun)  <!--zh-->
* [Redis Protocol specification](http://redis.io/topics/protocol)<!--en-->
* [Sean Pan](https://github.com/XinYang-Pan)  <!--zh-->
* [Redis Replication](http://redis.io/topics/replication)<!--en-->
* [René Kerner](https://github.com/rk3rn3r)  <!--zh-->
* [Redis-replicator Design and Implementation](https://github.com/leonchen83/mycode/blob/master/redis/redis-share/Redis-replicator%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%AE%9E%E7%8E%B0.md)<!--en-->
* [Maplestoria](https://github.com/maplestoria)  <!--zh-->
<!--en-->
* 特别感谢 [Kevin Zheng](https://github.com/KevinZheng001)  <!--zh-->
# 9. Supported By<!--en-->
  <!--zh-->
<!--en-->
# 7. 商业咨询  <!--zh-->
## 9.1. 宁文君<!--en-->
<!--zh-->
<!--en-->
`redis-replicator` 支持如下的商业咨询服务:<!--zh-->
January 27, 2023, was a sad day as I lost my mother, 宁文君. She was always encouraging and supportive of my work on this tool. Every time a company started using it, she would get as excited as a child and motivate me to continue. Without her, I could not have maintained this tool for so many years. Even though I haven't achieved much, she was always proud of me. R.I.P, and may God bless her.<!--en-->
* 现场咨询. 50,000元/天<!--zh-->
<!--en-->
* 现场培训. 50,000元/天<!--zh-->
## 9.2. YourKit<!--en-->
<!--zh-->
<!--en-->
可以直接联系`陈宝仪`, 发送邮件至 [chen.bao.yi@gmail.com](mailto:chen.bao.yi@qq.com).<!--zh-->
![YourKit](https://www.yourkit.com/images/yklogo.png)<!--en-->
  <!--zh-->
YourKit is kindly supporting this open source project with its full-featured Java Profiler.<!--en-->
# 8. 相关引用  <!--zh-->
YourKit, LLC is the creator of innovative and intelligent tools for profiling<!--en-->
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  <!--zh-->
Java and .NET applications. Take a look at YourKit's leading software products:<!--en-->
  * [Redis RDB文件格式](https://github.com/leonchen83/redis-replicator/wiki/RDB-dump-data-format)  <!--zh-->
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and<!--en-->
  * [Redis 协议指南](http://redis.io/topics/protocol)<!--zh-->
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.<!--en-->
  * [Redis 同步协议](http://redis.io/topics/replication)<!--zh-->
<!--en-->
  * [Redis-replicator 设计与实现](https://github.com/leonchen83/mycode/blob/master/redis/redis-share/Redis-replicator%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%AE%9E%E7%8E%B0.md)<!--zh-->
## 9.3. IntelliJ IDEA<!--en-->
<!--zh-->
<!--en-->
# 9. 致谢  <!--zh-->
[IntelliJ IDEA](https://www.jetbrains.com/?from=redis-replicator) is a Java Integrated Development Environment (IDE) for developing computer software.<!--en-->
<!--zh-->
It is developed by JetBrains (formerly known as IntelliJ), and is available as an Apache 2 Licensed community edition,<!--en-->
## 9.1. 宁文君<!--zh-->
and in a proprietary commercial edition. Both can be used for commercial development.<!--en-->
<!--zh-->
<!--en-->
2023年1月27日，在这一天我的妈妈宁文君（1953-2023）离世了。她是一个慈祥严格又乐于助人的老太太，自己的退休金虽然不多，但每年也会给贫困山区捐衣物现金。她是支撑我写下这个工具的最大动力，每当我跟她说又有新的公司在用这个工具时，她都和我一样高兴并鼓励我继续维护下去，也一直鼓励我参加各种技术分享活动。虽然我并没有取得多少成就，但她一直为我自豪。可能很多年后宁文君这个名字会被遗忘，但我希望 Github 会再有将数据备份到北极的活动，这样这个名字就会保存一千年。愿逝者安息。<!--zh-->
## 9.4. Redisson<!--en-->
<!--zh-->
<!--en-->
## 9.2. YourKit  <!--zh-->
[Redisson](https://github.com/redisson/redisson), a Redis-based In-Memory Data Grid for Java, offers distributed objects and services (`BitSet`, `Set`, `Multimap`, `SortedSet`, `Map`, `List`, `Queue`, `BlockingQueue`, `Deque`, `BlockingDeque`, `Semaphore`, `Lock`, `AtomicLong`, `CountDownLatch`, `Publish / Subscribe`, `Bloom filter`, `Remote service`, `Spring cache`, `Executor service`, `Live Object service`, `Scheduler service`) backed by a Redis server. Redisson provides a more convenient and easier way to work with Redis. Redisson objects provide a separation of concerns, allowing you to focus on data modeling and application logic.<!--en-->
<!--zh-->
![YourKit](https://www.yourkit.com/images/yklogo.png)  <!--zh-->
YourKit is kindly supporting this open source project with its full-featured Java Profiler.  <!--zh-->
YourKit, LLC is the creator of innovative and intelligent tools for profiling  <!--zh-->
Java and .NET applications. Take a look at YourKit's leading software products:  <!--zh-->
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and<!--zh-->
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.  <!--zh-->
<!--zh-->
## 9.3. IntelliJ IDEA  <!--zh-->
<!--zh-->
[IntelliJ IDEA](https://www.jetbrains.com/?from=redis-replicator) is a Java integrated development environment (IDE) for developing computer software.  <!--zh-->
It is developed by JetBrains (formerly known as IntelliJ), and is available as an Apache 2 Licensed community edition,  <!--zh-->
and in a proprietary commercial edition. Both can be used for commercial development.  <!--zh-->
<!--zh-->
## 9.4. Redisson<!--zh-->
<!--zh-->
[Redisson](https://github.com/redisson/redisson) is Redis based In-Memory Data Grid for Java offers distributed objects and services (`BitSet`, `Set`, `Multimap`, `SortedSet`, `Map`, `List`, `Queue`, `BlockingQueue`, `Deque`, `BlockingDeque`, `Semaphore`, `Lock`, `AtomicLong`, `CountDownLatch`, `Publish / Subscribe`, `Bloom filter`, `Remote service`, `Spring cache`, `Executor service`, `Live Object service`, `Scheduler service`) backed by Redis server. Redisson provides more convenient and easiest way to work with Redis. Redisson objects provides a separation of concern, which allows you to keep focus on the data modeling and application logic.<!--zh-->

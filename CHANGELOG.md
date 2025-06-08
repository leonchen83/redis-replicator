### 3.9.0

Support for redis 7.4.x and 8.0.x  
Fix rate limit bug.  

RDB
1. Add support for Rdb event `RDB_TYPE_HASH_METADATA`.
2. Add support for Rdb event `RDB_TYPE_HASH_LISTPACK_EX`.
3. Add support for Rdb event `RDB_OPCODE_SLOT_INFO`.

AOF
1. Add support for `HPERSIST` command.
2. Add support for `HSETEX` command.
3. Add support for `HPEXPIREAT` command.

### 3.8.1

Fix full sync offset bug. this bug could cause losing data in reconnection  
Support for java 21.  

### 3.8.0

Support for redis 7.2-RC2.  
Support for rdb v11.

### 3.7.0

Add support `SCAN` mode to instead of `PSYNC` command.  

### 3.6.5

Upgrade `slf4j-api` to `2.0.6`.  
Upgrade test dependency `jedis` to `4.3.1`.  
Upgrade example `MigrationExample`.  
Add cookie to `Event.Context` class.  
Fix lzf compress bug.  

### 3.6.4

Fix `listpack` decoding bug.  

### 3.6.3

Redis 7.0-GA `RDB_OPCODE_FUNCTION2` support.  

1. Deprecate `name`, `engineName`, `description` properties in `Function` class.
2. Deprecate `libraryName`, `engineName`, `description` properties in `FunctionLoadCommand` class.

### 3.6.2

Fix `BaseRdbEncoder.rdbSaveLen` bug.  
Redis 7.0-RC2 support.  
  
RDB
1. Add support for Rdb event `RDB_TYPE_STREAM_LISTPACKS_2`.  
2. `DumpRdbValueVisitor` support downgrade `RDB_TYPE_STREAM_LISTPACKS_2` to `RDB_TYPE_STREAM_LISTPACKS`.

AOF
1. `XGROUP CREATE` and `SETID`: new `ENTRIESREAD` optional argument.  
2. `XSETID` new `ENTRIESADDED` and `MAXDELETEDID` optional arguments.  

### 3.6.1

Convert Timestamp annotations to `unix timestamp`.  

### 3.6.0

Jdk 17 support.  

Redis 7.0 support.  
  
RDB  
1. Add support for Rdb event `RDB_OPCODE_FUNCTION`.
2. Add support for Rdb event `RDB_TYPE_HASH_LISTPACK`.
3. Add support for Rdb event `RDB_TYPE_ZSET_LISTPACK`.
4. Add support for Rdb event `RDB_TYPE_LIST_QUICKLIST_2`.
  
AOF  
1. Add support for `SPUBLISH` command.  
2. Add support for `FUNCTION LOAD` command.  
3. Add support for `FUNCTION FLUSH` command.  
4. Add support for `FUNCTION DELETE` command.  
5. Add support for `FUNCTION RESTORE` command.  
6. Add support for Timestamp annotations parser.  
  
Bug fix.  
  
Fix `ValueIterableEventListener` NPE bug.  

### 3.5.5

Optimize `DumpRdbValueVisitor` memory usage.  

### 3.5.4

Fix `DumpRdbValueVisitor` lzf compress bug.  

### 3.5.3

`DumpRdbValueVisitor` support downgrade from redis 6.2 to 2.8.  

### 3.5.2

Redis 6.2 support.  
  
1. Add `PXAT/EXAT` arguments to `SET` command.  
2. Add the `CH`, `NX`, `XX` arguments to `GEOADD`.
3. Add the `COUNT` argument to `LPOP` and `RPOP`.
4. Add `SYNC` arg to `FLUSHALL` and `FLUSHDB`, and `ASYNC/SYNC` arg to `SCRIPT FLUSH`
5. Add the `MINID` trimming strategy and the `LIMIT` argument to `XADD` and `XTRIM`

### 3.5.1

Fix `DumpRdbValueVisitor` OOM bug.  

### 3.5.0

Redis 6.2-rc1 support.  
Add command `COPY`,`LMOVE`,`BLMOVE`,`ZDIFFSTORE`,`GEOSEARCHSTORE`, `XGROUP CREATECONSUMER`.  
  
Modify command  
1. `ZADD` add `GT`, `LT` option.
2. `XADD` add `NOMKSTREAM` option.
3. `SET` add `GET` option.
  
External heartbeat scheduled executor support.  
1. `Configuration.setScheduledExecutor();`  
  
Fix `ZPopMinCommand.getKey()` return `null` bug.  

### 3.4.4

Fix `RedisURI` `setAuthUser` bug.  
Change default `readTimeout`, `connectionTimeout` from `30` seconds to `60` seconds.  

### 3.4.3
Fix NPE of `Configuration.toString()`.  

### 3.4.2

add ssl support on `AsyncRedisReplicator`.  
fix issue [#38](https://github.com/leonchen83/redis-replicator/issues/38).  

### 3.4.1

Mask auth password.  

### 3.4.0

Redis6 SSL, ACL support.  
`SetCommand` add keepttl parameter.  

### 3.3.3
Parse `PING` command.  

### 3.3.2
Fix `RedisCodec` bug.  

### 3.3.1
Add `GenericKeyCommand`,`GenericKeyValueCommand`.  

### 3.3.0
Add `Event` offset.  

### 3.2.1
Fix decode bug.  
Fix select bug.  

### 3.2.0
Add `ConnectinoListener`.  

### 3.1.1
Fix `RedisScoketReplicator.executor` shutdown too early bug.  

### 3.1.0
Support parse dump value.  

### 2.6.3
Fix `RedisScoketReplicator.executor` shutdown too early bug.  

### 2.6.2
Fix offset bug.  

### 2.6.1
Support redis-5.0-GA.  
Add new command : `XSETID`.  

### 2.6.0
Support jdk9, jdk10.  
**Breaking change**: migrate `commons-longging-1.2` to `slf4j-api-1.8.0-beta2`.  
Add new command : `ZPOPMAX`, `ZPOPMIN`, `XACK`, `XADD`, `XCLAIM`, `XDEL`, `XGROUP`, `XTRIM`.  
Change `RESTORE` command to `RESTORE key ttl serialized [REPLACE] [ABSTTL] [IDLETIME time] [FREQ freq]`.  
Support redis-5.0-rc1 `STREAM`.  

### 2.5.0

Add `ValueIterableRdbListener` to handle huge kv.  
Add `DefaultCommand` and `DefaultCommandParser` to handle raw command.  
Add `DumpRdbVisitor` and `DumpKeyValuePair` to convert rdb to `dump` format.  

### 2.4.7

Add new command : `EVALSHA`.  
Add `ReplicatorRetirer`.  

### 2.4.6

Add a new RdbVisitor : `SkipRdbVisitor`.  
Fix serializable bug.  

### 2.4.5

Fix PSYNC2 bug.  
Fix repl-stream-db bug.  

### 2.4.4

Fix ByteArrayMap serialize bug.  

### 2.4.3

Fix multi thread `close` bug.  
Fix `restore` command bug.  

### 2.4.2

Fix `Replicator.open` IOException bug.

### 2.4.1

Add `Replicator.getStatus`.  
Fix close replicator bug.  

### 2.4.0  

Add `RedisSocketReplicator.getStatus`.  
Add constructor `RedisReplicator(String uri)`.  

Redis uri support.  
Disk-less replication support.  
Fix offset bug. only ping heartbeat offset after event consumed.  

### 2.3.2

Add `Configuration.rateLimit`.

### 2.3.1

Add 3 commands.

```java  

LTRIM
RPOPLPUSH
SORT

```

Fix reconnect bug.

### 2.3.0

Module_2 support.  

Add a new RdbVisitor : `ValueIterableRdbVisitor`.

**API changes** :  

ModuleParser.parse(RedisInputStream in) -> ModuleParser.parse(RedisInputStream in, int version)  
DefaultRdbModuleParser add following methods for module_2:  

```java  

loadSigned(int version)
loadUnsigned(int version)
loadString(int version)
loadStringBuffer(int version)
loadDouble(int version)
loadFloat(int version)

```

### 2.2.0

Raw bytes support.  

### 2.1.2

DefaultRdbModuleParser.loadUnSigned mark deprecated. use DefaultRdbModuleParser.loadUnsigned instead.  
Fix compile warning.  
Fix javadoc.  
Fix log format.  

### 2.1.1

**API changes** :  

Add new commands :  

```java  
SWAPDB, ZREMRANGEBYLEX, ZREMRANGEBYRANK, ZREMRANGEBYSCORE, MULTI, EXEC.
RPushXCommand value -> values (redis 4.0 compatibility)
LPushXCommand value -> values (redis 4.0 compatibility)
```

### 2.1.0

**API changes** :

```java  
RdbVisitor interface -> abstract  
```

**Command changes** :  

```java  
ZIncrByCommand.increment int -> double  
SetTypeOffsetValue.value int -> long  
SetRangeCommand.index int -> long  
SetBitCommand.offset int -> long  
LSetCommand.index int -> long  
LRemCommand.index int -> long  
IncrByTypeOffsetIncrement.increment int -> long  
IncrByCommand.value int -> long
HIncrByCommand.increment int -> long
DecrByCommand.value int -> long
```

### 2.0.0-rc3

**API changes** :  

```java  
ReplicatorListener.addRdbRawByteListener -> ReplicatorListener.addRawByteListener
ReplicatorListener.removeRdbRawByteListener -> ReplicatorListener.removeRawByteListener
```

### 2.0.0-rc2

No API changes  

### 2.0.0-rc1

2.0.0 Initial commit  
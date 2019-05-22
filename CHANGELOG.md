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
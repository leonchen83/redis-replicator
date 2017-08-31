### 2.3.3  

add `RedisSocketReplicator.getStatus`  
fix offset bug. only add heart beat offset after event consumed.  

### 2.3.2

add `Configuration.rateLimit`  

### 2.3.1

add 3 commands

```java  

LTRIM
RPOPLPUSH
SORT

```

fix reconnect bug.

### 2.3.0
module_2 support.  

add a new RdbVisitor : `ValueIterableRdbVisitor`  

**api changes** :  

```java  
ModuleParser.parse(RedisInputStream in) -> ModuleParser.parse(RedisInputStream in, int version)  
DefaultRdbModuleParser add following methods for module_2
loadSigned(int version)
loadUnsigned(int version)
loadString(int version)
loadStringBuffer(int version)
loadDouble(int version)
loadFloat(int version)
```

### 2.2.0
raw bytes support.

### 2.1.2  

```java  
DefaultRdbModuleParser.loadUnSigned mark deprecated. use DefaultRdbModuleParser.loadUnsigned instead  
fix compile warning  
fix javadoc  
fix log format  
```

### 2.1.1  
**api changes** :  

```java  
add new commands :  
swapdb,zremrangebylex,zremrangebyrank,zremrangebyscore,multi,exec  
RPushXCommand value -> values (redis 4.0 compatibility)
LPushXCommand value -> values (redis 4.0 compatibility)
```

### 2.1.0
**api changes** :

```java  
RdbVisitor interface -> abstract  
```

**command changes** :  

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
**api changes** :  

```java  
ReplicatorListener.addRdbRawByteListener -> ReplicatorListener.addRawByteListener
ReplicatorListener.removeRdbRawByteListener -> ReplicatorListener.removeRawByteListener
```

### 2.0.0-rc2  
no api changes  

### 2.0.0-rc1  
2.0.0 Initial commit  
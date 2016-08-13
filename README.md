# Redis-replicator
`Redis Replicator` is a redis `RDB` and `Command` parser written in java.  
It can `parse`,`filter`,`broadcast` the `RDB` and `Command` events in a real time manner.  

#Usage  
  
##Socket  

```java
        RedisReplicator replicator = new RedisReplicator("127.0.0.1", 6379);
        replicator.addRdbListener(new RdbListener() {
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

##File  

```java
        RedisReplicator replicator = new RedisReplicator(new File("dump.rdb"));
        replicator.addRdbFilter(new RdbFilter() {
            @Override
            public boolean accept(KeyValuePair<?> kv) {
                return kv.getKey().startsWith("SESSION");
            }
        });
        replicator.addRdbListener(new RdbListener() {
            @Override
            public void handle(Replicator replicator, KeyValuePair<?> kv) {
                System.out.println(kv);
            }
        });

        replicator.open();
```  

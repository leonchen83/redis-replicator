# Redis-replicator  
`Redis Replicator` is a redis `RDB` and `Command` parser written in java.  
It can `parse`,`filter`,`broadcast` the `RDB` and `Command` events in a real time manner.  
  
#Requirements  
jdk 1.7+  
rdb version 6  
rdb version 7  

#Maven Dependency

```java  
<dependency>
    <groupId>com.moilioncircle</groupId>
    <artifactId>redis-replicator</artifactId>
    <version>1.0.4</version>
</dependency>
```
  
#Usage  
  
##Socket  
  
```java  
        RedisReplicator replicator = new RedisReplicator("127.0.0.1", 6379, Configuration.defaultSetting());
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
        RedisReplicator replicator = new RedisReplicator(new File("dump.rdb"), Configuration.defaultSetting());
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

#Command Extension  
  
* **write a command parser.**  
```java  
public class AppendParser implements CommandParser<AppendParser.AppendCommand> {

    @Override
    public AppendCommand parse(CommandName cmdName, Object[] params) {
        return new AppendCommand((String) params[0], (String) params[1]);
    }

    public static class AppendCommand implements Command {
        public final String key;
        public final String value;

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
    RedisReplicator replicator = new RedisReplicator("127.0.0.1",6379);
    replicator.addCommandParser(CommandName.name("APPEND"),new AppendParser());
```
  
* **handle event about this command.**  
```java
        replicator.addCommandListener(new CommandListener() {
            @Override
            public void handle(Replicator replicator, Command command) {
                if(command.name().equals(CommandName.name("APPEND"))){
                    //your code here
                }
            }
        });
```
  
#References  
  * [rdb.c](https://github.com/antirez/redis/blob/unstable/src/rdb.c)  
  * [Redis RDB File Format](https://github.com/sripathikrishnan/redis-rdb-tools/wiki/Redis-RDB-Dump-File-Format)  
  * [Redis Protocol specification](http://redis.io/topics/protocol)  

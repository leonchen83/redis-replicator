## Before pull request  

* Execute following commands and make sure you can pass all test cases.

```
sudo wget https://github.com/antirez/redis/archive/3.2.3.tar.gz && tar -xvzf 3.2.3.tar.gz && cd redis-3.2.3 && make && cd src && nohup ./redis-server --port 6380 --requirepass test &
sudo wget https://github.com/antirez/redis/archive/3.0.7.tar.gz && tar -xvzf 3.0.7.tar.gz && cd redis-3.0.7 && make && cd src && nohup ./redis-server --port 6379 &
sudo mvn clean package
```

* For Valkey development, start a Valkey 9 server and run tests with the Valkey flavor:

```
docker run -d --rm -p 6380:6380 valkey/valkey:9 --port 6380 --requirepass test --repl-diskless-sync no
docker run -d --rm -p 6379:6379 valkey/valkey:9
mvn test -Dtest.flavor=valkey
```

> Note: Valkey 9 changed the default of `repl-diskless-sync` to `yes`. This replicator requires disk-based RDB transfer, so `--repl-diskless-sync no` must be set explicitly.
> TODO: Add support for `repl-diskless-sync yes` (diskless replication via EOF transfer).

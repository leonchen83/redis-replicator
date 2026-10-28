#!/usr/bin/env bash
# Starts a 2-node Valkey cluster for SlotMigrationTest.
# Node A (127.0.0.1:7379) owns every slot. Node B (127.0.0.1:7380) owns none.
# The nodes meet over a docker network, so this works on Docker Desktop without host networking.
#
#   .github/scripts/valkey-cluster.sh up [image]   # default image: valkey/valkey:9
#   .github/scripts/valkey-cluster.sh down
#   .github/scripts/valkey-cluster.sh test [image [mvn args...]]   # up, run SlotMigrationTest, down
set -euo pipefail

NETWORK=valkey-cluster-test

down() {
  docker rm -f valkey-cluster-a valkey-cluster-b >/dev/null 2>&1 || true
  docker network rm "$NETWORK" >/dev/null 2>&1 || true
}

up() {
  local image=${1:-valkey/valkey:9}
  down
  docker network create "$NETWORK" >/dev/null
  # The source pauses writes for 2 x cluster-manual-failover-timeout while the test holds the import.
  # The 5s default fails the migration after 10s, so give the test 60s.
  docker run -d --name valkey-cluster-a --network "$NETWORK" -p 7379:7379 "$image" \
    valkey-server --port 7379 --cluster-enabled yes --enable-debug-command yes \
    --cluster-manual-failover-timeout 30000 >/dev/null
  docker run -d --name valkey-cluster-b --network "$NETWORK" -p 7380:7380 "$image" \
    valkey-server --port 7380 --cluster-enabled yes --enable-debug-command yes >/dev/null

  local b_ip
  b_ip=$(docker inspect -f "{{(index .NetworkSettings.Networks \"$NETWORK\").IPAddress}}" valkey-cluster-b)
  until docker exec valkey-cluster-a valkey-cli -p 7379 ping >/dev/null 2>&1; do sleep 0.2; done
  until docker exec valkey-cluster-b valkey-cli -p 7380 ping >/dev/null 2>&1; do sleep 0.2; done

  docker exec valkey-cluster-a valkey-cli -p 7379 cluster addslotsrange 0 16383 >/dev/null
  docker exec valkey-cluster-a valkey-cli -p 7379 cluster meet "$b_ip" 7380 >/dev/null

  for port in 7379 7380; do
    local node=valkey-cluster-a
    [ "$port" = 7380 ] && node=valkey-cluster-b
    for _ in $(seq 1 100); do
      docker exec "$node" valkey-cli -p "$port" cluster info | grep -q 'cluster_state:ok' && break
      sleep 0.2
    done
  done
  docker exec valkey-cluster-b valkey-cli -p 7380 cluster info | grep -q 'cluster_state:ok' || {
    echo "cluster did not reach cluster_state:ok" >&2
    exit 1
  }
  echo "valkey cluster ready: A=127.0.0.1:7379 B=127.0.0.1:7380"
}

run_test() {
  local image=${1:-valkey/valkey:9}
  shift || true
  trap down EXIT
  up "$image"
  cd "$(dirname "$0")/../.."
  if ! mvn test -Dtest=SlotMigrationTest -Dtest.flavor=valkey -Dtest.valkey.cluster=true "$@"; then
    docker logs valkey-cluster-a
    docker logs valkey-cluster-b
    exit 1
  fi
}

case "${1:-}" in
  up) up "${2:-}" ;;
  down) down ;;
  test) shift; run_test "$@" ;;
  *) echo "usage: $0 up [image] | down | test [image [mvn args...]]" >&2; exit 2 ;;
esac

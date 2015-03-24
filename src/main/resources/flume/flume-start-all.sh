#!/usr/bin/env bash
for port in 5141 5142
do
  echo "$port is prepared!"
  $FLUME_HOME/bin/flume-start.sh $port
done

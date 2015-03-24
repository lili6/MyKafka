#!/usr/bin/env bash

for pid in `ps aux | grep flume | grep jdk | awk '{print $2}'`
do
  kill -9 $pid
done

#!/usr/bin/env bash

port=$1
#FLUME_HOME=/home/crash/flume-1.4.0
#FLUME_HOME=/home/mrdTomcat/env/deploy/flume-1.4.0
if [[ -z $port ]];then
  echo "Please use $0 port"
  exit 0;
fi
echo "Flume $port Starting..."
nohup $FLUME_HOME/bin/flume-ng agent --conf $FLUME_HOME/conf --conf-file $FLUME_HOME/conf/flume-$port.properties --name agent -Dlog.dir="/home/mrdTomcat/env/flume-1.5.2/logs/flume-$port" 2>&1 &
echo "Flume $port Started"

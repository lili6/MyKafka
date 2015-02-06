# MyKafka
kafka test
              kafka作为分布式日志收集或系统监控服务，我们有必要在合适的场合使用它。kafka的部署包括zookeeper环境/kafka环境，同时还需要进行一些配置操作.接下来介绍如何使用kafka.

                  我们使用3个zookeeper实例构建zk集群，使用2个kafka broker构建kafka集群.

                  其中kafka为0.8V，zookeeper为3.4.6V
1、使用一个zookeeper配置
zookeeper/conf/ cp zoo_sample.cfg zoo.cfg
然后修改zoo.cfg---------

tickTime=2000
initLimit=10
syncLimit=5
# example sakes.
#dataDir=/tmp/zookeeper
dataDir=/home/lgf/zookeeper/data
dataLogDir=/home/lgf/zookeeper/log
# the port at which the clients will connect
clientPort=2181

2、启动zookeeper
./bin/zkServer.sh

3、部署kafka 两个节点 kafka-0,kafka-1
mkdir /kafka-0/tmp 用来存放日志
修改配置：/kafka-0/config/server.properties
broker.id=1
port=9092
修改配置：/kafka-1/config/server.properties
broker.id=1
port=9093

 修改每台服务器的config/server.properties
 broker.id：  唯一，填数字，本文中分别为132/133/134
 host.name：唯一，填服务器IP，之前配置时，把中间的'.'给忘写了，导致kafka集群形同虚设（基本只有leader机器在起作用），以及一系列莫名其妙的问题，伤啊
 zookeeper.connect=192.168.40.134:2181,192.168.40.132:2181,192.168.40.133:2181

注意host.name 和zookeeper.connect 必须为ip地址..

4、启动：
  JMS_PORT=9998 ./bin/kafka-server-start.sh ./config/server.properties &

  JMS_PORT=9997 ./bin/kafka-server-start.sh ./config/server.properties &

然后先运行LogConsumer,在运行LogProducer.java
5、官网是最好的参考
http://kafka.apache.org/documentation.html
#/bin/bash

JETTY_HOME=/opt/jetty



pid=`ps aux|grep start.jar|grep -v "grep" |awk '{print $2}'`
echo $pid
kill -9 $pid


cd $JETTY_HOME
nohup java -jar $JETTY_HOME/start.jar > /dev/null 2>&1 &

str=$"\n"
sstr=$(echo -e $str)

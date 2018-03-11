#/bin/bash

CODE_HOME=/home/q/system/
JETTY_HOME=/opt/jetty
cd $HOME
rm -rf $CODE_HOME/target
rm -rf $JETTY_HOME/webapps/*
mvn package

cp -rf ./target/*.war $JETTY_HOME/webapps/

sh ./restart.sh

#!/usr/bin/env bash

CODE_HOME=/Users/fanyuanyuan/workspace/smartdevice_cloud_service
JETTY_HOME=/opt/jetty
cd $CODE_HOME
rm -rf $CODE_HOME/target
rm -rf $JETTY_HOME/webapps/*
mvn package

cp -rf ./target/*.war $JETTY_HOME/webapps/

sh ./restart.sh

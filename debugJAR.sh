#!/usr/bin/env bash

$JAVA_HOME/bin/java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 \
  -jar nasapp/target/m1m-nascontrol-app-1.0.1.jar

#!/bin/bash

clj -T:build uberjar 
native-image --initialize-at-build-time=org.yaml.snakeyaml --no-server --no-fallback  -H:+ReportExceptionStackTraces  -jar target/tas.jar  -H:Name=target/tas
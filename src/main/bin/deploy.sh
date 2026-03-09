#!/bin/sh


JAR_NAME=deep-thinking-1.0.jar

APP_ENV=" --spring.profiles.active=prd --file.encoding=UTF-8"
JAVA_OPTS=" -server -Xms4g -Xmx4g -XX:+UseZGC -XX:+ZGenerational -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+UseStringDeduplication -XX:+UseStringDeduplication -XX:+PerfDisableSharedMem"

cd $APP_LOG_DIR ; git checkout -f main; git pull; mvn clean package -U -Dmaven.test.skip=true; cd ..

ps -ef | grep $JAR_NAME | grep -v grep | awk 'NR==1{print \$2}' | xargs -t -r kill -9; sleep 1s;

cp $APP_LOG_DIR/target/$JAR_NAME ./

nohup java $JAVA_OPTS -jar $JAR_NAME $APP_ENV > /dev/null 2>&1 &


# 查看 GC 统计（每5秒刷新）
# jstat -gc $(pgrep -f quant-system) 5s


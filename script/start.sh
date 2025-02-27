#!/bin/bash

dirpath="$(cd "$(dirname "$0")" && pwd)"
cd $dirpath

# JAVA_HOME="/nemo/jdk8"
APP_MAIN=com.webank.ppc.iss.PpcIssApplication
CLASSPATH='conf/:app/*:libs/*'
CURRENT_DIR=`pwd`
LOG_DIR=/data/app/logs/ppcs-modeladm
#LOG_DIR=${CURRENT_DIR}/logs
CONF_DIR=${CURRENT_DIR}/conf
os_platform=`uname -s`


if [ "${JAVA_HOME}" = "" ];then
    echo "JAVA_HOME has not been configured"
    exit -1
fi

mkdir -p logs

# 初始化全局变量，用于标识系统的PID（0表示未启动）
tradePortalPID=0
start_timestamp=0

getTradeProtalPID(){
    javaps=`$JAVA_HOME/bin/jps -l | grep $APP_MAIN`
    if [ -n "$javaps" ]; then
        tradePortalPID=`echo $javaps | awk '{print $1}'`
    else
        tradePortalPID=0
    fi
}

JAVA_OPTS=" -Dfile.encoding=UTF-8"
JAVA_OPTS+=" -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS+=" -Xmx1024m -Xms1024m -Xmn512m -Xss512k -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m"
JAVA_OPTS+=" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_DIR}/heap_error.log"

function get_start_time() {
    start_time=$(date "+%Y-%m-%d %H:%M:%S")
    if [[ "${os_platform}" = "Darwin" ]];then
        start_timestamp=$(date -j -f "%Y-%m-%d %H:%M:%S" "${start_time}" +%s)
    elif [[ "${os_platform}" = "Linux" ]];then
        start_timestamp=$(date -d "${start_time}" +%s)
    else
        echo -e "\033[31m[ERROR] Server start fail!\033[0m"
        echo "check platform...[failed]"
        echo "==============================================================================================="
        kill $tradePortalPID
        exit 1
    fi
}

function waiting_for_start() {
    echo ""
    i=0
    while [ $i -le 30 ]
    do
        for j in '\\' '|' '/' '-'
        do
            printf "%c%c%c%c%c Waiting for server started %c%c%c%c%c\r" \
            "$j" "$j" "$j" "$j" "$j" "$j" "$j" "$j" "$j" "$j"
            check_time=$(cat ${LOG_DIR}/ppcs-modeladm-sync.log | grep -a "JVM running for" | tail -n1 | awk -F "]" '{print $2}' | awk -F "[" '{print $2}' | awk -F " " '{print $1, $2}')
            if [ -n "$check_time" ]; then
                if [[ "${os_platform}" = "Darwin" ]];then
                    timestamp=$(date -j -f "%Y-%m-%d %H:%M:%S" "${check_time}" +%s)
                elif [[ "${os_platform}" = "Linux" ]];then
                    timestamp=$(date -d "${check_time}" +%s)
                else
                    echo -e "\033[31m[ERROR] Server start fail!\033[0m"
                    echo "check platform...[failed]"
                    echo "==============================================================================================="
                    kill $tradePortalPID
                    exit 1
                fi
                if [[ ${timestamp} -gt ${start_timestamp} ]]; then
                    echo ""
                    echo -e "\033[32m[INFO] Server start Successful!\033[0m"
                    echo "(PID=$tradePortalPID)...[Success]"
                    echo "==============================================================================================="
                    rm -rf logs
                    ln -s ${LOG_DIR} logs
                    exit 0
                fi
            fi
            sleep 1
        done
        let i=i+5
    done
    echo ""
    echo -e "\033[31m[ERROR] Server start fail!\033[0m"
    echo "timeout...[failed]"
    echo "==============================================================================================="
    kill $tradePortalPID
    exit 1
}

start(){
    getTradeProtalPID
    echo "==============================================================================================="
    if [ $tradePortalPID -ne 0 ]; then
        echo "$APP_MAIN already started(PID=$tradePortalPID)"
        echo "==============================================================================================="
    else
        get_start_time
        nohup $JAVA_HOME/bin/java -Djdk.tls.namedGroups="secp256k1" $JAVA_OPTS -cp $CLASSPATH $APP_MAIN  >start.out 2>&1 &
        sleep 1
        getTradeProtalPID
        if [ $tradePortalPID -ne 0 ]; then
            waiting_for_start
        else
            echo -e "\033[31m[ERROR] Server start fail!\033[0m"
            echo "[Failed]"
            echo "==============================================================================================="
        fi
    fi
}

start

#!/bin/sh

USER="@deb.user.name@"
NAME="@project.final.name@"
PATH_TO_JAR="@deb.lib.path@/${NAME}.jar"

PIDFILE="var/run/${NAME}.pid"
STATUS_CMD="pgrep -U ${USER} -fn ${PATH_TO_JAR}"

VM_OPTIONS_CONFIG="@deb.config.path@/application.vmoptions"
APP_CONFIG="@deb.config.path@/application.properties"
LOGGER_CONFIG="@deb.config.path@/logback.xml"
OUT_LOG="@deb.log.path@/@deb.log.out.name@"
ERR_LOG="@deb.log.path@/@deb.log.err.name@"
JAVA_ARGS="${JAVA_ARGS} -Dproperties.location=${APP_CONFIG} -Dlogging.config=${LOGGER_CONFIG}"

if [ -f ${VM_OPTIONS_CONFIG} ]; then
    JAVA_ARGS="${JAVA_ARGS} $(cat "${VM_OPTIONS_CONFIG}" | grep -v '^#' | tr '\n' ' ' | tr -s ' ')"
fi

is_running() {
    if [ -f "${PIDFILE}" ] && ps -p `cat "${PIDFILE}"` >/dev/null 2>&1 ; then
        echo 1
    else
        echo 0
    fi
}

check_root() {
    if [ "$(id -u)" != "0" ]; then
        echo "This script must be run using sudo or as the root user"
        exit 1
    fi
}

case $1 in
    start)
        check_root
        if [ ! -f ${PIDFILE} ]; then
            echo "Starting ${NAME} ..."
#            nohup sudo -u ${USER} java ${JAVA_ARGS} -jar ${PATH_TO_JAR} 1>>${ERR_LOG} >> ${OUT_LOG} 2>&1 &
            nohup sudo -u ${USER} java ${JAVA_ARGS} -jar ${PATH_TO_JAR} 1>>${OUT_LOG} 2>&1 &
                        echo $! > ${PIDFILE}

            sleep 1
            eval "${STATUS_CMD}" > ${PIDFILE}

            if [ $(is_running) = 0 ]; then
                echo -n ". . . "
                sleep 4
            fi

            if [ $(is_running) = 0 ]; then
                echo "FAILED"
                exit 1
            fi

            echo "OK"
        else
            echo "${NAME} is already running ..."
        fi
    ;;
    stop)
        if [ -f ${PIDFILE} ]; then
            PID=$(cat ${PIDFILE});
            kill ${PID};
            echo "${NAME} stopped ..."
            rm ${PIDFILE}
        else
            echo "${NAME} is not running ..."
        fi
    ;;
    restart)
        if [ -f ${PIDFILE} ]; then
            PID=$(cat ${PIDFILE});
            kill ${PID};
            echo "${NAME} stopped ...";
            rm ${PIDFILE}
            nohup sudo -u ${USER} java ${JAVA_ARGS} -jar ${PATH_TO_JAR} 1>>${LOG_FILE} 2>&1 &
                        echo $! > ${PIDFILE}
            echo "${NAME} started ..."
        else
            echo "${NAME} is not running ..."
        fi
    ;;
    status)
        if [ -f ${PIDFILE} ]; then
            PID=$(cat ${PIDFILE});
            echo "${NAME} (pid ${PID}) is running...";
        else
            echo "${NAME} is stopped"
        fi
    ;;
    *)
        echo "Usage: service ${NAME} {start|stop|restart|status}"
        exit 1
    ;;
esac 

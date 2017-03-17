#!/bin/bash

TRACE=$1
TRACE_DIR="src/main/resources"
JAR="target/compute-penalty-0.0.1-SNAPSHOT.jar"
CLASS="ntu.dplab.thesis.containerscaling.Main"

if [ "$TRACE" == "wiki" ]
then
    REQ="$TRACE_DIR/wiki.simple.26_30"
    PREDICT="$TRACE_DIR/wiki.predict.26_30"
elif [ "$TRACE" == "world" ]
then
    REQ="$TRACE_DIR/world.simple.66_70"
    PREDICT="$TRACE_DIR/world.predict.66_70"
else
    echo "Please specify TRACE={wiki, world}" >&2
    exit
fi

echo "Running trace: $TRACE"
echo "Request path: $REQ"
echo "PREDICT path: $PREDICT"

java -cp $JAR $CLASS $REQ $PREDICT

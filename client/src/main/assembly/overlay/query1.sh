#!/bin/bash

java -cp 'lib/jars/*' "$@" -Dquery=1 "ar.edu.itba.pod.client.Client"

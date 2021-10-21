#!/bin/bash

java -cp 'lib/jars/*' "$@" -Dquery=4 "ar.edu.itba.pod.client.Client"

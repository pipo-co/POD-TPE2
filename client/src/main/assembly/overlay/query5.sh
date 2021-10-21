#!/bin/bash

java -cp 'lib/jars/*' "$@" -Dquery=5 "ar.edu.itba.pod.client.Client"

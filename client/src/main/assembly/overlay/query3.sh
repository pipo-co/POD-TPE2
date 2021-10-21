#!/bin/bash

java -cp 'lib/jars/*' "$@" -Dquery=3 "ar.edu.itba.pod.client.Client"

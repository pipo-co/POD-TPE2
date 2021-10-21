#!/bin/bash

java -cp 'lib/jars/*' "$@" -Dquery=2 "ar.edu.itba.pod.client.Client"

#!/usr/bin/env bash

cat $1 | curl -X POST --data @- -H "Content-Type: application/json" http://localhost:8080/calculateExchangeRate_1_0.json

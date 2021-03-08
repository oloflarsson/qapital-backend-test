#!/usr/bin/env bash

userId="1337"

curl \
    --silent \
    --header "Accept: application/json" \
    --header "Content-Type: application/json" \
    --request "GET" \
    "http://localhost:8080/api/savings/rule/active/${userId}" \
    | json_pp

# Returns the following:
# [
#    {
#       "id" : 1,
#       "savingsGoalIds" : [
#          1,
#          2
#       ],
#       "status" : "active",
#       "amount" : 3,
#       "ruleType" : "guiltypleasure",
#       "active" : true,
#       "userId" : 1337,
#       "placeDescription" : "Starbucks"
#    },
#    {
#       "userId" : 1337,
#       "placeDescription" : null,
#       "amount" : 2,
#       "ruleType" : "roundup",
#       "active" : true,
#       "status" : "active",
#       "id" : 2,
#       "savingsGoalIds" : [
#          1
#       ]
#    }
# ]

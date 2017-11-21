#!/bin/bash

# A simple example script that publishes a number of scripts to the Nexus Repository Manager
# and executes them.

# fail if anything errors
set -e
# fail if a function call is missing an argument
set -u

username=scripter
password=AddScript1

# add the context if you are not using the root context
host=http://localhost:8081

# add a script to the repository manager and run it
function runScript {
  name=$1
  repo=$2
  args="$repo".json
  assets="$repo"_assets.json
  text="$repo"_assets.txt
  curl -v -X POST -u $username:$password --header "Content-Type: text/plain" "$host/nexus-public/service/siesta/rest/v1/script/$name/run" -d @$args > $assets
  printf "\nSuccessfully executed $name script\n\n\n"
  groovy -Dgroovy.grape.report.downloads=true -Dgrape.config=grapeConfig.xml parseAssets.groovy -f "$assets" -t "$text"
  printf "\nSuccessfully generated $text file\n\n\n"
}

printf "GetAssets API Script Starting \n\n" 
printf "Executing on $host\n"

runScript assets $1

printf "\nGetAssets Script Completed\n\n"

#!/bin/bash
modules=("omisego-core" "omisego-client" "omisego-admin")

for module in "${modules[@]}"
do
  echo ">> Deploying $module ..."
  ./gradlew :$module:clean :$module:build :$module:bintrayUpload -PbintrayUser=$BINTRAY_USER -PbintrayKey=$BINTRAY_KEY -PdryRun=false
  echo ">> Done deploying for $module"
done

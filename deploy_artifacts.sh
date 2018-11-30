#!/bin/bash
modules=("omisego-core" "omisego-client" "omisego-admin")

#./gradlew clean assemble # Without tests
./gradlew clean build # With tests

for module in "${modules[@]}"
do
  echo ">> Deploying $module ..."
    ./gradlew $module:bintrayUpload -PbintrayUser=$BINTRAY_USER -PbintrayKey=$BINTRAY_KEY -PdryRun=false
  echo ">> Done deploying for $module"
done

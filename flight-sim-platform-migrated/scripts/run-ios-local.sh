#!/usr/bin/env bash
# Runs the instructor station API locally against training-data/, on http://localhost:8090.
# The simulation, recorder and API all run in this one process; press Ctrl+C to stop.
set -euo pipefail
cd "$(dirname "$0")/.."

# The IOS development loop skips the coding standard check to keep start-up fast.
./mvnw -B -q -ntp -pl ios-api -am -DskipTests -Dcheckstyle.skip \
  package dependency:build-classpath \
  -Dmdep.outputFile=target/classpath.txt -Dmdep.includeScope=runtime

exec java -Dios.http.bind=127.0.0.1 \
  -cp "ios-api/target/classes:$(cat ios-api/target/classpath.txt)" \
  com.example.flightsim.ios.IosServer

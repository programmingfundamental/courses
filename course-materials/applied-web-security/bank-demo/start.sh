#!/bin/sh
set -eu
cd "$(dirname "$0")"
command -v java >/dev/null 2>&1 || { echo 'Java 17 or newer is required.' >&2; exit 1; }
if [ -f app/bank-demo.jar ]; then
    bank_jar=app/bank-demo.jar
else
    sh ./mvnw -q package
    bank_jar=target/bank-demo.jar
fi
echo 'Bank Lab: http://127.0.0.1:8087/login  -  admin / admin'
echo 'Stop with Ctrl+C. Restart to reset the demo data.'
exec java -jar "$bank_jar" --server.address=127.0.0.1 --server.port=8087

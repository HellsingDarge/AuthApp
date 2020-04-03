#!/usr/bin/env bash
sep=":"

if [[ "$OSTYPE" == "cygwin" ]]; then
        sep=";"
elif [[ "$OSTYPE" == "msys" ]]; then
        sep=";"
elif [[ "$OSTYPE" == "win32" ]]; then
        sep=";"
fi

rm -rf ./build
mkdir ./build
kotlinc \
        -cp "lib/kotlinx-cli-0.2.1.jar${sep}lib/flyway-core-6.3.2.jar${sep}lib/h2-1.4.200.jar${sep}lib/log4j-api-2.13.1.jar${sep}lib/log4j-core-2.13.1.jar" \
        src \
        -include-runtime \
        -d ./build/AuthApp.jar/
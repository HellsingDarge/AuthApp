#!/usr/bin/env bash
sep=":"

if [[ "$OSTYPE" == "cygwin" ]]; then
        sep=";"
elif [[ "$OSTYPE" == "msys" ]]; then
        sep=";"
elif [[ "$OSTYPE" == "win32" ]]; then
        sep=";"
fi

java -classpath "lib/kotlinx-cli-0.2.1.jar${sep}lib/flyway-core-6.3.2.jar${sep}lib/h2-1.4.200.jar${sep}lib/log4j-api-2.13.1.jar${sep}lib/log4j-core-2.13.1.jar${sep}src${sep}lib/kotlin-stdlib-jdk7.jar${sep}log4j2.xml${sep}build/AuthApp.jar" ru.kafedrase.authapp.MainKt "$@"

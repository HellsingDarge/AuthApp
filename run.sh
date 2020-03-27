#!/usr/bin/env bash
sep=":"

if [[ "$OSTYPE" == "cygwin" ]]; then
        sep=";"
elif [[ "$OSTYPE" == "msys" ]]; then
        sep=";"
elif [[ "$OSTYPE" == "win32" ]]; then
        sep=";"
fi

java -classpath "lib/kotlinx-cli-0.2.1.jar${sep}build/AuthApp.jar" ru.kafedrase.authapp.MainKt "$@"

#!/usr/bin/env bash
rm -rf ./out
mkdir ./out
kotlinc \
        -cp lib/kotlinx-cli-0.2.1.jar \
        src \
        -include-runtime \
        -d ./out/app.jar/
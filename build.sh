#!/usr/bin/env bash
rm -rf ./build
mkdir ./build
kotlinc \
        -cp lib/kotlinx-cli-0.2.1.jar \
        src \
        -include-runtime \
        -d ./build/AuthApp.jar/
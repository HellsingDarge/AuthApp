[![Build Status](https://travis-ci.org/HellsingDarge/AuthApp.svg?branch=master)](https://travis-ci.org/HellsingDarge/AuthApp)  [GiHub Pages](https://hellsingdarge.github.io/AuthApp/)

A prototype/model authentication app for working with DB, currently bare bones  

Usage
---
`./gradlew build` - build app jar with all dependencies  
`./gradlew test` - test implementation  
`java -jar AuthApp-<version>.jar <args>` - run the jar with specified `<args>`    

Work with terminal
---
Requires to have `kotlinc` and `java` installed  
Build: `kotlinc -cp lib/kotlinx-cli-0.2.1.jar src -include-runtime -d JAR_NAME.jar/`  
Run: `java -cp "lib/kotlinx-cli-0.2.1.jar:JAR_NAME.jar" -jar JAR_NAME.jar <args>`  
Tests: run `test.sh`

Roadmaps:
1. [Initial model/specification](docs/roadmap1.md)
2. [Cleaning repository](docs/roadmap2.md)
3. [Implementing DB I/O](docs/roadmap3.md)
4. [Moving to Gradle](docs/roadmap4.md)
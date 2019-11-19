#!/bin/bash

echo Compiling...
rm -rf build/
mkdir build/
mkdir build/classes
javac -cp lib/symspell-lib-6.6-SNAPSHOT-jar-with-dependencies.jar -d build/classes $(find ./src/* | grep .java)
# cp -R src/resources build/
echo Run...
java -cp lib/symspell-lib-6.6-SNAPSHOT-jar-with-dependencies.jar:build/classes com.parisilabs.SymSpellExample
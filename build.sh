#!/bin/bash
# author Loreto Parisi loretoparisi at gmail dot com

echo Compiling...
rm -rf build/
mkdir build/
mkdir build/classes
javac -cp lib/symspell-lib-6.6-SNAPSHOT-jar-with-dependencies.jar -d build/classes $(find ./src/* | grep .java)
echo Run...
java -cp lib/symspell-lib-6.6-SNAPSHOT-jar-with-dependencies.jar:build/classes com.parisilabs.SymSpellExample
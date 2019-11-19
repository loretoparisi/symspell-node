# symspell-node
SymSpell node - based on [customized-symspell](https://github.com/MighTguY/customized-symspell)


# How to build
To build customized-symspell please refer to [customized-symspell](https://github.com/MighTguY/customized-symspell)

```
git clone https://github.com/MighTguY/customized-symspell
cd customized-symspell/
sudo mvn compile
sudo mvn build
```

The compiled jar will be available in `symspell-lib/target/`. Copy the jar into the `symspell-node/lib/` folder.

then

```
$ cd symspell-node
$ ./build.sh
```

If it works you should see a log like


```
Compiling...
Run...
Loaded SymSpell
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
loading unigram...
loading bigram...
Element : biggest
compound : the quick brown fox jumps over the lazy dog
compound : there is the love he has rated FOREEVER for much of the past who couldn't read in sixth grade of a inspired him
compound : there is the love he has
lookup : mesial
lookup : redial
lookup : reseal
getCorrectedString : it was bright cold day in april and the clock were striking thirteen
getItemFrequency cool:5.647118E7
weightedDamerauLevenshteinDistance sommer-summer:1.0
customDamerauLevenshtein sommer-summer:0.8999999761581421
damerauLevenshtein sommer-summer:1.0
```
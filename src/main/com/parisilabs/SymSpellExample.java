/**
 * SymSpell example
 * @author Loreto Parisi
 * based on https://github.com/MighTguY
 * customized-symspell
 * https://github.com/MighTguY/customized-symspell/pull/14
 */

package com.parisilabs;

import io.github.mightguy.spellcheck.symspell.api.StringDistance;
import io.github.mightguy.spellcheck.symspell.api.DataHolder;
import io.github.mightguy.spellcheck.symspell.common.Composition;
import io.github.mightguy.spellcheck.symspell.common.DictionaryItem;
import io.github.mightguy.spellcheck.symspell.common.Murmur3HashFunction;
import io.github.mightguy.spellcheck.symspell.common.SpellCheckSettings;
import io.github.mightguy.spellcheck.symspell.common.SpellHelper;
import io.github.mightguy.spellcheck.symspell.common.SuggestionItem;
import io.github.mightguy.spellcheck.symspell.common.Verbosity;
import io.github.mightguy.spellcheck.symspell.common.WeightedDamerauLevenshteinDistance;
import io.github.mightguy.spellcheck.symspell.exception.SpellCheckException;
import io.github.mightguy.spellcheck.symspell.exception.SpellCheckExceptionCode;
import io.github.mightguy.spellcheck.symspell.impl.InMemoryDataHolder;
import io.github.mightguy.spellcheck.symspell.impl.SymSpellCheck;
import io.github.mightguy.spellcheck.symspell.common.QwertyDistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;

// LP: testing only
import org.junit.Assert;

/**
 * LP: ResourceLoader helper load from fs
 */
class ResourceLoader {

  public static URL getResource(String resource) {
    final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    classLoaders.add(Thread.currentThread().getContextClassLoader());
    classLoaders.add(ResourceLoader.class.getClassLoader());

    for (ClassLoader classLoader : classLoaders) {
      final URL url = getResourceWith(classLoader, resource);
      if (url != null) {
        return url;
      }
    }

    final URL systemResource = ClassLoader.getSystemResource(resource);
    if (systemResource != null) {
      return systemResource;
    } else {
      try {
        return new File(resource).toURI().toURL();
      } catch (MalformedURLException e) {
        return null;
      }
    }
  }

  private static URL getResourceWith(ClassLoader classLoader, String resource) {
    if (classLoader != null) {
      return classLoader.getResource(resource);
    }
    return null;
  }

}

/**
 * SymSpell Example
 */
public class SymSpellExample {

  static DataHolder dataHolder;
  static SymSpellCheck symSpellCheck;

  static WeightedDamerauLevenshteinDistance weightedDamerauLevenshteinDistance;
  static StringDistance damerauLevenshtein;
  static StringDistance customDamerauLevenshtein;

  public static void main(String[] args) throws IOException, SpellCheckException {

    SymSpellExample symSpellExample = new SymSpellExample();
    symSpellExample.run();

  }

  public void run() throws IOException, SpellCheckException {
    System.out.println("Loaded SymSpell");

    ClassLoader classLoader = SymSpellExample.class.getClassLoader();

    // If we need to use Default IMPL then use
    SpellCheckSettings spellCheckSettings = SpellCheckSettings.builder().countThreshold(1).deletionWeight(1f)
        .insertionWeight(1f).replaceWeight(1f).maxEditDistance(2).transpositionWeight(1f).topK(5).prefixLength(10)
        .verbosity(Verbosity.ALL).build();

    // Damerau-Levenshtein
    damerauLevenshtein = new WeightedDamerauLevenshteinDistance(1, 1, 1, 1, null);
    customDamerauLevenshtein = new WeightedDamerauLevenshteinDistance(0.8f, 1.01f, 0.9f, 0.7f, new QwertyDistance());

    // weighted Damerau-Levenshtein
    weightedDamerauLevenshteinDistance = new WeightedDamerauLevenshteinDistance(spellCheckSettings.getDeletionWeight(),
        spellCheckSettings.getInsertionWeight(), spellCheckSettings.getReplaceWeight(),
        spellCheckSettings.getTranspositionWeight(), null); // new QwertyDistance()

    // DataHolder init
    dataHolder = new InMemoryDataHolder(spellCheckSettings, new Murmur3HashFunction());

    symSpellCheck = new SymSpellCheck(dataHolder, weightedDamerauLevenshteinDistance, spellCheckSettings);
    List<String> result = new ArrayList<>();

    System.out.println("loading unigram...");
    loadUniGramFile(new File(ResourceLoader.getResource("data/frequency_dictionary_en_82_765.txt").getFile()));

    System.out.println("loading bigram...");
    loadBiGramFile(new File(ResourceLoader.getResource("data/frequency_bigramdictionary_en_243_342.txt").getFile()));

    // max editing distance
    int maxEd = 2;

    // lookup suggestions
    List<SuggestionItem> suggestionItems;
    suggestionItems = symSpellCheck.lookupCompound("bigjest", maxEd);
    for (SuggestionItem elem : suggestionItems) {
      System.out.println("Element : " + elem.getTerm().trim());
    }

    suggestionItems = symSpellCheck.lookupCompound("theq uick brown f ox jumps over the lazy dog", maxEd);
    for (SuggestionItem elem : suggestionItems) {
      System.out.println("compound : " + elem.getTerm().trim());
    }

    suggestionItems = symSpellCheck.lookupCompound(
        "Whereis th elove hehaD Dated FOREEVER forImuch of thepast who couqdn'tread in sixthgrade AND ins pired him",
        maxEd);
    for (SuggestionItem elem : suggestionItems) {
      System.out.println("compound : " + elem.getTerm().trim());
    }

    suggestionItems = symSpellCheck.lookupCompound("Whereis th elove hehaD", maxEd);
    for (SuggestionItem elem : suggestionItems) {
      System.out.println("compound : " + elem.getTerm().trim());
    }

    // lookup
    suggestionItems = symSpellCheck.lookup("resial", Verbosity.CLOSEST);
    for (SuggestionItem elem : suggestionItems) {
      System.out.println("lookup : " + elem.getTerm().trim());
    }

    // word break segmentation
    Composition compositions = symSpellCheck
        .wordBreakSegmentation("itwasabrightcolddayinaprilandtheclockswerestrikingthirteen", 10, 2.0);
    System.out.println("getCorrectedString : " + compositions.getCorrectedString());

    // Data Holder
    double freq = dataHolder.getItemFrequency("cool");
    System.out.println("getItemFrequency cool:" + Double.toString(freq));

    // distances
    double distance = weightedDamerauLevenshteinDistance.getDistance("sommer", "summer");
    System.out.println("weightedDamerauLevenshteinDistance sommer-summer:" + Double.toString(distance));
    distance = customDamerauLevenshtein.getDistance("sommer", "summer");
    System.out.println("customDamerauLevenshtein sommer-summer:" + Double.toString(distance));
    distance = damerauLevenshtein.getDistance("sommer", "summer");
    System.out.println("damerauLevenshtein sommer-summer:" + Double.toString(distance));
    

  }

  private static void loadUniGramFile(File file) throws IOException, SpellCheckException {
    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    while ((line = br.readLine()) != null) {
      String[] arr = line.split("\\s+");
      dataHolder.addItem(new DictionaryItem(arr[0], Double.parseDouble(arr[1]), -1.0));
    }
  }

  private static void loadBiGramFile(File file) throws IOException, SpellCheckException {
    BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    while ((line = br.readLine()) != null) {
      String[] arr = line.split("\\s+");
      dataHolder.addItem(new DictionaryItem(arr[0] + " " + arr[1], Double.parseDouble(arr[2]), -1.0));
    }
  }

}
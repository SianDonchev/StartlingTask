
package com.example.startlingtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NineLetterReducer {
    private static final int MAX_WORD_LENGTH = 9;
    private static final int MIN_WORD_LENGTH = 1;
    private static final String LETTER_A = "a";
    private static final String LETTER_I = "i";
    private static final String LETTER_PATTERN = "^[a-z]+$";
    private static final String DICTIONARY_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";

    /**
     * Reads lines from the given Reader (one word per line), filters out
     * anything longer than 9 characters or containing non-a..z letters,
     * and returns a Set<String> of all valid words (length 1..9).
     */
    public static Set<String> loadDictionary(final Reader reader) throws IOException {
        final Set<String> dict = new HashSet<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty() && line.length() <= MAX_WORD_LENGTH && line.matches(LETTER_PATTERN)) {
                    dict.add(line);
                }
            }
        }
        return dict;
    }

    /**
     * Given a Set<String> dict (all words ≤9 letters), computes and returns
     * the set of all words in dict that can be reduced (one letter at a time)
     * down to "a" or "i" (with each intermediate also in dict).
     */
    public static Set<String> buildReducibleSet(final Set<String> dict) {
        final List<List<String>> byLength = IntStream.rangeClosed(0, MAX_WORD_LENGTH)
                .mapToObj(i -> new ArrayList<String>())
                .collect(Collectors.toList());
        dict.forEach(word -> byLength.get(word.length()).add(word));

        final Set<String> reducibleSet = new HashSet<>(Set.of(LETTER_A, LETTER_I));

        // 3) DP pass from length=2..9
        for (int length = 2; length <= MAX_WORD_LENGTH; length++) {
            final int currentLength = length;
            byLength.get(length).stream()
                    .filter(word -> IntStream.range(0, currentLength)
                            .anyMatch(i -> reducibleSet.contains(word.substring(0, i) + word.substring(i + 1))))
                    .forEach(reducibleSet::add);

            // Free up memory for length-1
            byLength.get(length - 1).clear();
            byLength.set(length - 1, null);
        }
        return reducibleSet;
    }

    /**
     * Helper to check whether a single word is reducible, given a precomputed reducibleSet.
     * Returns true if word ∈ reducibleSet, false otherwise.
     */
    public static boolean isReducible(final String word, final Set<String> reducibleSet) {
        return reducibleSet.contains(word.toLowerCase());
    }

    /**
     * Main method: load from the GitHub URL, build the reducible set, then print all
     * 9-letter words that are reducible.
     */
    public static void main(String[] args) {
        try {
            // 1. Load dictionary from URL
            final URL url = new URL(DICTIONARY_URL);
            final Set<String> dict = loadDictionary(new InputStreamReader(url.openStream()));

            // 2. Build reducible set
            final Set<String> reducibleSet = buildReducibleSet(dict);

            // 3. Filter only those of length 9, sort, and print
            final List<String> nineLetterReducible = new ArrayList<>();
            for (String w : dict) {
                if (w.length() == MAX_WORD_LENGTH && reducibleSet.contains(w)) {
                    nineLetterReducible.add(w);
                }
            }
            Collections.sort(nineLetterReducible);

            System.out.println("Total reducible 9-letter words: " + nineLetterReducible.size());
            for (String w : nineLetterReducible) {
                System.out.println(w);
            }
        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
        }
    }
}
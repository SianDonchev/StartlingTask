package com.example.startlingtask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("NineLetterReducer Tests")
class NineLetterReducerTest {

    private static final String LETTER_A = "a";
    private static final String LETTER_I = "i";

    // Test dictionary containing words up to 9 letters that reduce to 'a' or 'i'
    private static final String TEST_DICTIONARY = String.join("\n",
            LETTER_A,        // base case
            LETTER_I,        // base case
            "at",           // at → a
            "it",           // it → i
            "in",           // in → i
            "cat",          // cat → at → a
            "sat",          // sat → at → a
            "sin",          // sin → in → i
            "scat",         // scat → cat → at → a
            "sati",         // sati → sat → at → a
            "sing",         // sing → sin → in → i
            "sciat",        // sciat → sati → sat → at → a
            "sating"        // sating → sati → sat → at → a
    );

    private Set<String> dictionary;
    private Set<String> reducibleSet;

    @BeforeEach
    void setUp() throws IOException {
        dictionary = NineLetterReducer.loadDictionary(new StringReader(TEST_DICTIONARY));
        reducibleSet = NineLetterReducer.buildReducibleSet(dictionary);
    }

    @Nested
    @DisplayName("Dictionary Loading Tests")
    class DictionaryLoadingTests {

        @Test
        @DisplayName("Should load valid dictionary words correctly")
        void shouldLoadValidDictionaryWords() {
            Set<String> expectedWords = Set.of(
                    LETTER_A, LETTER_I, "at", "it", "in", "cat", "sat", "sin",
                    "scat", "sati", "sing", "sciat", "sating"
            );
            assertEquals(expectedWords, dictionary, "Dictionary should contain exactly the expected words");
        }

        @Test
        @DisplayName("Should exclude words longer than 9 letters")
        void shouldExcludeLongWords() throws IOException {
            String dictionaryWithLongWords = TEST_DICTIONARY + "\ntoolongword\nanotherwaytoolong\n";
            Set<String> dict = NineLetterReducer.loadDictionary(new StringReader(dictionaryWithLongWords));

            assertTrue(dict.stream().allMatch(w -> w.length() <= 9),
                    "Dictionary should not contain words longer than 9 letters");
        }

        @Test
        @DisplayName("Should exclude words with non-alphabetic characters")
        void shouldExcludeNonAlphabeticWords() throws IOException {
            String dictionaryWithSpecialChars = TEST_DICTIONARY + "\nice-cream\nhello123\nabc_def\n";
            Set<String> dict = NineLetterReducer.loadDictionary(new StringReader(dictionaryWithSpecialChars));

            Set<String> invalidWords = Set.of("ice-cream", "hello123", "abc_def");
            assertTrue(invalidWords.stream().noneMatch(dict::contains),
                    "Dictionary should not contain words with non-alphabetic characters");
        }
    }

    @Nested
    @DisplayName("Reducible Words Tests")
    class ReducibleWordsTests {

        @Test
        @DisplayName("Should identify correct set of reducible words")
        void shouldIdentifyReducibleWords() {
            Set<String> expectedReducible = Set.of(
                    LETTER_A, LETTER_I,
                    "at", "it", "in",
                    "cat", "sat", "sin",
                    "scat", "sati", "sing",
                    "sciat"
            );
            assertEquals(expectedReducible, reducibleSet, "Should identify all reducible words correctly");
        }

        @Test
        @DisplayName("Should correctly validate individual reducible words")
        void shouldValidateReducibleWords() {
            assertTrue(NineLetterReducer.isReducible(LETTER_A, reducibleSet), "Base word 'a' should be reducible");
            assertTrue(NineLetterReducer.isReducible("at", reducibleSet), "'at' should reduce to 'a'");
            assertTrue(NineLetterReducer.isReducible("cat", reducibleSet), "'cat' should reduce through 'at' to 'a'");
            assertTrue(NineLetterReducer.isReducible("sing", reducibleSet), "'sing' should reduce through 'sin' to 'i'");
        }

        @Test
        @DisplayName("Should correctly identify non-reducible words")
        void shouldIdentifyNonReducibleWords() {
            assertFalse(NineLetterReducer.isReducible("dog", reducibleSet),
                    "Non-dictionary word 'dog' should not be reducible");
            assertFalse(NineLetterReducer.isReducible("cattle", reducibleSet),
                    "Word 'cattle' should not be reducible as its reductions are not in dictionary");
        }
    }
}
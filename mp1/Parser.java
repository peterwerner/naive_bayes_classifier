/*
 * CS 165A - Winter 2016 - Project 1
 * Peter Werner - peterwerner@umail.ucsb.edu - 3259306
 */
package mp1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Peter
 */
public class Parser {
    
    /**
     * Parses a string into an array of words
     * @param text
     * @return set of words
     */
    public static Set<String> parse(String text) 
    {
        Set<String> words = new HashSet<String>(Arrays.asList(text.split("\\s+")));
        
        splitPunctuation(words, ".,!?$");
        removeNeutrals(words);
        
        return words;
    }
    
    
    /**
     * lowercase everything
     */
    public static void toLowercase(Set<String> words)
    {
        Set<String> toAdd = new HashSet<String>();
        for (Iterator<String> it = words.iterator(); it.hasNext();) {
            String word = it.next(), word_modified = new String(word);
            word_modified = word.toLowerCase();
            it.remove();
            toAdd.add(word_modified);
        }
        for (String s : toAdd)
            words.add(s);
    }
    
    
    /**
     * Remove punctuation 
     * @param words
     * @param punctuation 
     */
    public static void removePunctuation(Set<String> words, String punctuation)
    {
        Set<String> toAdd = new HashSet<String>();
        for (Iterator<String> it = words.iterator(); it.hasNext();) {
            String word = it.next(), word_modified = new String(word);
            boolean modified = false;
            for (char c : punctuation.toCharArray())
            {
                if (word.indexOf(c) > 0) {
                    modified = true;
                    word_modified.replace("" + c, "");
                }
            }
            if (modified) {
                it.remove();
                toAdd.add(word_modified);
            }
        }
        for (String s : toAdd)
            words.add(s);
    }
    
    
    /**
     * Isolate punctuation 
     * @param words
     * @param punctuation 
     */
    public static void splitPunctuation(Set<String>words, String punctuation)
    {
        Set<String> toAdd = new HashSet<String>();
        for (Iterator<String> it = words.iterator(); it.hasNext();) {
            String word = it.next(), word_modified = new String(word);
            boolean modified = false;
            for (char c : punctuation.toCharArray())
            {
                if (word.indexOf(c) > 0) {
                    modified = true;
                    word_modified.replace("" + c, "");
                    toAdd.add("" + c);
                }
            }
            if (modified) {
                it.remove();
                toAdd.add(word_modified);
            }
        }
        for (String s : toAdd)
            words.add(s);
    }
    
    
    /**
     * Remove common neutral words from the set like "the", "and", "to", etc.
     * @param words 
     */
    public static void removeNeutrals(Set<String> words)
    {
        String[] neutrals = {
            "the", "be", "and", "of", "a", "in", "to", "have", "to", "it",
            "that", "for", "with", "on", "do", "at", "from", "that", "by",
            "or", "as", "if", "their", "they", "her", "him", "his", "hers",
            "these", "those", "in", "so"
        };
        for (String neutral : neutrals) {
            words.remove(neutral);
        }
    }
    
    
    /**
     * Given {a,b,c}, return {a,b,c,ab,bc}
     * @param words 
     * @param N = size of phrases to add - ie: 2 => ab, 3 => abc
     */
    public static void addAdjacents(Set<String> words, int N)
    {
        Object[] words_single = words.toArray();
        for (int i = 0; i < words_single.length - N; i++)
        {
            String phrase = (String)words_single[i];
            for (int k = 1; k < N; k++)
            {
                phrase += " " + (String)words_single[i+k];
            }
            words.add(phrase);
        }
    }
    
    
    /**
     * Stem a set of words
     * @param words 
     * @param allWords - library of words with which to stem the set of words
     */
    public static void stem(Set<String> words, Set<String> allWords)
    {
        Set<String> toAdd = new HashSet<String>(), toRemove = new HashSet<String>();
        
        String[] suffixes = {"ed", "ing"};
        String[] prefixes = {};
        
        for (String word : words) {
            for (String suffix : suffixes) {
                String stripped = word.replaceAll(suffix + "$", "");
                if (allWords.contains(stripped)) {
                    toRemove.add(word);
                    toAdd.add(stripped);
                }
            }
            for (String prefix : prefixes) {
                String stripped = word.replaceAll("^" + prefix, "");
                if (allWords.contains(stripped)) {
                    toRemove.add(word);
                    toAdd.add(stripped);
                }
            }
        }
        
        for (String r : toRemove)
            words.remove(r);
        for (String a : toAdd)
            words.add(a);
    }
}

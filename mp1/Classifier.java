/*
 * CS 165A - Winter 2016 - Project 1
 * Peter Werner - peterwerner@umail.ucsb.edu - 3259306
 */
package mp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 *
 * @author Peter
 */
public class Classifier {
    
    WordWeight wordWeight;
    // Training fields
    private Integer totalCatCount, totalInstanceCount;
    private HashMap<String, Integer> catCounts;  // Stores the number of instances of each category in the training set
    private HashMap<String, HashMap<String, Integer> > catWords;   // Stores hashmaps containing the number of instances containing this word in this category 
    private HashMap<String, Integer> wordInstanceCounts; // Stores the number of training instances which contain this word
    // Processed after training
    private double average, deviation;  // Average and standard deviation word count among all instances
    private double deviationGroupSize;  // Size of ranges into which we will classify based on word count (in terms of standard deviations from the average)
    // Timing data
    private long timeTrainFileMs;      // Time in ms that the last training took
    private long timeLabelFileMs;      // Time in ms that the last classification took

    
    public Classifier()
    {
        wordWeight = new WordWeight(2.8);
        totalCatCount = 0;
        totalInstanceCount = 0;
        catCounts = new HashMap<String, Integer>();
        catWords = new HashMap<String, HashMap<String, Integer> >();
        wordInstanceCounts = new HashMap<String, Integer>();
    }
    
    
    /**
     * Updates the data with a file full of training instances
     */
    public void trainFile(String file_training) throws IOException
    {
        timeTrainFileMs = System.currentTimeMillis();
        
        BufferedReader r_training = new BufferedReader(new FileReader(file_training));
                
        // Word counts for each instance in the file
        List<String> categories = new ArrayList<String>();
        List<Integer> wordCounts = new ArrayList<Integer>();
        
        // Train using each instance in the file and update word counts and average
        String line;
        while ((line = r_training.readLine()) != null) {
            String cat = line.substring(0, line.indexOf('\t'));
            String text = line.substring(line.indexOf('\t') + 1);
            int count = train(cat, text);
            wordCounts.add(count);
            categories.add(cat);
        }
   
        // Add instance word count to the data used by the model
        // updateLengthStats(categories, wordCounts);
        
        timeTrainFileMs = System.currentTimeMillis() - timeTrainFileMs;
    }
    /**
     * Updates the data with one training instance
     * @param category
     * @param text 
     * @return number of words parsed from the instance
     */
    public int train(String category, String text)
    {
        // Parse the text into a list of words
        Set<String> words = Parser.parse(text);
        
        // Increment the individual category and total category counts
        totalCatCount++;
        Integer catCount = catCounts.get(category);
        if (catCount == null)
            catCounts.put(category, 1);
        else
            catCounts.put(category, ++catCount);
                
        // Increment the word count within the category
        HashMap<String, Integer> catWordMap = catWords.get(category);
        if (catWordMap == null) {
            catWordMap = new HashMap<String, Integer>();
            catWords.put(category, catWordMap);
        }
        for (String word : words) {
            Integer wordCount = catWordMap.get(word);
            if (wordCount == null)
                catWordMap.put(word, 1);
            else
                catWordMap.put(word, ++wordCount);
            // Increment the word category count
            Integer c = wordInstanceCounts.get(word);
            if (c == null)
                wordInstanceCounts.put(word, 1);
            else
                wordInstanceCounts.put(word, ++c);
        }
        
        // Increment the total count
        totalInstanceCount++;
        
        return words.size();
    }
    
    
    /**
     * Classify a file full of data
     * @throws IOException 
     * @return ratio correct guesses / total guesses
     */
    public double classifyFile(String file_testing, boolean verbose) throws IOException 
    {        
        timeLabelFileMs = System.currentTimeMillis();
        
        BufferedReader r_testing = new BufferedReader(new FileReader(file_testing));
        
        int count_total = 0, count_correct = 0;
        
        String line;
        while ((line = r_testing.readLine()) != null) {
            String realCat = line.substring(0, line.indexOf('\t'));
            String text = line.substring(line.indexOf('\t') + 1);
            String guessCat = classify(text);
            
            count_total++;
            if (realCat.equals(guessCat))
                count_correct++;
            
            if (verbose)
                System.out.println(guessCat);
        }
        // System.out.println(count_correct + " / " + count_total);
        // System.out.println(100.0 * count_correct / count_total + "% correct.");
        
        timeLabelFileMs = System.currentTimeMillis() - timeLabelFileMs;
        
        return (double)count_correct / count_total;
    }
    /**
     * Classify the text based on the current data
     * @param text
     * @return category string
     */
    public String classify(String text) 
    {
        // Parse the text into a set of words
        Set<String> words = Parser.parse(text);
        
        // Add dummy words based on post-processed statistics
        // ie: add words representing the word count categories
        // So all long texts get a "long" word, all short texts get a "short" word, etc.
        // updateLengthStatsInstance(words, 3);
                
        // Find the most likely category
        double maxLogProb = -999.0;
        String maxCat = null;
        
        for (String category : catCounts.keySet()) {
            HashMap<String, Integer> wordSet = catWords.get(category);
            Integer wordCatCount = catCounts.get(category);
            // Let p' = P(cat|word_1,...,word_N)
            //       => P(cat) * P(word_1|cat)*...*P(word_N|cat) / [P(word_1)*...*P(word_N)]
            // Let p = logP(cat) + logP(word_1|cat)+...+logP(word_N|cat) - logP(word_1)-...-logP(wordN)
            double p = log((double)catCounts.get(category) / totalCatCount);
            double notFoundProb = log(1.0 / totalInstanceCount);
            for (String word : words) 
            {
                // Weight based on known positive / negative words lists
                double known = wordWeight.getWordWeight(word);
                if ((known < 0 && category.equals("0")) || (known > 0 && category.equals("1")))
                    p += log(Math.abs(known));
                if ((known > 0 && category.equals("0")) || (known < 0 && category.equals("1")))
                    p += log(1 / Math.abs(known));
                
                Integer wordCount = wordSet.get(word);
                if (wordCount != null) 
                    p += log((double)wordCount / wordCatCount);
                Integer wordInstanceCount = wordInstanceCounts.get(word);
                if (wordInstanceCount != null)
                    p -= log((double)wordInstanceCount / totalInstanceCount);
                // Smoothing
                if (wordCount == null && wordInstanceCount != null) {                    
                    /* Laplace 
                    double k = 1.0;
                    p += log((wordInstanceCount + k) / ((k + 1) * wordSet.size()));
                    */
                    p += notFoundProb;
                } 
            }
            // Determine if this category is the most likely
            if (p > maxLogProb || maxCat == null) {
                maxLogProb = p;
                maxCat = category;
            }
        }
        return maxCat;
    }

    
    public long getTimeTrainFileMs() {
        return timeTrainFileMs;
    }

    public long getTimeLabelFileMs() {
        return timeLabelFileMs;
    }

    
    /**
     * Post-process the data to:
     *  Add length (number of words) as a data point in the model
     *
    private boolean updateLengthStats(List<String> categories, List<Integer> wordCounts)
    {      
        if (categories.size() != wordCounts.size())
            return false;
        
        // Calculate average and standard deviation
        average = 0;
        deviation = 0;
        int i = 0;
        for (Integer count : wordCounts) 
        {
            deviation += Math.pow(count - average, 2);
            average = average*(i/(1.0+i)) + count/(1.0+i);
            i++;
        }
        deviation = Math.sqrt(deviation / i);
        
        // Determine which size category each instance belongs in and add it to the model
        int N = wordCounts.size();
        String sizeCat;
        for (i = 0; i < N; i++) 
        {
            // Determine size category
            sizeCat = getSizeCat(wordCounts.get(i));
            
            // Add to the category word count
            String cat = categories.get(i);
            Integer c = catWords.get(cat).get(sizeCat);
            if (c == null)
                catWords.get(cat).put(sizeCat, 1);
            else
                catWords.get(cat).put(sizeCat, ++c);
            
            // Add to the instance word count
            c = wordInstanceCounts.get(sizeCat);
            if (c == null)
                wordInstanceCounts.put(sizeCat, 1);
            else
                wordInstanceCounts.put(sizeCat, ++c);
        }

        return true;
    } */
    /**
     * Update a testing instance's set of words using the post-processed statistical data
     *
    private void updateLengthStatsInstance(Set<String> words, int weight)
    {
        for (int i = 0; i < weight; i++)
            words.add(getSizeCat(words.size()) + i);
    } */
    /**
     * Helper method - return the size cat (string) based on average and std dev
     *
    private String getSizeCat(int wordCount)
    {
        String sizeCat = "LEN MEDIUM";
        double diff = wordCount - average;
        if (diff > 0.5 * deviationGroupSize)
            sizeCat = "LEN BIG";
        else if (diff < -0.5 * deviationGroupSize)
            sizeCat = "LEN SMALL";
        return sizeCat;
    } */
    
    
    
    /**
     * DEBUG method - prints all the data gathered so far
     *
    public void debug_printData()
    {
        System.out.println("Total categories: " + totalCatCount);
        for (String key_cat : catWords.keySet()) {
            String header = key_cat;
            Integer count = catCounts.get(key_cat);
            if (count != null)
                header += " (" + count + ")";
            System.out.println(header);
            HashMap<String, Integer> wordSet = catWords.get(key_cat);
            String data = "";
            for (String key_word : wordSet.keySet()) {
                data += " " + key_word + "(" + wordSet.get(key_word) + ")";
            }
            System.out.println(data);
        }
    } */
    
}

/*
 * CS 165A - Winter 2016 - Project 1
 * Peter Werner - peterwerner@umail.ucsb.edu - 3259306
 */
package mp1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * For N iterations:
 *  randomly select 66% of the data to train with,
 *  then attempt to classify the remaining 33% using that training
 * Then print average and standard deviation
 * @author Peter
 */
public class RandomTest {
    
    public static void start(int N) {
        System.out.println("Randomized test using training set");
        double total = 0.0;
        double[] values = new double[N];
        for (int i = 0; i < N; i++) {
            try {
                System.out.println(i);
                values[i] = start(new BufferedReader(new FileReader("C:/Users/Peter/OneDrive/CS 165A/project1/guide/training.txt")));
                total += values[i];
            } catch (FileNotFoundException ex) { }
        }
        double average = total / N;
        double deviation = 0;
        for (double value : values)
            deviation += Math.pow(value - average, 2);
        deviation = Math.sqrt(deviation / N);
        System.out.println("Average: " + 100 * average + "%");
        System.out.println("Std Dev: " + 100 * deviation + "%");
    }
    
    /**
     * Given a buffered reader representing a training file,
     * randomly select 66% of the data to train with,
     * then attempt to classify the remaining 33% using that training
     * @param r_training
     * @return 
     */
    public static double start(BufferedReader r_training) {
        Classifier c = new Classifier();
        String[] ar = new String[2000];
        String line;
        int i = 0;
        try {
            while((line = r_training.readLine()) != null & i < ar.length) {
                ar[i] = line;
                i++;
            }
        } catch (IOException ex) { }
        Random rnd = ThreadLocalRandom.current();
        for (i = ar.length - 1; i > 0; i--)
        {
          int index = rnd.nextInt(i + 1);
          // Simple swap
          String a = ar[index];
          ar[index] = ar[i];
          ar[i] = a;
        }
        for (i = 0; i < ar.length / 1.5; i++){
            String cat = ar[i].substring(0, ar[i].indexOf('\t'));
            String text = ar[i].substring(ar[i].indexOf('\t') + 1);
            c.train(cat, text);
        }
        int c_total=0, c_good = 0;
        for ( ; i < ar.length; i++){
            String cat_real = ar[i].substring(0, ar[i].indexOf('\t'));
            String text = ar[i].substring(ar[i].indexOf('\t') + 1);
            String cat_guess = c.classify(text);
            c_total++;
            if (cat_real.equals(cat_guess))
                c_good++;
        }
        System.out.println(c_good + " / " + c_total);
        System.out.println(100.0 * c_good / c_total + "%");
        return (double)c_good / c_total;
    }
}

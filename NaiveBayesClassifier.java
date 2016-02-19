/*
 * CS 165A - Winter 2016 - Project 1
 * Peter Werner - peterwerner@umail.ucsb.edu - 3259306
 */
import mp1.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Peter Werner
 */
public class NaiveBayesClassifier {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        if (args.length != 2) {
            System.out.println("Usage: NaiveBayesClassifier training.txt testing.txt");
            return;
        }
        
        //String FILE_TRAIN = "C:/Users/Peter/OneDrive/CS 165A/project1/guide/training.txt";
        //String FILE_TEST = "C:/Users/Peter/OneDrive/CS 165A/project1/guide/testing10.txt";
        String FILE_TRAIN = args[0];
        String FILE_TEST = args[1];
        
        // Attempt to open the training and testing files before training and testing begins
        try {
            BufferedReader r_training = new BufferedReader(new FileReader(FILE_TRAIN));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NaiveBayesClassifier.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error: Could not find training file");
            return;
        }
        try {
            BufferedReader r_testing = new BufferedReader(new FileReader(FILE_TEST));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NaiveBayesClassifier.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error: Could not find testing file");
            return;
        }
        
        // Run the training and classify the testing data
        Classifier c = new Classifier();
        try {
            long timeTrain, timeLabel;
            double accuracyTraining, accuracyTesting;
            // Train using training set
            c.trainFile(FILE_TRAIN);
            timeTrain = c.getTimeTrainFileMs();
            // Test on training set
            accuracyTraining = c.classifyFile(FILE_TRAIN, false);
            timeLabel = c.getTimeLabelFileMs();
            // Test on testing set (and print each result)
            accuracyTesting = c.classifyFile(FILE_TEST, true);
            timeLabel += c.getTimeLabelFileMs();
            // Print stats
            System.out.println(timeTrain / 1000 + " seconds (training)");
            System.out.println(timeLabel / 1000 + " seconds (labeling)");
            System.out.println(String.format("%.3f (training)", accuracyTraining));
            System.out.println(String.format("%.3f (testing)", accuracyTesting));
        } 
        catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            Logger.getLogger(NaiveBayesClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}

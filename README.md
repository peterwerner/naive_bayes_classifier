# Naive Bayes Text Classifier

Classifies strings of words to categories using Bayes rule.  I used it to classify Amazon.com reviews into positive and negative sentiment categories.

### How it works

Baye's rule states that P(x|y), the probability of x occuring given that y has occured, is equal to P(y|x)P(x)/P(y).

Let s be the string which we would like to classify.  Let w1,w2,...,wn be the words in s.  Let c be an arbitrary category.
P(c|s) = P(s|c) * P(c) / P(s)
       = [P(w1|c)P(w2|c)...P(wn|c)] * P(c) / [P(w1)P(w2)...P(wn)]

### Accuracy

###### Random partitioning

I randomly selected 2/3 of the training data to train with, then tested on the remaining 1/3.  Accuracy: ~87% (standard deviation ~1.25%)

###### External testing data

Accuracy: ~84%

###### Testing on the training set

Accuracy: >99%

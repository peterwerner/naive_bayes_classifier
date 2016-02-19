JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	NaiveBayesClassifier.java \
	mp1/Classifier.java \
	mp1/Parser.java \
	mp1/WordWeight.java \
	mp1/RandomTest.java

default: classes

classes: $(CLASSES:%.java=%.class)

clean:
	$(RM) *.class mp1/*.class
A note to beginners: EPUBGENERATOR_HOME refers to the directory where you've installed isgihgen.  It is where you will find the directories lib, input and output, as well as this README file.

isgihgen is a java application that builds an epub from an input file, and one or more content files.

It can be invoked this way...

java -jar EPUBGENERATOR_HOME/lib/isgihgen-[version].jar input=PATH_TO_INPUT_FILE

The input file needs to be a legal xml file.  A heavily commented sample input file is located at EPUBGENERATOR_HOME/input/sample_a.xml.

The input file tells isgihgen where to find the content that will go into your epub and where to output the finished (zipped) product.

Provided you have write permissions at the EPUBGENERATOR_HOME directory, you can create a sample epub by running this command from EPUBGENERATOR_HOME (the same directory as this README file):

     java -jar ./lib/isgihgen-[version].jar input=./input/sample_a.xml

sample_a.xml specifies input and output directories within EPUBGENERATOR_HOME, but your input can reside anywhere the isgihgen user has read permission, and the output can likewise go anyplace where the isgihgen user has write permission.

You will probably find it's convenient to keep your input and output somewhere external to EPUBGENERATOR_HOME.

isgihgen is a java application that builds an epub from an XML input file (called the ISGIH file), and one or more content files.

ISGIH stands for "Important stuff goes in here."

You can invoke isgihgen this way...

java -jar PATH_TO_ISGIHGEN/isgihgen-[version].jar input=PATH_TO_INPUT_FILE

(Note: you will need to have write permission at the path specified at the XPath epub->output->paths->path->root  in your ISGIH  file.)

The ISGIH file needs to be a legal XML file.  A heavily commented sample ISGIH file is located in this repository at input/sample_a.xml.

The ISGIH file tells isgihgen where to find the content that will go into your epub and where to output the finished (zipped) product.

sample_a.xml specifies relative paths for input and output, but your input can reside anywhere the isgihgen user has read permission, and the output can likewise go anyplace where the isgihgen user has write permission.

You will probably find it's convenient to keep your input and output somewhere external to the location of your isgihgen jar.

A word of warning with regard to your output path: if the value at XPath epub->output->delete in your ISGIH document is set to 'yes', isgihgen will ask you-- at runtime-- if you want to perform a recursive delete on the output path specified in your ISGIH document (at XPath epub->output->paths->path->root), and, if you confirm, will recusively delete all content at that path.  If that makes you nervous, set the value of epub->output->delete to 'no'.  You can always manually delete any stale content in your output directory.

A guide to using isgihgen is located at this url: http://www.kurttrue.net/epubgen

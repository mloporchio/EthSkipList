#
#	File:		makefile
#	Author:		Matteo Loporchio
#	Thanks to:	https://www.cs.swarthmore.edu/~newhall/unixhelp/javamakefiles.html
#

JC=javac
JFLAGS=-cp ".:./lib/gson-2.10.jar"

default: 
	$(JC) $(JFLAGS) *.java

clean:
	$(RM) *.class
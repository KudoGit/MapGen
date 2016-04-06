#Simple Makefile

SOURCES = Visualizer.java Tile.java Pair.java Main.java
CLASSES = Visualizer.class Tile.class Pair.class Main.class

${CLASSES}:	${SOURCES}
	javac -Xlint ${SOURCES}

clean:
	rm -f *.class

JAR = "target/compute-penalty-0.0.1-SNAPSHOT.jar"
CLASS = "ntu.dplab.thesis.containerscaling.Main"

all:
	mvn package
run:
	java -cp $(JAR) $(CLASS)
clean:
	mvn clean
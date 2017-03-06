JAR = "target/compute-penalty-0.0.1-SNAPSHOT.jar"
CLASS = "ntu.dplab.thesis.containerscaling.Main"
REQ = "src/main/resources/wiki.simple.26_30"
PREDICT = "src/main/resources/wiki.predict.26_30"

all:
	mvn package
run:
	java -cp $(JAR) $(CLASS) $(REQ) $(PREDICT)
clean:
	mvn clean
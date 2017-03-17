TRACE ?= ""

all:
	mvn package

run:
	./run.sh $(TRACE)

clean:
	mvn clean

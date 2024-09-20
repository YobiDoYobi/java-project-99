.DEFAULT_GOAL := build-run

clean:
	gradle clean

lint:
	gradle checkstyleMain
	gradle checkstyleTest

build:
	make clean
	make lint
	make test

test:
	gradle test

report:
	gradle jacocoTestReport

.PHONY: build
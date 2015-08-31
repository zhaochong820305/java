
all: start common client server stop
	@echo "compile complete"

start:
	@-rm -rf bin
	@-mkdir class
	@-mkdir bin

stop:
	@-rm -rf bin
	
client: ./src/client/Client.java
	javac -g -cp ./class/binary.jar -d ./class $^

server: ./src/server/User.java ./src/server/Server.java
	javac -g -cp ./class/binary.jar: -d ./class $^

common: protocol util
	jar cvf binary.jar -C ./bin .
	mv binary.jar ./class

protocol: ./src/protocol/Protocol.java
	javac -g -d ./bin $^

util: ./src/util/Util.java
	javac -g -d ./bin $^

run_client:
	@echo "run binary_demo client:"
	java -cp ./class:./class/binary.jar Client ./test/a.txt

run_server:
	@echo "run binary_demo server:"
	java -cp ./class:./class/binary.jar Server

.PHONY: start stop clean run_client run_server

clean:
	@-rm -rf class bin


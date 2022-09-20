
CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread -lboost_filesystem -lboost_thread

all:BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/clientGet.o bin/clientSend.o bin/BidiEncoderDecoder.o bin/BGSclient.o $(LDFLAGS)

BGSclient : bin/connectionHandler.o bin/clientGet.o bin/clientSend.o bin/BidiEncoderDecoder.o bin/BGSclient.o

bin/BidiEncoderDecoder.o : src/BidiEncoderDecoder.cpp
	g++ $(CFLAGS)  -o bin/BidiEncoderDecoder.o src/BidiEncoderDecoder.cpp

bin/clientGet.o : src/clientGet.cpp
	g++ $(CFLAGS)  -o bin/clientGet.o src/clientGet.cpp

bin/clientSend.o :  src/clientSend.cpp
	g++ $(CFLAGS)  -o bin/clientSend.o src/clientSend.cpp

bin/BGSclient.o : src/BGSclient.cpp
	g++ $(CFLAGS)  -o bin/BGSclient.o src/BGSclient.cpp

bin/connectionHandler.o : src/connectionHandler.cpp
	g++ $(CFLAGS)  -o bin/connectionHandler.o src/connectionHandler.cpp

.PHONY: clean
clean:
	rm -f bin/*
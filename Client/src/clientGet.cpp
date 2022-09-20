//
// Created by eshm on 31/12/2021.
//
#include "BidiEncoderDecoder.h"
#include "clientGet.h"


clientGet::clientGet(ConnectionHandler &connectionHandler, bool &shouldTerminate, std::mutex &mutex, bool &isLoggedIn):
        connectionHandler(connectionHandler), shouldTerminate(shouldTerminate), mutex(mutex), isLoggedIn(isLoggedIn) {}


int clientGet::run() {
//    BidiEncoderDecoder encdec;

    while(!shouldTerminate) {
        std::unique_lock<std::mutex> lock(mutex);
        std::string answer = "";

        connectionHandler.getLine(answer);

        answer = answer.substr(0, answer.length()-1);
        std::cout << answer <<std::endl; //todo: check if working for logStat and Stat, if not use below lines

        if(answer.compare("ACK 3") == 0) { //if logout acknowledged: stop loop
            shouldTerminate = true;
            return 0;
        }
        else if(answer.compare("ERROR 3") == 0){ //if logout command didn't work: continue
            isLoggedIn = true;
        }
    }
    return 0;
}



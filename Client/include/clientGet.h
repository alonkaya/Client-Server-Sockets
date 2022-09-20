//
// Created by eshm on 31/12/2021.
//
#include <connectionHandler.h>
#ifndef ASSIGNMENT_3_CLIENTGET_H
#define ASSIGNMENT_3_CLIENTGET_H
#include "mutex"


class clientGet {
public:
    clientGet(ConnectionHandler &connectionHandler, bool &shouldTerminate, std::mutex &mutex, bool &isLoggedIn);
    int run ();
private:
    ConnectionHandler &connectionHandler;
    bool &shouldTerminate;
    std::mutex &mutex;
    bool &isLoggedIn;
};


#endif //ASSIGNMENT_3_CLIENTGET_H

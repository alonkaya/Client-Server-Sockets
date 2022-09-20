//
// Created by eshm on 31/12/2021.
//
#include <connectionHandler.h>
#ifndef ASSIGNMENT_3_CLIENTSEND_H
#define ASSIGNMENT_3_CLIENTSEND_H
#include "mutex"
#include <iostream>


class clientSend {
public:
    clientSend(ConnectionHandler &connectionHandler, bool &shouldTerminate, std::mutex &mutex, bool &isLoggedIn);
    int run ();
private:
    ConnectionHandler &connectionHandler;
    bool &shouldTerminate;
    std::mutex &mutex;
    bool &isLoggedIn;
};


#endif //ASSIGNMENT_3_CLIENTSEND_H

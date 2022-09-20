//
// Created by eshm on 30/12/2021.
//
#include <string>
#include <boost/lexical_cast.hpp>
#include "BidiEncoderDecoder.h"
#include "iostream"

//-------constructor---------
BidiEncoderDecoder::BidiEncoderDecoder():length(0), position(0), opCode(0), messageType(0), decodedMessage(""),bytesVector(),stringVector() {

}

// ------ Destructor------------
BidiEncoderDecoder::~BidiEncoderDecoder() {
    reset();
}



void BidiEncoderDecoder::reset() {
    length = 0;
    position = 0;
    opCode = 0;
    messageType = 0;
    bytesVector.clear();
    stringVector.clear();
}

//----------------Encode------------------

std::string BidiEncoderDecoder::encode(std::string message){
    std::string encodedMessage = "";

    //gets the first word of the message which represents the type of it
    int first = message.find_first_of(" ");
    std::string messageType = message.substr(0,first);
    //gets the opcode of the message to encode
    opCode = getType(messageType);
    message = message.substr(first + 1);
    std::stringstream s1(message);
    std::string segment;


    switch(opCode) {
        case 1: //register
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            while(std::getline(s1,segment,' ')) {
                stringVector.push_back(segment);
            }
            encodedMessage += stringVector[0]; //username
            encodedMessage += '\0';
            encodedMessage += stringVector[1]; //password
            encodedMessage += '\0';
            encodedMessage += stringVector[2]; //birthday
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 2: //login
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            while(std::getline(s1,segment,' ')) {
                stringVector.push_back(segment);
            }
            encodedMessage += stringVector[0];
            encodedMessage += '\0';
            encodedMessage += stringVector[1];
            encodedMessage += '\0';
            encodedMessage += stringVector[2];
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 3: //logout
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 4: //follow
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0]; //probably the opcode
            encodedMessage += bytesVector[1]; //probably the opcode
            while(std::getline(s1,segment,' ')) {
                stringVector.push_back(segment);
            }
            encodedMessage += std::stoi(stringVector[0]); //probably follow/unfollow (0/1)
            encodedMessage += stringVector[1]; //probably username
//            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 5: //post
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += message;
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 6: //PM
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            first = message.find_first_of(' ');
            encodedMessage += message.substr(0, first);
            encodedMessage += '\0';
            message = message.substr(first + 1);
            encodedMessage += message;
            encodedMessage += '\0';
            //todo: add time stamp to the message
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 7: //logStat
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 8: //stat
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += message;
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 9: //notification
        case 10: //ack
        case 11: //error
        case 12: //block
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += message;
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
    }
    return nullptr;
}


//-----------------Decode----------------------

//std::string BidiEncoderDecoder::decodeNextByte(char nextByte) {
//    if(opCode == 0){
//        if(length == 1){
//            bytesVector.push_back(nextByte);
//            length++;
//            opCode = bytesToShort(0);
//            return "";
//        }
//        bytesVector.push_back(nextByte);
//        length++;
//        return "";
//
//    }
//    if((opCode == 9) & (length > 2)) {
//        char type = bytesVector[2];
//        if((nextByte != '\0') | (nextByte != ';')){
//            bytesVector.push_back(nextByte);
//            length++;
//            position++;
//            return "";
//        }
//        else if(nextByte != ';'){
//            std::string s(bytesVector.begin(),bytesVector.end());
//            s = s.substr(3 + position);
//            stringVector.push_back(s);
//            return "";
//        } else {
//            std::string ans = "";
//            ans += "NOTIFICATION " + notificationType(type) + " " + stringVector[0] + " " + stringVector[1];
//            reset();
//            return ans;
//        }
//    }
//    else if(length > 2) {
//        if(messageType == 0)
//            messageType = bytesToShort(2);
//        if (opCode == 11) { //error
//            std::string ans = "ERROR " + std::to_string(messageType);
//            reset();
//            return ans;
//        }
//        if (opCode == 10) { //ack
//            if (messageType == 4) { //follow
//                if ((nextByte != '\0') & (nextByte != ';')) {
//                    bytesVector.push_back(nextByte);
//                    length++;
//                    return "";
//                } else {
//                    std::string allBytes(bytesVector.begin(), bytesVector.end());
//                    std::string ans = "ACK " + std::to_string(messageType) + " " + allBytes.substr(4);
//                    reset();
//                    return ans;
//                }
//            }
//            if(messageType == 7 || messageType == 8) { //logStat, stat
//                if((nextByte != ';') & (nextByte)) {
//                    bytesVector.push_back(nextByte);
//                    length++;
//                    return "";
//                }
//                else  if(nextByte != ';'){ //we are at the '\0' char
//                    short age = bytesToShort(length - 8);
//                    short NumPosts = bytesToShort(length - 6);
//                    short NumFollowers = bytesToShort(length - 4);
//                    short NumFollowing = bytesToShort(length - 2);
//                    std::string user = std::to_string(age) + " "
//                                       + std::to_string(NumPosts) + " "
//                                       + std::to_string(NumFollowers) + " "
//                                       + std::to_string(NumFollowing) + '\n';
//                    stringVector.push_back(user);
//                    return "";
//                }
//                else { //nextByte = ';'
//                    std::string ans = "";
//                    for(int i = 0; i < static_cast<int>(stringVector.size()); i++){
//                            ans += "ACK " + std::to_string(messageType) + " " + stringVector[i];
//                    }
//                    reset();
//                    return ans;
//                }
//            }
//            std::string ans = "";
//            ans = "ACK " + std::to_string(messageType);
//            reset();
//            return ans;
//        }
//
//    } else {
//        bytesVector.push_back(nextByte);
//        length++;
//    }
//    return "";
//}













//std::string BidiEncoderDecoder::notificationType(char c){
//    if (c == '1') {
//        return "Public";
//    }
//    else {
//        return "PM";
//    }
//}







short BidiEncoderDecoder::getType(std::string MessageType) {
    if(MessageType == "REGISTER" || MessageType == "Register" || MessageType == "register")
        return 1;
    if(MessageType == "LOGIN" || MessageType == "Login" || MessageType == "login")
        return 2;
    if(MessageType == "LOGOUT" || MessageType == "Logout" || MessageType == "logout")
        return 3;
    if(MessageType == "FOLLOW" || MessageType == "Follow" || MessageType == "follow")
        return 4;
    if(MessageType == "POST" || MessageType == "Post" || MessageType == "post")
        return 5;
    if(MessageType == "PM" || MessageType == "pm")
        return 6;
    if(MessageType == "LOGSTAT" || MessageType == "Logstat" || MessageType == "logstat")
        return 7;
    if(MessageType == "STAT" || MessageType == "Stat" || MessageType == "stat")
        return 8;
    if(MessageType == "NOTIFICATION" || MessageType == "notification" || MessageType == "Notification")
        return 9;
    if(MessageType == "ACK")
        return 10;
    if(MessageType == "ERROR" || MessageType == "Error" || MessageType == "error")
        return 11;
    if(MessageType == "BLOCK" || MessageType == "Block" || MessageType == "block")
        return 12;
    return 0;
}

void BidiEncoderDecoder::ShortToBytes(short num, std::vector<char> *bytesArr) {
    bytesArr-> push_back((num>>8) & 0xFF);
    bytesArr->push_back(num & 0xFF);
}

short BidiEncoderDecoder::bytesToShort(int startingPosition) {
    std::string s = "";
    s += bytesVector[startingPosition];
    s += bytesVector[startingPosition + 1];
    short result = std::stoi(s);
//    short result = (short)((bytesVector[startingPosition] & 0xff) << 8);
//    result += (short)(bytesVector[startingPosition + 1] & 0xff);
    return result;
}
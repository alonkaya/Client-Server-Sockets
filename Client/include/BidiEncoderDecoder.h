//
// Created by eshm on 30/12/2021.
//
#include <iostream>
#include <string>
#include <sstream>
#include <vector>
#ifndef CLIENT_BIDIENCODERDECODER_H
#define CLIENT_BIDIENCODERDECODER_H

class BidiEncoderDecoder {
public:
    BidiEncoderDecoder();
    ~BidiEncoderDecoder();
    std::string encode(std::string message);
    std::string decodeNextByte(char nextByte);
    void reset();
    short getType(std::string MessageType);
    short bytesToShort (int startingPosition);
    void ShortToBytes(short num, std::vector<char> *bytesArr);
    std::string notificationType(char c);

private:
    int length;
    int position;
    short opCode;
    short messageType;
    std::string decodedMessage;
    std::vector<char> bytesVector;
    std::vector<std::string> stringVector;

};

#endif //CLIENT_BIDIENCODERDECODER_H

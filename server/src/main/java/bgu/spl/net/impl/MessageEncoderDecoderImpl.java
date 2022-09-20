package bgu.spl.net.impl;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] opcodeBytes = new byte[2];
    private byte[] messageBytes = new byte[1<<10];
    private int counter = 0;
    private Short opcode = 0;
    private Message message;

    public Message decodeNextByte(byte nextByte) {
        if(opcode == 0) {//Haven't initialized opcode yet
            if(message != null) message = null;
            opcodeBytes[counter] = nextByte;
            counter++;
            if (counter == 2) {
                opcode = bytesToShort(opcodeBytes);
                resetBytes();
            }
            return null;
        }
        ///////////////////////////////////////Register///////////////////////////////////////
        if(opcode == 1 && nextByte != ';') {
            if (message == null) message = new RegisterMessage();

            if(nextByte != '\0'){
                pushByte(nextByte);//Insert next byte in place if there is available space (if not makes a copy array with double the size):
            }
            else {//If nextByte = '\0' then we are ready to convert an object from bytes to string
                if (((RegisterMessage)message).getUsername() == null) { //If we haven't decoded username yet, convert bytes to string
                    String username = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((RegisterMessage) message).setUsername(username); //set message username
                } else if (((RegisterMessage)message).getPassword() == null) {//If we haven't decoded password yet, convert bytes to string
                    String password = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((RegisterMessage) message).setPassword(password); //set message password
                }
                else if (((RegisterMessage)message).getBirthday() == null) {//If we haven't decoded birthday yet, convert bytes to string
                    String birthday = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((RegisterMessage) message).setBirthday(birthday); //set message birthday
                }
                resetBytes();
            }
            return null; //so that the handler would know to continue decoding the next byte
        }
        ///////////////////////////////////////Login///////////////////////////////////////
        else if(opcode == 2 && nextByte != ';') {
            if(message == null) message = new LoginMessage();

            //we've reached the captcha byte:
            if (((LoginMessage)message).getUsername() != null && ((LoginMessage)message).getPassword() != null && ((LoginMessage) message).getCaptcha() == -1){
                ((LoginMessage)message).setCaptcha(nextByte);
            }
            else if(nextByte != '\0'){
                pushByte(nextByte);//Insert next byte in place if there is available space (if not makes a copy array with double the size):
            }
            else{//If nextByte = '\0' then we are ready to convert an object from bytes to string
                if (((LoginMessage)message).getUsername() == null) { //If we haven't decoded username yet, convert bytes to string
                    String username = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((LoginMessage) message).setUsername(username); //set message username
                } else if (((LoginMessage)message).getPassword() == null) {//If we haven't decoded password yet, convert bytes to string
                    String password = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((LoginMessage) message).setPassword(password); //set message password
                }
                resetBytes();
            }
            return null;
        }
        ///////////////////////////////////////Logout///////////////////////////////////////
        else if(opcode == 3){
            message = new LogoutMessage();
        }
        ///////////////////////////////////////Follow///////////////////////////////////////
        else if(opcode == 4) {
            if (message == null) message = new FollowMessage();
            if (nextByte == '0')return null;
            //If nextByte is the captcha byte:
            if (((FollowMessage)message).getFollow() == -1) {
                ((FollowMessage)message).setFollow(nextByte);
                return null;
            }

            //If next byte is a username string byte
            if (nextByte != ';'){
                pushByte(nextByte);//Insert next byte in place if there is available space (if not makes a copy array with double the size):
                return null;
            }

            //nextByte is ';'
            String username = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
            ((FollowMessage) message).setUsername(username); //set message username

        }
        ///////////////////////////////////////Post///////////////////////////////////////
        else if(opcode == 5 && nextByte != ';') {
            if (message == null) message = new PostMessage();

            if(nextByte != '\0'){
                pushByte(nextByte);//Insert next byte in place if there is available space (if not makes a copy array with double the size):
            }
            else{ //If nextByte = '\0' then we are ready to convert from bytes to string
                String content = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                ((PostMessage) message).setContent(content); //set message content
            }
            return null;
        }
        ///////////////////////////////////////PM///////////////////////////////////////
        else if(opcode == 6 && nextByte != ';'){
            if (message == null) message = new PMMessage();

            if(nextByte != '\0'){
                pushByte(nextByte);//Insert next byte in place if there is available space (if not makes a copy array with double the size):
            }
            else {//If nextByte = '\0' then we are ready to convert an object from bytes to string
                if (((PMMessage)message).getUsername() == null) { //If we haven't decoded username yet, convert bytes to string
                    String username = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((PMMessage) message).setUsername(username); //set message username
                } else if (((PMMessage)message).getContent() == null) {//If we haven't decoded content yet, convert bytes to string
                    String content = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((PMMessage) message).setContent(content); //set message content
                }
                else if (((PMMessage)message).getTime() == null) {//If we haven't decoded time yet, convert bytes to string
                    String time = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((PMMessage) message).setTime(time); //set message time
                }
                resetBytes();
            }
            return null; //so that the handler would know to continue decoding the next byte
        }
        ///////////////////////////////////////LogStat///////////////////////////////////////
        else if(opcode == 7){
            message = new LogStatMessage();
        }
        ///////////////////////////////////////Stat///////////////////////////////////////
        else if (opcode == 8 && nextByte != ';') {
            if (message == null) message = new StatMessage();

            //We got all bytes: convert username from bytes to String. Then add the username to the users list:
            if(nextByte == '\0' || nextByte == '|'){
                if (counter > 0) { //Do this just in case the last username doesn't end with an '|'
                    String username = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                    ((StatMessage)message).addUsername(username);
                    resetBytes();
                }
            }
            else //if we're still not done getting all if this username's bytes
                pushByte(nextByte);

            return null;

        }
//        ///////////////////////////////////////Notification///////////////////////////////////////
//        else if(opcode == 9 && nextByte != ';'){
//            if (message == null) message = new NotificationMessage();
//
//            //If nextByte is the notification type byte:
//            if (((NotificationMessage)message).getType() == -1) {
//                ((NotificationMessage)message).setType(nextByte);
//            }
//
//            else if(nextByte != '\0'){
//                pushByte(nextByte);
//            }
//            else {//If nextByte = '\0' then we are ready to convert an object from bytes to string
//                if (((NotificationMessage) message).getPostingUser() == null) { //If we haven't decoded posting user yet, convert bytes to string
//                    String username = new String(messageBytes, StandardCharsets.UTF_8);
//                    ((NotificationMessage) message).setPostingUser(username); //set message posting user
//                } else if (((NotificationMessage) message).getContent() == null) {//If we haven't decoded content yet, convert bytes to string
//                    String content = new String(messageBytes, StandardCharsets.UTF_8);
//                    ((NotificationMessage) message).setContent(content); //set message content
//                }
//                resetBytes();
//            }
//            return null;
//        }
//        ///////////////////////////////////////Ack///////////////////////////////////////
//        else if(opcode == 10 && nextByte != ';') {
//            if (message == null) message = new AckMessage();
//
//            short messageOpcode = ((AckMessage) message).getMessageOpcode();
//            if (messageOpcode == -1) {//If we haven't initialized messageOpcode yet:
//                if (counter == 2) {
//                    ((AckMessage) message).setMessageOpcode(bytesToShort(messageBytes));
//                    resetBytes();
//                } else pushByte(nextByte);
//            } else if (messageOpcode == 4) {//Follow
//                if (nextByte != '\0') {
//                    pushByte(nextByte);
//                } //Aggregate bytes until reaching the 0 byte
//                else { //When reaching \0, decode bytes to String and update the username to this string
//                    String username = new String(messageBytes, StandardCharsets.UTF_8);
//                    ((AckMessage) message).setUsername(username);
//                    resetBytes();
//                }
//            } else if (messageOpcode == 7 || messageOpcode == 8) {//LogStat or Stat
//                //Get and update all 4 parameters: age, numPosts, numFollowers, numFollowing:
//                if (((AckMessage) message).getAge() == -1) {
//                    if (counter == 2) {//When got both bytes, decode bytes to short and update the age to this short
//                        ((AckMessage) message).setAge(bytesToShort(messageBytes));
//                        resetBytes();
//                    } else pushByte(nextByte); //Aggregate bytes until getting both bytes for short
//                } else if (((AckMessage) message).getNumPosts() == -1) {
//                    if (counter == 2) {//When got both bytes, decode bytes to short and update the numPosts to this short
//                        ((AckMessage) message).setNumPosts(bytesToShort(messageBytes));
//                        resetBytes();
//                    } else pushByte(nextByte); //Aggregate bytes until getting both bytes for short
//                } else if (((AckMessage) message).getNumFollowers() == -1) {
//                    if (counter == 2) {//When got both bytes, decode bytes to short and update the numFollowers to this short
//                        ((AckMessage) message).setNumFollowers(bytesToShort(messageBytes));
//                        resetBytes();
//                    } else pushByte(nextByte); //Aggregate bytes until getting both bytes for short
//                } else if (((AckMessage) message).getNumFollowing() == -1) {
//                    if (counter == 2) {//When got both bytes, decode bytes to short and update the numFollowing to this short
//                        ((AckMessage) message).setNumFollowing(bytesToShort(messageBytes));
//                        resetBytes();
//                    } else pushByte(nextByte); //Aggregate bytes until getting both bytes for short
//                }
//            }
//        }
//            return null;
//
//        } ///////////////////////////////////////Error///////////////////////////////////////
//        else if(opcode == 11 && nextByte != ';') {
//            if (message == null) message = new ErrorMessage();
//
//            short messageOpcode = ((ErrorMessage)message).getMessageOpcode();
//            if(messageOpcode == -1) {//If we haven't initialized messageOpcode yet:
//                if (counter == 2){
//                    ((ErrorMessage)message).setMessageOpcode(bytesToShort(messageBytes));
//                    resetBytes();
//                }
//                else pushByte(nextByte);
//            }
//            return null;
//        }//////////////////////////////////////////////////Block///////////////////////////////////////
        else if(opcode == 12 && nextByte != ';') {
            if (message == null) message = new BlockMessage();

            if(nextByte != '\0'){
                pushByte(nextByte);//Aggregate bytes until reaching the 0 byte
            }
            else{ //If nextByte = '\0' then we are ready to convert from bytes to string
                String username = new String(messageBytes, 0, counter, StandardCharsets.UTF_8);
                ((BlockMessage) message).setUsername(username); //set message username
            }
            return null;
        }



        //If we got here it means that nextByte is ';', which means that message is decoded and ready to be sent.
        //Therefore, restart all fields(except for the message field which will be changed to null when next message will be sent at the beginning)
        counter = 0;
        messageBytes = new byte[1<<10];
        opcode = 0;
        opcodeBytes = new byte[2];
        return message;
    }






    public byte[] encode(Message message) {
        byte[] encodedMessage;
        short opcode = message.getOpcode();
        String messageString = "";

        ///////////////////////////////////////Notification///////////////////////////////////////
        if(opcode == 9){
            messageString += "NOTIFICATION ";
            int notificationType = ((NotificationMessage)message).getType();
            String type;
            if (notificationType == 0) type = "PM ";
            else type = "PUBLIC ";
            String postingUser = ((NotificationMessage)message).getPostingUser().getUsername();
            String content = ((NotificationMessage)message).getContent();
            messageString += type + postingUser + " " + content;
        }
        ///////////////////////////////////////Ack///////////////////////////////////////
        else if(opcode == 10){
            short messageOpcode = ((AckMessage)message).getMessageOpcode();
            messageString += "";
            if (messageOpcode == 4) {//Follow
                messageString +=  "ACK " + String.valueOf(messageOpcode) + ' ' + ((AckMessage)message).getUsername();
            }
            else if(messageOpcode == 7 || messageOpcode == 8) {//Stat/Logstat
                ConcurrentLinkedQueue<short[]> stats = ((AckMessage) message).getStats();
                for (short[] stat : stats) {
                    messageString += "ACK " + String.valueOf(messageOpcode) + ' ';
                    for (short data : stat)
                        messageString += String.valueOf(data) + " ";
                    messageString += '\n';
                }
            }
            else messageString += "ACK " + String.valueOf(messageOpcode); //other kind of ACK
        }
        ///////////////////////////////////////Error///////////////////////////////////////
        else if(opcode == 11){
            messageString += "ERROR ";
            short messageOpcode = ((ErrorMessage)message).getMessageOpcode();
            messageString += String.valueOf(messageOpcode);
        }

        messageString += ';';
        encodedMessage = messageString.getBytes(StandardCharsets.UTF_8);
        return encodedMessage;
    }




    private void resetBytes(){
        counter = 0;
        messageBytes = new byte[1<<10];
    }

    private void pushByte(byte nextByte){
        if(counter >= messageBytes.length) messageBytes = Arrays.copyOf(messageBytes, counter*2);
        messageBytes[counter] = nextByte;
        counter++;
    }

    public byte[] shortToBytes(short num) //encode
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    public short bytesToShort(byte[] byteArr) //decode
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
}

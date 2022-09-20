package bgu.spl.net.messages;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AckMessage implements Message{
    private final short opcode = 10;
    private short messageOpcode = -1;
    ////For Follow message:////
    private String username;
    ////For stat/logStat message:////
    private ConcurrentLinkedQueue<short[]> stats;


    public AckMessage(short messageOpcode){
        this.messageOpcode = messageOpcode;
        stats = new ConcurrentLinkedQueue<>();
    }

    public short getOpcode() {return opcode;}

    public short getMessageOpcode() {return messageOpcode;}
    public void setMessageOpcode(short messageOpcode) {this.messageOpcode = messageOpcode;}

    ////For Follow message:////
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    //For stat/logStat message:////
    public void addStat(short age, short numPosts, short numFollowers, short numFollowing){
        short[] stat = new short[4];
        stat[0] = age;
        stat[1] = numPosts;
        stat[2] = numFollowers;
        stat[3] = numFollowing;
        stats.add(stat);
    }

    public ConcurrentLinkedQueue<short[]> getStats() {return stats;}

    //    public short getAge() {return age;}
//    public void setAge(short age) {this.age = age;}
//
//    public short getNumPosts() {return numPosts;}
//    public void setNumPosts(short numPosts) {this.numPosts = numPosts;}
//
//    public short getNumFollowers() {return numFollowers;}
//    public void setNumFollowers(short numFollowers) {this.numFollowers = numFollowers;}
//
//    public short getNumFollowing() {return numFollowing;}
//    public void setNumFollowing(short numFollowing) {this.numFollowing = numFollowing;}
}

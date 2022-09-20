package bgu.spl.net.messages;

import bgu.spl.net.srv.Client;

public class NotificationMessage implements Message{
    private final short opcode = 9;
    private int type = -1;
    private Client postingUser;
    private String content;

    public NotificationMessage(int type, Client postingUser, String content){
        this.postingUser = postingUser;
        this.type = type;
        this.content = content;
    }

    public short getOpcode() {return opcode;}

    public int getType() {return type;}
    public String getContent() {return content;}
    public Client getPostingUser() {return postingUser;}

}

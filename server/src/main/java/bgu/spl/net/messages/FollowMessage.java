package bgu.spl.net.messages;

public class FollowMessage implements Message{
    private final short opcode = 4;
    private int follow = -1;
    private String username;

    @Override
    public short getOpcode() {return opcode;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public int getFollow() {return follow;}
    public void setFollow(int a) {this.follow = a;}
}

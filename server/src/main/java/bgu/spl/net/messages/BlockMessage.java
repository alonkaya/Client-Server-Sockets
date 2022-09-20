package bgu.spl.net.messages;

public class BlockMessage implements Message{
    private final short opcode = 12;
    private String username;

    @Override
    public short getOpcode() {return opcode;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

}

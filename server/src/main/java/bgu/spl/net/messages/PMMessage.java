package bgu.spl.net.messages;

public class PMMessage implements Message{
    private final short opcode = 6;
    private String username;
    private String content;
    private String time;

    @Override
    public short getOpcode() {return opcode;}

    public String getContent() {return content;}
    public String getUsername() {return username;}
    public String getTime() {return time;}

    public void setContent(String content) {this.content = content;}
    public void setTime(String time) {this.time = time;}
    public void setUsername(String username) {this.username = username;}
}

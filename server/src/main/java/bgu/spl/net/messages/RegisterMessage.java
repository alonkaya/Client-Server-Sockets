package bgu.spl.net.messages;

public class RegisterMessage implements Message{
    private final short opcode = 1;
    private String username;
    private String password;
    private String birthday;

    @Override
    public short getOpcode() {return opcode;}

    public String getUsername(){return username;}
    public String getPassword() {return password;}
    public String getBirthday() {return birthday;}

    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setBirthday(String birthday) {this.birthday = birthday;}
}

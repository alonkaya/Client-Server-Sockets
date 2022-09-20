package bgu.spl.net.messages;

public class ErrorMessage implements Message{
    private final short opcode = 11;
    private short messageOpcode = -1;

    public ErrorMessage(short messageOpcode){
        this.messageOpcode = messageOpcode;
    }
    public short getOpcode() {return opcode;}

    public short getMessageOpcode() {return messageOpcode;}
//    public void setMessageOpcode(short messageOpcode) {this.messageOpcode = messageOpcode;}

}

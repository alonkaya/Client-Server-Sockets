package bgu.spl.net.messages;

public class LogStatMessage implements Message{
    private final short opcode = 7;

    @Override
    public short getOpcode() {return opcode;}
}

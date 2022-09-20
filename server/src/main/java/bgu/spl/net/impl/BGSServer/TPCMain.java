package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.ConnectionsImpl;
import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.messages.Message;
import bgu.spl.net.srv.ThreadPerClientImpl;

public class TPCMain {

    public static void main(String[] args){
        ConnectionsImpl<Message> connections = new ConnectionsImpl<>();
        int port = 7777;

        ThreadPerClientImpl<Message> TPC = new ThreadPerClientImpl<Message>(connections,
                port,
                ()-> {return new BidiMessagingProtocolImpl();},
                ()->{ return new MessageEncoderDecoderImpl();});

        TPC.serve();


    }


}
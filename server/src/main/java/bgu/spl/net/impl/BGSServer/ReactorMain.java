package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.ConnectionsImpl;
import bgu.spl.net.impl.MessageEncoderDecoderImpl;
import bgu.spl.net.messages.Message;
import bgu.spl.net.srv.Reactor;

public class ReactorMain {
    public static void main (String[] args){
        ConnectionsImpl<Message> connections = new ConnectionsImpl<>();

        Reactor<Message> reactor = new Reactor<>(connections, Integer.decode(args[1]).intValue(),Integer.decode(args[0]).intValue(),
                ()-> {return new BidiMessagingProtocolImpl();}, ()->{ return new MessageEncoderDecoderImpl();});

        reactor.serve();

    }
}

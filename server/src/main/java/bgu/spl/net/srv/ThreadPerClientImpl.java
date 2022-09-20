package bgu.spl.net.srv;


import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.impl.ConnectionsImpl;

import java.util.function.Supplier;

public class ThreadPerClientImpl<T> extends BaseServer<T> {
    public ThreadPerClientImpl(ConnectionsImpl<T> connections, int port, Supplier<BidiMessagingProtocol<T>> protocolFactory,
                               Supplier<MessageEncoderDecoder<T>> encdecFactory) {
        super(connections, port, protocolFactory, encdecFactory);
    }

    protected void execute(BlockingConnectionHandler<T> handler){
        new Thread(handler).start();
    }
}

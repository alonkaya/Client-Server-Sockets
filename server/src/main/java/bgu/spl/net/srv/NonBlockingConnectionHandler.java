package bgu.spl.net.srv;



import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.impl.ConnectionsImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;
    private final Reactor reactor;
    //////////////////////////////////////////////////
    private ConnectionsImpl<T> connections;
    private Client client;
    private int id;


    public NonBlockingConnectionHandler(
            ConnectionsImpl<T> connections,
            MessageEncoderDecoder<T> reader,
            BidiMessagingProtocol<T> protocol,
            SocketChannel chan,
            Reactor reactor) {
        this.chan = chan;
        this.encdec = reader;
        this.protocol = protocol;
        this.reactor = reactor;
        this.connections = connections;
        this.id = connections.getAndIncrementID();
    }



    ///////////////////////////////////////-----NEW-----/////////////////////////////////
    @Override
    public void send(T msg) {
        writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));
        reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    public void setClient(Client client){
        this.client = client;
        this.id = client.getId();
    }
    public Client getClient() {return client;}

    /////////////////////////////////////////////////////////////////////////////////////

    public Runnable continueRead() {
        ByteBuffer buf = leaseBuffer();

        boolean success = false;
        try {
            success = chan.read(buf) != -1; //reads from channel to buffer!!
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (success) {
            buf.flip();
            return () -> {
                try {
                    protocol.start(id, connections); //sets protocol id to be the same as this id
                    connections.connect(id, this); //Insert id and connection handler to map

                    while (buf.hasRemaining()) {
                        T nextMessage = encdec.decodeNextByte(buf.get());
                        if (nextMessage != null) {
                            protocol.process(nextMessage);
                        }
                    }
                } finally {
                    releaseBuffer(buf);
                }
            };

        } else {
            releaseBuffer(buf);
            close();
            return null;
        }

    }

    public void close() {
        try {
            chan.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isClosed() {
        return !chan.isOpen();
    }

    public void continueWrite() {  //writes from write queue buffer to channel!!
        while (!writeQueue.isEmpty()) {
            try {
                ByteBuffer top = writeQueue.peek(); //notice that top is a ByteBuffer!
                chan.write(top);//write everything from the buffer 'top' to the channel
                if (top.hasRemaining()) { //if position < limit: meaning if the channel hadn't written everything he got to the buffer (because the buffer is full at the moment)
                    return;
                } else {
                    writeQueue.remove();//removes the head queue which is the byte buffer 'top' (got the writing now we can move to next one)
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                close();
            }
        }

        if (writeQueue.isEmpty()) {//check again because of multi threading and concurrency problems
            if (protocol.shouldTerminate()) close();
            else reactor.updateInterestedOps(chan, SelectionKey.OP_READ);//All worked out, and we don't have any more writing to get from channel
        }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }

        buff.clear();
        return buff;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }

}

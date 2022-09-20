package bgu.spl.net.impl;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.messages.NotificationMessage;
import bgu.spl.net.messages.PMMessage;
import bgu.spl.net.messages.PostMessage;
import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionHandlerMap;
    private ConcurrentLinkedQueue<Client> clients;
    private HashMap<Integer, ConcurrentLinkedQueue<PostMessage>> postsMap; //holds all posts this client had posted
    private HashMap<Integer, ConcurrentLinkedQueue<PMMessage>> PMMessageMap; //for each client hold PMs he sent
    private HashMap<Integer, ConcurrentLinkedQueue<NotificationMessage>> waitingNotifications; //notifications waiting to be sent

    private String[] wordsToFilter = {"Luke", "I", "am", "your", "father"};
    private AtomicInteger id;

    //todo: make singelton
    public ConnectionsImpl(){
        connectionHandlerMap = new ConcurrentHashMap<>();
        id = new AtomicInteger();
        postsMap = new HashMap<>();
        PMMessageMap = new HashMap<>();
        waitingNotifications = new HashMap<>();
        clients = new ConcurrentLinkedQueue<>();
    }

    public boolean send(int connectionId, T msg){
        ConnectionHandler<T> clientCH = connectionHandlerMap.get(connectionId); //get the client's key
        if(clientCH != null) { //If client exists send the message and return true
            clientCH.send(msg);
            return true;
        }
        return false; //If client doesn't exist return false
    }

    public void broadcast(T msg){
        for(Client client : clients){
            send(client.getId(), msg);
        }
    }

    public void disconnect(int connectionId, T message){
        send(connectionId, message);
        connectionHandlerMap.remove(connectionId);

    }
    public void connect(int connectionId, ConnectionHandler<T> handler){connectionHandlerMap.put(connectionId, handler);}


    public ConnectionHandler<T> getHandler(int id){return connectionHandlerMap.get(id);}

    public int getAndIncrementID() {
        int id = this.id.getAndIncrement();
        return id;
    }

    public Client getClient(String username) {
        for(Client client : clients){
            if ((client.getUsername()).equals(username))
                return client;
        }
        return null;
    }
    public Client getClient(int id){return connectionHandlerMap.get(id).getClient();}

    public ConcurrentLinkedQueue<Client> getClients() {return clients;}

    //Until setting the client, the connection handler's client field is null
    public void setClient(int id, Client client){ //sets client for the relevant connection handler, and adds him to clients list

        connectionHandlerMap.get(id).setClient(client);//first set handler's id and client.

        // now, remove and save the connection handler from where it was in the map, so we can place it in the position with this client.
        connectionHandlerMap.put(client.getId(), connectionHandlerMap.remove(id));
    }
    public void addClient(Client client){clients.add(client);}



    public void addPost(int id, PostMessage message, Client client){
        postsMap.get(id).add(message);
        client.incrementNumOfPosts();
    }
    public void addPM(int sendingID, PMMessage message){PMMessageMap.get(sendingID).add(message);}

    public ConcurrentLinkedQueue<NotificationMessage> getWaitingNotifications(int id){return waitingNotifications.get(id);}
    public void addWaitingNotification(int id, NotificationMessage message){waitingNotifications.get(id).add(message);}
    public void setMaps(int id){
        waitingNotifications.put(id, new ConcurrentLinkedQueue<>());
        postsMap.put(id, new ConcurrentLinkedQueue<>());
        PMMessageMap.put(id, new ConcurrentLinkedQueue<>());
    }


    public String filter(String content){
        String[] contentWords = content.split(" "); //string to array of words
        String filteredContent = "";

        for(String word : contentWords){//go through each word in content
            boolean filter = false;
            for(String filteredWord : wordsToFilter) //check if word is in filtered list
                if (word.equals(filteredWord)) {
                    filter = true;
                    break;
                }
            if(filter)
                filteredContent = filteredContent + "<filter> ";
            else
                filteredContent = filteredContent + word + " ";
        }
        return filteredContent;
    }

}
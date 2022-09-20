package bgu.spl.net.srv;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private final int id;
    private String username;
    private String password;
    private short age;
    //there is no real synchronization here for example if a user posts something and then a diffrent user follows him' he might get the
    //post also, even though he gave the following command after the post message command. relevant for al three fields
    private ConcurrentLinkedQueue<Client> following;
    private ConcurrentLinkedQueue<Client> followers;
    private ConcurrentLinkedQueue<Client> blocked;
    private boolean loggedIn;
    private AtomicInteger numOfPosts;


    public Client(int id, String username, String password, short age){
        this.id = id;
        this.username = username;
        this.password = password;
        loggedIn = false;
        this.age = age;
        numOfPosts = new AtomicInteger();
        blocked = new ConcurrentLinkedQueue<>();
        following = new ConcurrentLinkedQueue<>();
        followers = new ConcurrentLinkedQueue<>();
    }

    public int getId() {return id;}

    public short getAge() {return age;}

    public short getNumOfPosts() {return (short) numOfPosts.get();}
    public void incrementNumOfPosts(){numOfPosts.getAndIncrement();}

    public ConcurrentLinkedQueue<Client> getFollowers() {return followers;}
    public ConcurrentLinkedQueue<Client> getFollowing() {return following;}

    public String getPassword() {return password;}
    public String getUsername() {return username;}

    public boolean isLoggedIn() {return loggedIn;}
    public void login(){loggedIn = true;}
    public void logOut(){loggedIn = false;}

    public boolean isFollowing(String username){
        for(Client client : following)
            if(client.username.equals(username)) return true;
        return false;
    }
    public void addFollower(Client client){followers.add(client);}
    public void addFollowing(Client client){following.add(client);}

    public void removeFollower(Client client){followers.remove(client);}
    public void removeFollowing(Client client){following.remove(client);}

    public short getNumFollowers(){return (short) followers.size();}
    public short getNumFollowing(){return (short) following.size();}

    public void addBlockedUser(Client client){blocked.add(client);}
    public boolean isUserBlocked(Client client){return blocked.contains(client);}
}

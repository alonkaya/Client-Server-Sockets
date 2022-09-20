A Client-Server program. The server was written using Java and the client with C++. 
Created a unique binary protocol for client and server communication.
Experience with sockets, serialization, and understanding the differences between the
Thread Per Client (TPC) pattern versus the Reactor pattern (using thread pool).

1.1) How to run our code:

- Open a new terminal for the server and type the next lines:

	mvn clean
	mvn compile
	//for TPC//:
	mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"
	//for REACTOR//:
	mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 5"
	
	// Note that 5 represents the number of threads, you can change it as you wish


- Open a new terminal for each client you want to run and type the next lines:

	make
	./bin/BGSclient 127.0.0.1 7777


1.2) Example messages:

Register -> REGISTER A 123 01-01-2000
Login -> LOGIN A 123 1
Logout -> LOGOUT
Follow -> FOLLOW 0 A
Unfollow -> FOLLOW 1 A
Post -> POST Hello World
PM -> PM A Hi Mr. A, how you doin'?
Logstat -> LOGSTAT
Stat -> STAT A|B
Block -> BLOCK A

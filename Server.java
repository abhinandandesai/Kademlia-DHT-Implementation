
import java.net.InetAddress;
import java.rmi.*;
import java.rmi.RemoteException;
import java.lang.*;
import java.rmi.registry.LocateRegistry;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;

public class Server {



    private int port = 5000;
    private String hostName = "rmiserver";
    private kademlia.Server kadServer = null;

    public Server(String[] args) {
        try {
            if (hostName.length() == 0)
                hostName = "rmiserver";


            InetAddress address1 = InetAddress.getLocalHost();

            kadServer = new kademlia.Server(port, new Node(new Id(), address1, port));
            Naming.rebind("//localhost:5000/KademliaServer", kadServer);
            System.out.println("Rebind complete.");
            System.out.println("Kademlia Server bound in registry at " + hostName + ":" + port);
            kadServer.startListener();
        } catch (Exception e) {
            System.out.println( "kadServer error");
            System.exit(1);
        }
    }


    public static void main(String[] args) throws RemoteException {
        System.setSecurityManager(new SecurityManager());
        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(5000);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            LocateRegistry.getRegistry();
            System.err.println("java RMI registry already exists.");
        }

        Server server = new Server(args);
    }
}
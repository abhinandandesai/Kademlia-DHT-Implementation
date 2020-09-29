package main.java.edu.rit.cs;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;


public class Client {

    private String hostName = "server";
    private Node node = null;

    public Client(String[] args){
        if (args.length > 1){
            System.out.println("Invalid number of arguments");
            System.exit(1);
        }
        try {
                hostName = "server";
                int port = Integer.parseInt(args[1]);

                node = new Node(new Id(), InetAddress.getLocalHost(),port);
            } catch (IOException ioException) {
            System.out.println("Cannot connect to Server. Try again");
            System.exit(1);
            ioException.printStackTrace();
        }
    }


    public static void main(String[] args) throws RemoteException {
        System.setSecurityManager(new SecurityManager());

        Client client = new Client(args);

    }
}
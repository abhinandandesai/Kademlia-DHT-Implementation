package main.java.edu.rit.cs.nodeOperations;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;

import java.io.IOException;

import java.util.NoSuchElementException;


/**
 * The main Kademlia Node on the network, this node manages everything for this local system.
 */
public class KademliaNode
{


    private final String ownerId;


    private final transient Node localNode;
    private final transient Server server;
    private final transient DHT dht;
    private transient RoutingTable routingTable;
    private final int udpPort;
    private final StoredContentManager contentManager;



    public KademliaNode(String ownerId, Node localNode, int Port, DHT dht, RoutingTable routingTable) throws IOException
    {
        this.ownerId = ownerId;
        this.udpPort = Port;
        this.localNode = localNode;
        this.dht = dht;
        this.routingTable = routingTable;

        this.server = new Server(udpPort, this.localNode);
        this.contentManager = null;
    }




    public Node getNode()
    {
        return this.localNode;
    }

    public Server getServer()
    {
        return this.server;
    }


    public DHT getDHT()
    {
        return this.dht;
    }
    



    public synchronized final void connect(Node n) throws Exception
    {
        long startTime = System.nanoTime();
        Connect op = new Connect(this.server, this, n);
        op.execute();
        long endTime = System.nanoTime();
    }

    public int put(StorageEntryMetadata content)
    {
        return contentManager.put(new StorageEntry(content));
    }





    public void putLocally(StorageEntryMetadata content) throws IOException
    {
        this.dht.store(new StorageEntry(content));
    }


    public StorageEntry get(GetParameter param) throws NoSuchElementException, IOException, Exception
    {
        if (this.dht.contains(param))
        {
            return this.dht.get(param);
        }

    }



    public String getOwnerId()
    {
        return this.ownerId;
    }


    public int getPort()
    {
        return this.udpPort;
    }


    public void shutdown(final boolean saveState) throws IOException
    {

        this.server.shutdown();

    }


    public RoutingTable getRoutingTable()
    {
        return this.routingTable;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder("\n\nKademlia State: ");
        sb.append("Owner ID: ");
        sb.append(this.ownerId);


        sb.append("\n");
        sb.append("Local Node: ");
        sb.append(this.localNode);


        sb.append("\n");
        sb.append("Routing Table: ");
        sb.append(this.getRoutingTable());

        sb.append("\n");
        sb.append("kademlia.DHT: ");
        sb.append(this.dht);

        sb.append("\n\n");

        return sb.toString();
    }
}

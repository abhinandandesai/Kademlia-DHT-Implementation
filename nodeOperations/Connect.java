package main.java.edu.rit.cs.nodeOperations;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;

import java.io.IOException;


public class Connect
{



    private final Server server;
    private final KademliaNode localNode;
    private final Node connectNode;

    private boolean error;
    private int attempts;

    /**
     * @param server    The message server used to send/receive messages
     * @param local     The local node
     * @param bootstrap Node to use to bootstrap the local node onto the network
     */
    public Connect(Server server, KademliaNode local, Node bootstrap)
    {
        this.server = server;
        this.localNode = local;
        this.connectNode = bootstrap;
    }


    public synchronized void execute() throws IOException
    {
        try
        {
            // Contact the cpnnecting node
            this.error = true;
            this.attempts = 0;
            Message m = new ConnectMessage(this.localNode.getNode());

            server.sendMessage(this.connectNode, m, this);


            int totalTimeWaited = 0;
            int timeInterval = 50;
            while (totalTimeWaited < 300)
            {
                if (error)
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }
            if (error)
            {
                throw new Exception("Connect: Node did not respond: " + connectNode);
            }

            // Perform lookup for our own ID to get nodes close to us
            NodeLookupOperation lookup = new NodeLookupOperation(this.server, this.localNode, this.localNode.getNode().getNodeId());
            lookup.execute();

        }
        catch (InterruptedException e)
        {
            System.err.println("Connect operation was interrupted. ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
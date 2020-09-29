package main.java.edu.rit.cs.nodeOperations;

import java.io.IOException;
import java.util.List;



import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;


public class Receiver
{

    private final Server server;
    private final KademliaNode localNode;


    public Receiver(Server server, KademliaNode local)
    {
        this.server = server;
        this.localNode = local;

    }

    /**
     * Handle receiving a LookupMessage
     * Find the set of K nodes closest to the lookup ID and return them
     *
     * @param comm
     *
     * @throws java.io.IOException
     */

    public void receive(Message incoming, int comm) throws IOException
    {
        LookupMessage msg = (LookupMessage) incoming;

        Node origin = msg.getOrigin();

        // Update the local space by inserting the origin node
        this.localNode.getRoutingTable().insert(origin);

        // Find nodes closest to the LookupId
        List<Node> nodes = this.localNode.getRoutingTable().findClosest(msg.getLookupId());

        // Respond to the LookupMessage
        Message reply = new Message(this.localNode.getNode(), nodes);

        if (this.server.isRunning())
        {
            this.server.reply(origin, reply, comm);
        }
    }

}
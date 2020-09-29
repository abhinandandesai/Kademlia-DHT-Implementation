package main.java.edu.rit.cs.nodeOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;
/**
 * Finds the K closest nodes to a specified identifier
 * The algorithm terminates when it has gotten responses from the K closest nodes it has seen.
 * Nodes that fail to respond are removed from consideration
 *
 */
public class NodeLookupOperation
{

    /* Constants */
    private static final String UNASKED = "UnAsked";
    private static final String AWAITING = "Awaiting";
    private static final String ASKED = "Asked";
    private static final String FAILED = "Failed";

    private final Server server;
    private final KademliaNode localNode;

    private final Message lookupMessage;        // Message sent to each peer
    private final Map<Node, String> nodes;


    private final Comparator comparator;


    /**
     * @param server    Server used for communication
     * @param localNode The local node making the communication
     * @param lookupId  The ID for which to find nodes close to
     */
    public NodeLookupOperation(Server server, KademliaNode localNode, Id lookupId)
    {
        this.server = server;
        this.localNode = localNode;


        this.lookupMessage = new LookupMessage(localNode.getNode(), lookupId);

        // Comparator link to calculate xor distance
        this.comparator = new KeyComparator(lookupId);
        // Tree Map to store closest nodes
        this.nodes = new TreeMap(this.comparator);
    }

    /**
     * @throws Exception
     */
    public synchronized void execute() throws Exception
    {
        try
        {
            // Set the local node as already asked
            nodes.put(this.localNode.getNode(), ASKED);


            this.addNodes(this.localNode.getRoutingTable().getAllNodes());


            int totalTimeWaited = 0;
            int timeInterval = 10;     // We re-check every n milliseconds
            while (totalTimeWaited < 50)
            {
                if (!this.askNodesorFinish())
                {
                    wait(timeInterval);
                    totalTimeWaited += timeInterval;
                }
                else
                {
                    break;
                }
            }

            // Now after we've finished update routing table according to online and offline nodes
            this.localNode.getRoutingTable().setUnresponsiveContacts(this.getFailedNodes());

        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }



    private boolean askNodesorFinish() throws IOException
    {


        // Get unqueried nodes among the K closest seen that have not FAILED
        List<Node> unasked = this.closestNodesNotFailed(UNASKED);

        if (unasked.isEmpty())
        {
            // We have no unasked nodes so return
            return true;
        }


        // send message to the nodes in the list
        for (int i = 0;i < unasked.size(); i++)
        {
            Node n = (Node) unasked.get(i);

            int comm = server.sendMessage(n, lookupMessage, this);

            this.nodes.put(n, AWAITING);
        }


        return false;
    }


    private List<Node> closestNodesNotFailed(String status)
    {
        List<Node> closestNodes = new ArrayList<>();

        for (Map.Entry<Node, String> e : this.nodes.entrySet())
        {
            if (!FAILED.equals(e.getValue()))
            {
                if (status.equals(e.getValue()))
                {
                    // We got one so add it
                    closestNodes.add(e.getKey());
                }

            }
        }

        return closestNodes;
    }


    public List<Node> getClosestNodes()
    {
        return this.closestNodes(ASKED);
    }

    /**
     * Add nodes from this list to the set of nodes to lookup
     *
     * @param list The list from which to add nodes
     */
    public void addNodes(List<Node> list)
    {
        for (Node o : list)
        {
            // If this node is not in the list, add the node
            if (!nodes.containsKey(o))
            {
                nodes.put(o, UNASKED);
            }
        }
    }



    private List<Node> closestNodes(String status)
    {
        List<Node> closestNodes = new ArrayList<>();

        for (Map.Entry e : this.nodes.entrySet())
        {
            if (status.equals(e.getValue()))
            {
                // We got one with the required status, now add it
                closestNodes.add((Node) e.getKey());
            }
        }

        return closestNodes;
    }



    public synchronized void receive(Message incoming, int comm) throws IOException
    {

        // We receive a NodeReplyMessage with a set of nodes, read this message
        Message msg = (Message) incoming;

        // Add the origin node to our routing table
        Node origin = msg.getOrigin();
        this.localNode.getRoutingTable().insert(origin);

        this.nodes.put(origin, ASKED);


        // Add the received nodes to our nodes list to query
        this.addNodes(msg.getNodes());
    }



    public List<Node> getFailedNodes()
    {
        List<Node> failedNodes = new ArrayList<>();

        for (Map.Entry<Node, String> e : this.nodes.entrySet())
        {
            if (e.getValue().equals(FAILED))
            {
                failedNodes.add(e.getKey());
            }
        }

        return failedNodes;
    }
}
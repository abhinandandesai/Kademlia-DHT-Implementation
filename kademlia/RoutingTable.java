package main.java.edu.rit.cs.kademlia;


import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


/**
 * Implementation of a Kademlia routing table
 *
 */
public class RoutingTable
{

    private final Node localNode;  // The current node


    private Bucket[] buckets;

    public RoutingTable(Node localNode)
    {
        this.localNode = localNode;

        this.initialize();

        this.insert(localNode);
    }

    /**
     * Initialize the RoutingTable to it's default state
     */
    public final void initialize()
    {
        this.buckets = new Bucket[Id.ID_LENGTH];
        for (int i = 0; i < Id.ID_LENGTH; i++)
        {
            buckets[i] = new Bucket(i);
        }
    }



    /**
     * Adds a node to the routing table based on how far it is from the LocalNode.
     *
     * @param n The node to add
     */
    public synchronized final void insert(Node n)
    {
        this.buckets[this.getBucketId(n.getNodeId())].insert(n);
    }


    /**
     * Compute the bucket ID in which a given node should be placed; the bucketId is computed based on how far the node is away from the Local Node.
     *
     * @param nid The NodeId for which we want to find which bucket it belong to
     *
     * @return Integer The bucket ID in which the given node should be placed.
     */
    public final int getBucketId(Id nid)
    {
        int bId = this.localNode.getNodeId().getDistance(nid) - 1;

        return bId < 0 ? 0 : bId;
    }


    public synchronized final List<Node> findClosest(Id target, int numNodesRequired)
    {
        TreeSet<Node> sortedSet = new TreeSet<>();
        sortedSet.addAll(this.getAllNodes());

        List<Node> closest = new ArrayList<>(numNodesRequired);

        /* Now we have the sorted set, lets get the top numRequired */
        int count = 0;
        for (Node n : sortedSet)
        {
            closest.add(n);
            if (++count == numNodesRequired)
            {
                break;
            }
        }
        return closest;
    }

    /**
     * @return List A List of all Nodes in this RoutingTable
     */
    public synchronized final List<Node> getAllNodes()
    {
        List<Node> nodes = new ArrayList<>();

        for (Bucket b : this.buckets)
        {
            for (Contact c : b.getContacts())
            {
                nodes.add(c.getNode());
            }
        }

        return nodes;
    }



    public final Bucket[] getBuckets()
    {
        return this.buckets;
    }

    /**
     * Set the KadBuckets of this routing table, mainly used when retrieving saved state
     *
     * @param buckets
     */
    public final void setBuckets(Bucket[] buckets)
    {
        this.buckets = buckets;
    }

    /**
     * Method used by operations to notify the routing table of any contacts that have been unresponsive.
     *
     * @param contacts The set of unresponsive contacts
     */
    public void setUnresponsiveContacts(List<Node> contacts)
    {
        if (contacts.isEmpty())
        {
            return;
        }
        for (Node n : contacts)
        {
            this.setUnresponsiveContact(n);
        }
    }

    /**
     * Method used to notify the routing table of any contacts that have been unresponsive.
     *
     * @param n
     */
    public synchronized void setUnresponsiveContact(Node n)
    {
        int bucketId = this.getBucketId(n.getNodeId());

        this.buckets[bucketId].removeNode(n);
    }

    public synchronized final String toString()
    {
        StringBuilder sb = new StringBuilder();
        int totalContacts = 0;
        for (Bucket b : this.buckets)
        {
            if (b.numContacts() > 0)
            {
                totalContacts += b.numContacts();
                sb.append("Depth -> ");
                sb.append(b.getDepth());
                sb.append("\nNodes: ");
                sb.append(b.numContacts());
                sb.append("\n");
                sb.append(b.toString());
                sb.append("\n");
            }
        }

        sb.append("\nTotal Contacts: ");
        sb.append(totalContacts);
        sb.append("\n\n");

        sb.append("------------X-X-X-X-X-X-X-X-X-X-X-X-X-X------------");

        return sb.toString();
    }

}
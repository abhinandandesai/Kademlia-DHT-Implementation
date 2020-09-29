package main.java.edu.rit.cs.message;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.nodeOperations.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * A message sent to other nodes requesting the K-Closest nodes to a key sent in this message.
 */
public class LookupMessage implements Message
{

    private Node origin;
    private Id lookupId;

    public static final byte CODE = 0x05;

    /**
     * A new LookupMessage to find nodes
     *
     * @param origin The Node from which the message is coming from
     * @param lookup The key for which to lookup nodes for
     */
    public LookupMessage(Node origin, Id lookup)
    {
        this.origin = origin;
        this.lookupId = lookup;
    }

    public LookupMessage(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }


    public final void fromStream(DataInputStream in) throws IOException
    {
        this.origin = new Node(in);
        this.lookupId = new Id(in);
    }


    public void toStream(DataOutputStream out) throws IOException
    {
        this.origin.toStream(out);
        this.lookupId.toStream(out);
    }

    public Node getOrigin()
    {
        return this.origin;
    }

    public Id getLookupId()
    {
        return this.lookupId;
    }


    public byte code()
    {
        return CODE;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("----------Lookup Message---------");
        sb.append("Origin Node ID -> ");
        sb.append(origin);
        sb.append("Lookup ID -> ");
        sb.append(lookupId);

        return sb.toString();
    }
}
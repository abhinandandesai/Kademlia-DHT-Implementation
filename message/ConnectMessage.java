package main.java.edu.rit.cs.message;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.nodeOperations.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * A message sent to another node requesting to connect to them.
 */
public class ConnectMessage implements Message
{

    private Node origin;
    public static final byte CODE = 0x02;

    public ConnectMessage(Node origin)
    {
        this.origin = origin;
    }

    public ConnectMessage(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }


    public final void fromStream(DataInputStream in) throws IOException
    {
        this.origin = new Node(in);
    }


    public void toStream(DataOutputStream out) throws IOException
    {
        origin.toStream(out);
    }

    public Node getOrigin()
    {
        return this.origin;
    }


    public byte code()
    {
        return CODE;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("----------Connect Message---------");
        sb.append("Origin Node ID -> ");
        sb.append(origin.getNodeId());
        return sb.toString();
    }
}
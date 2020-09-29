package main.java.edu.rit.cs.nodeOperations;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * A Node in the Kademlia network - Contains basic node network information.
 */
public class Node implements Serializable
{

    private Id nodeId;
    private InetAddress inetAddress;
    private int port;

    public Node(Id nid, InetAddress ip, int port)
    {
        this.nodeId = nid;
        this.inetAddress = ip;
        this.port = port;
    }


    /**
     * Set the InetAddress of this node
     *
     * @param addr The new InetAddress of this node
     */
    public void setInetAddress(InetAddress addr)
    {
        this.inetAddress = addr;
    }

    /**
     * @return The NodeId object of this node
     */
    public Id getNodeId()
    {
        return this.nodeId;
    }

    /**
     * Creates a SocketAddress for this node
     * @return
     */
    public InetSocketAddress getSocketAddress()
    {
        return new InetSocketAddress(this.inetAddress, this.port);
    }


    public void toStream(DataOutputStream out) throws IOException
    {
        // Add the NodeId to the stream
        this.nodeId.toStream(out);

        // Add the Node's IP address to the stream
        byte[] a = inetAddress.getAddress();
        if (a.length != 4)
        {
            throw new RuntimeException("Expected InetAddress of 4 bytes, got " + a.length);
        }
        out.write(a);

        // Add the port to the stream
        out.writeInt(port);
    }


    public final void fromStream(DataInputStream in) throws IOException
    {
        // Load the NodeId
        this.nodeId = new Id(in);

        // Load the IP Address
        byte[] ip = new byte[4];
        in.readFully(ip);
        this.inetAddress = InetAddress.getByAddress(ip);


        this.port = in.readInt();
    }

    public boolean equals(Object o)
    {
        if (o instanceof Node)
        {
            Node n = (Node) o;
            if (n == this)
            {
                return true;
            }
            return this.getNodeId().equals(n.getNodeId());
        }
        return false;
    }


    public int hashCode()
    {
        return this.getNodeId().hashCode();
    }


    public String toString()
    {
        return this.getNodeId().toString();
    }
}
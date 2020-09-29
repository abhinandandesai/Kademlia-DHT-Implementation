package main.java.edu.rit.cs.nodeOperations;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;


public class Contact implements Comparable<Contact>
{

    private final Node n;
    private long lastSeen;


    /**
     *
     * When a contact fails to respond, if the replacement cache is empty and there is no replacement for the contact,
     * just mark it as stale.
     *
     * Now when a new contact is added, if the contact is stale, it is removed.
     */
    private int staleCount;


    /**
     * Create a contact object
     *
     * @param n The node associated with this contact
     */
    public Contact(Node n)
    {
        this.n = n;
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }


    public Node getNode()
    {
        return this.n;
    }


    /**
     * Update last seen to now if the contact connects.
     */
    public void setSeenNow()
    {
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }


    /**
     * The last time this contact was seen.
     */
    public long lastSeen()
    {
        return this.lastSeen;
    }

    @Override
    public boolean equals(Object c)
    {
        if (c instanceof Contact)
        {
            return ((Contact) c).getNode().equals(this.getNode());
        }

        return false;
    }


    public int staleCount()
    {
        return this.staleCount;
    }


    public void resetStaleCount()
    {
        this.staleCount = 0;
    }

    @Override
    public int compareTo(Contact o)
    {
        if (this.getNode().equals(o.getNode()))
        {
            return 0;
        }

        return (this.lastSeen() > o.lastSeen()) ? 1 : -1;
    }

    @Override
    public int hashCode()
    {
        return this.getNode().hashCode();
    }

}

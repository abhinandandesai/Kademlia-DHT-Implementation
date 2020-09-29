package main.java.edu.rit.cs.kademlia;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;

/**
 * A bucket in the Kademlia routing table
 *
 */
public class Bucket
{


    private final int depth;

    // Contacts stored in this routing table
    private final TreeSet<Contact> contacts;

    // A set last seen contacts that can replace stale contacts
    private final TreeSet<Contact> replacementCache;




    {
        contacts = new TreeSet<>();
        replacementCache = new TreeSet<>();
    }

    /**
     * @param depth  depth of the bucket
     */
    public Bucket(int depth)
    {
        this.depth = depth;
    }



    public synchronized void insert(Node n)
    {
        this.insert(new Contact(n));
    }


    public synchronized boolean containsContact(Contact c)
    {
        return this.contacts.contains(c);
    }


    public synchronized boolean removeContact(Contact c)
    {
        /* If the contact does not exist, then we failed to remove it */
        if (!this.contacts.contains(c))
        {
            return false;
        }

        /* Contact exist, lets remove it only if our replacement cache has a replacement */
        if (!this.replacementCache.isEmpty())
        {
            /* Replace the contact with one from the replacement cache */
            this.contacts.remove(c);
            Contact replacement = this.replacementCache.first();
            this.contacts.add(replacement);
            this.replacementCache.remove(replacement);
        }
        else
        {
            this.getFromContacts(c.getNode()).incrementStaleCount();
        }

        return true;
    }

    private synchronized Contact getFromContacts(Node n)
    {
        for (Contact c : this.contacts)
        {
            if (c.getNode().equals(n))
            {
                return c;
            }
        }


        throw new NoSuchElementException("The contact does not exist in the contacts list.");
    }

    private synchronized Contact removeFromContacts(Node n)
    {
        for (Contact c : this.contacts)
        {
            if (c.getNode().equals(n))
            {
                this.contacts.remove(c);
                return c;
            }
        }

        /* We got here means this element does not exist */
        throw new NoSuchElementException("Node does not exist in the replacement cache. ");
    }

    public synchronized boolean removeNode(Node n)
    {
        return this.removeContact(new Contact(n));
    }

    public synchronized int numContacts()
    {
        return this.contacts.size();
    }

    public synchronized int getDepth()
    {
        return this.depth;
    }

    public synchronized List<Contact> getContacts()
    {
        final ArrayList<Contact> ret = new ArrayList<>();

        /* If we have no contacts, return the blank arraylist */
        if (this.contacts.isEmpty())
        {
            return ret;
        }

        /* We have contacts, lets copy put them into the arraylist and return */
        for (Contact c : this.contacts)
        {
            ret.add(c);
        }

        return ret;
    }


    private synchronized void insertIntoReplacementCache(Contact c)
    {
        /* Just return if this contact is already in our replacement cache */
        if (this.replacementCache.contains(c))
        {
            /**
             * If the contact is already in the bucket, lets update that we've seen it
             * We need to remove and re-add the contact to get the Sorted Set to update sort order
             */
            Contact tmp = this.removeFromReplacementCache(c.getNode());
            tmp.setSeenNow();
            this.replacementCache.add(tmp);
        }
        else
        {
            this.replacementCache.add(c);
        }
    }

    private synchronized Contact removeFromReplacementCache(Node n)
    {
        for (Contact c : this.replacementCache)
        {
            if (c.getNode().equals(n))
            {
                this.replacementCache.remove(c);
                return c;
            }
        }

        try {
            throw new Exception("Node does not exist in the replacement cache. ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
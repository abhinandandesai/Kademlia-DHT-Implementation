package main.java.edu.rit.cs.nodeOperations;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;

import java.util.Objects;


/**
 * Keeps track of data for a Content stored.
 */
public class  StorageEntryMetadata
{

    private final Id key;
    private final String ownerId;
    private final String type;
    private final int contentHash;
    private final long updatedTs;

    private long lastRepublished;

    public StorageEntryMetadata(StorageEntry content)
    {
        this.key = content.getKey();
        this.ownerId = content.getOwnerId();
        this.type = content.getType();
        this.contentHash = content.hashCode();
        this.updatedTs = content.getLastUpdatedTimestamp();

        this.lastRepublished = System.currentTimeMillis() / 1000L;
    }


    public Id getKey()
    {
        return this.key;
    }

    public String getOwnerId()
    {
        return this.ownerId;
    }

    public String getType()
    {
        return this.type;
    }

    public int getContentHash()
    {
        return this.contentHash;
    }


    public long getLastUpdatedTimestamp()
    {
        return this.updatedTs;
    }

    /**
     * When a node is looking for content, he sends the search criteria in a GetParameter object
     * Check the object for the details.
     * */

    public boolean satisfiesParameters(GetParameter params)
    {

        if ((params.getOwnerId() != null) && (!params.getOwnerId().equals(this.ownerId)))
        {
            return false;
        }


        if ((params.getType() != null) && (!params.getType().equals(this.type)))
        {
            return false;
        }

        if ((params.getKey() != null) && (!params.getKey().equals(this.key)))
        {
            return false;
        }

        return true;
    }


    public long lastRepublished()
    {
        return this.lastRepublished;
    }



    /**
     * Whenever we republish a content or get this content from the network, we update the last republished time
     */

    public void updateLastRepublished()
    {
        this.lastRepublished = System.currentTimeMillis() / 1000L;
    }


    public boolean equals(Object o)
    {
        if (o instanceof StorageEntryMetadata)
        {
            return this.hashCode() == o.hashCode();
        }

        return false;
    }


    public int hashCode()
    {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.key);
        hash = 23 * hash + Objects.hashCode(this.ownerId);
        hash = 23 * hash + Objects.hashCode(this.type);
        return hash;
    }


}
package main.java.edu.rit.cs.kademlia;


import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;

/**
 * A StorageEntry class that is used to store a content on the dht
 */
public class StorageEntry
{

    private String content;
    private final StorageEntryMetadata metadata;


    public StorageEntry(final StorageEntryMetadata metadata)
    {
        this.metadata = metadata;
    }

    public final void setContent(final byte[] data)
    {
        this.content = new String(data);
    }

    public final byte[] getContent()
    {
        return this.content.getBytes();
    }

    public final StorageEntryMetadata getContentMetadata()
    {
        return this.metadata;
    }


}
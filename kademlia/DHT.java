package main.java.edu.rit.cs.kademlia;
import java.io.IOException;



import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;

public class DHT
{

    private transient StoredContentManager contentManager;

    private final String ownerId;

    public DHT(String ownerId)
    {
        this.ownerId = ownerId;
        this.initialize();
    }


    public final void initialize()
    {
        contentManager = new StoredContentManager();
    }



    public boolean store(StorageEntry content) throws IOException
    {

            StorageEntryMetadata current = this.contentManager.get(content.getContentMetadata());

            // update the last republished time
            current.updateLastRepublished();

        try
        {
            //System.out.println("Adding new content.");
            // Keep track of the content in the entries manager
            StorageEntryMetadata sEntry = this.contentManager.put(content.getContentMetadata());

            return true;
        }
        catch (Exception e)
        {
           System.err.println("Content already exists");
            return false;
        }
    }


    public boolean store(StorageEntryMetadata content) throws IOException
    {
        return this.store(new StorageEntry(content));
    }




    public synchronized String toString()
    {
        return this.contentManager.toString();
    }
}
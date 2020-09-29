package main.java.edu.rit.cs.nodeOperations;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * It would be infeasible to keep all content in memory to be send when requested
 * Keep track of all content stored
 *
 */
public class StoredContentManager
{

    private final Map<Id, List<StorageEntryMetadata>> entries;


    {
        entries = new HashMap<>();
    }


    /**
     * Add a new entry to the
     *
     * @param entry The StorageEntry to store
     */
    public StorageEntryMetadata put(StorageEntryMetadata entry) throws Exception
    {
        if (!this.entries.containsKey(entry.getKey()))
        {
            this.entries.put(entry.getKey(), new ArrayList<>());
        }

        // If this entry doesn't already exist, then add it
        if (!this.contains(entry))
        {
            this.entries.get(entry.getKey()).add(entry);

            return entry;
        }
        else
        {
            try {
                throw new Exception("Content already exists on this kademlia.DHT");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if there is Content for the given criteria
     *
     * @param param The parameters used to search for a content
     *
     * @return boolean
     */
    public synchronized boolean contains(GetParameter param)
    {
        if (this.entries.containsKey(param.getKey()))
        {
            // Content with this key exist, check if any match the rest of the search criteria
            for (StorageEntryMetadata e : this.entries.get(param.getKey()))
            {
                // If any entry satisfies the given parameters
                if (e.satisfiesParameters(param))
                {
                    return true;
                }
            }
        }
        return false;
    }



    public StorageEntryMetadata get(GetParameter param)
    {
        if (this.entries.containsKey(param.getKey()))
        {
            // Content with this key exist
            for (StorageEntryMetadata e : this.entries.get(param.getKey()))
            {
                // If any entry satisfies the given parameters, return true
                if (e.satisfiesParameters(param))
                {
                    return e;
                }
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    public StorageEntryMetadata get(StorageEntryMetadata md)
    {
        return this.get(new GetParameter(md));
    }

    /**
     * @return A list of all storage entries
     */
    public synchronized List<StorageEntryMetadata> getAllEntries()
    {
        List<StorageEntryMetadata> entriesRet = new ArrayList<>();

        for (List<StorageEntryMetadata> entrySet : this.entries.values())
        {
            if (entrySet.size() > 0)
            {
                entriesRet.addAll(entrySet);
            }
        }

        return entriesRet;
    }


    public void remove(StorageEntryMetadata entry) throws Exception
    {
        if (contains(entry))
        {
            this.entries.get(entry.getKey()).remove(entry);
        }
        else
        {
            throw new Exception("This content does not exist in the Storage Entries");
        }
    }


    public synchronized String toString()
    {
        StringBuilder sb = new StringBuilder("Stored Content: \n");
        int count = 0;
        for (List<StorageEntryMetadata> sEM : this.entries.values())
        {
            for (StorageEntryMetadata sE : sEM)
            {
                sb.append(++count);
                sb.append(". ");
                sb.append(sE);
                sb.append("\n");
            }
        }

        sb.append("\n");
        return sb.toString();
    }
}
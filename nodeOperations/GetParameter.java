package main.java.edu.rit.cs.nodeOperations;

import main.java.edu.rit.cs.kademlia.*;
import main.java.edu.rit.cs.message.*;

public class GetParameter
{

    private Id key;
    private String ownerId = null;
    private String type = null;


    /**
     * Construct a GetParameter to search for data by NodeId, owner, type
     *
     * @param key
     * @param type
     * @param owner
     */
    public GetParameter(Id key, String type, String owner)
    {

        this.key = key;
        this.type = type;
        this.ownerId = owner;
    }



    public Id getKey()
    {
        return this.key;
    }

    public void setOwnerId(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId()
    {
        return this.ownerId;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return this.type;
    }


}
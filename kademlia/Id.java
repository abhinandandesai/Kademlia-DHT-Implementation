package main.java.edu.rit.cs.kademlia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;


import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;


public class Id implements Serializable
{

    public final transient static int ID_LENGTH = 160;
    private byte[] keyBytes;

    /**
     * Generate a random key
     */
    public Id()
    {
        keyBytes = new byte[ID_LENGTH / 8];
        new Random().nextBytes(keyBytes);
    }

    /**
     * Generate the NodeId from a given byte[]
     *
     * @param bytes
     */
    public Id(byte[] bytes)
    {
        if (bytes.length != ID_LENGTH / 8)
        {
            throw new IllegalArgumentException("Specified Data need to be " + (ID_LENGTH / 8) + " characters long. Data Given: '" + new String(bytes) + "'");
        }
        this.keyBytes = bytes;
    }

    /**
     * Load the NodeId from a DataInput stream
     *
     * @param in The stream from which to load the NodeId
     *
     * @throws IOException
     */
    public Id(DataInputStream in) throws IOException
    {
        this.fromStream(in);
    }

    public byte[] getBytes()
    {
        return this.keyBytes;
    }

    /**
     * @return The BigInteger representation of the key
     */
    public BigInteger getInt()
    {
        return new BigInteger(1, this.getBytes());
    }

    /**
     * Compares a NodeId to this NodeId
     *
     * @param o The NodeId to compare to this NodeId
     *
     * @return boolean Whether the 2 NodeIds are equal
     */
    public boolean equals(Object o)
    {
        if (o instanceof Id)
        {
            Id nid = (Id) o;
            return this.hashCode() == nid.hashCode();
        }
        return false;
    }

    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + Arrays.hashCode(this.keyBytes);
        return hash;
    }

    /**
     * Checks the distance between this and another NodeId
     *
     * @param nid
     *
     * @return The distance of this NodeId from the given NodeId
     */
    public Id xor(Id nid)
    {
        byte[] result = new byte[ID_LENGTH / 8];
        byte[] nidBytes = nid.getBytes();

        for (int i = 0; i < ID_LENGTH / 8; i++)
        {
            result[i] = (byte) (this.keyBytes[i] ^ nidBytes[i]);
        }

        Id resNid = new Id(result);

        return resNid;
    }


    /**
     * Counts the number of leading 0's in this NodeId
     *
     * @return Integer The number of leading 0's
     */
    public int getFirstSetBitIndex()
    {
        int prefixLength = 0;

        for (byte b : this.keyBytes)
        {
            if (b == 0)
            {
                prefixLength += 8;
            }
            else
            {
                // If the byte is not 0, we need to count how many MSBs are 0
                int count = 0;
                for (int i = 7; i >= 0; i--)
                {
                    boolean a = (b & (1 << i)) == 0;
                    if (a)
                    {
                        count++;
                    }
                    else
                    {
                        break;   // Reset the count if we encounter a non-zero number
                    }
                }

                // Add the count of MSB 0s to the prefix length
                prefixLength += count;

                break;
            }
        }
        return prefixLength;
    }

    /**
     * Gets the distance from this NodeId to another NodeId
     *
     * @param to
     *
     * @return Integer The distance
     */
    public int getDistance(Id to)
    {
        /**
         * Compute the xor of this and to
         * Get the index i of the first set bit of the xor returned NodeId
         * The distance between them is ID_LENGTH - i
         */
        return ID_LENGTH - this.xor(to).getFirstSetBitIndex();
    }

    public void toStream(DataOutputStream out) throws IOException
    {
        // Add the NodeId to the stream
        out.write(this.getBytes());
    }

    public final void fromStream(DataInputStream in) throws IOException
    {
        byte[] input = new byte[ID_LENGTH / 8];
        in.readFully(input);
        this.keyBytes = input;
    }

    public String hexRepresentation()
    {
        BigInteger bi = new BigInteger(1, this.keyBytes);
        return String.format("%0" + (this.keyBytes.length << 1) + "X", bi);
    }

    public String toString()
    {
        return this.hexRepresentation();
    }

}
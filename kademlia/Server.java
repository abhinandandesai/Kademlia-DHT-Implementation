package main.java.edu.rit.cs.kademlia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import main.java.edu.rit.cs.message.*;
import main.java.edu.rit.cs.nodeOperations.*;

/**
 * The server that handles sending and receiving messages between nodes on the Kad Network
 */
public class Server implements Remote {


    private static final int DATAGRAM_BUFFER_SIZE = 64 * 1024;      // 64KB



    private final DatagramSocket socket;
    private transient boolean isRunning;
    private final Map<Integer, Receiver> receivers;
    private final Timer timer;      // Schedule future tasks
    private final Map<Integer, TimerTask> tasks;    // Keep track of scheduled tasks

    private final Node localNode;


    {
        isRunning = true;
        this.tasks = new HashMap<>();
        this.receivers = new HashMap<>();
        this.timer = new Timer(true);
    }

    /**
     * Initialize our Server
     *
     * @param udpPort      The port to listen on
     * @param localNode    Local node on which this server runs on
     *
     * @throws java.net.SocketException
     */
    public Server(int udpPort, Node localNode) throws SocketException
    {
        this.socket = new DatagramSocket(udpPort);
        this.localNode = localNode;


        this.startListener();
    }

    /**
     * Starts the listener to listen for incoming messages
     */
    public void startListener()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                listen();
            }
        }.start();
    }

    /**
     * Sends a message
     *
     * @param msg  The message to send
     * @param to   The node to send the message to
     * @param recv The receiver to handle the response message
     *
     * @return Integer The communication ID of this message

     * @throws Exception
     */
    public synchronized int sendMessage(Node to, Message msg, Receiver recv) throws Exception
    {
        if (!isRunning)
        {
            throw new Exception(this.localNode + " - Server is not running.");
        }

        // Generate a random communication ID
        int comm = new Random().nextInt();

        if (recv != null)
        {
            try
            {
                // Setup the receiver to handle message response
                receivers.put(comm, recv);
                TimerTask task = new TimeoutTask(comm, recv);
                tasks.put(comm, task);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        // Send the message
        sendMessage(to, msg, comm);

        return comm;
    }

    /**
     * Method called to reply to a message received
     *
     * @param to   The Node to send the reply to
     * @param msg  The reply message
     * @param comm The communication ID - the one received
     *
     * @throws java.io.IOException
     */
    public synchronized void reply(Node to, Message msg, int comm) throws IOException
    {
        if (!isRunning)
        {
            throw new IllegalStateException("Server is not running.");
        }
        sendMessage(to, msg, comm);
    }

    /**
     * Internal sendMessage method called by the public sendMessage method after a communicationId is generated
     */
    private void sendMessage(Node to, Message msg, int comm) throws IOException
    {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); DataOutputStream dout = new DataOutputStream(bout);)
        {

            dout.writeInt(comm);
            dout.writeByte(msg.code());
            msg.toStream(dout);
            dout.close();

            byte[] data = bout.toByteArray();

            if (data.length > DATAGRAM_BUFFER_SIZE)
            {
                throw new IOException("Message is too big");
            }

            DatagramPacket pkt = new DatagramPacket(data, 0, data.length);
            pkt.setSocketAddress(to.getSocketAddress());
            socket.send(pkt);

        }
    }

    /**
     * Listen for incoming messages in a separate thread
     */
    public void listen()
    {
        try
        {
            while (isRunning)
            {
                try
                {
                    byte[] buffer = new byte[DATAGRAM_BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);


                    // We've received a packet
                    try (ByteArrayInputStream bin = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
                         DataInputStream din = new DataInputStream(bin);)
                    {

                        //Read in the conversation Id to know which handler to handle this response
                        int comm = din.readInt();
                        byte messCode = din.readByte();

                        ConnectMessage msg = createMessage(messCode, din);
                        din.close();

                        // Get a receiver for this message
                        Receiver receiver;
                        if (this.receivers.containsKey(comm))
                        {
                            // If there is a receiver in the receivers to handle this
                            synchronized (this)
                            {
                                receiver = this.receivers.remove(comm);
                                TimerTask task = (TimerTask) tasks.remove(comm);
                                if (task != null)
                                {
                                    task.cancel();
                                }
                            }
                        }

                        if (receiver != null)
                        {
                            receiver.receive(msg, comm);
                        }
                    }
                }
                catch (IOException e)
                {
                    //this.isRunning = false;
                    System.err.println("Server ran into a problem in listener method. Message: " + e.getMessage());
                }
            }
        }
        finally
        {
            socket.close();
            this.isRunning = false;
        }
    }


    private synchronized void unregister(int comm)
    {
        receivers.remove(comm);
        this.tasks.remove(comm);
    }

    /**
     * Stops listening and shuts down the server
     */
    public synchronized void shutdown()
    {
        this.isRunning = false;
        this.socket.close();
        timer.cancel();
    }


    class TimeoutTask extends TimerTask
    {

        private final int comm;
        private final Receiver recv;

        public TimeoutTask(int comm, Receiver recv)
        {
            this.comm = comm;
            this.recv = recv;
        }

        @Override
        public void run()
        {
            if (!Server.this.isRunning)
            {
                return;
            }

            try
            {
                unregister(comm);
                recv.timeout(comm);
            }
            catch (IOException e)
            {
                System.err.println("Cannot unregister a receiver. Message: " + e.getMessage());
            }
        }
    }

    public void printReceivers()
    {
        for (Integer r : this.receivers.keySet())
        {
            System.out.println("Receiver for comm: " + r + "; Receiver: " + this.receivers.get(r));
        }
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }

}
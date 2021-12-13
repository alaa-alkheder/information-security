
import org.json.simple.JSONObject;

import java.net.*;
import java.io.*;


public class Client {

    // for I/O
    private ObjectInputStream sInput;        // to read from the socket
    private ObjectOutputStream sOutput;        // to write on the socket
    private Socket socket;                    // socket object

    private String server, username;    // server and username

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Default Constrictor
     *
     * @param server:   the server address
     * @param username: the username
     */
    Client(String server, String username) {
        this.server = server;
        this.username = username;
    }

    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, StaticVariable.PORT);
        }
        // exception handler if it failed
        catch (Exception ec) {
            display("Error connectiong to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages
       /* try {
            sOutput.writeObject(new Message(StaticVariable.LOGIN, "soso"));
        } catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }*/
        // success we inform the caller that it worked
        return true;
    }

    /**
     * To send a message to the console
     */
    private void display(String msg) {

        System.out.println(msg);

    }

    /**
     * To send a message to the server
     */
    void sendMessage(Message msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /**
     * When something goes wrong
     * Close the Input/Output streams and disconnect
     */
    private void disconnect() {
        try {
            if (sInput != null) sInput.close();
        } catch (Exception e) {
        }
        try {
            if (sOutput != null) sOutput.close();
        } catch (Exception e) {
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
        }

    }

    public static void main(String[] args) {

        Client client = new Client(StaticVariable.SERVER_ADDERSS, "meme");
        // try to connect to the server and return if not connected
        if (client.start()) System.out.println("#Server is Start");


        // infinite loop to get the input from the user



            switch (4) {
                case 1://Sigin IN
                {
                    JSONObject jo = new JSONObject();
                    // putting data to JSONObject
                    jo.put(StaticVariable.USERNAME, "John");
                    jo.put(StaticVariable.PASSWORD, "Smith");
                    System.out.println(jo.toJSONString());
                  client.sendMessage(new Message(StaticVariable.SIGNIN, jo.toJSONString()));
                  break;
                }
                case 2://Log IN
                {
                    JSONObject jo = new JSONObject();
                    // putting data to JSONObject
                    jo.put(StaticVariable.USERNAME, "John");
                    jo.put(StaticVariable.PASSWORD, "Smith");
                    System.out.println(jo.toJSONString());
                  client.sendMessage(new Message(StaticVariable.LOGIN, jo.toJSONString()));
                  break;
                }
                case 3://Add new Password
                {
                    JSONObject jo = new JSONObject();
                    // putting data to JSONObject
                    jo.put(StaticVariable.EMAIL, "John");
                    jo.put(StaticVariable.PASSWORD, "Smith");
                    jo.put(StaticVariable.ADDRESS, "address");
                    jo.put(StaticVariable.DESCRIPTION, "Drscription");
                    jo.put(StaticVariable.ATTACHED_FILE, "ATTACHED_FILE");
                    System.out.println(jo.toJSONString());
                  client.sendMessage(new Message(StaticVariable.ADD_NEW_PASSWORD, jo.toJSONString()));
                  break;
                }
                case 4://Display new Password
                {
                    JSONObject jo = new JSONObject();
//                    // putting data to JSONObject
//                    jo.put(StaticVariable.EMAIL, "John");
//                    jo.put(StaticVariable.PASSWORD, "Smith");
//                    jo.put(StaticVariable.ADDRESS, "address");
//                    jo.put(StaticVariable.DESCRIPTION, "Drscription");
//                    jo.put(StaticVariable.ATTACHED_FILE, "ATTACHED_FILE");
//                    System.out.println(jo.toJSONString());
                  client.sendMessage(new Message(StaticVariable.DISPLAY_PASSWORD, jo.toJSONString()));
                  break;
                }
                case 5://Edit Password
                {
                    JSONObject jo = new JSONObject();
                    // putting data to JSONObject
//                    jo.put(StaticVariable.EMAIL, "John");
//                    jo.put(StaticVariable.PASSWORD, "Smith");
//                    jo.put(StaticVariable.ADDRESS, "address");
//                    jo.put(StaticVariable.DESCRIPTION, "Drscription");
//                    jo.put(StaticVariable.ATTACHED_FILE, "ATTACHED_FILE");
//                    System.out.println(jo.toJSONString());
                    client.sendMessage(new Message(StaticVariable.EDIT_PASSWORD, jo.toJSONString()));
                    break;
                }
                case 6://Delete Password
                {
                    JSONObject jo = new JSONObject();
                    // putting data to JSONObject
//                    jo.put(StaticVariable.EMAIL, "John");
//                    jo.put(StaticVariable.PASSWORD, "Smith");
//                    jo.put(StaticVariable.ADDRESS, "address");
//                    jo.put(StaticVariable.DESCRIPTION, "Drscription");
//                    jo.put(StaticVariable.ATTACHED_FILE, "ATTACHED_FILE");
//                    System.out.println(jo.toJSONString());
                    client.sendMessage(new Message(StaticVariable.DELETE_PASSWORD, jo.toJSONString()));
                    break;
                }
            }
            while (true){}
//        }

        // client completed its job. disconnect client.
//        client.disconnect();
    }

    /**
     * a class that waits for the message from the server
     */
    class ListenFromServer extends Thread {

        public void run() {
            while (true) {
                try {
                    // read the message form the input datastream
                    Message msg = (Message) sInput.readObject();
                    // print the message
                    System.out.println(msg.toString());
                    System.out.print("> ");
                } catch (IOException e) {
                    display("# Server has closed the connection: " + e.getMessage());
                    break;
                } catch (ClassNotFoundException e2) {
                }
            }
        }
    }
}


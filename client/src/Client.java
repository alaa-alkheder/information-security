
import cryptography.AES;
import cryptography.KeySize;
import cryptography.RSA;
import org.json.simple.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.net.*;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;


public class Client {

     // for I/O
    private ObjectInputStream sInput;        // to read from the socket
    private ObjectOutputStream sOutput;        // to write on the socket
    private Socket socket;                    // socket object

    private String server, username;    // server and username

   public PublicKey publicKey;
    SecretKey sessionkey;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public SecretKey getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(SecretKey sessionkey) {
        this.sessionkey = sessionkey;
    }

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

        /** Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }
        /**create session key*/
        try {
            sessionkey= AES.generateKey(KeySize.AES_KEY_128);

        } catch (NoSuchAlgorithmException e) {
            display("Error when we create session key " + e);
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
    private static Client client;
    public static void main(String[] args) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        client = new Client(StaticVariable.SERVER_ADDERSS, "meme");

        // try to connect to the server and return if not connected
        if (client.start()) System.out.println("#Server is Start");

        client.sendMessage(new Message(StaticVariable.REQUEST_PUBLIC_KEY,""));



    }
    boolean loggedIn = false;
    void dispalyChoise(){
        System.out.println("----------------------------------------------------------");
        if(!loggedIn)
        {
            System.out.println("1. Sign Up");
            System.out.println("2. Log In");
        }else{
            System.out.println("3. Add New Password");
            System.out.println("4. Display new Password");
            System.out.println("5. Edit Password");
            System.out.println("6. Delete Password");
        }
        System.out.print("Your Choice: ");
    }


    void sendSessionKey() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        IvParameterSpec ivParameterSpec =  AES.generateIv();
        String encodedKey = Base64.getEncoder().encodeToString(client.sessionkey.getEncoded());

        System.out.println("Session Key Is : " + encodedKey);
        Message message=new Message(StaticVariable.SEND_SESSION_KEY,AES.encrypt(StaticVariable.ALGORITHM,"khara",client.sessionkey,ivParameterSpec));
        message.setSessionKey(RSA.encrypt(client.publicKey,encodedKey));

        client.sendMessage(message);
    }
    void doTheWhile() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        // infinite loop to get the input from the user
        Scanner in = new Scanner(System.in);

        int a;
        dispalyChoise();
        a= in.nextInt();
        switch (a) {
            case 1://Sigin IN
            {
                JSONObject jo = new JSONObject();
                // putting data to JSONObject
                jo.put(StaticVariable.USERNAME, "John");
                jo.put(StaticVariable.PASSWORD, "Smith");
                System.out.println(jo.toJSONString());
                client.sendMessage(new Message(StaticVariable.SIGNIN, AES.encrypt(StaticVariable.ALGORITHM,jo.toJSONString(),sessionkey,AES.generateIv())));
                break;
            }
            case 2://Log IN
            {
                JSONObject jo = new JSONObject();
                // putting data to JSONObject
                jo.put(StaticVariable.USERNAME, "John");
                jo.put(StaticVariable.PASSWORD, "Smith");
                System.out.println(jo.toJSONString());
                client.sendMessage(new Message(StaticVariable.LOGIN, AES.encrypt(StaticVariable.ALGORITHM,jo.toJSONString(),sessionkey,AES.generateIv())));

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
            case 7://Get server public key
            {
                client.sendMessage(new Message(StaticVariable.REQUEST_PUBLIC_KEY,""));
                break;
            }
            case 8://send Session key key
            {

                IvParameterSpec ivParameterSpec =  AES.generateIv();
                String encodedKey = Base64.getEncoder().encodeToString(client.sessionkey.getEncoded());

                System.out.println(encodedKey);
                System.out.println(RSA.encrypt(client.publicKey,encodedKey));
                Message message=new Message(StaticVariable.SEND_SESSION_KEY,AES.encrypt(StaticVariable.ALGORITHM,"khara",client.sessionkey,ivParameterSpec));
                message.setSessionKey(RSA.encrypt(client.publicKey,encodedKey));

                client.sendMessage(message);
                break;
            }
        }
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
                    String temp = msg.getType();
                    switch (temp) {
                        case StaticVariable.LOGIN: {

                            break;
                        }
                        case StaticVariable.SEND_PUBLIC_KEY: {
                           publicKey=msg.getPublicKey();
                            System.out.println("public key " + publicKey);
                            sendSessionKey();
                            break;
                        }
                        case StaticVariable.ACCEPT_SESSION_KEY: {
                            System.out.println("Session Key Accepted From Server");
                            doTheWhile();
                            break;
                        }
                        case StaticVariable.LOGIN_ACCEPTED: {
                            System.out.println("LOGIN ACCEPTED");
                            loggedIn=true;
                            doTheWhile();
                            break;
                        }
                        case StaticVariable.LOGIN_REJECTED: {
                            System.out.println("LOGIN REJECTED");
                            loggedIn=false;
                            doTheWhile();
                            break;
                        }
                    }

                } catch (IOException e) {
                    display("# Server has closed the connection: " + e.getMessage());
                    break;
                } catch (ClassNotFoundException e2) {
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


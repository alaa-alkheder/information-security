


import cryptography.AES;
import cryptography.RSA;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;


import java.net.Socket;


import java.security.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientHandler extends Thread {

    JSONArray users;
    //Define Socket
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    ObjectOutputStream outp;
    ObjectInputStream inp;
    String currentUser;
    SecretKey sessionKey;
    static LinkedList<ClientHandler> handlers = new LinkedList<ClientHandler>();
    static Map onlineUser = new HashMap<String, ClientHandler>();
    //public and private key for RSA
    PrivateKey privateKey;
    PublicKey publicKey;


    /**
     * Default Constrictor
     *
     * @param socket
     */
    public ClientHandler(Socket socket) {
        try {

            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            outp = new ObjectOutputStream(socket.getOutputStream());
            inp = new ObjectInputStream(socket.getInputStream());

            //RSA Initializer
            KeyPairGenerator generator = null;
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();

            JSONParser parser = new JSONParser();
            users = (JSONArray) parser.parse(new FileReader("users.json"));
            if(users == null) users = new JSONArray();
        } catch (IOException | NoSuchAlgorithmException | ParseException e) {
            e.printStackTrace();
        }
    }


    @Override

    public void run() {
        String name = "";
//        ArrayList<Object> data = new ArrayList();
        Message data;
        try {

            while (true) {
                data = (Message) inp.readObject();
                String temp = data.getType();
                switch (temp) {
                    case StaticVariable.SIGNIN: {
                        RegisterUser(data);
                        break;
                    }
                    case StaticVariable.LOGIN: {
                        Connect_user(data);

                        break;
                    }
                    case StaticVariable.GETALLUSER: {
//                        getAllUser();
                        break;
                    }
                    case StaticVariable.ADD_NEW_PASSWORD: {
                        AddNewPassword(data);
                        break;
                    }
                    case StaticVariable.DISPLAY_PASSWORD: {
                        DisplayPassword(data);
                        break;
                    }
                    case StaticVariable.EDIT_PASSWORD: {
                        EditPassword(data);
                        break;
                    }
                    case StaticVariable.DELETE_PASSWORD: {
                        DeletePassword(data);
                        break;
                    }
                    case StaticVariable.REQUEST_PUBLIC_KEY: {
                        Message message = new Message(StaticVariable.SEND_PUBLIC_KEY, "");
                        message.setPublicKey(publicKey);
                        System.out.println(publicKey);
                        outp.writeObject(message);
                        break;
                    }
                    case StaticVariable.SEND_SESSION_KEY: {
                        String encodedKey = RSA.dencrypt(privateKey, data.getSessionKey());
                        // decode the base64 encoded string
                        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
                        // rebuild key using SecretKeySpec
                        sessionKey = new SecretKeySpec(decodedKey, "AES");

                        System.out.println("Session Key Received From Client : " + encodedKey);
                        Message message = new Message(StaticVariable.ACCEPT_SESSION_KEY, "");
                        outp.writeObject(message);

                        break;
                    }

                }

            }
        } catch (IOException e) {
            System.out.println("-- Connection to user lost." + e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } finally {
            handlers.remove(this);
            onlineUser.remove(this.currentUser);
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    //todo delete data from database
    private void DeletePassword(Message data) throws IOException {
        JSONObject jo = new JSONObject();
        // putting data to JSONObject
        jo.put("msg", "Delete");
        outp.writeObject(new Message(null, jo.toJSONString()));

    }

    //todo edit data in database
    private void EditPassword(Message data) throws IOException {
        JSONObject jo = new JSONObject();
        // putting data to JSONObject
        jo.put("msg", "edit");
        outp.writeObject(new Message(null, jo.toJSONString()));
    }

    //todo dispaly data from database
    private void DisplayPassword(Message data) throws IOException {
        JSONObject jo = new JSONObject();
        // putting data to JSONObject
        jo.put("msg", "display");
        outp.writeObject(new Message(null, jo.toJSONString()));
    }

    //todo add data to database
    private void AddNewPassword(Message data) throws ParseException {


    }

    private String hashPassword(String password) {
        String generatedPassword = "";
        try
        {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(password.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // This bytes[] has bytes in decimal format. Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    //todo add user to database
    public void RegisterUser(Message message) {
        try {
            JSONObject object = JsonFunction.decode(AES.decrypt(StaticVariable.ALGORITHM,message.getMessage(),sessionKey,AES.generateIv()));

            object.replace(StaticVariable.PASSWORD,hashPassword(object.get("password").toString()));
            System.out.println(object);
            users.add(object);

           saveUserInDb();
            System.out.println("# register new User " + this.currentUser);
//            System.out.println((String) object.get("password"));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    //todo check user auth
    public void Connect_user(Message message) throws ParseException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException {
        JSONObject object = JsonFunction.decode(AES.decrypt(StaticVariable.ALGORITHM,message.getMessage(),sessionKey,AES.generateIv()));
        object.replace(StaticVariable.PASSWORD,hashPassword(object.get("password").toString()));
        if (users.contains(object)) {
            this.currentUser = (String) object.get("username");
            handlers.add(this);
            onlineUser.put(this.currentUser, this);
            outp.writeObject(new Message(StaticVariable.LOGIN_ACCEPTED,""));
            System.out.println("# log in User " + this.currentUser);
        }
        else { outp.writeObject(new Message(StaticVariable.LOGIN_REJECTED,""));
            System.out.println("# login error " + this.currentUser);}

    }


    public void saveUserInDb() {
        FileWriter file = null;
        try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            file = new FileWriter("users.json");
            file.write(users.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }
    }


    public void getAllUser() {

    }

}



import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import java.net.Socket;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientHandler extends Thread {

    //Define Socket
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    ObjectOutputStream outp;
    ObjectInputStream inp;
    String currentUser;
    static LinkedList<ClientHandler> handlers = new LinkedList<ClientHandler>();
    static Map onlineUser = new HashMap<String, ClientHandler>();

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
        } catch (IOException e) {
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

                }

            }
        } catch (IOException e) {
            System.out.println("-- Connection to user lost." + e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException e) {
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
        JSONObject object = JsonFunction.decode(data.getMessage());
        System.out.println(object.toJSONString());

    }

    //todo add user to database
    public void RegisterUser(Message message) {
        try {
            JSONObject object = JsonFunction.decode(message.getMessage());
            this.currentUser = (String) object.get("username");
            handlers.add(this);
            onlineUser.put(this.currentUser, this);
            System.out.println("# register new User " + this.currentUser);
//            System.out.println((String) object.get("password"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //todo check user auth
    public void Connect_user(Message message) throws ParseException {
        JSONObject object = JsonFunction.decode(message.getMessage());
        this.currentUser = (String) object.get("username");
        handlers.add(this);
        onlineUser.put(this.currentUser, this);
        System.out.println("# log in User " + this.currentUser);

    }


    public void getAllUser() {

    }

}
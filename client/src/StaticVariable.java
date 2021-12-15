/**
 * Created by IntelliJ IDEA.
 * User: Alaa Alkheder
 * Email:alaa-alkheder@outlook.com
 * Github:alaa-alkheder
 */
public class StaticVariable {
    //    server info
    public final static int PORT = 1234;
    public final static String SERVER_ADDERSS = "localhost";


    //      massage Type
    public final static String SIGNIN = "signin";
    public final static String LOGIN = "login";
    public final static String GETALLUSER = "get_all_user";
    public final static String ADD_NEW_PASSWORD = "add_password";
    public final static String DISPLAY_PASSWORD = "dispaly_password";
    public final static String EDIT_PASSWORD = "edit_password";
    public final static String DELETE_PASSWORD = "delete_password";
    public final static String LOGIN_ACCEPTED = "login_accept";
    public final static String LOGIN_REJECTED = "login_reject";

    //handshaking protocol massage Type
    public final static String REQUEST_PUBLIC_KEY = "req_public_key";
    public final static String SEND_PUBLIC_KEY = "send_public_key";
    public final static String SEND_SESSION_KEY = "send_session_key";
    public final static String ACCEPT_SESSION_KEY = "accept_session_key";


    //    Json Key Name
    public final static String USERNAME = "username";
    public final static String PASSWORD = "password";
    public final static String ADDRESS = "address";
    public final static String EMAIL = "email";
    public final static String DESCRIPTION = "description";
    public final static String ATTACHED_FILE = "attached";

    //    AES ALGO
    public final static String ALGORITHM = "AES/CBC/PKCS5Padding";
}

import java.net.ServerSocket;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        // write your code here

        try {
//             TODO code application logic here
            ServerSocket s = new ServerSocket(StaticVariable.PORT);
            while (true) {
                System.out.println("-------------------------------------------------------");
                new ClientHandler(s.accept()).start();

            }
        } catch (IOException ex) {
            System.out.println(";;;;;;;;;;;;;;;;;;;;;;;");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

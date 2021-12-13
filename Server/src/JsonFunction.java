import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Alaa Alkheder
 * Email:alaa-alkheder@outlook.com
 * Github:alaa-alkheder
 */
public class JsonFunction {

    public  static JSONObject decode(String s) throws ParseException {

             return  (JSONObject) new JSONParser().parse(s);

        }

}

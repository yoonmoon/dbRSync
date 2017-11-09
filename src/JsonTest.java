import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonTest {

	public static void main(String[] args) {
		 
      try {
      	BufferedReader reader = new BufferedReader(new FileReader("config.json"));
         String txt = new String();
      	String line;
         while ((line = reader.readLine()) != null)
         {
           txt+=line;
         }
         System.out.println("txt="+txt);
      	JSONObject jsonObject = new JSONObject( txt );
      	System.out.println("json="+jsonObject.toString());
      } catch (Exception e) {
          e.printStackTrace();
      }

	}
}

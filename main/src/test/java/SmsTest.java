import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SmsTest {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args){
        SmsTest http = new SmsTest();

        System.out.println("Testing 1 - Send Http GET request");
        try {
            http.sendGet();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendGet() throws Exception {

//        String url = "http://safexi.ru/api.php?action=send&login=knpdeveloper@gmail.com&pass=25oct87!&number=79263959143&name=Grimm&mess=Just+a+test+sms";
        String url = "http://safexi.ru/api.php?action=code_sms&login=knpdeveloper@gmail.com&pass=25oct87!";
//        String url = "http://safexi.ru/api.php?action=status&code=565682639";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        //name
//        con.addRequestProperty("login","knpdeveloper@gmail.com");
//        con.addRequestProperty("pass","25oct87!");
//        con.addRequestProperty("number","79263959143");
//        con.addRequestProperty("name","GrimmStory");
//        con.addRequestProperty("mess","Just a test sms");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

//    protected String addLocationToUrl(String url){
//        if(!url.endsWith("?"))
//            url += "?";
//
//        List<NameValuePair> params = new LinkedList<NameValuePair>();
//
//        if (lat != 0.0 && lon != 0.0){
//            params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
//            params.add(new BasicNameValuePair("lon", String.valueOf(lon)));
//        }
//
//        if (address != null && address.getPostalCode() != null)
//            params.add(new BasicNameValuePair("postalCode", address.getPostalCode()));
//        if (address != null && address.getCountryCode() != null)
//            params.add(new BasicNameValuePair("country",address.getCountryCode()));
//
//        params.add(new BasicNameValuePair("user", agent.uniqueId));
//
//        String paramString = URLEncodedUtils.format(params, "utf-8");
//
//        url += paramString;
//        return url;
//    }

}

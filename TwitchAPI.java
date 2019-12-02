
 
package streamercollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;





/**
 *
 * @author cesar
 */
public class TwitchAPI {
    private TwitchBaseURL URL = new TwitchBaseURL();
    private TwitchDashboardInformation DASHBOARD = new TwitchDashboardInformation();    
    final String APEX_GAME_ID = "511224";
    final String MAX_STREAMERS_PER_CALL = "100";
    
    public String getStreamersPlaying(String game_id) throws IOException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("game_id", APEX_GAME_ID);
        return searchStreamersByGameRequest(parameters);
    }
    
    public String getStreamersPlaying(String game_id, String numOfStreamers) throws IOException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("game_id", APEX_GAME_ID);
        parameters.put("first", numOfStreamers);
        return searchStreamersByGameRequest(parameters);
    }
    
    public String getStreamersPlaying(String game_id, String numOfStreamers, String page) throws IOException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("game_id", game_id);
        parameters.put("first", numOfStreamers);
        
        if(!page.equals("")){
            parameters.put("after", page);}
        
        return searchStreamersByGameRequest(parameters);
    }
    
    
    public String getPagination(String jsonString){
        JSONObject JSONObj = new JSONObject(jsonString);        
        return JSONObj.getJSONObject("pagination").get("cursor").toString();
    }
   
    
    private String searchStreamersByGameRequest(Map<String, String> parameters) throws IOException {
        String url_string = URL.API+"streams?"+getParamsString(parameters);
        HttpURLConnection con = getConnection(url_string);

        int responseCode = makeCall(con, "GET");
        System.out.println("Response Code : " + responseCode);

        return getJSONString(con);
    }
    
    
    //AppAccessToken
    public String getAppAccessToken() throws UnsupportedEncodingException, IOException{
        String urlString = appAccessURL();
        HttpURLConnection con = getConnection(urlString);
        int responseCode = makeCall(con, "POST");
        System.out.println("Response Code : " + responseCode);
        return jsonToAccesToken(getJSONString(con));
    
    }
    
    private String appAccessURL() throws UnsupportedEncodingException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", DASHBOARD.CLIENT_ID);
        parameters.put("client_secret", DASHBOARD.CLIENT_SECRET);
        parameters.put("grant_type", "client_credentials");
        parameters.put("scope", "clips:edit");
        
        return URL.TOKEN+getParamsString(parameters);
    }
    
    private String jsonToAccesToken(String jsonString){
        System.out.println(jsonString);
        JSONObject JSONObj = new JSONObject(jsonString);
        return JSONObj.get("access_token").toString();
    }
    
    
    //Streamer ID
    public String getStreamerID(String streamerName) throws UnsupportedEncodingException, IOException{
        String urlString = getStreamerIdURL(streamerName);
        System.out.println(urlString);
        HttpURLConnection con = getConnection(urlString);
        int responseCode = makeCall(con, "GET");        
        String json = getJSONString(con);
        return jsonToStreamerID(json);
    }
    
    private String getStreamerIdURL(String streamerName) throws UnsupportedEncodingException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("login", streamerName);
        return URL.API+"users?"+getParamsString(parameters);
    }
    
    private String jsonToStreamerID(String json){
        JSONObject JSONObj = new JSONObject(json);
        JSONArray arr = new JSONArray(JSONObj.get("data").toString());
        return arr.getJSONObject(0).get("id").toString();
    }
    
    
    //Clip Streamer
    public String clipStreamerNow(String broadcasterID, String token) throws IOException{
        String clipURL = getClipURL(broadcasterID);
        HttpURLConnection con = getConnection(clipURL);
        int responseCode = makeCall(con, "POST", token);
        System.out.println("Response Code : " + responseCode);

        return jsonToClipURL(getJSONString(con));
    }
    
    private String getClipURL(String broadcasterID) throws UnsupportedEncodingException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("broadcaster_id", broadcasterID);
        return URL.API+"clips?"+getParamsString(parameters);
    }
    
    
    private int makeCall(HttpURLConnection con,String requestType, String token) throws ProtocolException, IOException{
        con.setRequestMethod(requestType);
        con.setRequestProperty("Client-ID",DASHBOARD.CLIENT_ID);
        con.setRequestProperty("Authorization","Bearer "+token);
        return con.getResponseCode();
    }
    
    private String jsonToClipURL(String json){
        JSONObject JSONObj = new JSONObject(json);
        JSONArray arr = new JSONArray(JSONObj.get("data").toString());
        return arr.getJSONObject(0).get("edit_url").toString();
    }
    
    
    //Validate
    public String validateAuthToken(String token) throws IOException{
        HttpURLConnection con = getConnection(URL.VALIDATE);
        int responseCode = makeValidateCall(con, "GET", token);
        System.out.println("Response Code : " + responseCode);

        return getJSONString(con);
    }
    
    private int makeValidateCall(HttpURLConnection con,String requestType, String token) throws ProtocolException, IOException{ 
         con.setRequestMethod(requestType);
         con.setRequestProperty("Authorization","OAuth "+token);
         con.setRequestProperty("Client-ID",DASHBOARD.CLIENT_ID);
         return con.getResponseCode();
    }
    
    
    //Authentication Website 
    public void openUserTokenAuthWebsite() throws UnsupportedEncodingException, IOException, URISyntaxException{
        String url = getAuthURLString();
        HttpURLConnection con = getConnection(url);
        int responseCode = makeCall(con, "GET");
        System.out.println("Response Code : " + responseCode);
        openWebpage(con);
    }
    
    private String getAuthURLString() throws UnsupportedEncodingException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", DASHBOARD.CLIENT_ID);
        parameters.put("redirect_uri", DASHBOARD.CALLBACK_URI);
        parameters.put("response_type", "token");
        parameters.put("scope", "clips:edit");
        
        return URL.AUTHORIZE+getParamsString(parameters);
    }
    
    private void openWebpage(HttpURLConnection con) throws URISyntaxException, IOException{
        java.awt.Desktop.getDesktop().browse(con.getURL().toURI());
    }
    
    
    //General Functions 
    private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
 
        for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          result.append("&");
        }
 
        String resultString = result.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
    
    private HttpURLConnection getConnection(String urlString) throws MalformedURLException, IOException{
        URL url = new URL(urlString);
        return (HttpURLConnection) url.openConnection();
    }
    
    private int makeCall(HttpURLConnection con,String requestType) throws ProtocolException, IOException{
        con.setRequestMethod(requestType);
        con.setRequestProperty("Client-ID",DASHBOARD.CLIENT_ID);
        return con.getResponseCode();
    }
    
    private String getJSONString(HttpURLConnection con) throws IOException{
        BufferedReader iny = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer jsonres = new StringBuffer();

        while ((output = iny.readLine()) != null) {
            jsonres.append(output);
        }
        iny.close();
        
        return jsonres.toString();
    }
    
    public ArrayList<String> getNames(String jsonString){
        JSONObject JSONObj = new JSONObject(jsonString);
        JSONArray arr = new JSONArray(JSONObj.get("data").toString());
        
        ArrayList<String>  result = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            
            result.add(arr.getJSONObject(i).get("user_name").toString());
        }
        return result;
        
    }
}

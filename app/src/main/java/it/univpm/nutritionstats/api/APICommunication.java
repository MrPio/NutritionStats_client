package it.univpm.nutritionstats.api;

//import org.json.JSONObject;

import org.json.simple.*;
import org.threeten.bp.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import it.univpm.nutritionstats.activity.MainActivity;

public class APICommunication {
    final static String API_BASE_URL          = "http://192.168.1.5:5000";
    final static String ENDPOINT_EAN          = "/api/ean/";
    final static String ENDPOINT_SIGNUP       = "/signup";
    final static String ENDPOINT_LOGIN        = "/login";
    final static String       ENDPOINT_TODAY_VALUES = "/diary/";
    static       JSONObject[] outputJson            = {null};

    public static JSONObject getInfoFromEan(String ean) {
        HttpURLConnection conn =null;
        try {
            conn = (HttpURLConnection)new URL(API_BASE_URL + ENDPOINT_EAN + ean).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String,String> response=new HashMap<>();
            response.put("result","error: "+e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestSignUp(String userName, String userEmail, int born, MainActivity.Diet diet, int weight, int height, MainActivity.Gender gender) {
        String url = API_BASE_URL + ENDPOINT_SIGNUP
                + "?nickname=" + userName
                + "&email=" + userEmail
                + "&year=" + born
                + "&weight=" + weight
                + "&height=" + height
                + "&diet=" + diet.name()
                + "&gender=" + gender.name();

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        } catch (IOException e) {
            HashMap<String,String> response=new HashMap<>();
            response.put("result","error: "+e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestLogin(String token) {
        HttpURLConnection conn=null;
        try {
            String url = API_BASE_URL + ENDPOINT_LOGIN
                    + "?token=" + token;
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String,String> response=new HashMap<>();
            response.put("result","error: "+e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestTodayValues(String token) {
        String dayId =
                String.valueOf(LocalDate.now().getDayOfMonth()) + "-" +
                        String.valueOf(LocalDate.now().getMonthValue()) + "-" +
                        String.valueOf(LocalDate.now().getYear());
        String url = API_BASE_URL + ENDPOINT_TODAY_VALUES + dayId
                + "?token=" + token;
        HttpURLConnection conn=null;
        try {
        conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String,String> response=new HashMap<>();
            response.put("result","error: "+e.getMessage());
            return new JSONObject(response);
        }
        return  makeRequest(conn);
    }

    private static JSONObject makeRequest(HttpURLConnection conn){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    outputJson[0] = (JSONObject) JSONValue.parseWithException(readResult(conn));
                    outputJson[0].toJSONString();
                } catch (Exception e) {
                    HashMap<String,String> response=new HashMap<>();
                    response.put("result","error: "+e.getMessage());
                    outputJson[0]=new JSONObject(response);
                }
            }
        });
        thread.start();


        while (outputJson[0] == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JSONObject response=outputJson[0];
        outputJson[0]=null;
        return response;
    }

    private static String readResult(HttpURLConnection conn) throws IOException {
        StringBuilder data = new StringBuilder();
        String line = "";
        InputStream in;
        //TODO STUCK HERE!
        if (conn.getResponseCode() < 400)
            in = conn.getInputStream();
        else
            in = conn.getErrorStream();

        InputStreamReader inR = new InputStreamReader(in);
        BufferedReader buf = new BufferedReader(inR);

        while ((line = buf.readLine()) != null)
            data.append(line);
        return data.toString();
    }
}

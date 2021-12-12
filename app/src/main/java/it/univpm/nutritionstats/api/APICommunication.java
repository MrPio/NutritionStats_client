package it.univpm.nutritionstats.api;

//import org.json.JSONObject;

import org.json.simple.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import it.univpm.nutritionstats.MainActivity;

public class APICommunication {
    final String API_BASE_URL    = "http://192.168.1.16:5000";
    final String ENDPOINT_EAN    = "/api/ean/";
    final String ENDPOINT_SIGNUP = "/signup";

    public JSONObject getInfoFromEan(String ean) {
        final JSONObject[] outputJson = {null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection conn =
                            new URL(API_BASE_URL + ENDPOINT_EAN + ean).openConnection();

                    StringBuilder data = new StringBuilder();
                    String line = "";
                    try (InputStream in = conn.getInputStream();) {
                        InputStreamReader inR = new InputStreamReader(in);
                        BufferedReader buf = new BufferedReader(inR);

                        while ((line = buf.readLine()) != null)
                            data.append(line);
                    }
                    outputJson[0] = (JSONObject) JSONValue.parseWithException(data.toString());
                } catch (Exception e) {
                    e.printStackTrace();
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
        return outputJson[0];
    }

    public JSONObject requestSignUp(String userName, String userEmail, int born, MainActivity.Diet diet, int weight, int height, MainActivity.Gender gender) {
        final JSONObject[] outputJson = {null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = API_BASE_URL + ENDPOINT_SIGNUP
                            + "?nickname=" + userName
                            + "&email=" + userEmail
                            + "&year=" + born
                            + "&weight=" + weight
                            + "&height=" + height
                            + "&diet=" + diet.name()
                            + "&gender=" + gender.name();

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    StringBuilder data = new StringBuilder();
                    String line = "";
                    try (InputStream in = conn.getInputStream();) {
                        InputStreamReader inR = new InputStreamReader(in);
                        BufferedReader buf = new BufferedReader(inR);

                        while ((line = buf.readLine()) != null)
                            data.append(line);
                    }
                    outputJson[0] = (JSONObject) JSONValue.parseWithException(data.toString());
                        /*
                        {
                            "email":"email@example.com",
                            "token":"G4UB53UYIHUER9UFHER8IVHRIU"
                        }
                         */

                } catch (Exception e) {
                    e.printStackTrace();
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
        return outputJson[0];
    }
}

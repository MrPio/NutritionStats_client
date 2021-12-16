package it.univpm.nutritionstats.api;

//import org.json.JSONObject;

import org.json.simple.*;
import org.threeten.bp.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.format.DateTimeFormatter;

import it.univpm.nutritionstats.activity.MainActivity;

public class APICommunication {
    final static String API_BASE_URL          = "http://192.168.1.5:5000";
    final static String ENDPOINT_EAN          = "/api/ean/";
    final static String ENDPOINT_SIGNUP       = "/signup";
    final static String ENDPOINT_LOGIN        = "/login";
    final static String ENDPOINT_TODAY_VALUES = "/diary/";

    public static JSONObject getInfoFromEan(String ean) {
        final JSONObject[] outputJson = {null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn =
                            (HttpURLConnection)new URL(API_BASE_URL + ENDPOINT_EAN + ean).openConnection();

                    outputJson[0] = (JSONObject) JSONValue.parseWithException(readResult(conn));
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

    public static JSONObject requestSignUp(String userName, String userEmail, int born, MainActivity.Diet diet, int weight, int height, MainActivity.Gender gender) {
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

                    outputJson[0] = (JSONObject) JSONValue.parseWithException(readResult(conn));
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

    public static JSONObject requestLogin(String token) {
        final JSONObject[] outputJson = {null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = API_BASE_URL + ENDPOINT_LOGIN
                            + "?token=" + token;

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");

                    outputJson[0] = (JSONObject) JSONValue.parseWithException(readResult(conn));
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

    public static JSONObject requestTodayValues(String token) {
        final JSONObject[] outputJson = {null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String dayId =
                            String.valueOf(LocalDate.now().getDayOfMonth()) + "-" +
                                    String.valueOf(LocalDate.now().getMonthValue()) + "-" +
                                    String.valueOf(LocalDate.now().getYear());
                    String url = API_BASE_URL + ENDPOINT_TODAY_VALUES + dayId
                            + "?token=" + token;

                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");

                    outputJson[0] = (JSONObject) JSONValue.parseWithException(readResult(conn));
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

    private static String readResult(HttpURLConnection conn) throws IOException {
        StringBuilder data = new StringBuilder();
        String line = "";
        InputStream in;
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

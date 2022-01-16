package it.univpm.nutritionstats.api;

//import org.json.JSONObject;

import org.json.simple.*;
import org.threeten.bp.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.univpm.nutritionstats.activity.MainActivity;
import it.univpm.nutritionstats.utility.Elements;

public class APICommunication {
    final static int TIME_OUT_ERROR=20000;
    //public final static String       API_BASE_URL              = "https://nutritionstatsoop.herokuapp.com";
    public final static String       API_BASE_URL              = "http://192.168.1.8:5000";
    final static        String       ENDPOINT_EAN              = "/api/ean/";
    final static        String       ENDPOINT_NAME             = "/api/name/";
    final static        String       ENDPOINT_SIGNUP           = "/signup";
    final static        String       ENDPOINT_LOGIN            = "/login";
    final static        String       ENDPOINT_TODAY_VALUES     = "/diary/";
    final static        String       ENDPOINT_ADD_FOOD_BY_EAN  = "/add/food/by_ean";
    final static        String       ENDPOINT_ADD_FOOD_BY_NAME = "/add/food/by_name";
    final static        String       ENDPOINT_ADD_WATER        = "/add/water";
    final static        String       ENDPOINT_DIARY_INFO       = "/diary";
    final static        String       ENDPOINT_UPDATE_WEIGHT    = "/update_weight";
    final static        String       ENDPOINT_STATISTIC        = "/stats";
    final static        String       ENDPOINT_FILTERS        = "/filters";
    static              JSONObject[] outputJson                = {null};

    public static JSONObject getInfoFromEan(String ean) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(API_BASE_URL + ENDPOINT_EAN + ean).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject getInfoFromFoodName(String foodName, int weight, String measure) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(API_BASE_URL + ENDPOINT_NAME + foodName
                    + "?portion_weight=" + weight
                    + "&unit_of_measure=" + measure).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestSignUp(String userName, String userEmail, Date birth,
                                           MainActivity.Diet diet, int weight, int height, MainActivity.Gender gender) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String url = API_BASE_URL + ENDPOINT_SIGNUP
                + "?nickname=" + userName
                + "&email=" + userEmail
                + "&year=" + df.format(birth)
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
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestLogin(String token) {
        HttpURLConnection conn = null;
        try {
            String url = API_BASE_URL + ENDPOINT_LOGIN
                    + "?token=" + token;
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestTodayValues(String token) {
        String dayId;
        if (MainActivity.dateForValues == null) {
            dayId = String.format("%02d",LocalDate.now().getDayOfMonth()) + "-" +
                    String.format("%02d",LocalDate.now().getMonthValue()) + "-" +
                    String.format("%02d",LocalDate.now().getYear());
        } else
            dayId = MainActivity.dateForValues;

        String url = API_BASE_URL + ENDPOINT_TODAY_VALUES + dayId
                + "?token=" + token;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestAddFoodByName(String token, MainActivity.MealType mealType,
                                                  String foodName, int portionWeight) {
        String url = API_BASE_URL + ENDPOINT_ADD_FOOD_BY_NAME
                + "?token=" + token
                + "&day_id=" + generateTodayId()
                + "&meal_type=" + mealType.name()
                + "&food_name=" + foodName
                + "&portion_weight=" + portionWeight;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestAddFoodByEan(String token, MainActivity.MealType mealType,
                                                 long ean, int portionWeight) {
        String url = API_BASE_URL + ENDPOINT_ADD_FOOD_BY_EAN
                + "?token=" + token
                + "&day_id=" + generateTodayId()
                + "&meal_type=" + mealType.name()
                + "&ean_code=" + ean
                + "&portion_weight=" + portionWeight;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestAddWater(String token, int portionVolume) {
        String url = API_BASE_URL + ENDPOINT_ADD_WATER
                + "?token=" + token
                + "&day_id=" + generateTodayId()
                + "&portion_volume=" + portionVolume;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static HashMap<MainActivity.MealType, ArrayList<String>> requestFoodList(String token) {
        HashMap<MainActivity.MealType, ArrayList<String>> foodList = new HashMap<>();
        String dayId;
        if (MainActivity.dateForValues == null) {
            dayId = String.format("%02d",LocalDate.now().getYear()) + "-" +
                    String.format("%02d",LocalDate.now().getMonthValue()) + "-" +
                    String.format("%02d",LocalDate.now().getDayOfMonth());
        } else {
            String[] split = MainActivity.dateForValues.split("-");
            dayId = split[2] + "-" + split[1] + "-" + split[0];
        }

        String url = API_BASE_URL + ENDPOINT_DIARY_INFO
                + "?token=" + token;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        JSONObject response = (JSONObject) makeRequest(conn).get("diary");
        for (Object day : (JSONArray) response.get("dayList")) {
            if (((JSONObject) day).get("date").equals(dayId)) {
                for (Object meal : (JSONArray) Objects.requireNonNull(((JSONObject) day).get("mealList"))) {
                    MainActivity.MealType mealType =
                            MainActivity.MealType.valueOf(((JSONObject) meal).get("mealType").toString());
                    foodList.put(mealType, new ArrayList<>());
                    for (Object food : (JSONArray) Objects.requireNonNull(((JSONObject) meal).get("foodList")))
                        foodList.get(mealType).add(((JSONObject) food).get("name").toString());
                }
            }
        }
        return foodList;
    }

    public static JSONObject requestUpdateWeight(String token, String data, Float weight) {
        String url = API_BASE_URL + ENDPOINT_UPDATE_WEIGHT
                + "?token=" + token
                + "&weight=" + weight
                + "&date=" + data;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("PUT");
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn);
    }

    public static JSONObject requestStats(String token, String startDate, String endDate, ArrayList<Elements> elements) {
        String nutrientList = "[";
        for (int i = 0; i < elements.size(); i++) {
            if (i == elements.size() - 1)
                nutrientList += "\"" + elements.get(i).name() + "\"";
            else
                nutrientList += "\"" + elements.get(i).name() + "\",";
        }
        nutrientList+="]";
        String url = API_BASE_URL + ENDPOINT_STATISTIC
                + "?token=" + token
                + "&type=PERCENTAGE, MEAN, STANDARD_DEVIATION, CORRELATION";
        String jsonBody = "{\n" +
                "    \"start_date\": \"" + startDate + "\",\n" +
                "    \"end_date\": \"" + endDate + "\"\n" +
                //"     \"nutrient_name\":" + nutrientList + "\n"+
                "}";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn,jsonBody);
    }

    public static JSONObject requestFilters(String token, String startDate, String endDate, ArrayList<Elements> elements) {
        String nutrientList = "[";
        for (int i = 0; i < elements.size(); i++) {
            if (i == elements.size() - 1)
                nutrientList += "\"" + elements.get(i).name() + "\"";
            else
                nutrientList += "\"" + elements.get(i).name() + "\",";
        }
        nutrientList+="]";
        String url = API_BASE_URL + ENDPOINT_FILTERS
                + "?token=" + token;
        String jsonBody = "{\n" +
                "    \"start_date\": \"" + startDate + "\",\n" +
                "    \"end_date\": \"" + endDate + "\"\n" +
                //"     \"nutrient_name\":" + nutrientList + "\n"+
                "}";
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
        } catch (IOException e) {
            HashMap<String, String> response = new HashMap<>();
            response.put("result", "error: " + e.getMessage());
            return new JSONObject(response);
        }
        return makeRequest(conn,jsonBody);
    }


    private static String generateTodayId() {
        String dayId;
        if (MainActivity.dateForValues == null) {
            dayId = String.format("%02d",LocalDate.now().getDayOfMonth()) + "-" +
                    String.format("%02d",LocalDate.now().getMonthValue()) + "-" +
                    String.format("%02d",LocalDate.now().getYear());
        } else
            dayId = MainActivity.dateForValues;
        return dayId;
    }

    private static JSONObject makeRequest(HttpURLConnection conn,String ...body) {
        Thread thread = new Thread(() -> {
            try {
                if(body.length>0){
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = body[0].getBytes(StandardCharsets.US_ASCII);
                        os.write(input);
                    }
                }

                outputJson[0] = (JSONObject) JSONValue.parseWithException(readResult(conn));
                outputJson[0].toJSONString();
            } catch (Exception e) {
                HashMap<String, String> response = new HashMap<>();
                response.put("result", "error: " + e.getMessage());
                outputJson[0] = new JSONObject(response);
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
        JSONObject response = outputJson[0];
        outputJson[0] = null;
        return response;
    }

    private static String readResult(HttpURLConnection conn) throws IOException {
        final InputStream[] in = new InputStream[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (conn.getResponseCode() < 400)
                        in[0] = conn.getInputStream();
                    else
                        in[0] = conn.getErrorStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        StringBuilder data = new StringBuilder();
        String line = "";
        //se non trova il server continua ad aspettare per 15-20 min la risposta;
        long timer = 0;
        while (in[0] == null) {
            if (timer > TIME_OUT_ERROR)
                return "{\"result\":\"error: request timeout\"}";
            try {
                Thread.sleep(100);
                timer += 100;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        InputStreamReader inR = new InputStreamReader(in[0]);
        BufferedReader buf = new BufferedReader(inR);

        while ((line = buf.readLine()) != null)
            data.append(line);
        return data.toString();
    }


}

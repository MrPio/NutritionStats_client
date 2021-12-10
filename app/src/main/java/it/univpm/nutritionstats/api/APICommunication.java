package it.univpm.nutritionstats.api;

//import org.json.JSONObject;

import org.json.simple.*;
import org.json.simple.parser.ParseException;

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

public class APICommunication {
    String baseUrl;

    public APICommunication(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public JSONObject getInfoFromEan(String ean) {
        final JSONObject[] outputJson = {null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    String route = "/api/ean/" + ean;
                    try {

                        URLConnection openConnection = new URL(baseUrl+route).openConnection();
                        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                        InputStream in = openConnection.getInputStream();

                        StringBuilder data = new StringBuilder();
                        String line = "";
                        try {
                            InputStreamReader inR = new InputStreamReader( in );
                            BufferedReader buf = new BufferedReader( inR );

                             while ( ( line = buf.readLine() ) != null ) {
                                data.append(line);
                                System.out.println( line );
                            }
                        } finally {
                            in.close();
                        }
                        outputJson[0] = (JSONObject) JSONValue.parseWithException(data.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        while(outputJson[0]==null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return outputJson[0];
    }
}

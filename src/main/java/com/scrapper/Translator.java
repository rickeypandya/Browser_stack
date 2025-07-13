package com.scrapper;

import java.net.URI;
import java.net.http.*;
import java.util.*;
import org.json.*;

public class Translator {
    private static final String API_URL = "https://ultra-fast-translation.p.rapidapi.com/t";
    private static final String API_KEY = "c2432b444dmsh909f603234f0c69p1a0e09jsne64b3c285be3";
    private static final String API_HOST = "ultra-fast-translation.p.rapidapi.com";

    public static String translate(String text, String toLang) {
        try {
            // Create request body
            JSONArray qArray = new JSONArray();
            qArray.put(text);

            JSONObject jsonBody = new JSONObject()
                    .put("from", "es") // Spanish as source language
                    .put("to", toLang)
                    .put("e", "")
                    .put("q", qArray);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("X-RapidAPI-Key", API_KEY)
                    .header("X-RapidAPI-Host", API_HOST)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("RAW RESPONSE:\n" + response.body());
                    
                    JSONArray resultArray = new JSONArray(response.body());
                    return resultArray.length() > 0 ? resultArray.getString(0) : "";

        } catch (Exception e) {
            System.err.println("Translation error: " + e.getMessage());
            return "";
        }
    }
    
}

    

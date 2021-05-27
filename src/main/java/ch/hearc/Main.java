package ch.hearc;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.CDL;

import java.io.*;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;


import java.util.Scanner;


public class Main {

    final static String TOKEN = "Bearer gCrK-Tyj_wqgSp8UkHBzgTmXOJhGHDxxUwffP8mtC5Mfa5gtLD03ygJ2wbT0F2pYLFLXpCoGe7MWpy1melt_9eAbmMOeIu-zcd3NlWHZiqIVJx_4hebvfDXQ1f8u34VT9u5ftgTKxkAKV_caDx2F6Fg4lhSf_aczu_kPz4ZMUvhQsmzlnOw1ooOvcfa3RZWkKIzk_kfr_a538qQGq_pAsyRYEdiJVlOVzogjYz8sVWgbJFRO6Yky8hkvWIB4JpOKl1tnu2VqTt8SqmXvCea38WS1Snzc9ynZNG0LxftRaI_r6owgoLWgZjmOKAOl0xs-FqA4hsqGp-pr3d1-Phn1MQ";

    public static void main(String[] args) {

        try {

            getMetadata("13893");
            getAttachement("13896");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void getMetadata(String fileID) {

        try {
            // Création de connexion
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://157.26.82.44:2240/api/document/").append(fileID).append("/metadata");
            URL url = new URL(stringBuilder.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Création de la requête
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", TOKEN);
            con.connect();

            int responseCode = con.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else

                // Sort le code et la réponse
                System.out.println(con.getResponseCode() + " " + con.getResponseMessage());

            // Traitement du JSON
            String inline = "";
            Scanner scanner = new Scanner(con.getInputStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            //Close the scanner
            scanner.close();

            //Using the JSON simple library parse the string into a json object
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(inline);

            //Affichage de la réponse entière
            //System.out.println(data_obj.toString());

            //Affichage d'un élément du JSON
            //System.out.println(data_obj.get("ObjectID"));

            // Pretty Print
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(data_obj.toString());
            String prettyJsonString = gson.toJson(je);
            System.out.println(prettyJsonString);


            org.json.JSONArray jsonArray = new JSONArray();
            jsonArray.put(data_obj);

            StringBuilder sb = new StringBuilder().append(fileID).append("_").append("metadata").append(".csv");

            File file = new File(sb.toString());
            String csv = CDL.toString(jsonArray);
            FileUtils.writeStringToFile(file, csv);

            // Fermeture de la connexion
            con.disconnect();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static void getAttachement(String fileID) {
        try {
            // Création de connexion
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://157.26.82.44:2240/api/document/").append(fileID).append("/attachment");
            URL url = new URL(stringBuilder.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Création de la requête
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", TOKEN);
            con.connect();

            int responseCode = con.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else

                // Sort le code et la réponse
                System.out.println(con.getResponseCode() + " " + con.getResponseMessage());

            // Traitement du JSON
            String inline = "";
            Scanner scanner = new Scanner(con.getInputStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            //Close the scanner
            scanner.close();

            //Using the JSON simple library parse the string into a json object
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(inline);

            //Affichage de la réponse entière
            //System.out.println(data_obj.toString());

            //Affichage d'un élément du JSON
            //System.out.println(data_obj.get("ObjectID"));


            // Fermeture de la connexion
            con.disconnect();

            // PDF Reader
            StringBuilder sb = new StringBuilder().append(fileID).append("_").append("attachment").append(".pdf");
            OutputStream out = new FileOutputStream(sb.toString());
            out.write(java.util.Base64.getDecoder().decode(data_obj.get("File").toString()));
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

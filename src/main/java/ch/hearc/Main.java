package ch.hearc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

  final static String TOKEN = "Bearer gCrK-Tyj_wqgSp8UkHBzgTmXOJhGHDxxUwffP8mtC5Mfa5gtLD03ygJ2wbT0F2pYLFLXpCoGe7MWpy1melt_9eAbmMOeIu-zcd3NlWHZiqIVJx_4hebvfDXQ1f8u34VT9u5ftgTKxkAKV_caDx2F6Fg4lhSf_aczu_kPz4ZMUvhQsmzlnOw1ooOvcfa3RZWkKIzk_kfr_a538qQGq_pAsyRYEdiJVlOVzogjYz8sVWgbJFRO6Yky8hkvWIB4JpOKl1tnu2VqTt8SqmXvCea38WS1Snzc9ynZNG0LxftRaI_r6owgoLWgZjmOKAOl0xs-FqA4hsqGp-pr3d1-Phn1MQ";

  public static void main(String[] args) {
    try {
      getMetadata("13893");
//      getAttachment("13896");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void getMetadata(String fileID) {
    final String TYPE = "metadata";
    try {
      JSONObject jsonObject = getJSON(TYPE, fileID);

      // Pretty Print
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      JsonParser jp = new JsonParser();
      JsonElement je = jp.parse(jsonObject.toString());
      String prettyJsonString = gson.toJson(je);
//      exportToCSV(fileID, prettyJsonString);
      System.out.println(prettyJsonString);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static void getAttachment(String fileID) {
    final String TYPE = "attachment";
    try {
      JSONObject jsonObject = getJSON(TYPE, fileID);

      // PDF Reader
      StringBuilder sb = new StringBuilder().append(fileID).append(".pdf");
      OutputStream out = new FileOutputStream(sb.toString());
      out.write(java.util.Base64.getDecoder().decode(jsonObject.get("File").toString()));
      out.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /*  public static void exportToCSV(String fileID, String jsonString) throws IOException {
      StringBuilder sb = new StringBuilder().append(fileID).append(".csv");
      JsonNode jsonTree = new ObjectMapper().readTree(jsonString);
      Builder csvSchemaBuilder = CsvSchema.builder();
      JsonNode firstObject = jsonTree.elements().next();
      firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
      CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
      CsvMapper csvMapper = new CsvMapper();
      csvMapper.writerFor(JsonNode.class)
          .with(csvSchema)
          .writeValue(new File(sb.toString()), jsonTree);
    }
  */
  public static JSONObject getJSON(String type, String fileID) throws IOException, ParseException {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("http://157.26.82.44:2240/api/document/").append(fileID)
        .append("/").append(type);
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
    } else {
      System.out.println(con.getResponseCode() + " " + con.getResponseMessage());
    }

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
    con.disconnect();

    return data_obj;
  }
}

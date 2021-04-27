package com.example.mmt.springboot.dao.filereader;

import com.example.mmt.springboot.dao.BaseDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// this will read the data from airport country file
public class AirportCountryFileReaderDao implements BaseDao {

//    public static void main(String[] args) {
//        AirportCountryFileReaderDao airportCountryFileReaderDao = new AirportCountryFileReaderDao();
//        airportCountryFileReaderDao.processInputFile("src/main/resources/static/airports-ivtest-countries.json");
//    }
//

    public static Map<String, String> airportCountryMap =
            processInputFile("src/main/resources/static/airports-ivtest-countries.json");
//    public Map<String, String> getAirportCountryMapping(){
//        return processInputFile();
//    }

    private static Map<String, String> processInputFile(String inputFilePath) {

        JSONParser jsonParser = new JSONParser();
        Map<String, String> airportCountryMap = new HashMap<>();

        try (FileReader reader = new FileReader(inputFilePath))
        {
            //Read JSON file
            JSONObject airportCountryList = (JSONObject) jsonParser.parse(reader);
            airportCountryMap = new Gson().fromJson(
                    String.valueOf(airportCountryList), new TypeToken<HashMap<String, String>>() {}.getType()
            );


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return airportCountryMap;
    }

}

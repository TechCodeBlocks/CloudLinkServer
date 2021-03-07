package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class JSONWriter {
    public static void write(HashMap<String, HashMap<String, String>> filesdata){
        JSONArray fileList = new JSONArray();
        for (HashMap<String, String> filedata : filesdata.values()){
            JSONObject fileDetails = new JSONObject();
            fileDetails.put("uuid", filedata.get("uuid"));
            fileDetails.put("path", filedata.get("path"));
            fileDetails.put("date-edited", filedata.get("date-edited"));
            fileDetails.put("online", filedata.get("online"));
            fileList.add(filedata);



        }
        try (FileWriter file = new FileWriter("files.json")){
            file.write(fileList.toJSONString());
            file.flush();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}

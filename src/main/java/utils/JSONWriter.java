package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface JSONWriter {

    /**
     * @param filesdata
     * Takes a list of HashMaps which constitutes all requisite file data and stores it in the JSON format, writing this to a file.
     * Provides data persistence.
     */
    static void write(List<HashMap<String, String>> filesdata){
        JSONArray fileList = new JSONArray();
        for (HashMap<String, String> filedata : filesdata){
            JSONObject fileDetails = new JSONObject();
            fileDetails.put("_id", filedata.get("_id"));
            fileDetails.put("path", filedata.get("path"));
            fileDetails.put("date-edited", filedata.get("date-edited"));
            fileDetails.put("online", filedata.get("online"));
            fileList.add(filedata);



        }
        try (FileWriter file = new FileWriter(GlobalValues.basePath +"/files.json")){
            file.write(fileList.toJSONString());
            file.flush();
        }catch (IOException e){
            e.printStackTrace();
        }


    }
}

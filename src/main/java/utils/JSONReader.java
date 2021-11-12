package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface JSONReader {
    List<HashMap<String, String>> readFilesData = new ArrayList<HashMap<String, String>>();
    static List<HashMap<String,String>> read(){
        JSONParser jsonParser = new JSONParser();
        try(FileReader reader = new FileReader(GlobalValues.basePath + "/files.json")){
            Object obj = jsonParser.parse(reader);
            JSONArray fileList = (JSONArray) obj;
            fileList.forEach(filedata -> parseFileData((JSONObject) filedata));
        }catch (IOException e){
            e.printStackTrace();
        }catch(ParseException e){
            e.printStackTrace();
        }
        return readFilesData;
    }

    static void parseFileData(JSONObject fileData){
        HashMap<String, String> fileDataMap = new HashMap<String, String>();
        fileDataMap.put("_id", (String) fileData.get("_id"));
        fileDataMap.put("path", (String) fileData.get("path"));
        fileDataMap.put("date-edited", (String) fileData.get("date-edited"));
        fileDataMap.put("online", (String) fileData.get("online"));
        readFilesData.add(fileDataMap);

    }
}

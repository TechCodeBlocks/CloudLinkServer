package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileDataHandler {
    public static List<HashMap<String, String>> mergeLists(List<HashMap<String, String>> oldList, List<HashMap<String, String>> newList){
        List<HashMap<String, String>> mergedList = new ArrayList<HashMap<String, String>>();
        //add unchanged values using original from old list
        for (int i = 0; i < oldList.size(); i++) {
            if(newList.contains(oldList.get(i))){
                mergedList.add(oldList.get(i));
            }

        }
        //add updated values to the list using download from the web server. Web server should have the always up to date version.
        for (HashMap<String,String> fileItem :newList) {
            if(!mergedList.contains(fileItem)){
                mergedList.add(fileItem);

            }

        }

        return mergedList;

    }
}

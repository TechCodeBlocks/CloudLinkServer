package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface FileDataHandler {
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
    //Use to compare JSON data - from previous download and from current one.
     static void testContents(List<HashMap<String, String>> oldList, List<HashMap<String, String>> newList){
        for(HashMap<String,String> fileItem : newList){
            if(!oldList.contains(fileItem)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                if(LocalDate.parse(fileItem.get("date-edited"), formatter).isAfter(LocalDate.now().minusDays(5))){
                    HTTPClient.uploadFileData(fileItem);
                    //add as new item to cloud database - this method is probably better to run then just use the
                    //new list as the up to date one.
                }
            }

        }
        System.out.println("-----------------------");
        for(HashMap<String,String> fileItem : oldList){
            //If the file entry is in the old list but not the new one, the file must have been deleted between lists
            //being checked. Delete the entry from the cloud database
            System.out.println(fileItem.get("path"));
            if(!newList.contains(fileItem)){
                HTTPClient.deleteFileData(fileItem.get("_id"));
                System.out.println("old file deleted.");
                System.out.println(fileItem.get("path"));
            }

        }


    }
}

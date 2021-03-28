package utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileDataHandler {
//    public static List<HashMap<String, String>> mergeLists(List<HashMap<String, String>> oldList, List<HashMap<String, String>> newList){
//        List<HashMap<String, String>> mergedList = new ArrayList<HashMap<String, String>>();
//        //add unchanged values using original from old list
//        for (int i = 0; i < oldList.size(); i++) {
//            if(newList.contains(oldList.get(i))){
//                mergedList.add(oldList.get(i));
//            }
//
//        }
//        //add updated values to the list using download from the web server. Web server should have the always up to date version.
//        for (HashMap<String,String> fileItem :newList) {
//            if(!mergedList.contains(fileItem)){
//                mergedList.add(fileItem);
//
//            }
//
//        }
//
//        return mergedList;
//
//    }
//    public static void test(List<HashMap<String, String>> oldList, List<HashMap<String, String>> newList){
//        for(HashMap<String,String> fileItem : newList){
//            if(!oldList.contains(fileItem)){
//                if(LocalDate.parse(fileItem.get("date-edited")).isAfter(LocalDate.now().minusDays(1))){
//                    //add as new item to cloud database - this method is probably better to run then just use the
//                    //new list as the up to date one.
//                }
//            }
//        }
//
//
//    }
    public static void filterOldFiles(List<HashMap<String,String>> oldList, List<HashMap<String,String>> newList){
        List<String> newPaths = getPathsFromList(newList);
        List<String> oldPaths = getPathsFromList(oldList);
        for(HashMap<String,String> oldFileItem : oldList){
            if(!newPaths.contains(oldFileItem.get("path"))){
                //delete entry in cloud database for this value
            }
        }
    }
    private static List<String> getPathsFromList(List<HashMap<String, String>> existingFiles) {
        List<String> paths = new ArrayList<String>();
        for (HashMap<String, String> fileData: existingFiles
        ) {
            paths.add(fileData.get("path"));


        }
        return paths;
    }
}

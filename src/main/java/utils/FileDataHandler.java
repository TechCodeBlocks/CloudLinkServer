package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface FileDataHandler {

    /**
     * @param oldList Data read in from persistent storage.
     * @param newList Data produced from a live index of files present.
     * Compares the contents of the 2 lists.
     * If a file is not in the old list but is in the new list, then it is a new file, and its data must be added to the cloud index.
     * If a file is in the old list, but not the new one, then it is a now deleted file and its data must be removed from the cloud index.
     */
     static void testContents(List<HashMap<String, String>> oldList, List<HashMap<String, String>> newList){
        for(HashMap<String,String> fileItem : newList){
            if(!oldList.contains(fileItem)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                if(LocalDate.parse(fileItem.get("date-edited"), formatter).isAfter(LocalDate.now().minusDays(5))){
                    HTTPClient.uploadFileData(fileItem);
                }
            }

        }
        System.out.println("-----------------------");
        for(HashMap<String,String> fileItem : oldList){
            System.out.println(fileItem.get("path"));
            if(!newList.contains(fileItem)){
                HTTPClient.deleteFileData(fileItem.get("_id"));
                System.out.println("old file deleted.");
                System.out.println(fileItem.get("path"));
            }

        }


    }
}

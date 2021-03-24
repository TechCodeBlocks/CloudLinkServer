package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileCrawler {
    String baseFilePath;
    public FileCrawler(String baseFilePath){
        this.baseFilePath = baseFilePath;
    }

    public List<HashMap<String,String>> crawl(List<HashMap<String,String>> exisingFiles){
        List<String> existingFilePaths = getPathsFromList(exisingFiles);
        //recursively traverse file structure and build up a list of files. if there is a new path create a
        //new file entry with a new UUID.
        return new ArrayList<>();
    }

    private List<String> getPathsFromList(List<HashMap<String, String>> exisingFiles) {
        List<String> paths = new ArrayList<String>();
        for (HashMap<String, String> fileData: exisingFiles
             ) {
            paths.add(fileData.get("path"));


        }
        return paths;
    }
}

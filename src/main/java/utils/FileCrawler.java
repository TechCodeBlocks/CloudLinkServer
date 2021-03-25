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

    public List<HashMap<String,String>> crawl(List<HashMap<String,String>> existingFiles){
        List<String> existingFilePaths = getPathsFromList(existingFiles);
        //recursively traverse file structure and build up a list of files. if there is a new path create a
        //new file entry with a new UUID.
        List<HashMap<String,String>> newFileList = crawler(existingFilePaths, new File(baseFilePath), existingFiles);

        return newFileList;
    }
    private List<HashMap<String,String>> crawler(List<String> knownPaths, File directory, List<HashMap<String,String>> existingFiles){
        File[] files = directory.listFiles();
        if(files!= null){
            for(File file: files){
                if(file.isDirectory()){
                    crawler(knownPaths, file, existingFiles);
                }else{
                    if(!knownPaths.contains(file.getPath())){
                        //create new entry for file, add to existing files;
                    }
                }

            }
        }
        return existingFiles;

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

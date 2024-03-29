package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileCrawler {
    String baseFilePath;
    private static List<String> existingFilePaths =  new ArrayList<>();
    private static List<HashMap<String, String>> preexistingFiles = new ArrayList<>();
    public FileCrawler(String baseFilePath){
        this.baseFilePath = baseFilePath;
    }

    /**
     * @param existingFiles List of files that already are known to the system - prevents UUID/Path conflicts
     * @return List of HashMaps that constitute a complete list of files data.
     */
    public List<HashMap<String,String>> crawl(List<HashMap<String,String>> existingFiles){
        existingFilePaths = getPathsFromList(existingFiles);
        preexistingFiles.addAll(existingFiles);
        checkIfFilesExistStill();
        List<HashMap<String,String>> newFileList = crawler(existingFilePaths, new File(baseFilePath));
        preexistingFiles.addAll(newFileList);

        return preexistingFiles;
    }

    /**
     * @param knownPaths List of known paths (to avoid conflict)
     * @param directory Current directory that is to be crawled.
     * @return List of new files found in the directory
     * Recursively indexes the file tree.
     */
    private List<HashMap<String,String>> crawler(List<String> knownPaths, File directory){
        File[] files = directory.listFiles();
        List<HashMap<String,String>> newFileList = new ArrayList<>();
        if(files!= null){
            for(File file: files){
                if(file.isDirectory()){
                    newFileList.addAll(crawler(knownPaths, file));
                }else{

                    if(!knownPaths.contains(file.getPath())){
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        System.out.println("Found a new file: " + file.getPath());
                        HashMap<String,String> appendadbleValue = new HashMap<>();
                        appendadbleValue.put("_id", UUID.randomUUID().toString());
                        appendadbleValue.put("path", file.getPath());
                        appendadbleValue.put("date-edited", sdf.format(file.lastModified()));
                        appendadbleValue.put("online", "false");
                        newFileList.add(appendadbleValue);
                        //add file to cloud system
                        //create new entry for file, add to existing files;
                    }
                }

            }
        }
        return newFileList;

    }


    /**
     * @param existingFiles Full file data
     * @return Abstracted file data, only containing paths as this is all that is required by the crawler.
     */
    private List<String> getPathsFromList(List<HashMap<String, String>> existingFiles) {
        List<String> paths = new ArrayList<String>();
        for (HashMap<String, String> fileData: existingFiles
             ) {
            paths.add(fileData.get("path"));


        }
        return paths;
    }

    /**
     * Removes files that don't exist anymore from the list of indexed files.
     */
    private void checkIfFilesExistStill(){
        List<HashMap<String, String>> filesToRemove = new ArrayList<HashMap<String, String>>();
        for(HashMap<String,String> filesData : preexistingFiles){
            File fileToTest = new File(filesData.get("path"));
            if(!fileToTest.isFile()){
                filesToRemove.add(filesData);
            }
        }
        for(HashMap<String,String> filesData : filesToRemove){
            preexistingFiles.remove(filesData);
            System.out.println("file has been deleted" + filesData.get("path"));
            //delete file from cloud system
        }

    }
}

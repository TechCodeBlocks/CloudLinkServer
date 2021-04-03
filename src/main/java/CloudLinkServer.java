import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import utils.FileCrawler;
import utils.HTTPClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//program needs to load a json map of file data and send it to the cloud.
//This program map will not be changed or uploaded if no new files are detected
//Message handling:
//[new_file] -> use uuid provided to download file from blob - get data from cloud sync to add data to local map, get path to dl file to
//[req_file] -> upload requested uuid to lookup then upload the file.

public class CloudLinkServer {
    static HubConnection hubConnection;
    static String url = "https://cloudlinkmessage.azurewebsites.net/api";
    public static void main(String[] args) {
        FileCrawler fileCrawler = new FileCrawler("/Users/sandrarolfe/Documents/servertest");
        List<HashMap<String,String>> filesEmpty = new ArrayList<>();
//        HashMap<String,String> testEntry1 = new HashMap<>();
//        testEntry1.put("path","/Users/sandrarolfe/Documents/servertest/Test1.rtf");
//        filesEmpty.add(testEntry1);
//        HashMap<String,String> testEntry2 = new HashMap<>();
//        testEntry2.put("path","/Users/sandrarolfe/Documents/servertest/test2/.DS_Store");
//        filesEmpty.add(testEntry2);
        List<HashMap<String,String>> files = fileCrawler.crawl(filesEmpty);
        for(HashMap<String,String> file : files){
            System.out.println(file.get("_id"));
            System.out.println(file.get("path"));
            System.out.println(file.get("date-edited"));
            System.out.println(file.get("online"));
        }
        /*
        Connect to communications hub
        hubConnection = HubConnectionBuilder.create(url).build();
        hubConnection.start();
        */
        //HTTPClient.uploadFileData(files.get(1));
        HTTPClient.deleteFileData("28727e4e-cbe8-4bbc-ab3d-da90d440baaf");
    }
}

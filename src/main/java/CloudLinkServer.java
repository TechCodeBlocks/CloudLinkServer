import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//program needs to load a json map of file data and send it to the cloud.
//This program map will not be changed or uploaded if no new files are detected
//Message handling:
//[new_file] -> use uuid provided to download file from blob - get data from cloud sync to add data to local map, get path to dl file to
//[req_file] -> upload requested uuid to lookup then upload the file.
//[shutdown] -> initiate shutdown procedures for the server/system

public class CloudLinkServer implements FileDataHandler, JSONReader, JSONWriter, HTTPClient {
    static HubConnection hubConnection;
    static String url = "https://cloudlinkmessage.azurewebsites.net/api";

    public static void main(String[] args) {
        FileCrawler fileCrawler = new FileCrawler(GlobalValues.basePath);
        List<HashMap<String,String>> oldFiles = JSONReader.read();
        List<HashMap<String,String>> newFiles = fileCrawler.crawl(oldFiles);
        FileDataHandler.testContents(oldFiles, newFiles);
        JSONWriter.write(newFiles);
        setupHubConnection();



    }

    private static void setupHubConnection(){
        hubConnection = HubConnectionBuilder.create(url).build();
        hubConnection.start();

        hubConnection.on("file-req", (message) -> {
            String uuid = message;
            for (HashMap<String, String> fileData : GlobalValues.FileMap) {
                if (fileData.get("_id").equals(uuid)) {
                    HTTPClient.uploadFile(fileData);
                }
            }

        }, String.class);

        hubConnection.on("new-file", (message) -> {
            String uuid = message;
            HashMap<String, String> fileData = HTTPClient.getFileData(uuid);
            HTTPClient.downloadFile(fileData);

        }, String.class);

        //Also receive a message to shut down the system:
        hubConnection.on("shut-down", ()->{
            // any other shutDown procedures, eg save files list.

            try {
                Runtime runtime = Runtime.getRuntime();
                Process proc = runtime.exec("shutdown -s -t 0");
                System.exit(0);
            }catch (IOException e){
                System.out.println("Error shutting down");
            }
        });

    }
}

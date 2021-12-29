import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        GlobalValues.FileMap = newFiles;
        JSONWriter.write(newFiles);
        setupHubConnection();
        String test1 = "ad4799d3-42c0-4bec-8aba-7e44c7c7db7c";
        String test2 = "ad4799d3-42c0-4bec-8aba-7e44c7c7db7c";
        System.out.println(test1.equals(test2));



    }

    private static void setupHubConnection() {
        hubConnection = HubConnectionBuilder.create(url).build();
        hubConnection.start();
        //change message reveived systems to all use same target 'newMessage'. Message itself will be formatted:
        // instruction::data
        //hubConnection.send("file-req", "ad4799d3-42c0-4bec-8aba-7e44c7c7db7c");
        /*
         * 'newMessage' routing has been implemented. The previous method did not work as expected so this change had to
         * be made. Web Socket requests are now send using a custom packet format of 'opcode::operand'.
         * The code below takes this packet and carries out the necessary actions. The previous method is now deleted.
         * A similar system will be used in the client program.
         * */

        hubConnection.on("newMessage", (message) -> {
            System.out.println(message);
            String[] packetComponents = message.split("::");
            String uuid;

            if (packetComponents.length == 2) {
                switch (packetComponents[0]) {
                    case "file-req":
                        //substring required to account for oddities in sending system. shouldn't cause future problems
                        uuid = packetComponents[1].substring(0, 36);
                        for (HashMap<String, String> fileData : GlobalValues.FileMap) {
                            if (fileData.get("_id").equals(uuid)) {
                                HTTPClient.uploadFile(fileData);
                            }
                        }
                        break;
                    case "new-file":
                        uuid = packetComponents[1];
                        HashMap<String, String> fileData = HTTPClient.getFileData(uuid);
                        HTTPClient.downloadFile(fileData);

                }
            } else {
                if (packetComponents[0].equals("shut-down")) {
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        Process proc = runtime.exec("shutdown -s -t 0");
                        System.exit(0);
                    } catch (IOException e) {
                        System.out.println("Error shutting down");
                    }
                }
            }

        }, String.class);

    }
}

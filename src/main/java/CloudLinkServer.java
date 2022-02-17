import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CloudLinkServer implements FileDataHandler, JSONReader, JSONWriter, HTTPClient {
    static HubConnection hubConnection;
    static String url = "https://cloudlinkmessage.azurewebsites.net/api";

    /**
     * @param args
     * Entry point for the server program.
     * Each time the program is run, the files will be reindexed. This allows the program to be used in conjunction with
     * direct local access to the server.
     * Once read in, the up to date list of files is written to the index file.
     */
    public static void main(String[] args) {
        FileCrawler fileCrawler = new FileCrawler(GlobalValues.basePath);
        List<HashMap<String,String>> oldFiles = JSONReader.read();
        List<HashMap<String,String>> newFiles = fileCrawler.crawl(oldFiles);
        FileDataHandler.testContents(oldFiles, newFiles);
        GlobalValues.FileMap = newFiles;
        JSONWriter.write(newFiles);
        //setupHubConnection();




    }

    /**
     * Establish a connection to the SignalR Hub.
     * Set up the way in which message packets are handled by implementing the equivalent of a listener.
     * 'file-req' will trigger the upload of a file to Azure blob.
     * 'new file' will result in the download of a file using the attached UUID.
     * Also provisions for remotely shutting down the server using a shell command.
     */
    private static void setupHubConnection() {
        hubConnection = HubConnectionBuilder.create(url).build();
        hubConnection.start();
        //change message reveived systems to all use same target 'newMessage'. Message itself will be formatted:
        // instruction::data
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

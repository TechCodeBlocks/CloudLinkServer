import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
//program needs to load a json map of file data and send it to the cloud.
//This program map will not be changed or uploaded if no new files are detected
//Message handling:
//[new_file] -> use uuid provided to download file from blob - get data from cloud sync to add data to local map, get path to dl file to
//[req_file] -> upload requested uuid to lookup then upload the file.w

public class CloudLinkServer {
    static HubConnection hubConnection;
    static String url = "https://cloudlinkmessage.azurewebsites.net/api";
    public static void main(String[] args) {
        /*
        Connect to communications hub
        hubConnection = HubConnectionBuilder.create(url).build();
        hubConnection.start();
        */
    }
}

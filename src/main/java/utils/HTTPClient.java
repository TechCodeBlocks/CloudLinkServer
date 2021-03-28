package utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class HTTPClient {
    //Functions needed:
        //Upload file data: json format from HashMap, post request to the correct endpoint
        //Delete file data: send request with the id of the file to delete as a paramater called _id
    public static Future<Boolean> uploadFileData(HashMap<String,String> fileData){
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(()->{
            try {
                URL url = new URL("https://cloudlink.azurewebsites.net/api/addfile");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true);
                String jsonInputStream = "{"+"_id: "+fileData.get("_id")+","
                        +"path: "+fileData.get("path")+","
                        +"date-edited: "+fileData.get("date-edited")+","
                        +"online: " + fileData.get("online")+"}";
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(jsonInputStream);
                dataOutputStream.flush();
                dataOutputStream.close();
                if(connection.getResponseCode() == 200){
                    return true;
                }

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            //Send http post request to server
            return false;

        });
        return completableFuture;
    }
    public static Future<Boolean> deleteFileData(String id){
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(()->{

            //Send http post request to server
            return false;

        });
        return completableFuture;
    }
}

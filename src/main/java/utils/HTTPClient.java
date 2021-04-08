package utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.json.simple.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HTTPClient {
    //Functions needed:
    //Upload file data: json format from HashMap, post request to the correct endpoint
    //Delete file data: send request with the id of the file to delete as a paramater called _id
    public static Boolean uploadFileData(HashMap<String, String> fileData) {
        //Async execution of code
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                //Create URL for the correct endpoint
                URL url = new URL("https://cloudlink.azurewebsites.net/api/addfile");
                //open and set up connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                //create a JSON object to represent the data
                JSONObject body = new JSONObject();
                body.put("_id", fileData.get("_id"));
                body.put("path", fileData.get("path"));
                body.put("date-edited", fileData.get("date-edited"));
                body.put("online", fileData.get("online"));
                //set up data stream, write to it - sends data to server. Close and disconnect once completed
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(body.toString().getBytes());
                dataOutputStream.close();
                connection.disconnect();
                //Check for success
                if (connection.getResponseCode() == 200) {
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;

        });
        //Call the async function and get result
        try {
            Boolean result = completableFuture.get();
            return result;
        } catch (InterruptedException e) {
            System.out.println(e.getStackTrace());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean deleteFileData(String id) {
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            String baseURL = "https://cloudlink.azurewebsites.net/api/delete-item?_id=";
            String requestURL = baseURL + id;
            try {
                URL url = new URL(requestURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                JSONObject body = new JSONObject();
                body.put("_id", id);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(body.toString().getBytes());
                dataOutputStream.close();
                connection.disconnect();
                if (connection.getResponseCode() == 404) {
                    System.out.println("error");
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;

        });
        try {
            Boolean result = completableFuture.get();
            return result;
        } catch (InterruptedException e) {
            System.out.println(e.getStackTrace());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean uploadFile(HashMap<String, String> fileData) {
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            String filepath = fileData.get("path");
            String connectStr = "DefaultEndpointsProtocol=https;AccountName=cloudlinkfilestore;AccountKey=c2CcTbSpewXh4jU85enZGpHYyiq2elYAUnVpKJLxIotTZWRoBFiQwcoj5kvaB4C6quaQ7KkifiJFdKXHGOPdWg==;EndpointSuffix=core.windows.net";
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();
            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("ytterbium");
            BlobClient blobClient = blobContainerClient.getBlobClient(fileData.get("_id"));
            blobClient.uploadFromFile(filepath, true);


            return true;
        });

        try {
            return completableFuture.get();
        }catch (ExecutionException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return false;

    }
    //Will need to run a get request to get file data before running this function. Should be added to the cloud database before it can be downloaded
    public static Boolean downloadFile(HashMap<String, String> fileData) {
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {

            File downloadedFile = new File(fileData.get("path"));
            //File downloadedFile = new File("/Users/sandrarolfe/Documents/servertest/DownloadTest.rtf");
            String connectStr = "DefaultEndpointsProtocol=https;AccountName=cloudlinkfilestore;AccountKey=c2CcTbSpewXh4jU85enZGpHYyiq2elYAUnVpKJLxIotTZWRoBFiQwcoj5kvaB4C6quaQ7KkifiJFdKXHGOPdWg==;EndpointSuffix=core.windows.net";
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();
            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("ytterbium");
            BlobClient blobClient = blobContainerClient.getBlobClient(fileData.get("_id"));
            blobClient.downloadToFile(fileData.get("path"));
            //blobClient.downloadToFile("/Users/sandrarolfe/Documents/servertest/DownloadTest.rtf");
            return true;
        });
        try {
            return completableFuture.get();
        }catch (ExecutionException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return false;

    }


}

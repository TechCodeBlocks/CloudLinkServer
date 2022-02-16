package utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface HTTPClient {
    //Functions needed:
    //Upload file data: json format from HashMap, post request to the correct endpoint
    //Delete file data: send request with the id of the file to delete as a paramater called _id
     static Boolean uploadFileData(HashMap<String, String> fileData) {
        System.out.println("Upload file data: Called");
        //Async execution of code
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Upload File Data: Attempting");
                //Create URL for the correct endpoint
                //URL url = new URL("https://cloudlink.azurewebsites.net/api/addfile");
                URL url = new URL("http://192.168.1.163:5000/cloudlink/addfile/");
                //open and set up connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                //create a JSON object to represent the data
                JSONObject body = new JSONObject();
                body.put("_id", fileData.get("_id"));
                //In final implementation, user id will be extracted from the file path.
                body.put("_userid", GlobalValues.userid);
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
                    System.out.println("File added to cloud system");
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

     static Boolean deleteFileData(String id) {
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            //String baseURL = "https://cloudlink.azurewebsites.net/api/delete-item?_id=";
            String baseURL = "http://192.168.1.163:5000/cloudlink/deletefile?id=";
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

    public static HashMap<String, String> getFileData(String id){
        CompletableFuture<HashMap<String, String>> completableFuture = CompletableFuture.supplyAsync(()->{
            try {
                //URL url = new URL("https://cloudlink.azurewebsites.net/api/get-single-file-data?");
                URL url = new URL("http://192.168.1.163:5000/cloudlink/file?filedid="+id);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
//                JSONObject body = new JSONObject();
//                body.put("_userid", GlobalValues.userid);
//                body.put("_id", id);
//                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
//                dataOutputStream.write(body.toString().getBytes());
//                dataOutputStream.close();
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuffer buf = new StringBuffer();
                String line;
                while ((line = reader.readLine())!=null) {
                    buf.append(line);
                }
                HashMap<String,String> fileData = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                try{
                    fileData = mapper.readValue(buf.toString(), new TypeReference<HashMap<String, String>>() {});
                }catch(Exception e){
                    e.printStackTrace();
                }
                return fileData;


            }catch (Exception e){
                e.printStackTrace();
            }
            return new HashMap<String, String >();
        });
        try {
            return completableFuture.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new HashMap<String,String>();

    }

    public static Boolean uploadFile(HashMap<String, String> fileData) {
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            String filepath = fileData.get("path");
            String connectStr = "DefaultEndpointsProtocol=https;AccountName=cloudlinkfilestore;AccountKey=c2CcTbSpewXh4jU85enZGpHYyiq2elYAUnVpKJLxIotTZWRoBFiQwcoj5kvaB4C6quaQ7KkifiJFdKXHGOPdWg==;EndpointSuffix=core.windows.net";
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();
            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("ytterbium");
            BlobClient blobClient = blobContainerClient.getBlobClient(fileData.get("_id"));
            blobClient.uploadFromFile(filepath, true);
            //send signalr message to notify client


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
    //not required in server program, will be used in http client for clients.
    static Boolean verifyUser(int id, String passwordHash){
        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() ->{
            try {
                URL url = new URL("https://cloudlink.azurewebsites.net/api/userverify");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                JSONObject body = new JSONObject();
                body.put("id", id);
                body.put("pass_hash", passwordHash);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(body.toString().getBytes());
                dataOutputStream.close();
                connection.disconnect();
                if (connection.getResponseCode() == 400) {
                    System.out.println("error");
                    return false;
                }
                return true;

            }catch (Exception e){

            }
            return false;
        });
        try {
            return completableFuture.get();
        }catch (Exception e){
            return false;
        }
    }


}

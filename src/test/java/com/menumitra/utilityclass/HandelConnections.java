package com.menumitra.utilityclass;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

public class HandelConnections
{
	private static URL url;
	private static HttpURLConnection httpurlconnection;
	
	
	public static String checkConnection(String endpoint,String method)  
	{
			int maxTime = 3000;  // Connection timeout
	        int intervalTime = 3000;  // Read timeout
	        int retries = 3;  // Number of retries if the connection is slow or there is an issue
	        int retryInterval = 5000;  // Time to wait between retries (in milliseconds)
	        HttpURLConnection httpURLConnection = null;
	        URL url = null;

	        try {
	            url = new URL(endpoint);
	            int attempt = 0;

	            while (attempt < retries) {
	                try {
	                    httpURLConnection = (HttpURLConnection) url.openConnection();
	                    httpURLConnection.setConnectTimeout(maxTime);
	                    httpURLConnection.setReadTimeout(intervalTime);
	                    
	                    httpURLConnection.setRequestMethod(method);
	                    httpURLConnection.connect();
	                    // Check the response code
	                    int responseCode = httpURLConnection.getResponseCode();
	                    System.out.println(responseCode);
	                    switch (responseCode) {
	                        case 200:
	                            LogUtils.info("Connection established successfully with endpoint: " + endpoint);
	                            return "Connection established successfully";
	                            
	                        case 400:
	                            LogUtils.error("Bad Request - Invalid syntax for endpoint: " + endpoint);
	                            return "Bad Request - The request could not be understood by the server";
	                            
	                        case 401:
	                            LogUtils.error("Unauthorized - Authentication required for endpoint: " + endpoint); 
	                            return "Unauthorized - Authentication credentials are required";
	                            
	                        case 403:
	                            LogUtils.error("Forbidden - Access denied to endpoint: " + endpoint);
	                            return "Forbidden - You don't have permission to access this resource";
	                            
	                        case 404:
	                            LogUtils.error("Not Found - Resource not found at endpoint: " + endpoint);
	                            return "Not Found - The requested resource could not be found";
	                            
	                        case 408:
	                            LogUtils.error("Request Timeout for endpoint: " + endpoint);
	                            return "Request Timeout - The server timed out waiting for the request";
	                            
	                        case 500:
	                            LogUtils.error("Internal server error - Server unable to handle the request for endpoint: " + endpoint);
	                            return "Internal server error - Server unable to handle the request";
	                            
	                        case 502:
	                            LogUtils.error("Bad Gateway - Invalid response from upstream server for endpoint: " + endpoint);
	                            return "Bad Gateway - The server received an invalid response";
	                            
	                        case 503:
	                            LogUtils.error("Service unavailable - Server is currently down for endpoint: " + endpoint);
	                            return "Service unavailable - Server is currently down";
	                            
	                        case 504:
	                            LogUtils.error("Gateway Timeout - Upstream server failed to respond in time for endpoint: " + endpoint);
	                            return "Gateway Timeout - The server failed to respond in time";
	                            
	                        default:
	                            LogUtils.warn("Unexpected response code " + responseCode + " received from endpoint: " + endpoint);
	                            return "Unexpected response code: " + responseCode + ". Please check server status.";
	                    }
	                } catch (IOException e) {
	                    // Retry logic for connection failure
	                    if (attempt < retries - 1) {
	                        LogUtils.warn("Connection attempt " + (attempt + 1) + " failed. Retrying in " + (retryInterval/1000) + " seconds...");
	                        attempt++;
	                        Thread.sleep(retryInterval);  // Wait before retrying
	                    } else {
	                        // If all attempts fail, provide appropriate message
	                        LogUtils.error("Connection failed after " + retries + " attempts to endpoint: " + endpoint + ". Error: " + e.getMessage());
	                        return "Connection failed after " + retries + " attempts. Please check your network connection or contact system administrator.";
	                    }
	                }
	            }
	        } catch (IOException | InterruptedException e) {
	            LogUtils.error("Critical error occurred while connecting to endpoint: " + endpoint + ". Error: " + e.getMessage());
	            return "Critical error occurred: " + e.getMessage() + ". Please verify endpoint configuration.";
	        } finally {
	            if (httpURLConnection != null) {
	                httpURLConnection.disconnect();
	            }
	        }
	        LogUtils.error("Unexpected error occurred while connecting to endpoint: " + endpoint);
	        return "An unexpected error occurred. Please try again later or contact support.";
	    }

	
}

package com.menumitra.utilityclass;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ResponseUtil 
{
	// Static request specification for API calls
    static RequestSpecification request;
    
    // Static response object to store API responses
    static Response response;

    /**
     * Makes an HTTP request based on the specified method and returns the response
     * 
     * @param url The endpoint URL to send the request to
     * @param requestBody The request body object
     * @param method The HTTP method to use (post, put, etc.)
     * @return Response object containing the API response
     * @throws MenumitraException If there is an error during the API request
     */
    public static Response getResponse(String url, Object requestBody, String method) throws MenumitraException 
    {
        try {
            LogUtils.info("Making " + method.toUpperCase() + " request to: " + url);
            
           
            switch (method.toLowerCase()) {
                case "post":
                   try
                   {
                	   LogUtils.info("Executing POST request");
                       response = RestAssured.given()
   	                    	    .contentType(ContentType.JSON)
   	                    	    .body(requestBody)
   	                    	    .when()
   	                    	    .post(url)
   	                    	    .then()
   	                    	    .log().all()
   	                    	    .extract()
   	                    	    .response();
                       LogUtils.info("POST request completed successfully");
                       return response;
                   }
                   catch (Exception e)
                   {
                	   LogUtils.error("Error: Get response..");
                       throw new MenumitraException("Error: Get response");
                   }
                    
                case "put":
		                	response=RestAssured.given()
				            	    .contentType(ContentType.JSON)
				            	    .body(requestBody)
				            	    .when()
				            	    .put(url)
				            	    .then()
				            	    .log().all()
				            	    .extract()
				            	    .response();
             
                    LogUtils.info("PUT request completed successfully");
                    return response;

                case "get":
                            try{
                                LogUtils.info("start get response...");
                                response=RestAssured
                                		.given()
                                		.contentType(ContentType.JSON)
                                		.when()
                                		.get(url)
                                		.then()
                                		.log()
                                		.all()
                                		.extract()
                                		.response();
                                
                                LogUtils.info("successfully get response..");
                                return response;
                            }
                            catch(Exception e)
                            {
                                LogUtils.error("Error: Get response..");
                                throw new MenumitraException("Error: Get response");
                              
                            }
                    
                case "delete":
                                try {
                                    
                                    LogUtils.info("start delete data..");
                                    response=RestAssured.given()
		                            	    .contentType(ContentType.JSON)
		                            	    .body(requestBody)
		                            	    .when()
		                            	    .delete(url)
		                            	    .then()
		                            	    .log().all()
		                            	    .extract()
		                            	    .response();
                                    LogUtils.info("Delete Data Successfully");
                                    return response;
                                    
                                } catch (Exception e) {
                                  
                                	LogUtils.error("Error:Unable to delete data check request body");
                                	throw new MenumitraException("Error:Unble to delete data check request body");
                                }
                
                default:
                    LogUtils.warn("Unsupported HTTP method: " + method);
                    return null;
            }
        } catch (Exception e) {
            LogUtils.error("Error during " + method.toUpperCase() + " request: " + e.getMessage());
            throw new MenumitraException("Error during API request: " + e.getMessage());
        }
    }

    /**
     * Makes an HTTP request based on the specified method and returns the response
     * 
     * @param url The endpoint URL to send the request to
     * @param requestBody The request body object
     * @param method The HTTP method to use (post, put, etc.)
     * @param jwttoken The JWT token for authorization
     * @return Response object containing the API response
     * @throws MenumitraException If there is an error during the API request
     */
    public static Response getResponseWithAuth(String url, Object requestBody, String method, String jwttoken) throws MenumitraException 
    {
        try {
            LogUtils.info("Making " + method.toUpperCase() + " request to: " + url);
            
           
            switch (method.toLowerCase()) {
                case "post":
                	 try
                     {
                  	   LogUtils.info("Executing POST request");
                         response = RestAssured.given()
     	                    	    .contentType(ContentType.JSON)
     	                    	    .header("Authorization","Bearer "+jwttoken)
     	                    	    .body(requestBody)
     	                    	    .when()
     	                    	    .post(url)
     	                    	    .then()
     	                    	    .log().all()
     	                    	    .extract()
     	                    	    .response();
                         LogUtils.info("POST request completed successfully");
                         return response;
                     }
                     catch (Exception e)
                     {
                  	   LogUtils.error("Error: Get response..");
                         throw new MenumitraException("Error: Get response");
                     }
                    
                case "put":
                    try
                    {
                        LogUtils.info("Executing PUT request");
                        response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .header("Authorization","Bearer "+jwttoken)
                            .body(requestBody)
                            .when()
                            .put(url)
                            .then()
                            .log().all()
                            .extract()
                            .response();
                        LogUtils.info("PUT request completed successfully");
                        return response;
                    }
                    catch (Exception e)
                    {
                        LogUtils.error("Error: Get response..");
                        throw new MenumitraException("Error: Get response");
                    }

                case "get":
                            try{
                                response =RestAssured.given()
                            .header("Authorization","Bearer ",jwttoken)
                            .when()
                            .get(url);
                                
                                LogUtils.info("successfully get response..");
                                return response;
                            }
                            catch(Exception e)
                            {
                                LogUtils.error("Error: Get response..");
                                throw new MenumitraException("Error: Get response");
                              
                            }
                    
                case "delete":
                                try {
                                    
                                    LogUtils.info("start delete data..");
                                    response =RestAssured.given()
                                    .contentType(ContentType.JSON)
                                    .header("Authorization", "Bearer " + jwttoken)
                                    .body(requestBody)
                                    .when()
                                    .delete(url)
                                    .then()
                                    .log()
                                    .all()
                                    .extract()
                                    .response();
                                    
                                    LogUtils.info("Delete Data Successfully");
                                    return response;
                                    
                                } catch (Exception e) {
                                  
                                	LogUtils.error("Error:Unable to delete data check request body");
                                	throw new MenumitraException("Error:Unble to delete data check request body");
                                }
                case "patch":
                                try {
                                    LogUtils.info("Executing PATCH request");
                                    response = RestAssured.given()
                                        .contentType(ContentType.JSON)  // Add content type
                                        .header("Authorization", "Bearer " + jwttoken)  // Fix the header format
                                        .body(requestBody)
                                        .when()
                                        .patch(url)
                                        .then()
                                        .log().all()
                                        .extract()
                                        .response();
                                    LogUtils.info("PATCH request completed successfully");
                                    return response;
                                } catch (Exception e) {
                                    LogUtils.error("Error: PATCH request failed");
                                    throw new MenumitraException("Error: Failed to execute PATCH request");
                                }
                default:
                    LogUtils.warn("Unsupported HTTP method: " + method);
                    return null;
            }
        } catch (Exception e) {
            LogUtils.error("Error during " + method.toUpperCase() + " request: " + e.getMessage());
            throw new MenumitraException("Error during API request: " + e.getMessage());
        }
    }

    /**
     * Validates the response schema against a JSON schema definition file
     * @param response The REST Assured Response object to validate
     * @param jsonPath Path to the JSON schema file to validate against
     * @throws MenumitraException If schema validation fails or other errors occur
     */
    public static void validateResponseSchema(Response response,String jsonPath) throws MenumitraException
    {
        try{
            LogUtils.info("Validating response schema");
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonPath));
            LogUtils.info("Success validate json schema validator..");
        }
        catch (Exception e) 
        {
			LogUtils.error("Error:Failed to validate response schema");
            throw new MenumitraException("Error: Failed to validate response schema");
		}
    }
}	

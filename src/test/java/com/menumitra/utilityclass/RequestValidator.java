package com.menumitra.utilityclass;

import java.net.MalformedURLException;
import java.net.URL;

public class RequestValidator 
{
	private static URL url;
	
	public static String buildUri(String endpoint,String baseUri) throws customException
	{
		try
		{
			url=new URL(endpoint);
		}
		catch (MalformedURLException e) 
		{
			throw new customException("Malformed URL Exception occured..");
		}
		catch (Exception e) 
		{
			throw new customException("unexpected error occured");
		}
		return baseUri+""+url.getPath();	
	}
}

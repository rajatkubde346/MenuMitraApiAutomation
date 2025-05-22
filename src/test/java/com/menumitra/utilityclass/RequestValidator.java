package com.menumitra.utilityclass;

import java.net.MalformedURLException;
import java.net.URL;

public class RequestValidator 
{
	private static URL url;
	
	public static String buildUri(String endpoint,String baseUri) throws MenumitraException
	{
		try
		{
			url=new URL(endpoint);
		}
		catch (MalformedURLException e) 
		{
			throw new MenumitraException("Malformed URL Exception occurred..");
		}
		catch (Exception e) 
		{
			throw new MenumitraException("unexpected error occurred");
		}
		return baseUri+""+url.getPath();	
	}
}

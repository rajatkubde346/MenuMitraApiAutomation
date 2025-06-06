package com.menumitra.utilityclass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class ReadFile
{
	public static File file;
	public static FileInputStream fis;
	public static FileOutputStream fos;
	
	 /**
     * Reads a file from the given path.
     *
     * @param path The file path.
     * @return A FileInputStream for reading the file.
     * @throws CustomExceptions If the file is not found or there is an unexpected error.
     */
	public static FileInputStream readFile(String path) throws customException
	{
		LogUtils.info("Attempting to read file from path: " + path);
		file=new File(path);
		try
		{
			LogUtils.info("Opening FileInputStream for file");
			fis=new FileInputStream(file);
			LogUtils.info("Successfully opened file stream");
			return fis;
		}
		catch (FileNotFoundException e) 
		{
			LogUtils.error("File not found at path: " + path);
			throw new customException("File Not Found. Check File Location: "+e.getMessage());
		}
		catch (Exception e) 
		{
			LogUtils.error("Unexpected error while reading file: " + e.getMessage());
			throw new customException("Unexpected error occured: "+e.getMessage());
		}
	}
	
}

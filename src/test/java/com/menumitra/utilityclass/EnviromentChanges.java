package com.menumitra.utilityclass;

import com.menumitra.superclass.APIBase;

public class EnviromentChanges
{
	 // Define environment types
    public enum Environment {
        DEV("dev"),
        QA("qa"),
        STAGING("staging"),
        PRODUCTION("prod");
        
        private final String name;
        
        Environment(String name) {
            this.name = name;
        }
        
        public String getName() {
        	System.out.println(name);
            return name;
        }
    }
    
    // Current environment (default to QA)
    private static Environment currentEnv = Environment.QA;
    
    /**
     * Set the current environment
     * @param env The environment to switch to
     */
    public static void setEnvironment(Environment env) 
    {
        currentEnv = env;
        LogUtils.info("Environment switched to: " + env.getName());
    }
    
    /**
     * Get the current environment
     */
    public static Environment getCurrentEnvironment() 
    {
    	
        return currentEnv;
    }
    
    /**
     * Get the base URL for the current environment
     */
    public static String getBaseUrl() 
    {
        switch(currentEnv) {
           
            case QA:
            	
                return APIBase.property.getProperty("QAbaseURI");
            
            case PRODUCTION:
                return APIBase.property.getProperty("ProductionBaseURI");
            default:
                return APIBase.property.getProperty("QAbaseURI");
        }
    }
}

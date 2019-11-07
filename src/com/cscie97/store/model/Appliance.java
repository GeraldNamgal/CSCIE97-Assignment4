/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

/* *
 * Appliance class that represents an appliance in a store. Extends Sensor class
 */
public class Appliance extends Sensor
{
    /* Variables */  

    /* *
     * Types of valid appliances
     */
    enum Type 
    { 
        speaker, robot, turnstile; 
    }
    
    private Turnstile turnstile;
    private Speaker speaker;
    private Robot robot;
        
    /* Constructor */
    
    /* *
     * Creates a new Appliance
     * @param type Type of appliance (e.g., speaker, robot, turnstile; enum of valid appliances shown above)
     * @param location The location of the appliance (e.g., aisle 2 in store 1)
     */
    public Appliance(String id, String name, String type, String storeAisleLoc)
    {
        super(id, name, type, storeAisleLoc);
        
        if (type.equals("turnstile"))
        {
            turnstile = new Turnstile();
        }
        
        if (type.equals("robot"))
        {
            robot = new Robot();
        }
        
        if (type.equals("speaker"))
        {
            speaker = new Speaker();
        }
    }
    
    /* Nested Classes */   
    
    public class Turnstile
    {       
        private boolean open = false;
        
        public boolean isOpen()
        {
            return open;
        }

        public boolean setOpen(boolean trueOrFalse)
        {
            open = trueOrFalse;
            return open;
        }
        
        public boolean speak(String expression)
        {
            boolean speaking = true;
            return speaking;
        }
        
        public boolean letPersonPass()
        {
            boolean personPassed = false;
            
            // Open turstile
            if (setOpen(true))
            {
                // Check that customer passed through turnstile
                personPassed = true;
                
                if (personPassed == true)
                {
                    // Close turnstile
                    setOpen(false);
                }
            }
            
            return personPassed;
        }
    }
    
    public class Speaker
    {
        public boolean announce(String expression)
        {
            boolean announcing = true;            
            return announcing;
        }
    }
    
    public class Robot
    {
        public boolean addressEmergency(String emergency, String aisleNumber)
        {
            boolean addressingEmergency = true;
            return addressingEmergency;
        }
        
        public boolean assstLeavingCstmrs(String storeId)
        {
            boolean assstingLeavingCstmrs = true;
            return assstingLeavingCstmrs;
        }
        
        public boolean restock(String product, String fromLocation, String toLocation)
        {
            boolean restocking = true;            
            return restocking;
        }
        
        public boolean clean(String productId, String aisleNumber)
        {
            boolean cleaning = true;
            return cleaning;
        }
        
        public boolean brokenGlass(String aisleNumber)
        {
            boolean cleaning = true;
            return cleaning;
        }
        
        public boolean fetchProduct(String productId, Integer number, String aisleShelfLoc, String customerId, String customerAisleLoc)
        {
            boolean fetchingProduct = true;
            return fetchingProduct;
        }
        
        public boolean carAssist(String customerId, String customerLocation)
        {
            boolean carAssisting = true;
            return carAssisting;
        }
    }
    
    /* Utility Methods */
 
    /* *
     * Checks a string if it's a Type enum
     */
    public static boolean containsTypeEnum(String testString)
    {
        for (Type type : Type.values())
        {
            if (type.name().equals(testString))
            {
                return true;
            }
        }

        return false;
    }

    /* Getters and Setters */
    
    public Turnstile getTurnstile()
    {
        return turnstile;
    }    

    public Speaker getSpeaker()
    {
        return speaker;
    }    

    public Robot getRobot()
    {
        return robot;
    }           
}

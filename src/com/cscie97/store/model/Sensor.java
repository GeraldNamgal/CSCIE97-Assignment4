/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* *
 * Sensor class that represents a sensor that's in a store
 */
public class Sensor
{
    /* API Variables */
    
    /* *
     * Types of valid sensors
     */
    enum Type 
    { 
        microphone, camera; 
    }
    
    private String id;
    private String name;
    private String type;
    private String location; 
    
    /* Constructor */
    
    /* *
     * Creates a new Sensor
     * @param type Type of sensor (enum of valid sensors shown above)
     * @param location Location of sensor (e.g., store1:aisle1)
     */
    public Sensor(String id, String name, String type, String storeAisleLoc)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = storeAisleLoc;        
    }
    
    /* API Methods */
    
    /* *
     * Receives events
     */
    public String[] event(String perceivedEvent)
    {        
        // Delimit event string on whitespace and add each value to an array
        String[] eventStrArr = perceivedEvent.split("\\s+");
        
        // Initialize a new array for "Customer Seen" case
        String[] newArray = null;
        
        // If event is "Customer Seen", add a timestamp
        if (eventStrArr[0].equals("customer_seen"))
        {
            // Get the timestamp
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");
            LocalDateTime currentDateTime = LocalDateTime.now();
            
            // Create a new array
            newArray = new String[eventStrArr.length + 1];
            
            // Copy eventStrArr to newArray
            System.arraycopy(eventStrArr, 0,newArray, 0, eventStrArr.length);
            
            // Add the time stamp to newArray
            newArray[eventStrArr.length] = dtf.format(currentDateTime);
        }
        
        if (newArray == null)        
            return eventStrArr;
        
        else
            return newArray;
    }
    
    /* Utility Methods */
 
    /* *
     *  Checks a string if it's a Type enum
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
    
    /* Getters and Setter */

    public String getId()
    {
        return id;
    }    

    public String getName()
    {
        return name;
    }   

    public String getType()
    {
        return type;
    }  

    public String getLocation()
    {
        return location;
    }     
}

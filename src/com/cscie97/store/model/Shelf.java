/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

/* *
 * Shelf class that represents a shelf in an aisle in a store
 */
public class Shelf
{
    /* API Variables */
    
    /* *
     * Valid levels
     */
    enum Level 
    { 
        high, medium, low; 
    }
    
    /* *
     * Valid temperature settings
     */
    enum Temperature
    {
        frozen, refrigerated, ambient, warm, hot;
    }
    
    private String id;
    private String name;
    private Level level;
    private String description;
    private Temperature temperature;
    
    /* Constructor */
    
    /* *
     * Creates a new shelf
     */
    public Shelf(String id, String name, Level level, String description, Temperature temperature)
    {
        this.id = id;
        this.name = name;
        this.level = level;
        this.description = description;
        this.temperature = temperature;
    }
    
    /* Utility Methods */
    
    /* *
     *  Checks a string if it's a Level enum
     */
    public static boolean containsLevelEnum(String testString)
    {
        for (Level level : Level.values())
        {
            if (level.name().equals(testString))
            {
                return true;
            }
        }

        return false;
    }
    
    /* *
     * Checks a string if it's a Temperature enum
     */
    public static boolean containsTempEnum(String testString)
    {
        for (Temperature temperature : Temperature.values())
        {
            if (temperature.name().equals(testString))
            {
                return true;
            }
        }

        return false;
    }
    
    /* Getters and Setters */
    
    public String getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Level getLevel()
    {
        return level;
    }
   
    public String getDescription()
    {
        return description;
    }
    
    public Temperature getTemperature()
    {
        return temperature;
    }  
}

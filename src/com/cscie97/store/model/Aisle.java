/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

import java.util.LinkedHashMap;

/* *
 * Aisle class that represents an aisle in a store 
 */
public class Aisle
{       
    /* API Variables */
    
    public enum Location 
    { 
        floor, storeroom; 
    }
    
    private String number;
    private String name;
    private String description;
    private Location location;
    private LinkedHashMap<String, Shelf> shelves;
    
    /* Constructor */
    
    /* *
     * Creates a new Aisle
     * @param number The unique id of the aisle
     * @param location The location of the aisle (floor | storeroom) 
     */
    public Aisle(String number, String name, String description, Location location)
    {
        this.number = number;
        this.name = name;
        this.description = description;
        this.location = location;
        shelves = new LinkedHashMap<String, Shelf>();
    }
    
    /* Utility Methods */
 
    /* *
     * Checks a string if it's a Location enum
     */
    public static boolean containsLocEnum(String testString)
    {
        for (Location location : Location.values())
        {
            if (location.name().equals(testString))
            {
                return true;
            }
        }

        return false;
    }

    /* Getters and Setters */
    
    public String getNumber()
    {
        return number;
    }

    public String getName() 
    {
        return name;
    }   

    public String getDescription() 
    {
        return description;
    }   

    public Location getLocation() 
    {
        return location;
    }    

    public LinkedHashMap<String, Shelf> getShelves() 
    {
        return shelves;
    }    
}

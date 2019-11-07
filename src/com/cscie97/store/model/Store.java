/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

import java.util.LinkedHashMap;

/* *
 * Store class that represents a store serviced by the Model Service
 */
public class Store
{
    /* API Variables */
    
    private String id;
    private String name;
    private String address;
    private LinkedHashMap<String, Customer> customers;
    private LinkedHashMap<String, Aisle> aisles;    
    private LinkedHashMap<String, Inventory> inventories;
    private LinkedHashMap<String, Sensor> devices;   
    
    /* Constructor */
    
    /* *
     * Creates a new store
     * @param id Globally unique store id
     * @param name Name of the store
     * @param address Postal address of the store
     */
    public Store(String id, String name, String address)
    {
        this.id = id;
        this.name = name;
        this.address = address;
        customers = new LinkedHashMap<String, Customer>();
        aisles = new LinkedHashMap<String, Aisle>();
        inventories = new LinkedHashMap<String, Inventory>();
        devices = new LinkedHashMap<String, Sensor>();                
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

    public String getAddress()
    {
        return address;
    }   

    public LinkedHashMap<String, Customer> getCustomers()
    {
        return customers;
    }    

    public LinkedHashMap<String, Aisle> getAisles()
    {
        return aisles;
    }   

    public LinkedHashMap<String, Inventory> getInventories()
    {
        return inventories;
    }    

    public LinkedHashMap<String, Sensor> getDevices()
    {
        return devices;
    }  
}

/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

import java.util.LinkedHashMap;

/* *
 * Basket class that represents a basket a customer uses to shop in a store
 */
public class Basket
{
    /* API Variables */
    
    private String id;
    private LinkedHashMap<String, Integer> basketItems;    
    
    /* Constructor */
    
    /* *
     * Creates a new Basket
     * @param customerId The unique id of the basket (same as customer's id)
     */
    public Basket(String customerId)
    {
        this.id = customerId;
        basketItems = new LinkedHashMap<String, Integer>();
    }  
    
    /* Getters and Setters */   
    
    public String getId() 
    {
        return id;
    }
    
    public LinkedHashMap<String, Integer> getBasketItems() 
    {
        return basketItems;
    }   
}

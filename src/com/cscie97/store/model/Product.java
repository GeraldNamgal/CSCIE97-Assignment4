/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.model;

/* *
 * Product class that represents a product that stores sell
 */
public class Product
{
    /* API Variables */
    
    String productId;
    String name;
    String description;
    String size;
    String category;
    Integer unitPrice;
    Shelf.Temperature temperature;
    
    /* Constructor */
    
    /* *
     * Creates a new Product
     */
    public Product(String productId, String name, String description, String size, String category, Integer unitPrice, Shelf.Temperature temperature)
    {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.size = size;
        this.category = category;
        this.unitPrice = unitPrice;
        this.temperature = temperature;
    }

    /* Getters and Setters */
    
    public String getProductId() 
    {
        return productId;
    }    

    public String getName() 
    {
        return name;
    }    

    public String getDescription() 
    {
        return description;
    }    

    public String getSize() 
    {
        return size;
    }    

    public String getCategory() 
    {
        return category;
    }    

    public Integer getUnitPrice() 
    {
        return unitPrice;
    }    

    public Shelf.Temperature getTemperature() 
    {
        return temperature;
    }   
}

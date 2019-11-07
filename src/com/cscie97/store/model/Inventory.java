/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3 
 */

package com.cscie97.store.model;

/* *
 * Inventory class that represents inventory information for a product-shelf relationship
 */
public class Inventory
{
    /* API Variables */
    
    private String inventoryId;
    private String location;
    private Integer capacity;
    private Integer count;
    private String productId;
    
    /* Constructor */
    
    /* *
     * Creates a new Inventory
     * @param location Location of shelf (e.g., store1:aisle1:shelf1)
     * @param capacity The maximum number of product allowed on shelf
     * @param count The number of the product on the shelf currently
     */
    public Inventory(String id, String storeAisleShelfLoc, Integer capacity, Integer count, String productId)
    {
        inventoryId = id;
        location = storeAisleShelfLoc;
        this.capacity = capacity;
        this.count = count;
        this.productId = productId;
    }
    
    /* API Methods */
    
    /* *
     * Updates the count of the product on the shelf 
     * @param updateAmount The amount to increment or decrement the product by (negative number for
     *                     decrementing)
     */
    public void updateCount(Integer updateAmount)
    {
        count += updateAmount;
    }

    /* Getters and Setters */
    
    public String getInventoryId()
    {
        return inventoryId;
    }    

    public String getLocation() 
    {
        return location;
    }   
   
    public Integer getCapacity() 
    {
        return capacity;
    }   

    public Integer getCount() 
    {
        return count;
    }   

    public String getProductId() 
    {
        return productId;
    }   
}

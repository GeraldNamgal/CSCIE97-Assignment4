/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

import java.util.LinkedHashMap;

/* *
 * The Store Model Service API interface that defines the methods for creating, maintaining, and updating stores
 * and their assets
 */
public interface StoreModelService
{
    Store defineStore(String id, String name, String address, String auth_token);
    void showStore(String storeId, String auth_token);
    Aisle defineAisle(String storeAisleLoc, String name, String description, String location, String auth_token);
    void showAisle(String storeAisleLoc, String auth_token);
    Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, String temperature, String auth_token);
    Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, String auth_token);
    void showShelf(String storeAisleShelfLoc, String auth_token);
    Inventory defineInventory(String id, String storeAisleShelfLoc, Integer capacity, Integer count, String productId, String auth_token);
    void showInventory(String id, String auth_token);
    void updateInventory(String id, Integer amount, String auth_token);
    Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
                , String temperature, String auth_token);
    Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
                , String auth_token);
    void showProduct(String id, String auth_token);
    Customer defineCustomer(String id, String firstName, String lastName, String ageGroup, String type, String emailAddress
                , String account, String auth_token);
    void showCustomer(String id, String auth_token);
    void updateCustomer(String id, String storeAisleLoc, String dateTime, String auth_token);
    Basket getCustomerBasket(String customerId, String auth_token);
    void addBasketItem(String customerId, String productId, Integer itemCount, String auth_token);
    void removeBasketItem(String customerId, String productId, Integer itemCount, String auth_token);
    void clearBasket(String customerId, String auth_token);
    void showBasketItems(String customerId, String auth_token);
    Sensor defineDevice(String id, String name, String type, String storeAisleLoc, String auth_token);
    void showDevice(String id, String auth_token);
    void createEvent(String id, String event, String auth_token);
    void createCommand(String id, String command, String auth_token);
    
    // Getters and Setters
    LinkedHashMap<String, Store> getStores(String auth_token);
    LinkedHashMap<String, Product> getProducts(String auth_token);
    LinkedHashMap<String, Customer> getCustomers(String auth_token);
}
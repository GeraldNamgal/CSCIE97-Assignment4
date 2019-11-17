/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

import java.util.LinkedHashMap;

import com.cscie97.store.authentication.AuthToken;

/* *
 * The Store Model Service API interface that defines the methods for creating, maintaining, and updating stores
 * and their assets
 */
public interface StoreModelService
{
    Store defineStore(String id, String name, String address, AuthToken authToken);
    void showStore(String storeId, AuthToken authToken);
    Aisle defineAisle(String storeAisleLoc, String name, String description, String location, AuthToken authToken);
    void showAisle(String storeAisleLoc, AuthToken authToken);
    Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, String temperature, AuthToken authToken);
    Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, AuthToken authToken);
    void showShelf(String storeAisleShelfLoc, AuthToken authToken);
    Inventory defineInventory(String id, String storeAisleShelfLoc, Integer capacity, Integer count, String productId, AuthToken authToken);
    void showInventory(String id, AuthToken authToken);
    void updateInventory(String id, Integer amount, AuthToken authToken);
    Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
                , String temperature, AuthToken authToken);
    Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
                , AuthToken authToken);
    void showProduct(String id, AuthToken authToken);
    Customer defineCustomer(String id, String firstName, String lastName, String ageGroup, String type, String emailAddress
                , String account, AuthToken authToken);
    void showCustomer(String id, AuthToken authToken);
    void updateCustomer(String id, String storeAisleLoc, String dateTime, AuthToken authToken);
    Basket getCustomerBasket(String customerId, AuthToken authToken);
    void addBasketItem(String customerId, String productId, Integer itemCount, AuthToken authToken);
    void removeBasketItem(String customerId, String productId, Integer itemCount, AuthToken authToken);
    void clearBasket(String customerId, AuthToken authToken);
    void showBasketItems(String customerId, AuthToken authToken);
    Sensor defineDevice(String id, String name, String type, String storeAisleLoc, AuthToken authToken);
    void showDevice(String id, AuthToken authToken);
    void createEvent(String id, String event, AuthToken authToken);
    void createCommand(String id, String command, AuthToken authToken);
    
    // Getters and Setters
    LinkedHashMap<String, Store> getStores(AuthToken authToken);
    LinkedHashMap<String, Product> getProducts(AuthToken authToken);
    LinkedHashMap<String, Customer> getCustomers(AuthToken authToken);
}
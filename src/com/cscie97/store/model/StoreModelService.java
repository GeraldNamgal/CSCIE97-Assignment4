/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

import java.util.LinkedHashMap;

import com.cscie97.store.authentication.AuthTokenTuple;

/* *
 * The Store Model Service API interface that defines the methods for creating, maintaining, and updating stores
 * and their assets
 */
public interface StoreModelService
{
    Store defineStore(String id, String name, String address, AuthTokenTuple authTokenTuple);
    void showStore(String storeId, AuthTokenTuple authTokenTuple);
    Aisle defineAisle(String storeAisleLoc, String name, String description, String location, AuthTokenTuple authTokenTuple);
    void showAisle(String storeAisleLoc, AuthTokenTuple authTokenTuple);
    Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, String temperature, AuthTokenTuple authTokenTuple);
    Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, AuthTokenTuple authTokenTuple);
    void showShelf(String storeAisleShelfLoc, AuthTokenTuple authTokenTuple);
    Inventory defineInventory(String id, String storeAisleShelfLoc, Integer capacity, Integer count, String productId, AuthTokenTuple authTokenTuple);
    void showInventory(String id, AuthTokenTuple authTokenTuple);
    void updateInventory(String id, Integer amount, AuthTokenTuple authTokenTuple);
    Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
                , String temperature, AuthTokenTuple authTokenTuple);
    Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
                , AuthTokenTuple authTokenTuple);
    void showProduct(String id, AuthTokenTuple authTokenTuple);
    Customer defineCustomer(String id, String firstName, String lastName, String ageGroup, String type, String emailAddress
                , String account, AuthTokenTuple authTokenTuple);
    void showCustomer(String id, AuthTokenTuple authTokenTuple);
    void updateCustomer(String id, String storeAisleLoc, String dateTime, AuthTokenTuple authTokenTuple);
    Basket getCustomerBasket(String customerId, AuthTokenTuple authTokenTuple);
    void addBasketItem(String customerId, String productId, Integer itemCount, AuthTokenTuple authTokenTuple);
    void removeBasketItem(String customerId, String productId, Integer itemCount, AuthTokenTuple authTokenTuple);
    void clearBasket(String customerId, AuthTokenTuple authTokenTuple);
    void showBasketItems(String customerId, AuthTokenTuple authTokenTuple);
    Sensor defineDevice(String id, String name, String type, String storeAisleLoc, AuthTokenTuple authTokenTuple);
    void showDevice(String id, AuthTokenTuple authTokenTuple);
    void createEvent(String id, String event, AuthTokenTuple authTokenTuple);
    void createCommand(String id, String command, AuthTokenTuple authTokenTuple);
    
    // Getters and Setters
    LinkedHashMap<String, Store> getStores(AuthTokenTuple authTokenTuple);
    LinkedHashMap<String, Product> getProducts(AuthTokenTuple authTokenTuple);
    LinkedHashMap<String, Customer> getCustomers(AuthTokenTuple authTokenTuple);
}
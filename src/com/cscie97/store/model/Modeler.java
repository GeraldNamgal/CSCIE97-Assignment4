/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3 
 */

package com.cscie97.store.model;

import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.GetPermissionVisitor;
import com.cscie97.store.authentication.PermissionTuple;
import com.cscie97.store.authentication.StoreAuthenticationService;
import com.cscie97.store.controller.UpdateEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/* *
 * The Store Model Service API implementation class that contains methods and attributes for creating,
 * maintaining, and updating stores and their assets. The Store Model Service interface also extends the
 * Subject interface (the subject in the observer pattern)
 */
public class Modeler implements StoreModelService, Subject
{
    /* Observer Variable(s) */
    
    /* *
     * The list of Observers the Model Service is contracted to notify with update events 
     */
    private ArrayList<Observer> observers; 
    
    /* API Variables */
	
    private LinkedHashMap<String, Store> stores;
    private LinkedHashMap<String, Product> products;	
    private LinkedHashMap<String, Customer> customers;
    private LinkedHashMap<String, Inventory> inventories;
    private LinkedHashMap<String, Basket> activeBaskets;
    private LinkedHashMap<String, Sensor> devices;
    private StoreAuthenticationService authenticator;
	
    /* Constructor */
    
    public Modeler(StoreAuthenticationService authenticator)
    {
        stores = new LinkedHashMap<String, Store>();
        products = new LinkedHashMap<String, Product>();
        customers = new LinkedHashMap<String, Customer>();
        inventories = new LinkedHashMap<String, Inventory>();
        activeBaskets = new LinkedHashMap<String, Basket>();
        devices = new LinkedHashMap<String, Sensor>();
        observers = new ArrayList<Observer>();
        this.authenticator = authenticator;
    }
    
    /* Subject interface API Methods */
    
    /* *
     * Registers a new Observer 
     */
    @Override
    public void registerObserver(Observer newObserver)
    {
        observers.add(newObserver);
    }
  
    /* *
     * Deregisters an Observer
     */
    @Override
    public void deregisterObserver(Observer observerToRemove)
    {
        observers.remove(observerToRemove);        
    }
    
    /* *
     * Notifies Model Service's set of Observers of UpdateEvents which includes the source device that perceived event
     */
    @Override
    public void notifyObservers(Sensor sourceDevice, String[] eventToSend)
    {
        for (Observer observer : observers)
        {
            observer.update(new UpdateEvent(sourceDevice, eventToSend));
        }
    }  
    
    /* Store Model Service interface API Methods */
    
    /* *
     * Sends a simulated event to a device and calls notifyObservers method
     * @param id The unique id of the device
     * @param simulatedEvent The event to be created 
     */
    @Override
    public void createEvent(String id, String simulatedEvent, AuthToken authToken)
    {
        Sensor sourceDevice = devices.get(id);
        
        // If device wasn't found
        if (sourceDevice == null)
        {
            try
            {
                throw new ModelerException("create event", "device not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Send simulated event to device's event method       
        String[] eventToSend = sourceDevice.event(simulatedEvent);
        
        // Notify observers of the event sent back from device
        notifyObservers(sourceDevice, eventToSend);   
    }     
    
    /* *
     * Creates a new Store
     * @param id A globally unique store id
     * @param name The name of the store
     * @param address The postal address of the store
     * @return A Store object
     */
    @Override
    public Store defineStore(String id, String name, String address, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method    
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;       
               
        // Check that store id is unique
        if (stores.containsKey(id))
        {
            try
            {
                throw new ModelerException("define store", "store id already exists; store not created");
            }
            
            catch (ModelerException duplicateIdException)
            {
                System.out.println();
                System.out.print(duplicateIdException.getMessage());      
                return null;
            }
        }
        
        Store store = new Store(id, name, address);
        
        // Add store to store list 
        if (store != null)
            stores.put(store.getId(), store);           
        
        return store;
    } 
    
    /* *
     * Prints a store's information to stdout
     * @param storeId The unique id of the store
     */
    @Override
    public void showStore(String storeId, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;        
        
        // Get store
        Store store = stores.get(storeId);
        
        // Check that store exists
        if (store == null)
        {
            try
            {
                throw new ModelerException("show store", "store not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        /* Gather and print store's information */
        
        // Initialize string of customer information for everyone currently in the store        
        String customersString = "";
        LinkedHashMap<String, Customer> storeCustomers = store.getCustomers();
        int counter = 1;        
        
        // If there are active customers
        if (storeCustomers.size() > 0)
        {
            Customer customer;             
            for (Entry<String, Customer> customerEntry : storeCustomers.entrySet())
            {
                customer = customerEntry.getValue();    
                customersString += "\n    " + counter + ".)" + " id = " + customer.getId() + "; first name = " + customer.getFirstName()
                        + "; last name = " + customer.getLastName() + "; type = " + customer.getType() + "; emailAddress = "
                        + customer.getEmailAddress() + "; account = " + customer.getAccount() + "; location = " + customer.getLocation();    
                counter++;
            }
        }
        
        // If there are no active customers
        else
            customersString += " None";
        
        // Initialize string of store's aisle information
        String aislesString = "";
        LinkedHashMap<String, Aisle> aisles = store.getAisles();        
        counter = 1;
        
        // If there are aisles
        if (aisles.size() > 0)
        {
            Aisle aisle;                
            for (Entry<String, Aisle> aisleEntry : aisles.entrySet())
            {
                aisle = aisleEntry.getValue();    
                aislesString += "\n    " + counter + ".)" + " number = " + aisle.getNumber() + "; name = " + aisle.getName()
                        + "; description = " + aisle.getDescription() + "; location = " + aisle.getLocation();    
                counter++;
            }
        }
        
        // If there are no aisles
        else
            aislesString += " None";
        
        // Initialize string of store's inventory information
        String inventoriesString = "";
        LinkedHashMap<String, Inventory> inventories = store.getInventories();        
        counter = 1;
        
        // If there are inventories
        if (inventories.size() > 0)
        {
            Inventory inventory;
            for (Entry<String, Inventory> inventoryEntry : inventories.entrySet())
            {
                inventory = inventoryEntry.getValue();                
                inventoriesString += "\n    " + counter + ".)" + " inventoryId = " + inventory.getInventoryId() + "; location = "
                        + inventory.getLocation() + "; capacity = " + inventory.getCapacity() + "; count = " + inventory.getCount()
                        + "; productId = " + inventory.getProductId();                
                counter++;
            }
        }
        
        // If there are no inventories
        else
            inventoriesString += " None";
        
        // Get sensor information
        String sensorsString = "";
        LinkedHashMap<String, Sensor> storeDevices = store.getDevices();        
        counter = 1;
        
        // If there are store devices
        if (storeDevices.size() > 0)
        {
            Sensor sensor;             
            for (Entry<String, Sensor> sensorEntry : storeDevices.entrySet())
            {
                sensor = sensorEntry.getValue();
                
                if (Sensor.containsTypeEnum(sensor.getType()))
                {
                    sensorsString += "\n    " + counter + ".)" + " id = " + sensor.getId() + "; name = " + sensor.getName() 
                            + "; type = " + sensor.getType() + "; location = " + sensor.getLocation();    
                    counter++;
                }
            }
        }
        
        // If there are no sensors
        else
            sensorsString += " None";
        
        // Get appliance information        
        String appliancesString = "";
        counter = 1;
        
        // If there are store devices
        if (storeDevices.size() > 0)
        {
            Sensor appliance;             
            for (Entry<String, Sensor> sensorEntry : storeDevices.entrySet())
            {
                appliance = sensorEntry.getValue();
                
                if (Appliance.containsTypeEnum(appliance.getType()))
                {
                    appliancesString += "\n    " + counter + ".)" + " id = " + appliance.getId() + "; name = " + appliance.getName() 
                            + "; type = " + appliance.getType() + "; location = " + appliance.getLocation();    
                    counter++;
                }
            }
        }
        
        // If there are no appliances
        else
            appliancesString += " None";
        
        // Combine strings together with other store information included
        String string;
        string = "\nStore \"" + store.getId() + "\" information --\n" + " - name = "
                + store.getName() + "\n - address = " + store.getAddress() + "\n - active customers ="
                + customersString + "\n - aisles =" + aislesString + "\n - inventories =" + inventoriesString
                + "\n - sensors=" + sensorsString + "\n - appliances=" + appliancesString + "\n";           

        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(string);                    
    }
    
    /* *
     * Defines an aisle
     * @param storeAisleLoc The unique id of the aisle 
     * @param location The location within the store the aisle should be (floor | storeroom)
     * @return An Aisle object
     */
    @Override
    public Aisle defineAisle(String storeAisleLoc, String name, String description, String location, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;        
        
        // Split storeAisleLoc on colon
        String[] splitLoc = storeAisleLoc.split(":");
        String storeId = splitLoc[0];
        String aisleNumber = splitLoc[1];
        
        // Check that store exists
        if (stores.containsKey(storeId) == false)
        {
            try
            {
                throw new ModelerException("define aisle", "store not found; aisle not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that aisle number is unique
        if (stores.get(storeId).getAisles().containsKey(aisleNumber))
        {
            try
            {
                throw new ModelerException("define aisle", "aisle number already exists; aisle not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that location input is valid enum
        if (!Aisle.containsLocEnum(location))
        {
            try
            {
                throw new ModelerException("define aisle", "location (case-sensitive) not found; aisle not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }      
        
        // Change location string to an enum
        Aisle.Location locationEnum = Aisle.Location.valueOf(location);
        
        Aisle aisle = new Aisle(aisleNumber, name, description, locationEnum);
        
        // Add aisle to store's aisle list
        if (aisle != null)
            stores.get(storeId).getAisles().put(aisle.getNumber(), aisle);        
        
        return aisle;
    }
    
    /* *
     * Print an aisle's information to stdout
     * @param storeAisleLoc
     */
    @Override
    public void showAisle(String storeAisleLoc, AuthToken authToken)
    {        
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
                
        // Split storeAisleLoc on colon
        String[] splitLoc = storeAisleLoc.split(":");
        String storeId = splitLoc[0];
        String aisleNumber = splitLoc[1];
        
        // Get store
        Store store = stores.get(storeId);
        
        // Check that store exists
        if (store == null)
        {
            try
            {
                throw new ModelerException("show aisle", "store not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that aisle exists
        if (!store.getAisles().containsKey(aisleNumber))
        {
            try
            {
                throw new ModelerException("show aisle", "aisle not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        Aisle aisle = store.getAisles().get(aisleNumber);
        
        // Initialize shelves information string
        String shelvesString = "";
        LinkedHashMap<String, Shelf> shelves = store.getAisles().get(aisleNumber).getShelves();        
        int counter = 1;
        
        // If there are shelves
        if (shelves.size() > 0)
        {
            Shelf shelf;
            for (Entry<String, Shelf> shelfEntry : shelves.entrySet())
            {
                shelf = shelfEntry.getValue();                
                shelvesString += "\n    " + counter + ".)" + " id = " + shelf.getId() + "; name = "
                        + shelf.getName() + "; level = " + shelf.getLevel() + "; description = " + shelf.getDescription()
                        + "; temperature = " + shelf.getTemperature();                
                counter++;
            }
        }
        
        // If there are no shelves
        else
            shelvesString += " None";
        
        // Combine shelf string with other aisle information
        String string;
        string = "\nAisle \"" + storeAisleLoc + "\" information --\n" + " - name = "
                + aisle.getName() + "\n - description = " + aisle.getDescription() + "\n - location = "
                + aisle.getLocation() + "\n - shelves =" + shelvesString + "\n";           

        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(string);
    }
    
    /* *
     * Defines a shelf with the temperature input given by the user
     * @param storeAisleShelfLoc The unique id of the shelf
     * @param level The height of the shelf (high | medium | low)
     * @return A Shelf object
     */
    @Override
    public Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, String temperature, AuthToken authToken)
    { 
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
               
        // Split storeAisleShelfLoc on colon
        String[] splitLoc = storeAisleShelfLoc.split(":");
        String storeId = splitLoc[0];
        String aisleNumber = splitLoc[1];
        String shelfId = splitLoc[2];
        
        // Check that store exists
        if (stores.containsKey(storeId) == false)
        {
            try
            {
                throw new ModelerException("define shelf", "store not found; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that aisle exists
        if (stores.get(storeId).getAisles().containsKey(aisleNumber) == false)
        {
            try
            {
                throw new ModelerException("define shelf", "aisle not found; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that shelf id is unique
        if (stores.get(storeId).getAisles().get(aisleNumber).getShelves().containsKey(shelfId))
        {
            try
            {
                throw new ModelerException("define shelf", "shelf id already exists; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that level input is a valid enum
        if (!Shelf.containsLevelEnum(level))
        {
            try
            {
                throw new ModelerException("define shelf", "level (case-sensitive) not found; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that temperature input is a valid enum
        if (!Shelf.containsTempEnum(temperature))
        {
            try
            {
                throw new ModelerException("define shelf", "temperature (case-sensitive) not found; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Change level string to an enum
        Shelf.Level levelEnum = Shelf.Level.valueOf(level);
        
        // Change temperature string to an enum
        Shelf.Temperature temperatureEnum = Shelf.Temperature.valueOf(temperature);
        
        Shelf shelf = new Shelf(shelfId, name, levelEnum, description, temperatureEnum);        
        
        // Add shelf to aisle's shelf list     
        if (shelf != null)
            stores.get(storeId).getAisles().get(aisleNumber).getShelves().put(shelf.getId(), shelf);        
        
        return shelf;       
    }
    
    /* *
     * Defines a shelf with a default temperature
     * @param storeAisleShelLoc The unique id of the shelf
     * @param level The height of the shelf (high | medium | low)
     * @return A Shelf object
     */
    @Override
    public Shelf defineShelf(String storeAisleShelfLoc, String name, String level, String description, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
        
        // Split storeAisleShelfLoc on colon
        String[] splitLoc = storeAisleShelfLoc.split(":");
        String storeId = splitLoc[0];
        String aisleNumber = splitLoc[1];
        String shelfId = splitLoc[2];
        
        // Check that store exists
        if (stores.containsKey(storeId) == false)
        {
            try
            {
                throw new ModelerException("define shelf", "store not found; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that aisle exists
        if (stores.get(storeId).getAisles().containsKey(aisleNumber) == false)
        {
            try
            {
                throw new ModelerException("define shelf", "aisle not found; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that shelf id is unique
        if (stores.get(storeId).getAisles().get(aisleNumber).getShelves().containsKey(shelfId))
        {
            try
            {
                throw new ModelerException("define shelf", "shelf id already exists; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that level input is a valid enum
        if (!Shelf.containsLevelEnum(level))
        {
            try
            {
                throw new ModelerException("define shelf", "level (case-sensitive) not found; shelf not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }       
        
        // Change level string to an enum
        Shelf.Level levelEnum = Shelf.Level.valueOf(level);
        
        // Get temperature enum for default temp
        Shelf.Temperature temperatureEnum = Shelf.Temperature.valueOf("ambient");
        
        Shelf shelf = new Shelf(shelfId, name, levelEnum, description, temperatureEnum);        
        
        // Add shelf to aisle's shelf list     
        if (shelf != null)
            stores.get(storeId).getAisles().get(aisleNumber).getShelves().put(shelf.getId(), shelf);        
        
        return shelf;
    }
    
    /* *
     * Prints a shelf's information to stdout
     * @param storeAisleShelfLoc The unique id of the shelf
     */
    @Override
    public void showShelf(String storeAisleShelfLoc, AuthToken authToken)
    {      
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        // Split storeAisleShelfLoc on colon
        String[] splitLoc = storeAisleShelfLoc.split(":");
        String storeId = splitLoc[0];
        String aisleNumber = splitLoc[1];
        String shelfId = splitLoc[2];
        
        // Check that store exists
        if (!stores.containsKey(storeId))
        {
            try
            {
                throw new ModelerException("show shelf", "store not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that aisle exists
        if (!stores.get(storeId).getAisles().containsKey(aisleNumber))
        {
            try
            {
                throw new ModelerException("show shelf", "aisle not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that shelf exists
        if (!stores.get(storeId).getAisles().get(aisleNumber).getShelves().containsKey(shelfId))
        {
            try
            {
                throw new ModelerException("show shelf", "shelf not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Initialize string of store's inventory information
        String inventoriesString = "";
        LinkedHashMap<String, Inventory> inventories = stores.get(storeId).getInventories();        
        int counter = 1;
        
        // If there are inventories
        if (inventories.size() > 0)
        {
            Inventory inventory;
            
            // Only get particular shelf's inventory           
            for (Entry<String, Inventory> inventoryEntry : inventories.entrySet())
            {
                inventory = inventoryEntry.getValue();
                
                // Split inventory location to get shelf
                String[] splitInvLoc = inventory.getLocation().split(":");                
                String invAisleNumber = splitInvLoc[1];
                String invShelfId = splitInvLoc[2];
                
                if(invAisleNumber.equals(aisleNumber) && invShelfId.equals(shelfId))
                {
                    inventoriesString += "\n    " + counter + ".)" + " inventoryId = " + inventory.getInventoryId() + "; location = "
                            + inventory.getLocation() + "; capacity = " + inventory.getCapacity() + "; count = " + inventory.getCount()
                            + "; productId = " + inventory.getProductId();                    
                    counter++;
                }
            }
            
            // If no inventory for shelf found
            if (inventoriesString.equals(""))
                inventoriesString += " None";
        }
        
        // If there are no inventories
        else
            inventoriesString += " None";
        
        // Combine inventories string with shelf's other information
        String string;
        Shelf shelf = stores.get(storeId).getAisles().get(aisleNumber).getShelves().get(shelfId);
        string = "\nShelf \"" + storeAisleShelfLoc + "\" information --\n" + " - name = "
                + shelf.getName() + "\n - level = " + shelf.getLevel() + "\n - description = "
                + shelf.getDescription() + "\n - temperature = " + shelf.getTemperature()
                + "\n - inventories =" + inventoriesString + "\n";           

        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(string);
    }
    
    /* *
     * Defines an inventory (product placement on shelf)
     * @param id The unique id of the inventory
     * @param storeAisleShelfLoc The location of the inventory
     * @param capacity The maximum number of a product item that can fit on the shelf
     * @param count The number of a product item on the shelf
     * @return An Inventory object
     */
    @Override
    public Inventory defineInventory(String id, String storeAisleShelfLoc, Integer capacity, Integer count, String productId, AuthToken authToken)
    {        
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
        
        // Check that count is within valid range
        if ((count < 0) || (count > capacity))
        {
            try
            {
                throw new ModelerException("define inventory", "count is not valid (between 0 and capacity); inventory not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that location and productId combination is unique
        Inventory inventory;
        for (Entry<String, Inventory> inventoryEntry : inventories.entrySet())
        {
            inventory = inventoryEntry.getValue();
            
            if (inventory.getLocation().equals(storeAisleShelfLoc) && inventory.getProductId().equals(productId))
            {
                try
                {
                    throw new ModelerException("define inventory", "inventory already defined for that product and shelf combination;"
                            + " inventory not created");
                }
                
                catch (ModelerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return null;
                }
            }
        }
        
        // Split storeAisleShelfLoc on colon
        String[] splitLoc = storeAisleShelfLoc.split(":");
        String storeId = splitLoc[0];
        String aisleNumber = splitLoc[1];
        String shelfId = splitLoc[2];        
        
        // Check that id is unique
        if (inventories.containsKey(id))
        {
            try
            {
                throw new ModelerException("define inventory", "id already exists; inventory not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that store exists
        if (!stores.containsKey(storeId))
        {
            try
            {
                throw new ModelerException("define inventory", "store not found; inventory not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that aisle exists
        if (!stores.get(storeId).getAisles().containsKey(aisleNumber))
        {
            try
            {
                throw new ModelerException("define inventory", "aisle not found; inventory not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that shelf exists
        if (!stores.get(storeId).getAisles().get(aisleNumber).getShelves().containsKey(shelfId))
        {
            try
            {
                throw new ModelerException("define inventory", "shelf not found; inventory not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }        
        
        // Check that productId exists
        if (!products.containsKey(productId))
        {
            try
            {
                throw new ModelerException("define inventory", "product not found; inventory not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Make sure shelf temperature can support product's temperature
        if (stores.get(storeId).getAisles().get(aisleNumber).getShelves().get(shelfId).getTemperature()
                != products.get(productId).getTemperature())
        {
            try
            {
                throw new ModelerException("define inventory", "shelf and product temperatures don't match; inventory not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        inventory = new Inventory(id, storeAisleShelfLoc, capacity, count, productId);        
                
        if (inventory != null)
        {
            // Add inventory id and its associated store to inventories list
            inventories.put(inventory.getInventoryId(), inventory);
            
            // Add inventory object to its associated store's inventory
            stores.get(storeId).getInventories().put(inventory.getInventoryId(), inventory);
        }  
        
        return inventory;
    }
    
    /* *
     * Prints an inventory to stdout
     * @param id The inventory's unique id
     */
    @Override
    public void showInventory(String id, AuthToken authToken)
    {         
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        // Retrieve inventory
        Inventory inventory = inventories.get(id);              
        
        // Check if inventory was found
        if (inventory == null)
        {
            try
            {                
                throw new ModelerException("show inventory", "inventory not found");
            }
            
            catch (ModelerException exception)
            {                
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }            
            
        // Concatenate inventory's information     
        String inventoryString = "\nInventory \"" + id + "\" information --\n" + " - location = " + inventory.getLocation() + "\n - capacity = " 
                + inventory.getCapacity() + "\n - count = " + inventory.getCount() + "\n - product id = "+ inventory.getProductId() + "\n";
        
        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(inventoryString);
    }
    
    /* *
     * Updates (increments or decrements) the product count on the shelf
     * @param id The unique id of the inventory
     * @param amount The amount to increment or decrement by, a positive or negative value, respectively 
     */
    @Override
    public void updateInventory(String id, Integer amount, AuthToken authToken)
    {       
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        Inventory inventory = inventories.get(id);
        
        // Check if inventory found
        if (inventory == null)
        {
            try
            {                
                throw new ModelerException("update inventory", "inventory not found");
            }
            
            catch (ModelerException exception)
            {                
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check if update amount plus count is greater than capacity or less than 0       
        if (((amount + inventory.getCount()) > inventory.getCapacity()) || ((amount + inventory.getCount()) < 0))
        {
            try
            {                
                throw new ModelerException("update inventory", "update would put count outside of valid range; update rejected");
            }
            
            catch (ModelerException exception)
            {                
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }       
        
        inventory.updateCount(amount);
    }
    
    /* *
     * Defines a store product with temperature input give
     * @return A Product object 
     */
    @Override
    public Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
            , String temperature, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
        
        // Check that productId is unique
        if (products.containsKey(productId))
        {
            try
            {                
                throw new ModelerException("define product", "productId already exists; product not created");
            }
            
            catch (ModelerException exception)
            {                
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that temperature is enum
        if (!Shelf.containsTempEnum(temperature))
        {
            try
            {
                throw new ModelerException("define product", "temperature (case-sensitive) invalid; product not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        } 
        
        // Change temperature string to an enum
        Shelf.Temperature temperatureEnum = Shelf.Temperature.valueOf(temperature);
        
        Product product = new Product(productId, name, description, size, category, unitPrice, temperatureEnum);
        
        // Add to list of products
        if (product != null)
            products.put(product.getProductId(), product);
        
        return product;
    }    
    
    /* *
     * Defines a store product with a default temperature
     * @return A Product object
     */
    @Override
    public Product defineProduct(String productId, String name, String description, String size, String category, Integer unitPrice
            , AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
        
        // Check that productId is unique
        if (products.containsKey(productId))
        {
            try
            {                
                throw new ModelerException("define product", "productId already exists; product not created");
            }
            
            catch (ModelerException exception)
            {                
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }        
        
        // Get default temperature enum ambient
        Shelf.Temperature temperatureEnum = Shelf.Temperature.valueOf("ambient");
        
        Product product = new Product(productId, name, description, size, category, unitPrice, temperatureEnum);
        
        // Add to list of products
        if (product != null)
            products.put(product.getProductId(), product);
        
        return product;
    }
    
    /* *
     * Prints a product's information to stdout
     * @param id The product's unique id
     */
    @Override
    public void showProduct(String id, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        // Get product
        Product product = products.get(id);            
        
        // Check if product was found
        if (product == null)
        {
            try
            {                
                throw new ModelerException("show product", "product not found");
            }
            
            catch (ModelerException exception)
            {                
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }            
            
        // Concatenate product's information     
        String productString = "\nProduct \"" + id + "\" information --\n" + " - name = " + product.getName() + "\n - description = " 
                + product.getDescription() + "\n - size = " + product.getSize() + "\n - category = "+ product.getCategory()
                + "\n - unit price = " + product.getUnitPrice() + "\n - temperature = " + product.getTemperature() + "\n";
        
        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(productString);       
    }
    
    /* *
     * Defines a customer
     * @param account The blockchain address of the customer used for billing
     * @return A Customer object
     */
    @Override
    public Customer defineCustomer(String id, String firstName, String lastName, String ageGroup, String type, String emailAddress
            , String account, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
        
        // Check if customer id is unique
        if (customers.containsKey(id))
        {
            try
            {
                throw new ModelerException("define customer", "id already exists; customer not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check if ageGroup is a valid enum
        if (!Customer.containsAgeGroupEnum(ageGroup))
        {
            try
            {
                throw new ModelerException("define customer", "age group (case-sensitive) not found; customer not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check if type is a valid enum
        if (!Customer.containsTypeEnum(type))
        {
            try
            {
                throw new ModelerException("define customer", "type (case-sensitive) not found; customer not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Convert ageGroup to enum
        Customer.AgeGroup ageGroupEnum = Customer.AgeGroup.valueOf(ageGroup);
        
        // Convert type to enum
        Customer.Type typeEnum = Customer.Type.valueOf(type);
        
        Customer customer = new Customer(id, firstName, lastName, ageGroupEnum, typeEnum, emailAddress, account);
        
        // Add to list of customers
        if (customer != null)
            customers.put(customer.getId(), customer);
            
        return customer;
    }
    
    /* *
     * Prints a customer's information to stdout
     * @param id The customer's unique id
     */
    @Override
    public void showCustomer(String id, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        Customer customer = customers.get(id);
        
        // Check if customer exists
        if (customer == null)
        {
            try
            {
                throw new ModelerException("show customer", "customer not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }    
        
        // Create customer's information string    
        String customerString = "\nCustomer \"" + customer.getId() + "\" information --\n" + " - first name = " + customer.getFirstName()
                + "\n - last name = " + customer.getLastName() + "\n - age group = " + customer.getAgeGroup() + "\n - type = "
                + customer.getType() + "\n - email address = " + customer.getEmailAddress() + "\n - account = " + customer.getAccount()
                + "\n - location = " + customer.getLocation() + "\n - time last seen = "+ customer.getTimeLastSeen() + "\n";
        
        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(customerString); 
    }
    
    /* *
     * Updates a customer's location
     * @param id The customer's unique id
     * @param storeAisleLoc The location to update the customer at
     * Referenced https://compiler.javatpoint.com/opr/test.jsp?filename=CurrentDateTimeExample1
     */
    @Override
    public void updateCustomer(String id, String storeAisleLoc, String dateTime, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        // Check that customer exists
        Customer customer = customers.get(id);
        
        if (customer == null)
        {
            try
            {
                throw new ModelerException("update customer", "customer not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // If customer's current location is null and update location is null
        if ((customer.getLocation() == null) && storeAisleLoc.equals("null"))
            return;               
        
        // If update location is not null, e.g., a store location or invalid syntax
        if (!storeAisleLoc.equals("null"))
        {
            // Split storeAisleLoc on colon
            String[] splitLoc = storeAisleLoc.split(":");
            String storeId = splitLoc[0];
            String aisleNumber = splitLoc[1];
            
            // Check that store exists
            if (!stores.containsKey(storeId))
            {
                try
                {
                    throw new ModelerException("update customer", "store not found");
                }
                
                catch (ModelerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
            
            // Check that aisle exists
            if (!stores.get(storeId).getAisles().containsKey(aisleNumber) && !aisleNumber.equals("null"))
            {
                try
                {
                    throw new ModelerException("update customer", "aisle not found");
                }
                
                catch (ModelerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
            
            // Add/update customer in store's list of active customers
            stores.get(storeId).getCustomers().put(customer.getId(), customer);
        }
        
        // Else customer left a store
        else
        {
            // Get customer's current location
            String currentLoc = customer.getLocation();
            
            // Split location to get store
            String storeId = currentLoc.split(":")[0];
            
            // Remove customer from store's list of active customers
            stores.get(storeId).getCustomers().remove(id);
        }
            
        // Update customer's "time last seen"        
        customer.setTimeLastSeen(dateTime);
        
        // Change customer's location
        if (storeAisleLoc.equals("null"))
            customer.setLocation(null);
        else
            customer.setLocation(storeAisleLoc);
    }
    
    /* *
     * Gets a customer's basket
     * @param customerId The unique id of the customer
     * @return A Basket object
     */
    @Override
    public Basket getCustomerBasket(String customerId, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
        
        Customer customer = customers.get(customerId);
        
        // Check that customer exists
        if (customer == null)
        {
            try
            {
                throw new ModelerException("get customer basket", "customer not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that customer is registered (not a guest)
        if (!customer.getType().equals(Customer.Type.registered))
        {
            try
            {
                throw new ModelerException("get customer basket", "unregistered customer; request denied");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that customer is in a store
        if (customers.get(customerId).getLocation() == null)
        {
            try
            {
                throw new ModelerException("get customer basket", "customer is not in a store; request denied");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        Basket basket = activeBaskets.get(customerId);
        
        // If customer basket wasn't found in activeBaskets list
        if (basket == null)
        {
            // Create a new basket
            basket = new Basket(customerId);
            
            // Add basket to list of active baskets
            if (basket != null)
                activeBaskets.put(basket.getId(), basket);
        }
        
        return basket;
    }
    
    /* *
     * Add a product to a customer's basket
     * @param customerId The basket's unique id (same as customer's id)
     * @param itemCount The amount of the product to add
     */
    @Override
    public void addBasketItem(String customerId, String productId, Integer itemCount, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        // Check that itemCount is greater than 0
        if (itemCount < 1)
        {
            try
            {
                throw new ModelerException("add basket item", "item_count must be 1 or greater; item not added");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that basket exists
        if (!activeBaskets.containsKey(customerId))
        {
            try
            {
                throw new ModelerException("add basket item", "basket not found; item not added");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that customer is in a store
        if (customers.get(customerId).getLocation() == null)
        {
            try
            {
                throw new ModelerException("add basket item", "customer is not in a store; item not added");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that product is sold at the particular store and its capacity is >= to itemCount        
        boolean hasProduct = false;
        int capacity = 0;
        Store store = stores.get(customers.get(customerId).getLocation().split(":")[0]);
        LinkedHashMap<String, Inventory> storeInventories = store.getInventories();
                
        Inventory inventory;              
        for (Entry<String, Inventory> inventoryEntry : storeInventories.entrySet())
        {
            inventory = inventoryEntry.getValue();

            if (inventory.getProductId().equals(productId))
            {
                hasProduct = true;                
                capacity += inventory.getCapacity();      
            }
        }
        
        if (!hasProduct)
        {
            try
            {
                throw new ModelerException("add basket item", "item not found; item not added");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // If max number of the item store sells is less than what customer wants to put in basket
        if (capacity < itemCount)
        {
            try
            {
                throw new ModelerException("add basket item", "not enough items available; item not added");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Get customer's basket to add item
        Basket basket = activeBaskets.get(customerId);
        LinkedHashMap<String, Integer> basketItems = basket.getBasketItems();
        
        // If basket doesn't already contain product
        if (!basketItems.containsKey(productId))
            basketItems.put(productId, itemCount);
        
        // Else add itemCount to the current count of the product
        else
        {            
            Integer currentItemCount = basketItems.get(productId);
            itemCount += currentItemCount;
            basketItems.put(productId, itemCount);
        }     
    }
    
    /* *
     * Remove a product from a customer's basket
     * @param customerId The basket's unique id (same as customer's id)
     * @param itemCount The amount of the product to remove
     */
    @Override
    public void removeBasketItem(String customerId, String productId, Integer itemCount, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        // Check that itemCount is greater than 0
        if (itemCount < 1)
        {
            try
            {
                throw new ModelerException("remove basket item", "item_count must be 1 or greater; item not removed");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that basket exists
        if (!activeBaskets.containsKey(customerId))
        {
            try
            {
                throw new ModelerException("remove basket item", "basket not found; item not removed");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that customer is in a store
        if (customers.get(customerId).getLocation() == null)
        {
            try
            {
                throw new ModelerException("remove basket item", "customer is not in a store; item not removed");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Get customer's basket to remove item
        Basket basket = activeBaskets.get(customerId);
        LinkedHashMap<String, Integer> basketItems = basket.getBasketItems();
        
        // If basket doesn't contain product
        if (!basketItems.containsKey(productId))
        {
            try
            {
                throw new ModelerException("remove basket item", "item not found in basket");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }        
        
        // Else remove itemCount to the current count of the product
        else
        {            
            Integer currentItemCount = basketItems.get(productId);
            
            // Check if difference is negative
            if ((currentItemCount - itemCount) < 0)
            {
                try
                {
                    throw new ModelerException("remove basket item", "attempting to remove more than is in basket; item(s) not removed");
                }
                
                catch (ModelerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
            
            else
            {
                currentItemCount = currentItemCount - itemCount;
                if (currentItemCount == 0)
                    basketItems.remove(productId);
                else
                    basketItems.put(productId, currentItemCount);
            }
        }          
    }
    
    /* *
     * Clears a customer's basket and removes its association to them
     * @param customerId The basket's unique id (same as customer's id)
     */
    @Override
    public void clearBasket(String customerId, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        // Check if basket exists
        if (!activeBaskets.containsKey(customerId))
        {
            try
            {
                throw new ModelerException("clear basket", "basket not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        else
            activeBaskets.remove(customerId);
    }
    
    /* *
     * Print a customer's basket items to stdout
     * @param customerId The basket's unique id (same as customer's id)
     */
    @Override
    public void showBasketItems(String customerId, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        Basket basket = activeBaskets.get(customerId);
        
        // Check if basket exists
        if (basket == null)
        {
            try
            {
                throw new ModelerException("show basket items", "basket not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Initialize string for basket items info
        String itemsString = "";        
        
        if (basket.getBasketItems().size() > 0)
        {
            String productId;
            Integer count;
            int counter = 1;
            for (Entry<String, Integer> integerEntry : basket.getBasketItems().entrySet())
            {
                productId = integerEntry.getKey();
                count = integerEntry.getValue();                
                itemsString += "\n " + counter + ".)" + " product id = " + productId + " : count = " + count;                
                counter++;            
            }
        }
        
        // If basket has no items
        else
            itemsString += " Basket empty";
        
        // Combine itemsString with header text
        String string;
        string = "\nBasket \"" + basket.getId() + "\" items --" + itemsString + "\n";           

        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(string); 
    }
    
    /* *
     * Defines a device, i.e., a Sensor of Appliance object (Appliance class extends Sensor)
     * @param storeAisleLoc The aisle location of the sensor
     * @return A Sensor object (Appliance class extends Sensor)
     */
    @Override
    public Sensor defineDevice(String id, String name, String type, String storeAisleLoc, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return null;
        
        // Check that id is unique
        if (devices.containsKey(id))
        {
            try
            {
                throw new ModelerException("define device", "device id already exists; device not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // Check that location is valid
        String storeId = storeAisleLoc.split(":")[0];
        String aisleNumber = storeAisleLoc.split(":")[1];
        
        if (!stores.containsKey(storeId))
        {
            try
            {
                throw new ModelerException("define device", "store not found; device not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
            
        if (!stores.get(storeId).getAisles().containsKey(aisleNumber) && !aisleNumber.equals("null"))
        {
            try
            {
                throw new ModelerException("define device", "aisle not found; device not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
        // If type is sensor enum
        if (Sensor.containsTypeEnum(type))
        {                     
            Sensor sensor = new Sensor(id, name, type, storeAisleLoc);
            
            if (sensor != null)
            {
                // Add sensor to device list
                devices.put(sensor.getId(), sensor);
                
                // Add sensor to store's device list
                stores.get(storeId).getDevices().put(sensor.getId(), sensor);
            }
            
            return sensor;
        }        
        
        // If type is appliance enum
        else if (Appliance.containsTypeEnum(type))
        {           
            Appliance appliance = new Appliance(id, name, type, storeAisleLoc);
            
            if (appliance != null)
            {
                // Add appliance to device list
                devices.put(appliance.getId(), appliance);
                
                // Add appliance to store's device list
                stores.get(storeId).getDevices().put(appliance.getId(), appliance);
            }
            
            return appliance;
        }
        
        else
        {
            try
            {
                throw new ModelerException("define device", "type unknown; device not created");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }            
        }
    }
    
    /* *
     * Prints a device's information to stdout
     * @param id The device's unique id
     */
    @Override
    public void showDevice(String id, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        Sensor device = devices.get(id);
        
        // If device wasn't found
        if (device == null)
        {
            try
            {
                throw new ModelerException("show device", "device not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Concatenate device's information     
        String deviceString = "\nDevice \"" + id + "\" information --\n" + " - name = " + device.getName() + "\n - type = " 
                + device.getType() + "\n - location = " + device.getLocation() + "\n";
        
        // Print string to stdout
        System.out.print("\nOutput:>>");
        System.out.print(deviceString);
    }
    
    /* *
     * Deprecated --
     * Sends a command to an appliance
     * @param id The unique id of the appliance
     * @param command The command to be sent
     */
    @Override
    public void createCommand(String id, String command, AuthToken authToken)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionVisitor getPermission = authenticator.hasPermission(new PermissionTuple("use StoreModelService"), authToken);
        if ((getPermission == null) || !getPermission.getHasPermission())
            return;
        
        Sensor device = devices.get(id);
        
        // If device wasn't found
        if (device == null)
        {
            try
            {
                throw new ModelerException("create command", "device not found");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
        
        // Check that device is an appliance
        if (!Appliance.containsTypeEnum(device.getType()))
        {
            try
            {
                throw new ModelerException("create command", "device is not an appliance; command not sent");
            }
            
            catch (ModelerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }              
    }    
    
    @Override
    public LinkedHashMap<String, Store> getStores(AuthToken authToken)
    {
        return stores;
    }    

    @Override
    public LinkedHashMap<String, Product> getProducts(AuthToken authToken)
    {
        return products;
    }    

    @Override
    public LinkedHashMap<String, Customer> getCustomers(AuthToken authToken)
    {
        return customers;
    }
    
    /* Getters and Setters */

    public LinkedHashMap<String, Inventory> getInventories()
    {
        return inventories;
    }    

    public LinkedHashMap<String, Basket> getActiveBaskets()
    {
        return activeBaskets;
    }    

    public LinkedHashMap<String, Sensor> getDevices()
    {
        return devices;
    }             
}

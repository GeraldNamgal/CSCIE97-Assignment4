/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthTokenTuple;
import com.cscie97.store.authentication.GetPermissionsVisitor;
import com.cscie97.store.authentication.StoreAuthenticationService;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Basket;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Inventory;
import com.cscie97.store.model.Observer;
import com.cscie97.store.model.Sensor;
import com.cscie97.store.model.Store;
import com.cscie97.store.model.StoreModelService;
import com.cscie97.store.model.Subject;

/* *
 * Controller implements the Observer interface (i.e., the observer in the observer pattern) which contains an update method
 * for receiving UpdateEvents from a subject it's registered with. It gets access to the Subject interface of the Store Model
 * Service and the Ledger Service via parameters that are passed into its constructor
 */
public class Controller implements Observer
{
    /* Variables */
    
    private StoreModelService modeler;
    private StoreAuthenticationService authenticator;
    private com.cscie97.ledger.CommandProcessor ledgerCp;
    private AuthToken myAuthToken;
    
    /* Constructor */ 
    
    public Controller(Subject modeler, com.cscie97.ledger.CommandProcessor ledgerCp, StoreAuthenticationService authenticator)
    {       
        // Register Controller with Model Service
        modeler.registerObserver(this);
        
        this.modeler = (StoreModelService) modeler;
        this.ledgerCp = ledgerCp;
        this.authenticator = authenticator;
    }

    /* API Method(s) */
    
    /* *
     * Receives UpdateEvents from the subject it's registered with and calls handleEvent with it
     * @param event The UpdateEvent sent from the subject
     */
    @Override
    public void update(UpdateEvent event)
    {        
        handleEvent(event);
    }    
    
    /* Utility Method(s) */
    
    /* *
     * The event handler for UpdateEvents received from a subject. Calls the appropriate Command type in response
     * to the UpdateEvent and executes it
     * @param event The UpdateEvent received from the subject
     */
    public void handleEvent(UpdateEvent event)
    {
        // Get event's string array
        String[] eventStrArr = event.getPerceivedEvent();
        
        /* Classify event to a Command: */
        
        // If Emergency event
        if ((eventStrArr.length == 3) && eventStrArr[0].equals("emergency"))
        {    
            // Check for emergency types
            if (eventStrArr[1].equals("fire") || eventStrArr[1].equals("flood") || eventStrArr[1].equals("earthquake")
                    || eventStrArr[1].equals("armed_intruder"))
            {
                // Create new Emergency               
                Command emergency = new Emergency(event.getSourceDevice(), eventStrArr[1], eventStrArr[2]);
                
                System.out.println("\nEMERGENCY EVENT received. Emergency Command created and executing...");
                
                // Run the Command's execute method
                emergency.execute();
            }
            
            else
            {
                try
                {
                    throw new ControllerException("handle event; emergency", "event is not recognized");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
        }
        
        // If Basket event
        else if ((eventStrArr.length == 6) && eventStrArr[0].equals("basket_items") && (eventStrArr[2].equals("add") || eventStrArr[2].equals("remove")))
        {            
            // Check if integer input is valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(eventStrArr[4]);                
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    throw new ControllerException("handle event; basket_items", "event is not recognized");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
            
            Command basketItems = new BasketItems(event.getSourceDevice(), eventStrArr[1], eventStrArr[2], eventStrArr[3], eventStrArr[4], eventStrArr[5]);
            
            System.out.println("\nBASKET EVENT received. BasketItems Command created and executing...");
            
            basketItems.execute();
        }
        
        // If Cleaning event
        else if ((eventStrArr.length == 3) && eventStrArr[0].equals("clean"))
        {
            Command clean = new Clean(event.getSourceDevice(), eventStrArr[1], eventStrArr[2]);
            
            System.out.println("\nCLEANING EVENT received. Clean Command created and executing...");
            
            clean.execute();
        }
        
        // If Broken Glass event
        else if ((eventStrArr.length == 2) && eventStrArr[0].equals("broken_glass"))
        {
            Command brokenGlass = new BrokenGlass(event.getSourceDevice(), eventStrArr[1]);
            
            System.out.println("\nBROKEN GLASS EVENT received. BrokenGlass Command created and executing...");
            
            brokenGlass.execute();
        }
        
        // If Missing Person event
        else if ((eventStrArr.length == 2) && eventStrArr[0].equals("missing_person"))
        {
            Command missingPerson = new MissingPerson(event.getSourceDevice(), eventStrArr[1]);
           
            System.out.println("\nMISSING PERSON EVENT received. MissingPerson Command created and executing...");
            
            missingPerson.execute();
        }
        
        // If Customer Seen event
        else if ((eventStrArr.length == 4) && eventStrArr[0].equals("customer_seen"))
        {
            Command customerSeen = new CustomerSeen(event.getSourceDevice(), eventStrArr[1], eventStrArr[2], eventStrArr[3]);
            
            System.out.println("\nCUSTOMER SEEN EVENT received. CustomerSeen Command created and executing...");
            
            customerSeen.execute();
        }
        
        // If Fetch Product event
        else if ((eventStrArr.length == 6) && eventStrArr[0].equals("fetch_product"))
        {
            // Check if integer input is valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(eventStrArr[2]);                
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    throw new ControllerException("handle event; fetch_product", "event is not recognized");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
            
            Command fetchProduct = new FetchProduct(event.getSourceDevice(), eventStrArr[1], Integer.parseInt(eventStrArr[2])
                    , eventStrArr[3], eventStrArr[4], eventStrArr[5]);
            
            System.out.println("\nFETCH PRODUCT EVENT received. FetchProduct Command created and executing...");
            
            fetchProduct.execute();
        }
        
        // If Check Account Balance event
        else if ((eventStrArr.length == 2) && eventStrArr[0].equals("account_balance"))
        {
            Command accountBalance = new AccountBalance(event.getSourceDevice(), eventStrArr[1]);
            
            System.out.println("\nCHECK ACCOUNT BALANCE EVENT received. AccountBalance Command created and executing...");
            
            accountBalance.execute();
        }
        
        // If Assist Customer to Car event
        else if ((eventStrArr.length == 2) && eventStrArr[0].equals("car_assist"))
        {
            Command carAssist = new CarAssist(event.getSourceDevice(), eventStrArr[1]);
            
            System.out.println("\nASSIST CUSTOMER TO CAR EVENT received. CarAssist Command created and executing...");
            
            carAssist.execute();
        }
        
        // If Checkout event
        else if ((eventStrArr.length == 2) && eventStrArr[0].equals("checkout"))
        {
            Command checkout = new Checkout(event.getSourceDevice(), eventStrArr[1]);
            
            System.out.println("\nCHECKOUT EVENT received. Checkout Command created and executing...");
            
            checkout.execute();
        }
        
        // If Enter Store event
        else if ((eventStrArr.length == 2) && eventStrArr[0].equals("enter_store"))
        {
            Command enterStore = new EnterStore(event.getSourceDevice(), eventStrArr[1]);
            
            System.out.println("\nENTER STORE EVENT received. EnterStore Command created and executing...");
            
            enterStore.execute();
        }
        
        // Else event wasn't recognized
        else
        {
            try
            {
                throw new ControllerException("handle event", "event is not recognized");
            }
            
            catch (ControllerException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }
        }
    }
    
    /* The Command Classes: */
    
    /* *
     * Emergency class that provides the actions needed to appropriately respond to an Emergency event 
     */
    public class Emergency extends Command
    {       
        /* Variables */
        
        private String eventType;
        private String aisleNumber;
        
        /* Constructor */
        
        /* *
         * @param sourceDevice The device that created / sent the event (which also contains a reference to the source store)
         * @param eventType The type of event (fire, flood, earthquake, or armed_intruder)
         * @param aisleNumber The aisle number the event is happening at
         */
        public Emergency(Sensor sourceDevice, String eventType, String aisleNumber)
        {
            super(sourceDevice);
            
            this.eventType = eventType;
            this.aisleNumber = aisleNumber;
        }

        /* Method(s) */
        
        /* *
         * Opens the store's turnstiles, and commands the store's speakers, and robots to perform certain duties
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            
            if (stores == null)
            {
                return;
            }
            
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);
            
            // Initialize array for getting store's device map's robot type appliance keys
            ArrayList<String> robotKeys = new ArrayList<String>();            
            
            // Iterate through devices and perform type-specific actions
            Sensor devicePointer;
            for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
            {
                devicePointer = deviceEntry.getValue();
                
                // Check if device is an appliance              
                if (Appliance.containsTypeEnum(devicePointer.getType()))
                {
                    Appliance appliance = (Appliance) devicePointer;
                
                    // If device is a turnstile
                    if (appliance.getType().equals("turnstile"))
                    {                    
                        // Open turnstile
                        if (appliance.getTurnstile(new AuthTokenTuple(myAuthToken)) != null)
                            System.out.println(appliance.getName() + ": open = " + appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).setOpen(true));
                    }
                    
                    // If device is a speaker
                    if (appliance.getType().equals("speaker"))
                    {
                        // Announce emergency
                        if (appliance.getSpeaker(new AuthTokenTuple(myAuthToken)) != null)
                        {
                            String announcement = "There is a " + eventType + " in " + store.getAisles().get(aisleNumber).getName()
                                    + " aisle. Please leave store immediately!";
                            
                            System.out.println(appliance.getName() + ": \"" + announcement + "\"");
                            appliance.getSpeaker(new AuthTokenTuple(myAuthToken)).announce(announcement);
                        }
                    }
                    
                    // If device is a robot
                    if (appliance.getType().equals("robot"))
                    {
                        // Add hash map's robot key to array
                        robotKeys.add(deviceEntry.getKey());                       
                    }
                }
            }
            
            // If store has a robot
            if (robotKeys.size() > 0)
            {
                Appliance appliance = (Appliance) store.getDevices().get(robotKeys.get(0));                
                
                if (appliance.getRobot(new AuthTokenTuple(myAuthToken)) != null)
                {
                    System.out.println(appliance.getName() + ": Addressing " + eventType + " in " + store.getAisles().get(aisleNumber).getName() + " aisle");
                    appliance.getRobot(new AuthTokenTuple(myAuthToken)).addressEmergency(eventType, store.getAisles().get(aisleNumber).getNumber());
                }
            }
            
            // If store has more than one robot
            if (robotKeys.size() > 1)
            {
                Appliance appliance; 
                for (int i = 1; i < robotKeys.size(); i++)
                {
                    appliance = (Appliance) store.getDevices().get(robotKeys.get(i));
                    
                    if (appliance.getRobot(new AuthTokenTuple(myAuthToken)) != null)
                    {
                        System.out.println(appliance.getName() + ": Assisting customers leaving " + store.getName());
                        appliance.getRobot(new AuthTokenTuple(myAuthToken)).assstLeavingCstmrs(store.getId());
                    }
                }
            }
        }
    }
    
    /* *
     * Class that represents the commands and actions to be taken in response to a Basket Items event
     */
    public class BasketItems extends Command
    {             
        private String customerId;
        private String addOrRemove;
        private String productId;
        private String number;
        private String aisleShelfLoc;
        
        /* Constructor */
        
        /* *
         * @param number The number of the product to add or remove
         * @param aisleShelfLoc The aisle and shelf location of the product that's being added or removed
         */
        public BasketItems(Sensor sourceDevice, String customerId, String addOrRemove, String productId, String number, String aisleShelfLoc)
        {
            super(sourceDevice);
            
            this.customerId = customerId;
            this.addOrRemove = addOrRemove;
            this.productId = productId;
            this.number = number;
            this.aisleShelfLoc = aisleShelfLoc;
        }

        /* Method(s) */
        
        /* *
         * Updates a customer's basket and the product's associated store inventory. Commands a robot to restock the product
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);
            
            // Get inventory using event's location and product info
            LinkedHashMap<String, Inventory> inventories = store.getInventories();
            Inventory inventory = null;
            
            for (Entry<String, Inventory> inventoryEntry : inventories.entrySet())
            {
                if (inventoryEntry.getValue().getLocation().equals(store.getId() + ":" + aisleShelfLoc)
                        && inventoryEntry.getValue().getProductId().equals(productId))
                {
                    inventory = inventoryEntry.getValue();
                    break;
                }
            }
            
            // Get customer's (virtual) basket
            modeler.getCustomerBasket(customerId, new AuthTokenTuple(myAuthToken));
            
            // Initialize array for getting store's robot Appliance map keys (for restocking)
            ArrayList<String> robotKeys = new ArrayList<String>();           
            
            // Iterate through all devices to find the robots (if any)
            Sensor devicePointer;
            
            for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
            {
                devicePointer = deviceEntry.getValue();
                
                // If device is an appliance              
                if (Appliance.containsTypeEnum(devicePointer.getType()))
                {
                    // Cast device to an appliance
                    Appliance appliance = (Appliance) devicePointer;             
                    
                    // If device is a robot
                    if (appliance.getType().equals("robot"))
                    {
                        // Add robot device's map key to array
                        robotKeys.add(deviceEntry.getKey());                       
                    }
                }
            }
                        
            // If event was customer added (rather than removed) a basket item
            if (addOrRemove.equals("add") && !inventory.equals(null))
            {                            
                // Update customer's virtual basket items by adding item to it
                System.out.println("Controller Service: Adding " + Integer.parseInt(number) + " of " + productId + " to customer "
                        + customerId + "'s virtual basket");
                modeler.addBasketItem(customerId, productId, Integer.parseInt(number), new AuthTokenTuple(myAuthToken));                
                
                // Update inventory by decrementing product count on the shelf
                System.out.println("Controller Service: Updating inventory " + inventory.getInventoryId() + "'s count by " + Integer.parseInt(number) * (-1));                
                modeler.updateInventory(inventory.getInventoryId(), (Integer.parseInt(number) * (-1)), new AuthTokenTuple(myAuthToken));
                             
                // If store has robots then command one to restock what customer removed
                if (robotKeys.size() > 0)
                {                
                    // Put all the storeroom aisle numbers in a hashset (for locating product in storeroom)
                    HashSet<String> storeroomAisles = new HashSet<String>();
                    
                    for (Entry<String, Aisle> aisleEntry : store.getAisles().entrySet())
                    {
                        if (aisleEntry.getValue().getLocation().toString().equals("storeroom"))
                            storeroomAisles.add(aisleEntry.getValue().getNumber());
                    }
                    
                    // If there were any storeroom aisles
                    if (storeroomAisles.size() > 0)
                    {
                        // Collect storeroom-only inventories that have the given productId and has supply
                        ArrayList<String> storeroomInvIds = new ArrayList<String>();
                     
                        for (Entry<String, Inventory> inventoryEntry : inventories.entrySet())
                        {
                            if (inventoryEntry.getValue().getProductId().equals(productId)
                                    && storeroomAisles.contains(inventoryEntry.getValue().getLocation().split(":")[1])
                                    && (inventoryEntry.getValue().getCount() > 0))
                            {                        
                                storeroomInvIds.add(inventoryEntry.getValue().getInventoryId());
                            }
                        }                       
                                               
                        // If product was found in storeroom
                        if (storeroomInvIds.size() > 0)
                        {
                            // Get robot
                            Appliance appliance = (Appliance) store.getDevices().get(robotKeys.get(0));    
                            
                            // Have robot restock
                            System.out.println(appliance.getName() + ": Restocking " + productId + " from "
                                    + (inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[1] + ":"
                                            + inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[2]) + " (storeroom) "
                                    + "to " + aisleShelfLoc);
                            
                            appliance.getRobot(new AuthTokenTuple(myAuthToken)).restock(productId, (inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[1]
                                    + inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[2]), aisleShelfLoc);                                         
                                
                            // If storeroom product supply is less than update amount then get all of the supply for restock
                            Integer updateAmount = Integer.parseInt(number);
                            if (inventories.get(storeroomInvIds.get(0)).getCount() < updateAmount)
                                updateAmount = inventories.get(storeroomInvIds.get(0)).getCount();
                            
                            // Update storeroom location's inventory
                            System.out.println("Controller Service: Updating inventory " + inventories.get(storeroomInvIds.get(0)).getInventoryId()
                                    + "'s (storeroom) count by " + (updateAmount * (-1)));
                            modeler.updateInventory(inventories.get(storeroomInvIds.get(0)).getInventoryId(), (updateAmount * (-1)), new AuthTokenTuple(myAuthToken));                                                

                            // Update floor location's inventory
                            System.out.println("Controller Service: Updating inventory " + inventory.getInventoryId() + "'s count by " + updateAmount);
                            modeler.updateInventory(inventory.getInventoryId(), updateAmount, new AuthTokenTuple(myAuthToken));                    
                        }
                        
                        // Else there's no product to restock with
                        else
                        {
                            try
                            {
                                throw new ControllerException("Basket Items event restock", "no items to restock with");
                            }
                            
                            catch (ControllerException exception)
                            {
                                System.out.println();
                                System.out.print(exception.getMessage());      
                                return;
                            }
                        }                        
                    }
                        
                    // Else there are no storeroom aisles; can't restock
                    else
                    {
                        try
                        {
                            throw new ControllerException("Basket Items event restock", "no storeroom aisles to restock from");
                        }
                        
                        catch (ControllerException exception)
                        {
                            System.out.println();
                            System.out.print(exception.getMessage());      
                            return;
                        }
                    }
                }
                
                // Else there are no robots found that can restock
                else
                {
                    try
                    {
                        throw new ControllerException("Basket Items event restock", "no robots found to restock");
                    }
                    
                    catch (ControllerException exception)
                    {
                        System.out.println();
                        System.out.print(exception.getMessage());      
                        return;
                    }
                }
            }
            
            // Else if event was customer removed (rather than added) a basket item
            else if (addOrRemove.equals("remove") && !inventory.equals(null))
            {
                // Update basket items by removing item from it
                System.out.println("Controller Service: Removing " + Integer.parseInt(number) + " of " + productId + " from customer " + customerId + "'s virtual basket");                
                modeler.removeBasketItem(customerId, productId, Integer.parseInt(number), new AuthTokenTuple(myAuthToken));
                
                // If there's room on the shelf to put back items, add them back to shelf
                if ((inventory.getCapacity() - inventory.getCount()) >= Integer.parseInt(number))
                    modeler.updateInventory(inventory.getInventoryId(), Integer.parseInt(number), new AuthTokenTuple(myAuthToken));
                
                // Else if there's no room on the floor shelf, put back on storeroom shelf
                else
                {
                    // Put all the storeroom aisle numbers in a hashset (for locating product storage in storeroom)
                    HashSet<String> storeroomAisles = new HashSet<String>();
                    
                    for (Entry<String, Aisle> aisleEntry : store.getAisles().entrySet())
                    {
                        if (aisleEntry.getValue().getLocation().toString().equals("storeroom"))
                            storeroomAisles.add(aisleEntry.getValue().getNumber());
                    }
                    
                    // If there were any storeroom aisles
                    if (storeroomAisles.size() > 0)
                    {
                        // Collect storeroom-only inventories that have the given productId and have shelf room
                        ArrayList<String> storeroomInvIds = new ArrayList<String>();
                     
                        for (Entry<String, Inventory> inventoryEntry : inventories.entrySet())
                        {
                            if (inventoryEntry.getValue().getProductId().equals(productId)
                                    && storeroomAisles.contains(inventoryEntry.getValue().getLocation().split(":")[1])
                                    && ((inventoryEntry.getValue().getCapacity() - inventoryEntry.getValue().getCount()) >= Integer.parseInt(number)))
                            {                        
                                storeroomInvIds.add(inventoryEntry.getValue().getInventoryId());
                            }
                        }                       
                                               
                        // If product storage was found in storeroom
                        if (storeroomInvIds.size() > 0)
                        {
                            // Get robot
                            Appliance appliance = (Appliance) store.getDevices().get(robotKeys.get(0));    
                            
                            // Have robot restock
                            System.out.println(appliance.getName() + ": No room on " + aisleShelfLoc + "; restocking "
                                    + productId + " to " + (inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[1] + ":"
                                            + inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[2]) + " (storeroom)");
                            
                            appliance.getRobot(new AuthTokenTuple(myAuthToken)).restock(productId, (inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[1]
                                    + inventories.get(storeroomInvIds.get(0)).getLocation().split(":")[2]), aisleShelfLoc);                       
                                
                            // Update storeroom location's inventory
                            System.out.println("Controller Service: Updating inventory " + inventories.get(storeroomInvIds.get(0)).getInventoryId()
                                    + "'s (storeroom) count by " + Integer.parseInt(number));
                            modeler.updateInventory(inventories.get(storeroomInvIds.get(0)).getInventoryId(), Integer.parseInt(number), new AuthTokenTuple(myAuthToken));                    
                        }
                        
                        // Else there's no storage to put items back on
                        else
                        {
                            try
                            {
                                throw new ControllerException("Basket Items event; customer put items back", "there is no storage to put items back on");
                            }
                            
                            catch (ControllerException exception)
                            {
                                System.out.println();
                                System.out.print(exception.getMessage());      
                                return;
                            }
                        }
                        
                    }
                        
                    // Else there are no storeroom aisles; can't restock
                    else
                    {
                        try
                        {
                            throw new ControllerException("Basket Items event; customer put items back", "no storeroom to put items in");
                        }
                        
                        catch (ControllerException exception)
                        {
                            System.out.println();
                            System.out.print(exception.getMessage());      
                            return;
                        }
                    }
                }                
            }                
        }
    }
    
    /* *
     * Class that represents the commands and actions to be taken in response to a Clean event 
     */
    public class Clean extends Command
    {       
        /* Variables */
        
        private String productId;
        private String aisleNumber;
        
        /* Constructor */
        
        /* *
         * @param aisleNumber The aisle number where to product to be cleaned is
         */
        public Clean(Sensor sourceDevice, String productId, String aisleNumber)
        {
            super(sourceDevice);
            
            this.productId = productId;
            this.aisleNumber = aisleNumber;
        }

        /* Method(s) */
        
        /* *
         * Commands a robot to clean the product up in the given aisle
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);                
            
            // Initialize array for getting store's robot appliance map keys
            ArrayList<String> robotKeys = new ArrayList<String>();           
            
            // Iterate through devices to find robots
            Sensor devicePointer;
            for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
            {
                devicePointer = deviceEntry.getValue();
                
                // Check if device is an appliance              
                if (Appliance.containsTypeEnum(devicePointer.getType()))
                {
                    Appliance appliance = (Appliance) devicePointer;             
                    
                    // If device is a robot
                    if (appliance.getType().equals("robot"))
                    {
                        // Add robot key to array
                        robotKeys.add(deviceEntry.getKey());                       
                    }
                }
            }            
            
            // If store has a robot, tell it to clean up the product
            if (robotKeys.size() > 0)
            {
                Appliance appliance = (Appliance) store.getDevices().get(robotKeys.get(0));      
                                    
                System.out.println(appliance.getName() + ": Cleaning " + modeler.getProducts(new AuthTokenTuple(myAuthToken)).get(productId).getName()+ " in "
                        + store.getAisles().get(aisleNumber).getName() + " aisle");
                
                appliance.getRobot(new AuthTokenTuple(myAuthToken)).clean(productId, aisleNumber);                
            }
            
            // Else if store doesn't have a robot; cancel actions
            else
            {
                try
                {
                    throw new ControllerException("Clean event", "no robot to perform cleaning");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
        }
    }
    
    /* *
     * Class that represents the device commands and actions to be taken in response to a Broken Glass event 
     */
    public class BrokenGlass extends Command
    {
        /* Variables */
        
        private String aisleNumber;
        
        /* Constructor */
        
        public BrokenGlass(Sensor sourceDevice, String aisleNumber)
        {
            super(sourceDevice);
            
            this.aisleNumber = aisleNumber;
        }

        /* *
         * Commands a robot to clean up broken glass in given aisle
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);                
            
            // Initialize array for getting store's robot appliance map keys
            ArrayList<String> robotKeys = new ArrayList<String>();           
            
            // Iterate through devices to find robots
            Sensor devicePointer;
            for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
            {
                devicePointer = deviceEntry.getValue();
                
                // Check if device is an appliance              
                if (Appliance.containsTypeEnum(devicePointer.getType()))
                {
                    Appliance appliance = (Appliance) devicePointer;             
                    
                    // If device is a robot
                    if (appliance.getType().equals("robot"))
                    {
                        // Add robot key to array
                        robotKeys.add(deviceEntry.getKey());                       
                    }
                }
            }            
            
            // If store has a robot, tell it to clean up the broke glass
            if (robotKeys.size() > 0)
            {
                Appliance appliance = (Appliance) store.getDevices().get(robotKeys.get(0));                
                                                                   
                System.out.println(appliance.getName() + ": Cleaning broken glass in " + store.getAisles().get(aisleNumber).getName() + " aisle");
                appliance.getRobot(new AuthTokenTuple(myAuthToken)).brokenGlass(aisleNumber);                
            }
            
            // Else if store doesn't have a robot; cancel actions
            else
            {
                try
                {
                    throw new ControllerException("Broken Glass event", "no robot to clean up broken glass");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
        }        
    }
    
    /* *
     * Class that represents the device commands and actions to be taken in response to a Missing Person event
     */
    public class MissingPerson extends Command
    {
        /* Variables */
        
        private String customerId;

        /* Constructor */
        
        /* *
         * @param customerId The id of the person to be sought
         */
        public MissingPerson(Sensor sourceDevice, String customerId)
        {
            super(sourceDevice);
            
            this.customerId = customerId;
        }

        /* *
         * Locates the sought after person in the store and commands a speaker to tell customer the location
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);
            
            // Initialize string for getting store's speaker that's nearest customer
            String speakerKey = null;           
            
            // Iterate through devices to find a speaker close to microphone/customer that triggered event
            Sensor devicePointer;
            for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
            {
                devicePointer = deviceEntry.getValue();
                
                // If device is a speaker and in same aisle as microphone, get its key and break out of loop      
                if (devicePointer.getType().equals("speaker") && devicePointer.getLocation().split(":")[1].equals(sourceDevice.getLocation().split(":")[1]))
                {
                    speakerKey = deviceEntry.getKey();
                    break;
                }
            }            
            
            // If store speaker was found near microphone that triggered event, command it to announce message
            if (speakerKey != null)
            {
                Appliance appliance = (Appliance) store.getDevices().get(speakerKey);               
                
                String announcement = "Customer " + customerId + " is in " + store.getCustomers().get(customerId).getLocation().split(":")[1] + " aisle";
                                   
                System.out.println(appliance.getName() + ": \"" + announcement + "\"");
                appliance.getSpeaker(new AuthTokenTuple(myAuthToken)).announce(announcement);                              
            }
            
            // Else if store didn't have a speaker
            else
            {
                try
                {
                    throw new ControllerException("Missing Person event", "no available speakers near customer");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }                
        }        
    }
    
    /* *
     * Class that represents the actions to be take in response to a Customer Seen event 
     */
    public class CustomerSeen extends Command
    {
        /* Variables */
        
        private String customerId;
        private String aisleId;
        private String dateTime;
        
        /* Constructor */        
       
        public CustomerSeen(Sensor sourceDevice, String customerId, String aisleId, String dateTime)
        {
            super(sourceDevice);
            
            this.customerId = customerId;
            this.aisleId = aisleId;
            this.dateTime = dateTime;
        }

        /* *
         * Updates the customer's location
         */
        @Override
        public void execute()
        {          
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);            
            
            // Update customer's location
            System.out.println("Controller Service: Updating customer " + customerId + "'s location");
            modeler.updateCustomer(customerId, store.getId() + ":" + aisleId, dateTime, new AuthTokenTuple(myAuthToken));            
        }        
    }
    
    /* *
     * Class that represents the device commands and actions to be taken in response to a Fetch Product event 
     */
    public class FetchProduct extends Command
    {
        /* Variables */
        
        private String productId;
        private Integer number;
        private String aisleShelfLoc;
        private String customerId;
        private String voiceprintId;
        
        /* Constructor */
        
        /* *
         * @param number The amount of the product to be fetched
         */
        public FetchProduct(Sensor sourceDevice, String productId, Integer number, String aisleShelfLoc, String customerId, String voiceprintId)
        {
            super(sourceDevice);
            
            this.productId = productId;
            this.number = number;
            this.aisleShelfLoc = aisleShelfLoc;
            this.customerId = customerId;
            this.voiceprintId = voiceprintId;
        }

        /* *
         * Commands a robot to fetch a product for the customer given
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);                
            
            // Initialize array for getting store's robot appliance map keys
            ArrayList<String> robotKeys = new ArrayList<String>();           
            
            // Iterate through devices to find robots
            Sensor devicePointer;
            for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
            {
                devicePointer = deviceEntry.getValue();
                
                // Check if device is an appliance              
                if (Appliance.containsTypeEnum(devicePointer.getType()))
                {
                    Appliance appliance = (Appliance) devicePointer;             
                    
                    // If device is a robot
                    if (appliance.getType().equals("robot"))
                    {
                        // Add robot key to array
                        robotKeys.add(deviceEntry.getKey());                       
                    }
                }
            }            
            
            // If store has a robot, tell it to fetch the product
            if (robotKeys.size() > 0)
            {
                Appliance appliance = (Appliance) store.getDevices().get(robotKeys.get(0));                              
                
                // Get customer's location
                String customerAisleLoc = modeler.getCustomers(new AuthTokenTuple(myAuthToken)).get(customerId).getLocation().split(":")[1];       
                
                // Have robot fetch product using customer's AuthToken
                System.out.println(appliance.getName() + ": Fetching " + number + " of product " + productId + " from " + aisleShelfLoc
                        + " for customer " + customerId + " in " + customerAisleLoc + " aisle");
                
                // Get customer's AuthToken
                AuthToken customerAuthToken = authenticator.login(customerId + "-vp", voiceprintId);
                
                // If AuthToken not cleared
                if (customerAuthToken == null)                
                    return; 
                
                // If user has permission; include source store in AuthTokenTuple passed in
                if (appliance.getRobot(new AuthTokenTuple(customerAuthToken, store.getId())) != null)
                    appliance.getRobot(new AuthTokenTuple(customerAuthToken, store.getId())).fetchProduct(productId, number, aisleShelfLoc, customerId, customerAisleLoc);
                
                // If permission not found
                else
                    return;
           
                // Get inventory using aisleShelfLoc and productId (to update inventory)
                LinkedHashMap<String, Inventory> inventories = store.getInventories();
                Inventory inventory = null;
                
                for (Entry<String, Inventory> inventoryEntry : inventories.entrySet())
                {
                    if (inventoryEntry.getValue().getLocation().equals(store.getId() + ":" + aisleShelfLoc)
                            && inventoryEntry.getValue().getProductId().equals(productId))
                    {
                        inventory = inventoryEntry.getValue();
                        break;
                    }
                }
                
                if (!inventory.equals(null))
                {
                    // Update inventory by decrementing product count on the shelf
                    System.out.println("Controller Service: Updating inventory " + inventory.getInventoryId() + "'s count by " + (number * (-1)));
                    modeler.updateInventory(inventory.getInventoryId(), (number * (-1)), new AuthTokenTuple(myAuthToken));                                                
                }
                
                // Else inventory wasn't found; can't update it
                else
                {
                    try
                    {
                        throw new ControllerException("Fetch Product event", "no inventory found for product; can't update inventory");
                    }
                    
                    catch (ControllerException exception)
                    {
                        System.out.println();
                        System.out.print(exception.getMessage());      
                        return;
                    }
                }              
                
                // Get customer's virtual basket (to update their basket items)
                modeler.getCustomerBasket(customerId, new AuthTokenTuple(myAuthToken));
                
                // Update customer's virtual basket items by adding item(s) to it
                System.out.println("Controller Service: Adding " + number + " of " + productId + " to customer "
                        + customerId + "'s virtual basket");
                modeler.addBasketItem(customerId, productId, number, new AuthTokenTuple(myAuthToken));        
            }
            
            // Else if store doesn't have a robot; cancel actions
            else
            {
                try
                {
                    throw new ControllerException("Fetch Product event", "no robots available to fetch product");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
        }        
    }
    
    /* *
     * Class that represents the device commands and actions to be taken in response to an Account Balance event
     */
    public class AccountBalance extends Command
    {
        /* Variables */
        
        private String customerId;

        /* Constructor */
      
        public AccountBalance(Sensor sourceDevice, String customerId)
        {
            super(sourceDevice);

            this.customerId = customerId;
        }

        /* *
         * Computes the total value of the items in customer's basket and retrieves the customer's Blockchain account balance.
         * Then, commands a speaker to tell the customer this information 
         */
        @Override
        public void execute()
        {            
            System.out.println("Controller Service: Computing the value of items in customer " + customerId + "'s basket");
            
            // Calculate the total value of the customer's basket items
            Basket basket = modeler.getCustomerBasket(customerId, new AuthTokenTuple(myAuthToken));
            LinkedHashMap<String, Integer> basketItems = basket.getBasketItems();
            Integer itemsTotValue = 0;
            for (Entry<String, Integer> integerEntry : basketItems.entrySet())
            {
                itemsTotValue += (integerEntry.getValue() * (modeler.getProducts(new AuthTokenTuple(myAuthToken)).get(integerEntry.getKey()).getUnitPrice()));
            }            
            
            System.out.println("Controller Service: Checking customer " + customerId + "'s account balance");           
            
            // Get the customer's account balance            
            String customerBalance = ledgerCp.getAccountBalance(modeler.getCustomers(new AuthTokenTuple(myAuthToken)).get(customerId).getAccount());
            
            // Output aesthetics
            if (customerBalance == null)
                System.out.println();
            
            // Assemble expression string for a speaker near the customer to recite to customer            
            String moreLessOrEqual = null;
            
            if (customerBalance != null)
            {
                if (Integer.parseInt(customerBalance) > itemsTotValue)
                    moreLessOrEqual = "less than";
                else if (Integer.parseInt(customerBalance) < itemsTotValue)
                    moreLessOrEqual = "more than";
                else
                    moreLessOrEqual = "equal to";
            }
            
            String announcement = null;
            
            if (customerBalance == null)
            {
                announcement = "Total value of basket items is " + itemsTotValue + " Units. Your account balance is unavailable at the moment";
            }
            
            else
            {
                announcement = "Total value of basket items is " + itemsTotValue + " Units which is " + moreLessOrEqual + " your account balance "
                        + "of " + customerBalance;
            }
            
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);
            
            // Initialize string for getting store's speaker that's nearest customer
            String speakerKey = null;           
            
            // Iterate through devices to find a speaker close to microphone/customer that triggered event
            Sensor devicePointer;
            for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
            {
                devicePointer = deviceEntry.getValue();
                
                // If device is a speaker and in same aisle as microphone, get its key and break out of loop      
                if (devicePointer.getType().equals("speaker") && devicePointer.getLocation().split(":")[1].equals(sourceDevice.getLocation().split(":")[1]))
                {
                    speakerKey = deviceEntry.getKey();
                    break;
                }
            }         
            
            // If store speaker was found near microphone that triggered event, command it to announce message
            if (speakerKey != null)
            {
                Appliance appliance = (Appliance) store.getDevices().get(speakerKey);       
                                          
                System.out.println(appliance.getName() + ": \"" + announcement + "\"");
                appliance.getSpeaker(new AuthTokenTuple(myAuthToken)).announce(announcement);                               
            }
            
            // Else if store didn't have a speaker
            else
            {
                try
                {
                    throw new ControllerException("Account Balance event", "no speaker available near customer");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }              
        }        
    }
    
    /* *
     * Class that represents the device commands and actions to be taken in response to a Car Assist event 
     */
    public class CarAssist extends Command
    {
        /* Variables */
        
        private String customerId;
        
        /* Constructor */        
       
        public CarAssist(Sensor sourceDevice, String customerId)
        {
            super(sourceDevice);
            
            this.customerId = customerId;
        }

        /* *
         * Commands a robot to assist a customer to their car if the customer's items weigh more than 10 lbs
         */
        @Override
        public void execute()
        {
            // Get customer's basket
            Basket basket = modeler.getCustomerBasket(customerId, new AuthTokenTuple(myAuthToken));
            
            // If basket returned null, return (Modeler's getCustomerBasket will thrown an exception)
            if (basket == null)            
                return;         
            
            // Confirm that basket items' weight is actually exceeding 10 lbs.
            LinkedHashMap<String, Integer> basketItems = basket.getBasketItems();
            Integer itemsTotWeight = 0;
            for (Entry<String, Integer> integerEntry : basketItems.entrySet())
            {
                itemsTotWeight += (integerEntry.getValue() * Integer.parseInt(modeler.getProducts(new AuthTokenTuple(myAuthToken)).get(integerEntry.getKey()).getSize().split(" ")[0]));
            }
            
            // If weight of basket does not exceed 10 lbs
            if (itemsTotWeight <= 10)
            {          
                try
                {
                    throw new ControllerException("Car Assist event", "total weight of basket does not exceed 10 lbs; car assist not needed");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());      
                    return;
                }
            }
            
            else
            {
             // Get the source store
                LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
                Store store = stores.get(sourceDevice.getLocation().split(":")[0]);                
                
                // Initialize array for getting store's robot appliance map keys
                ArrayList<String> robotKeys = new ArrayList<String>();           
                
                // Iterate through devices to find robots
                Sensor devicePointer;
                for (Entry<String, Sensor> deviceEntry : store.getDevices().entrySet())
                {
                    devicePointer = deviceEntry.getValue();
                    
                    // Check if device is an appliance              
                    if (Appliance.containsTypeEnum(devicePointer.getType()))
                    {
                        Appliance appliance = (Appliance) devicePointer;             
                        
                        // If device is a robot
                        if (appliance.getType().equals("robot"))
                        {
                            // Add robot key to array
                            robotKeys.add(deviceEntry.getKey());                       
                        }
                    }
                }
                
                // If store has a robot, tell it to assist a customer
                if (robotKeys.size() > 0)
                {
                    Appliance appliance = (Appliance) store.getDevices().get(robotKeys.get(0));
                    
                    // Get customer's location
                    String customerLocation = store.getDevices().get(sourceDevice.getId()).getName();            
                                            
                    System.out.println(appliance.getName() + ": Assisting customer " + customerId + " at " + customerLocation + " to car");
                    appliance.getRobot(new AuthTokenTuple(myAuthToken)).carAssist(customerId, sourceDevice.getLocation());                    
                }
                
                // Else if store doesn't have a robot; cancel actions
                else
                {
                    try
                    {
                        throw new ControllerException("Car Assist event", "store does not have robots to help customers");
                    }
                    
                    catch (ControllerException exception)
                    {
                        System.out.println();
                        System.out.print(exception.getMessage());      
                        return;
                    }
                }
            }
        }       
    }
    
    /* *
     * Class that represents the device commands and actions to be take in response to a Checkout event
     */
    public class Checkout extends Command
    {
        /* Variables */
        
        private String customerId;
        
        /* Constructor */
     
        public Checkout(Sensor sourceDevice, String customerId)
        {
            super(sourceDevice);
            
            this.customerId = customerId;
        }

        /* *
         * Identifies a customer, computes their basket items value, processes a transaction for the items, opens
         * turnstile to let customer pass and has turnstile say "goodbye" to customer 
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);
                       
            // Identify customer and have turnstile speaker acknowledge them
            System.out.println("Controller Service: Identifying customer " + customerId);
            Customer customer = store.getCustomers().get(customerId);            
            String expression = null;
            if (customer != null)
                expression = "Hello, customer " + customer.getFirstName();
            else
                expression = "Hello";
            Appliance appliance = (Appliance) sourceDevice;
            System.out.println(appliance.getName() + ": \"" + expression + "\"");
            appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);                           
            
            // Initialize variables for customer's basket items (for registered customers)
            Integer itemsTotValue = 0;
            Basket basket = null;
            LinkedHashMap<String, Integer> basketItems = null; 
            
            // If customer is registered
            if ((customer != null) && customer.getType().toString().equals("registered"))
            {
                // Get the customer's basket
                basket = modeler.getCustomerBasket(customerId, new AuthTokenTuple(myAuthToken));
                basketItems = basket.getBasketItems();
                
                // If basket is not empty
                if ((basket != null) && !basketItems.isEmpty())
                {
                    // Get customer's AuthToken to check that customer has permission at the source store to checkout
                    AuthToken customerAuthToken = authenticator.login(customerId + "-fp", "--face:" + customerId + "--");
                    
                    // Create AuthTokenTuple with customer's AuthToken, source store id as a resource            
                    AuthTokenTuple customerAuthTokenTuple = new AuthTokenTuple(customerAuthToken, store.getId());
                    
                    // Check if customer has the "checkout" permission for the source store; return if permission doesn't checkout
                    GetPermissionsVisitor getPermissionsVisitor = authenticator.getUserPermissions(customerAuthTokenTuple.getAuthToken());
                    if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(customerAuthTokenTuple.getPermissionTuple().setPermissionId("checkout")))
                    {
                        // Have turnstile tell customer they can't enter (if customer is registered or a guest)
                        if (customer != null)
                        {
                            expression = "Customer " + customer.getFirstName() + ", you do not have permission to checkout at " + store.getName()
                                    + ". Please return items to store. Thank you!";
                            System.out.println();
                            System.out.println(appliance.getName() + ": \"" + expression + "\"");
                            appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                        }
                        
                        return;
                    }
                    
                    // Calculate the cost of their basket items
                    System.out.println("Controller Service: Computing the value of items in customer " + customerId + "'s basket");              
                                   
                    for (Entry<String, Integer> integerEntry : basketItems.entrySet())
                    {
                        itemsTotValue += (integerEntry.getValue() * (modeler.getProducts(new AuthTokenTuple(myAuthToken)).get(integerEntry.getKey()).getUnitPrice()));
                    }
                }
            }          
            
            // If customer has a non-empty basket
            if ((basket != null) && !basket.getBasketItems().isEmpty())
            {         
                // Start Transaction
                System.out.println("Controller Service: Starting transaction for " + customerId + "'s checkout");
                String txnId = ledgerCp.processTransaction("0", itemsTotValue, 10, "checkout for " + customerId, customer.getAccount(), store.getId());
                System.out.println("Controller Service: Transaction submitted and processed for " + customerId + "'s checkout");
                
                // If transaction came back null, have turnstile speaker talk to customer
                if (txnId == null)
                {
                    try
                    {
                        throw new ControllerException("Checkout; process transaction", "transaction returned null");
                    }
                    
                    catch (ControllerException exception)
                    {
                        System.out.println();
                        System.out.print(exception.getMessage()); 
                        System.out.println();
                        expression = "Transaction failed, " + store.getCustomers().get(customerId).getFirstName() + ". Please return items to store. Thank you!";                
                        System.out.println(appliance.getName() + ": \"" + expression + "\"");
                        appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                        return;
                    }          
                }
                
                // If payer does not have enough funds, have turnstile speaker talk to customer
                if (txnId != null && txnId.equals("payer has insufficient funds"))
                {
                    try
                    {
                        throw new ControllerException("Checkout; process transaction", "payer has insufficient funds");
                    }
                    
                    catch (ControllerException exception)
                    {
                        System.out.println();
                        System.out.print(exception.getMessage());
                        System.out.println();
                        expression = "You do not have enough funds in your account for checkout, " + store.getCustomers().get(customerId).getFirstName()
                                + ". Please return some or all items to store before checking out again. Thank you!";
                        
                        System.out.println(appliance.getName() + ": \"" + expression + "\"");
                        appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                        return;
                    }            
                }
                    
                // If payer account was not found, have turnstile speaker talk to customer
                if (txnId != null && txnId.equals("payer account not found"))
                {
                    try
                    {
                        throw new ControllerException("Checkout; process transaction", "payer account not found");
                    }
                    
                    catch (ControllerException exception)
                    {
                        System.out.println();
                        System.out.print(exception.getMessage());
                        System.out.println();
                        expression = "Your Blockchain account was not found, " + store.getCustomers().get(customerId).getFirstName()
                                + ". Please return items to store.";
                        
                        System.out.println(appliance.getName() + ": \"" + expression + "\"");
                        appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                        return;
                    }                   
                }           
                     
                // If txn processed successfully
                if (txnId != null)
                {               
                    // Get weight of basket items                     
                    Integer itemsTotWeight = 0;
                    for (Entry<String, Integer> integerEntry : basketItems.entrySet())
                    {
                        itemsTotWeight += (integerEntry.getValue() * Integer.parseInt(modeler.getProducts(new AuthTokenTuple(myAuthToken)).get(integerEntry.getKey()).getSize().split(" ")[0]));
                    }
                    
                    // If weight of items is more than 10 lbs, command a robot to assist customer to car
                    if (itemsTotWeight > 10)
                    {
                        expression = "Your basket items weigh more than 10 pounds. Please wait for a robot to assist you to your car";
                        System.out.println(appliance.getName() + ": \"" + expression + "\"");
                        appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);                            
                        
                        System.out.println(appliance.getName() + ": Calling for a robot to assist customer " + customerId + " to their car");                    
                        
                        Command carAssist = new CarAssist(sourceDevice, customerId);
                        System.out.println(" - Break -");
                        System.out.println("ASSIST CUSTOMER TO CAR EVENT received. CarAssist Command created and executing...");
                        carAssist.execute();
                        System.out.print(" - End Break -");
                        System.out.println();
                    }
                }
            }
                
            // Tell customer they can pass
            if (customer == null)
                expression = "You may pass through turnstile";                
            else    
                expression = "Customer " + store.getCustomers().get(customerId).getFirstName() + ", you may pass through turnstile";
            System.out.println(appliance.getName() + ": \"" + expression + "\"");
            appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);                          
            
            // Open turnstile
            if (customer == null)
                expression = ": Person passed through turnstile";
            else
                expression = ": Customer " + customerId + " passed through turnstile";
            System.out.println(appliance.getName() + expression);
            appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).letPersonPass();                
            
            // Tell customer goodbye
            if (customer == null)
                expression = "Goodbye!";
            else
                expression = "Goodbye, customer " + store.getCustomers().get(customerId).getFirstName() + ". Thanks for shopping at "
                        + store.getName() + "!";
            System.out.println(appliance.getName() + ": \"" + expression + "\"");
            appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);                
            
            // If customer was a registered customer, clear their basket
            if ((customer != null) && customer.getType().toString().equals("registered"))
            {
                System.out.println("Controller Service: Clearing customer " + customerId + "'s basket");
                modeler.clearBasket(customerId, new AuthTokenTuple(myAuthToken));
            }
            
            // Update customer's location
            if (customer != null)
            {
                System.out.println("Controller Service: Updating customer " + customerId + "'s location");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");
                LocalDateTime currentDateTime = LocalDateTime.now();
                modeler.updateCustomer(customerId, "null", dtf.format(currentDateTime), new AuthTokenTuple(myAuthToken));
            }           
        }        
    }
    
    /* *
     * Class that represents the device commands and actions to be taken in response to an Enter Store event 
     */
    public class EnterStore extends Command
    {
        /* Variables */
        
        private String customerId;
        
        /* Constructor */
       
        public EnterStore(Sensor sourceDevice, String customerId)
        {
            super(sourceDevice);
             
            this.customerId = customerId;
        }

        /* *
         * Looks up customer, checks that customer has a positive account balance, assigns a customer a virtual basket,
         * opens turnstile, and has turnstile speaker greet the customer
         */
        @Override
        public void execute()
        {
            // Get the source store
            LinkedHashMap<String, Store> stores = modeler.getStores(new AuthTokenTuple(myAuthToken));
            Store store = stores.get(sourceDevice.getLocation().split(":")[0]);
            
            // Access source turnstile
            Appliance appliance = (Appliance) sourceDevice;
            
            // Initialize string for turnstile speaker
            String expression;            
            
            // Identify customer
            System.out.println("Controller Service: Identifying customer " + customerId);
            Customer customer = modeler.getCustomers(new AuthTokenTuple(myAuthToken)).get(customerId);
            
            // Get customer's AuthToken
            AuthToken customerAuthToken = authenticator.login(customerId + "-fp", "--face:" + customerId + "--");
            
            if (customerAuthToken == null)
            {
                expression = "Hello. You do not have permission to enter " + store.getName() + ". Please register an account first";
                System.out.println();
                System.out.println(appliance.getName() + ": \"" + expression + "\"");
                appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                return;
            }
            
            // Create AuthTokenTuple with customer's AuthToken, source store id as a resource            
            AuthTokenTuple customerAuthTokenTuple = new AuthTokenTuple(customerAuthToken, store.getId());
            
            // Check if customer has the "enter store" permission for the source store; return if permission doesn't checkout
            GetPermissionsVisitor getPermissionsVisitor = authenticator.getUserPermissions(customerAuthTokenTuple.getAuthToken());
            if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(customerAuthTokenTuple.getPermissionTuple().setPermissionId("enter store")))
            {
                // Have turnstile tell customer they can't enter (if customer is registered or a guest)
                if (customer != null)
                {
                    expression = "Hello, customer " + customer.getFirstName() + ", you do not have permission to enter " + store.getName();
                    System.out.println();
                    System.out.println(appliance.getName() + ": \"" + expression + "\"");
                    appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                }
                
                return;
            }
            
            // If customer is not registered or not a guest
            if (customer == null)
            {
                try
                {
                    throw new ControllerException("Enter Store event", "person is not registered");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());
                    System.out.println();
                    expression = "You do not have a registered account with " + store.getName() + ". Please register one and come back later";
                    System.out.println(appliance.getName() + ": \"" + expression + "\"");
                    appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                    return;
                }       
            }
            
            // Initialize customer balance string
            String customerBalance = null;
            
            // If customer is registered, get their account balance
            if (customer.getType().toString().equals("registered"))
            {
                System.out.println("Controller Service: Checking customer " + customerId + "'s account for positive balance");                 
                customerBalance = ledgerCp.getAccountBalance(customer.getAccount());                  
                
                // If account balance returned null
                if (customerBalance == null)
                {
                    try
                    {
                        throw new ControllerException("Enter Store event", "customer's account information is unavailable");
                    }
                    
                    catch (ControllerException exception)
                    {
                        System.out.println();
                        System.out.print(exception.getMessage());
                        System.out.println();
                        expression = "Your account information is unavailable. Please come back at another time";
                        System.out.println(appliance.getName() + ": \"" + expression + "\"");
                        appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                        return;
                    }                    
                }
            }           
            
            // If customer has no funds in their account
            if ((customerBalance != null) && (Integer.parseInt(customerBalance) <= 0))
            {
                try
                {
                    throw new ControllerException("Enter Store event", "customer has no funds in their account");
                }
                
                catch (ControllerException exception)
                {
                    System.out.println();
                    System.out.print(exception.getMessage());
                    System.out.println();
                    expression = "You have no funds in your account. Please come back when you do";
                    System.out.println(appliance.getName() + ": \"" + expression + "\"");
                    appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                    return;
                }                
            }          
            
            // Tell customer they can pass through turnstile and greet them
            expression = "Hello, customer " + modeler.getCustomers(new AuthTokenTuple(myAuthToken)).get(customerId).getFirstName() + ", you may pass through the turnstile. "
                    + "Welcome to " + store.getName() + "!";
            System.out.println(appliance.getName() + ": \"" + expression + "\"");
            appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);                          
            
            // Open turnstile
            System.out.println(appliance.getName() + ": Customer " + customerId + " passed through turnstile");
            appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).letPersonPass();                
            
            // Update customer's location
            System.out.println("Controller Service: Updating customer " + customerId + "'s location");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");
            LocalDateTime currentDateTime = LocalDateTime.now();
            modeler.updateCustomer(customerId, store.getId() + ":null", dtf.format(currentDateTime), new AuthTokenTuple(myAuthToken));
            
            // If customer is registered, assign them a virtual basket
            if ((customerBalance != null) && (Integer.parseInt(customerBalance) > 0))
            {             
                // If customer does not have the "checkout" permission for the source store, don't assign them a basket
                if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(customerAuthTokenTuple.getPermissionTuple().setPermissionId("checkout")))
                {
                    expression = "Customer " + customer.getFirstName() + ", you do not have permission to checkout at " + store.getName()
                            + ". You may not be assigned a basket";
                    System.out.println();
                    System.out.println(appliance.getName() + ": \"" + expression + "\"");
                    appliance.getTurnstile(new AuthTokenTuple(myAuthToken)).speak(expression);
                }
                
                else
                {
                    System.out.println("Controller Service: Getting customer " + customerId + "'s virtual basket");
                    modeler.getCustomerBasket(customerId, new AuthTokenTuple(myAuthToken));
                }
            }
        }        
    }
    
    /* Utility Methods */
    
    public void loginToAuthenticator()
    {
        // Login Controller
        myAuthToken = authenticator.login("controller-pwd", "password");
    }
}
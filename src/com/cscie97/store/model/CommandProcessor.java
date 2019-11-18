/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

import java.util.ArrayList;

import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.Authenticator;
import com.cscie97.store.authentication.StoreAuthenticationService;
import com.cscie97.store.controller.Controller;

/* *
 * Exercises the Modeler, utilizes Ledger Service's CommandProcessor, and creates and depends on Controller
 */
public class CommandProcessor
{
    /* Variables */

    private com.cscie97.store.authentication.CommandProcessor authenticatorCp;
    private com.cscie97.ledger.CommandProcessor ledgerCp;
    private StoreModelService modeler;
    private Controller controller;
    private AuthToken hardcodedUserAuthToken;    
    
    /* Constructor */
    
    public CommandProcessor(StoreAuthenticationService authenticator, com.cscie97.store.authentication.CommandProcessor authenticatorCp)
    { 
        // Create Modeler 
        modeler = new Modeler(authenticator);       
        
        // Create ledger 
        ledgerCp = new com.cscie97.ledger.CommandProcessor(authenticatorCp);
        
        // Create Controller 
        controller = new Controller((Subject) modeler, ledgerCp, authenticator);
        
        this.authenticatorCp = authenticatorCp;
        
        // Login CommandProcessor with hardcoded User credentials so can operate Modeler methods
        hardcodedUserAuthToken = authenticator.obtainAuthToken(Authenticator.getHardcodedUserUsername(), Authenticator.getHardcodedUserPassword());
    }
    
    /* API Methods */

    /* *
     * Parses a string for valid CLI/DSL command syntax and calls corresponding Modeler method
     * @command The input string to be parsed
     */
    public void processCommand(String command)
    {             	
        parseAndProcess(command);
    }
    
    
    /* *
     * Parses a file of strings for valid CLI/DSL command syntax and calls corresponding Modeler methods
     * for each string found    
     * @param commandFile the name of the file to be pased
     * Referenced https://www.journaldev.com/709/java-read-file-line-by-line
     */
    public void processCommandFile(String commandFile)
    {    	
        // Not needed
    }       
    
    /* Utility Methods */

    /* *
     * Utility method that parses a string for valid DSL/CLI command syntax and calls Modeler methods
     * based on parsing result
     * @param input The line of string to be parsed
     */
    public void parseAndProcess(String input)
    {
        // Trim leading and trailing whitespace
        String trimmedInput = input.trim();

        // Check if input is a comment
        if (trimmedInput.charAt(0) == '#')
        {                         
            System.out.println(trimmedInput + " [line " + authenticatorCp.getLineNum() + " in file]");          
            return;
        }

        // Delimit input string on whitespace and add each value to array
        String[] splitInputArr = trimmedInput.split("\\s+");
        
        /* If input contained quotes, then validate their correct usage and fix array - code block BEGINNING */
        
        boolean openQuote = false;
        ArrayList<Integer> indicesOfOpeningQuotes = new ArrayList<Integer>();
        ArrayList<Integer> indicesOfClosingQuotes = new ArrayList<Integer>();
        
        for (int i = 0; i < splitInputArr.length; i++)
        {
            // If a stand-alone quote, must check if it's an opening or closing quote
            if ((splitInputArr[i].length() == 1) && (splitInputArr[i].charAt(0) == '"'))
            {
                if (openQuote == false)
                {
                    indicesOfOpeningQuotes.add(i);
                    openQuote = true;
                }

                else
                {
                    indicesOfClosingQuotes.add(i);
                    openQuote = false;
                }
            }    
	        
            // If not a stand-alone quote
	    else
	    {
	       	// Checks string if has a quote as first character
	        if (splitInputArr[i].charAt(0) == '"')
	        {
	            if (openQuote == true)
	            {
	               	try
	                {
	                    if (authenticatorCp.getLineNum() == 0)
	                        throw new CommandProcessorException("in processCommand method", "missing closing quote in user input");
	
                            else
	                        throw new CommandProcessorException("in processCommandFile method", "missing closing quote in user input", authenticatorCp.getLineNum());            
	                }
	                    
	                catch (CommandProcessorException exception)
	                {
	                    if (authenticatorCp.getLineNum() == 0)
	                    {
	                        System.out.println("-: " + trimmedInput);
	                        System.out.println();
	                        System.out.println(exception.getMessage());	                            
	                        return;
	                    }
	            
	                    else
	                    {
	                        System.out.println("-: " + trimmedInput);
	                        System.out.println();
	                        System.out.println(exception.getMessageLine());	                           
	                        return;
	                    }
	                }
	            }
	              
	            else
	            {
	             	indicesOfOpeningQuotes.add(i);
	              	openQuote = true;
	            }
	        }
	
	        // Checks string if has a quote as last character
	        if (splitInputArr[i].charAt(splitInputArr[i].length() - 1) == '"')
	        {
	            if (openQuote == true)
	            {
	              	indicesOfClosingQuotes.add(i);
	               	openQuote = false;
	            }
	              
	            else
	            {
	               	try
	                {
	                    if (authenticatorCp.getLineNum() == 0)
	                        throw new CommandProcessorException("in processCommand method", "missing opening quote in user input");
	
	                    else
	                        throw new CommandProcessorException("in processCommandFile method", "missing opening quote in user input", authenticatorCp.getLineNum());            
	                }
	                    
	                catch (CommandProcessorException exception)
	                {
	                    if (authenticatorCp.getLineNum() == 0)
	                    {
	                        System.out.println("-: " + trimmedInput);
	                        System.out.println();
	                        System.out.println(exception.getMessage());	                            
	                        return;
	                    }
	            
	                    else
	                    {
	                        System.out.println("-: " + trimmedInput);
	                        System.out.println();
	                        System.out.println(exception.getMessageLine());	                            
	                        return;
	                    }
	                }
	            }
	        }
	    }
        }
        
        // If there is an ultimate open quote without a matching closing quote then throw exception 
        if (openQuote == true)
        {
            try
            {
                if (authenticatorCp.getLineNum() == 0)
                    throw new CommandProcessorException("in processCommand method", "missing closing quote in user input");

                else
                    throw new CommandProcessorException("in processCommandFile method", "missing closing quote in user input", authenticatorCp.getLineNum());            
            }
            
            catch (CommandProcessorException exception)
            {
                if (authenticatorCp.getLineNum() == 0)
                {
                    System.out.println("-: " + trimmedInput);
                    System.out.println();
                    System.out.println(exception.getMessage());                    
                    return;
                }
    
                else
                {
                    System.out.println("-: " + trimmedInput);
                    System.out.println();
                    System.out.println(exception.getMessageLine());                    
                    return;
                }
            }
        }
        
        // If input had quotes, string quoted input(s) back together
        if (indicesOfOpeningQuotes.size() > 0)
        {       	
            // Create a modified splitInputArr, named splitStringQuotesArr, with quoted input(s) back together
            ArrayList<String> splitStringQuotesArr = new ArrayList<String>();
            
            // Initialize index counter for opening and closing quotes arrays
            int index = 0;
        	
            // Initialize a quote string
            String quote = "";
        	
            // Loop through splitInputArr to create new splitStringQuotesArr string array
            for (int i = 0; i < splitInputArr.length; i++)
	    {
        	// If found all quotes then just transfer the element to new array
        	if (index >= indicesOfOpeningQuotes.size())        			
        	    splitStringQuotesArr.add(splitInputArr[i]);        		
        		
       		else
       		{
       		    if (openQuote == false)
       		    {
       		        if (i == indicesOfOpeningQuotes.get(index))       				
       		            openQuote = true;
	        			        			
	        	else	        			
	        	    splitStringQuotesArr.add(splitInputArr[i]);	        			
       		    }
	        			
       		    if (openQuote == true)
       		    {
       		        // If element contains the closing quote
       		        if (i == indicesOfClosingQuotes.get(index))
       		        {
       		            // Append element to quote string
       		            quote += splitInputArr[i];
    			        		
       		            // Remove quotes from quote and trim its whitespace
       		            StringBuffer sbf = new StringBuffer(quote);
       		            quote = sbf.deleteCharAt(0).toString();
       		            sbf = new StringBuffer(quote);
       		            quote = sbf.deleteCharAt(quote.length() - 1).toString();
       		            quote = quote.trim();
    			        		
       		            // Add quote to new array
       		            splitStringQuotesArr.add(quote);			        		
    			        		
       		            // Set openQuote to false, increment counter, and reset quote string since found closing quote
       		            openQuote = false;			        		
       		            index++;
       		            quote = "";
       		        }
    		        			        	
       		        else
       		        {
       		            // Append element to quote string with a space added
       		            quote += splitInputArr[i] + " ";
       		        }
       		    }
       		}
	    }    	
        	
            // Redefine splitInputArr with new array
            splitInputArr = new String[splitStringQuotesArr.size()]; 
            splitStringQuotesArr.toArray(splitInputArr);        	
        }
        
        /* code block END ("If input contained quotes, then validate their correct usage and fix array") */              
        
        if ((splitInputArr.length == 7) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("store")
                && splitInputArr[3].equalsIgnoreCase("name") && splitInputArr[5].equalsIgnoreCase("address"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.defineStore(splitInputArr[2], splitInputArr[4], splitInputArr[6], hardcodedUserAuthToken);
            System.out.println();
        }   	
        
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") && splitInputArr[1].equalsIgnoreCase("store"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showStore(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }
   
        else if ((splitInputArr.length == 9) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("aisle")
                && splitInputArr[3].equalsIgnoreCase("name") && splitInputArr[5].equalsIgnoreCase("description")
                && splitInputArr[7].equalsIgnoreCase("location") && (splitInputArr[2].split(":").length == 2))
        {
            System.out.println("-: " + trimmedInput);
            modeler.defineAisle(splitInputArr[2], splitInputArr[4], splitInputArr[6], splitInputArr[8], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") && splitInputArr[1].equalsIgnoreCase("aisle")
                && (splitInputArr[2].split(":").length == 2))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showAisle(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }
        
        // Shelf for when temperature is not given (default is ambient)        
        else if ((splitInputArr.length == 9) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("shelf")
                && splitInputArr[3].equalsIgnoreCase("name") && splitInputArr[5].equalsIgnoreCase("level")
                && splitInputArr[7].equalsIgnoreCase("description") && (splitInputArr[2].split(":").length == 3))
        {
            System.out.println("-: " + trimmedInput);
            modeler.defineShelf(splitInputArr[2], splitInputArr[4], splitInputArr[6], splitInputArr[8], hardcodedUserAuthToken);
            System.out.println();
        }    
    
        // Shelf for when temperature is given as input   
        else if ((splitInputArr.length == 11) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("shelf")
                && splitInputArr[3].equalsIgnoreCase("name") && splitInputArr[5].equalsIgnoreCase("level")
                && splitInputArr[7].equalsIgnoreCase("description") && splitInputArr[9].equalsIgnoreCase("temperature")
                && (splitInputArr[2].split(":").length == 3))
        {
            System.out.println("-: " + trimmedInput);
            modeler.defineShelf(splitInputArr[2], splitInputArr[4], splitInputArr[6], splitInputArr[8], splitInputArr[10], hardcodedUserAuthToken);
            System.out.println();
        }
    
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") && splitInputArr[1].equalsIgnoreCase("shelf")
                && (splitInputArr[2].split(":").length == 3))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showShelf(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }    
    
        else if ((splitInputArr.length == 11) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("inventory")
                && splitInputArr[3].equalsIgnoreCase("location") && splitInputArr[5].equalsIgnoreCase("capacity")
                && splitInputArr[7].equalsIgnoreCase("count") && splitInputArr[9].equalsIgnoreCase("product")
                && (splitInputArr[4].split(":").length == 3))
        {
            // Check if integer inputs are valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(splitInputArr[6]);
                Integer.parseInt(splitInputArr[8]);
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    if (authenticatorCp.getLineNum() == 0)
                        throw new CommandProcessorException("in processCommand method", "invalid integer input");

                    else
                        throw new CommandProcessorException("in processCommandFile method", "invalid integer input", authenticatorCp.getLineNum());            
                }
                
                catch (CommandProcessorException exception)
                {
                    if (authenticatorCp.getLineNum() == 0)
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessage());                    
                        return;
                    }
        
                    else
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessageLine());                    
                        return;
                    }
                }
            }
            
            // Call defineInventory(...)
            else
            {
                System.out.println("-: " + trimmedInput);
                modeler.defineInventory(splitInputArr[2], splitInputArr[4], Integer.parseInt(splitInputArr[6]),
                        Integer.parseInt(splitInputArr[8]), splitInputArr[10], hardcodedUserAuthToken);
                System.out.println();
            }
        }
   
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") && splitInputArr[1].equalsIgnoreCase("inventory"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showInventory(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 5) && splitInputArr[0].equalsIgnoreCase("update") && splitInputArr[1].equalsIgnoreCase("inventory")
                && splitInputArr[3].equalsIgnoreCase("update_count"))
        {           
            // Check if integer input is valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(splitInputArr[4]);                
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    if (authenticatorCp.getLineNum() == 0)
                        throw new CommandProcessorException("in processCommand method", "invalid integer input; inventory not updated");

                    else
                        throw new CommandProcessorException("in processCommandFile method", "invalid integer input; inventory not updated", authenticatorCp.getLineNum());            
                }
                
                catch (CommandProcessorException exception)
                {
                    if (authenticatorCp.getLineNum() == 0)
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessage());                    
                        return;
                    }
        
                    else
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessageLine());                    
                        return;
                    }
                }
            }
            
            System.out.println("-: " + trimmedInput);
            modeler.updateInventory(splitInputArr[2], Integer.parseInt(splitInputArr[4]), hardcodedUserAuthToken);
            System.out.println();            
        }       
        
        // defineProduct for when temperature input is given
        else if ((splitInputArr.length == 15) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("product")                
                && splitInputArr[3].equalsIgnoreCase("name") && splitInputArr[5].equalsIgnoreCase("description")
                && splitInputArr[7].equalsIgnoreCase("size") && splitInputArr[9].equalsIgnoreCase("category")
                && splitInputArr[11].equalsIgnoreCase("unit_price") && splitInputArr[13].equalsIgnoreCase("temperature"))
        {
            // Check if integer inputs are valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(splitInputArr[12]);                
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    if (authenticatorCp.getLineNum() == 0)
                        throw new CommandProcessorException("in processCommand method", "invalid integer input; product not created");

                    else
                        throw new CommandProcessorException("in processCommandFile method", "invalid integer input; product not created", authenticatorCp.getLineNum());            
                }
                
                catch (CommandProcessorException exception)
                {
                    if (authenticatorCp.getLineNum() == 0)
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessage());                    
                        return;
                    }
        
                    else
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessageLine());                    
                        return;
                    }
                }
            }           
                          
            System.out.println("-: " + trimmedInput);
            modeler.defineProduct(splitInputArr[2], splitInputArr[4], splitInputArr[6], splitInputArr[8], splitInputArr[10],
                    Integer.parseInt(splitInputArr[12]), splitInputArr[14], hardcodedUserAuthToken);
            System.out.println();            
        }
    
        // defineProduct for when temperature is not given (ambient is default)
        else if ((splitInputArr.length == 13) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("product")                
                && splitInputArr[3].equalsIgnoreCase("name") && splitInputArr[5].equalsIgnoreCase("description")
                && splitInputArr[7].equalsIgnoreCase("size") && splitInputArr[9].equalsIgnoreCase("category")
                && splitInputArr[11].equalsIgnoreCase("unit_price"))
        {
            // Check if integer inputs are valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(splitInputArr[12]);                
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    if (authenticatorCp.getLineNum() == 0)
                        throw new CommandProcessorException("in processCommand method", "invalid integer input; product not created");

                    else
                        throw new CommandProcessorException("in processCommandFile method", "invalid integer input; product not created", authenticatorCp.getLineNum());            
                }
                
                catch (CommandProcessorException exception)
                {
                    if (authenticatorCp.getLineNum() == 0)
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessage());                    
                        return;
                    }
        
                    else
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessageLine());                    
                        return;
                    }
                }
            }
            
            System.out.println("-: " + trimmedInput);
            modeler.defineProduct(splitInputArr[2], splitInputArr[4], splitInputArr[6], splitInputArr[8], splitInputArr[10],
                    Integer.parseInt(splitInputArr[12]), hardcodedUserAuthToken);
            System.out.println();
        }
    
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") && splitInputArr[1].equalsIgnoreCase("product"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showProduct(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }    
    
        else if ((splitInputArr.length == 15) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("customer")
                && splitInputArr[3].equalsIgnoreCase("first_name") && splitInputArr[5].equalsIgnoreCase("last_name")
                && splitInputArr[7].equalsIgnoreCase("age_group") && splitInputArr[9].equalsIgnoreCase("type")
                && splitInputArr[11].equalsIgnoreCase("email_address") && splitInputArr[13].equalsIgnoreCase("account"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.defineCustomer(splitInputArr[2], splitInputArr[4], splitInputArr[6], splitInputArr[8], splitInputArr[10], splitInputArr[12]
                    , splitInputArr[14], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 6) && splitInputArr[0].equalsIgnoreCase("update") && splitInputArr[1].equalsIgnoreCase("customer")
                && splitInputArr[3].equalsIgnoreCase("location") && ((splitInputArr[4].split(":").length == 2)
                || (splitInputArr[4].equals("null"))))
        {
            System.out.println("-: " + trimmedInput);
            modeler.updateCustomer(splitInputArr[2], splitInputArr[4], splitInputArr[5], hardcodedUserAuthToken);
            System.out.println();
        }   
        
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") && splitInputArr[1].equalsIgnoreCase("customer"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showCustomer(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }    
    
        else if ((splitInputArr.length == 2) && splitInputArr[0].equalsIgnoreCase("get_customer_basket"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.getCustomerBasket(splitInputArr[1], hardcodedUserAuthToken);
            System.out.println();
        }          
    
        else if ((splitInputArr.length == 6) && splitInputArr[0].equalsIgnoreCase("add_basket_item") && splitInputArr[2].equalsIgnoreCase("product")
                && splitInputArr[4].equalsIgnoreCase("item_count"))
        {
            // Check if integer input is valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(splitInputArr[5]);                
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    if (authenticatorCp.getLineNum() == 0)
                        throw new CommandProcessorException("in processCommand method", "invalid integer input; item not added");

                    else
                        throw new CommandProcessorException("in processCommandFile method", "invalid integer input; item not added", authenticatorCp.getLineNum());            
                }
                
                catch (CommandProcessorException exception)
                {
                    if (authenticatorCp.getLineNum() == 0)
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessage());                    
                        return;
                    }
        
                    else
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessageLine());                    
                        return;
                    }
                }
            }
            
            System.out.println("-: " + trimmedInput);
            modeler.addBasketItem(splitInputArr[1], splitInputArr[3], Integer.parseInt(splitInputArr[5]), hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 6) && splitInputArr[0].equalsIgnoreCase("remove_basket_item") && splitInputArr[2].equalsIgnoreCase("product")
                && splitInputArr[4].equalsIgnoreCase("item_count"))
        {
            // Check if integer input is valid
            Boolean validInts = true;
            try
            {
                Integer.parseInt(splitInputArr[5]);                
            }

            catch (NumberFormatException exception)
            {
                validInts = false;
            }
            
            if (validInts == false)
            {
                try
                {
                    if (authenticatorCp.getLineNum() == 0)
                        throw new CommandProcessorException("in processCommand method", "invalid integer input; item not removed");

                    else
                        throw new CommandProcessorException("in processCommandFile method", "invalid integer input; item not removed", authenticatorCp.getLineNum());            
                }
                
                catch (CommandProcessorException exception)
                {
                    if (authenticatorCp.getLineNum() == 0)
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessage());                    
                        return;
                    }
        
                    else
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessageLine());                    
                        return;
                    }
                }
            }
            
            System.out.println("-: " + trimmedInput);
            modeler.removeBasketItem(splitInputArr[1], splitInputArr[3], Integer.parseInt(splitInputArr[5]), hardcodedUserAuthToken);
            System.out.println();            
        }
        
        else if ((splitInputArr.length == 2) && splitInputArr[0].equalsIgnoreCase("clear_basket"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.clearBasket(splitInputArr[1], hardcodedUserAuthToken);
            System.out.println();
        }   
        
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") &&  splitInputArr[1].equalsIgnoreCase("basket_items"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showBasketItems(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }      
    
        else if ((splitInputArr.length == 9) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("device")
                && splitInputArr[3].equalsIgnoreCase("name") && splitInputArr[5].equalsIgnoreCase("type")
                && splitInputArr[7].equalsIgnoreCase("location") && (splitInputArr[8].split(":").length == 2))
        {
            System.out.println("-: " + trimmedInput);
            modeler.defineDevice(splitInputArr[2], splitInputArr[4], splitInputArr[6], splitInputArr[8], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 3) && splitInputArr[0].equalsIgnoreCase("show") && splitInputArr[1].equalsIgnoreCase("device"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.showDevice(splitInputArr[2], hardcodedUserAuthToken);
            System.out.println();
        }    
    
        else if ((splitInputArr.length == 5) && splitInputArr[0].equalsIgnoreCase("create") && splitInputArr[1].equalsIgnoreCase("event")
                && splitInputArr[3].equalsIgnoreCase("event"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.createEvent(splitInputArr[2], splitInputArr[4], hardcodedUserAuthToken);
            System.out.println();
        }    
    
        else if ((splitInputArr.length == 5) && splitInputArr[0].equalsIgnoreCase("create") && splitInputArr[1].equalsIgnoreCase("command")
                && splitInputArr[3].equalsIgnoreCase("message"))
        {
            System.out.println("-: " + trimmedInput);
            modeler.createCommand(splitInputArr[2], splitInputArr[4], hardcodedUserAuthToken);
            System.out.println();
        }        
    	
        else if ((splitInputArr.length == 4) && splitInputArr[0].equalsIgnoreCase("login") && splitInputArr[1].equalsIgnoreCase("to")
                && splitInputArr[2].equalsIgnoreCase("authenticator") && splitInputArr[3].equalsIgnoreCase("controller"))
        {
            System.out.println("-: " + trimmedInput);
            controller.loginToAuthenticator();
            System.out.println();
        }
        
    	// Else route the command to the Ledger Service's command processor
        else
        {            
            ledgerCp.processCommand(input);
        }                     
    }
}

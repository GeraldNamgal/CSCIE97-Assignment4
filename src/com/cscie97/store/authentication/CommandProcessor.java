package com.cscie97.store.authentication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CommandProcessor
{
    /* Variables */
    
    private StoreAuthenticationService authenticator;
    private com.cscie97.store.model.CommandProcessor modelerCp;
    private AuthToken hardcodedUserAuthToken;
    private int lineNum = 0;
    
    /* Constructor */
    
    public CommandProcessor()
    {
        // Create Authenticator      
        authenticator = new Authenticator();
        
        // Create Modeler 
        modelerCp = new com.cscie97.store.model.CommandProcessor(authenticator, this);
        
        // Login CommandProcessor with hardcoded User credentials so can operate Authenticator methods
        hardcodedUserAuthToken = authenticator.obtainAuthToken(Authenticator.getHardcodedUserUsername(), Authenticator.getHardcodedUserPassword());
    }
    
    /* API Methods */

    /* *
     * Parses a string for valid CLI/DSL command syntax and calls corresponding Authenticator method
     * @command The input string to be parsed
     */
    public void processCommand(String command)
    {
        parseAndProcess(command);
    }    
    
    /* *
     * Parses a file of strings for valid CLI/DSL command syntax and calls corresponding Authenticator methods
     * for each string found    
     * @param commandFile the name of the file to be pased
     * Referenced https://www.journaldev.com/709/java-read-file-line-by-line
     */
    public void processCommandFile(String commandFile)
    {
        // Check if the file is empty
        try
        {
            File newFile = new File(commandFile);
            if (newFile != null)
            {
                if (newFile.length() == 0)
                    throw new CommandProcessorException("in processCommandFile method", "file is empty");
            }
        }

        catch (CommandProcessorException exception)
        {
            System.out.println(exception.getMessage());
            return;
        }

        // Read file
        try
        {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(commandFile));
            String line = reader.readLine();

            while (line != null)
            {
                // Counter up lineNum
                lineNum++;

                // Call parseAndProcess method if line isn't empty
                if (line.length() > 0)
                    parseAndProcess(line);

                // Read next line
                line = reader.readLine();
            }

            reader.close();
        }

        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }       
    
    /* Utility Methods */

    /* *
     * Utility method that parses a string for valid DSL/CLI command syntax and calls Authenticator methods
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
            System.out.println(trimmedInput + " [line " + lineNum + " in file]");          
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
                            if (lineNum == 0)
                                throw new CommandProcessorException("in processCommand method", "missing closing quote in user input");
        
                            else
                                throw new CommandProcessorException("in processCommandFile method", "missing closing quote in user input", lineNum);            
                        }
                            
                        catch (CommandProcessorException exception)
                        {
                            if (lineNum == 0)
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
                            if (lineNum == 0)
                                throw new CommandProcessorException("in processCommand method", "missing opening quote in user input");
        
                            else
                                throw new CommandProcessorException("in processCommandFile method", "missing opening quote in user input", lineNum);            
                        }
                            
                        catch (CommandProcessorException exception)
                        {
                            if (lineNum == 0)
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
                if (lineNum == 0)
                    throw new CommandProcessorException("in processCommand method", "missing closing quote in user input");

                else
                    throw new CommandProcessorException("in processCommandFile method", "missing closing quote in user input", lineNum);            
            }
            
            catch (CommandProcessorException exception)
            {
                if (lineNum == 0)
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
        
        if ((splitInputArr.length == 5) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("permission"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.definePermission(splitInputArr[2], splitInputArr[3], splitInputArr[4], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 5) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("role"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.defineRole(splitInputArr[2], splitInputArr[3], splitInputArr[4], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 6) && splitInputArr[0].equalsIgnoreCase("add") && splitInputArr[1].equalsIgnoreCase("entitlement")
                && splitInputArr[2].equalsIgnoreCase("to") && splitInputArr[3].equalsIgnoreCase("role"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.addEntitlementToRole(splitInputArr[4], splitInputArr[5], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if((splitInputArr.length == 4) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("user"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.defineUser(splitInputArr[2], splitInputArr[3], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 6) && splitInputArr[0].equalsIgnoreCase("add") && splitInputArr[1].equalsIgnoreCase("user")
                && splitInputArr[2].equalsIgnoreCase("credential"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.addUserCredential(splitInputArr[3], splitInputArr[4], splitInputArr[5], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if((splitInputArr.length == 4) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("resource"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.defineResource(splitInputArr[2], splitInputArr[3], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 8) && splitInputArr[0].equalsIgnoreCase("define") && splitInputArr[1].equalsIgnoreCase("resource")
                && splitInputArr[2].equalsIgnoreCase("role"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.defineResourceRole(splitInputArr[3], splitInputArr[4], splitInputArr[5], splitInputArr[6], splitInputArr[7], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if ((splitInputArr.length == 6) && splitInputArr[0].equalsIgnoreCase("add") && splitInputArr[1].equalsIgnoreCase("entitlement")
                && splitInputArr[2].equalsIgnoreCase("to") && splitInputArr[3].equalsIgnoreCase("user"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.addEntitlementToUser(splitInputArr[4], splitInputArr[5], hardcodedUserAuthToken);
            System.out.println();
        }
        
        else if((splitInputArr.length == 2) && splitInputArr[0].equalsIgnoreCase("print") && splitInputArr[1].equalsIgnoreCase("inventory"))
        {
            System.out.println("-: " + trimmedInput);
            authenticator.printInventory();
            System.out.println();
        }
        
        // Else route the command to the Modeler Service's CommandProcessor
        else
        {            
            modelerCp.processCommand(input);
        }
        
        /*// TODO: Still need? -- Testing
        authenticator.definePermission("permission 1", "name", "description", hardcodedUserAuthToken);
        authenticator.definePermission("permission 2", "name", "description", hardcodedUserAuthToken);
        authenticator.definePermission("permission 3", "name", "description", hardcodedUserAuthToken);
        authenticator.definePermission("permission 4", "name", "description", hardcodedUserAuthToken);
        authenticator.definePermission("permission 5", "name", "description", hardcodedUserAuthToken);
        
        authenticator.defineRole("role 1", "name", "description", hardcodedUserAuthToken);
        authenticator.defineRole("role 2", "name", "description", hardcodedUserAuthToken);
        authenticator.defineRole("role 3", "name", "description", hardcodedUserAuthToken);
        authenticator.defineRole("role 4", "name", "description", hardcodedUserAuthToken);
        
        authenticator.defineResource("store 1", "description", hardcodedUserAuthToken);
        authenticator.defineResource("store 2", "description", hardcodedUserAuthToken);
        authenticator.defineResource("store 3", "description", hardcodedUserAuthToken);
        
        authenticator.defineResourceRole("resource role 1", "name", "description", "role 3", "store 1", hardcodedUserAuthToken);
        authenticator.defineResourceRole("resource role 2", "name", "description", "role 1", "store 1", hardcodedUserAuthToken);
        authenticator.defineResourceRole("resource role 3", "name", "description", "role 4", "store 2", hardcodedUserAuthToken);
        authenticator.defineResourceRole("resource role 4", "name", "description", "role 2", "store 2", hardcodedUserAuthToken);
        authenticator.defineResourceRole("resource role 5", "name", "description", "permission 2", "store 3", hardcodedUserAuthToken);
        
        authenticator.addEntitlementToRole("role 1", "permission 1", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("role 1", "permission 5", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("authenticatorAPIUserRole", "resource role 2", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("role 2", "permission 2", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("role 2", "permission 1", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("authenticatorAPIUserRole", "resource role 4", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("role 3", "permission 3", hardcodedUserAuthToken);
        authenticator.addEntitlementToUser("hardcodedUser", "resource role 1", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("role 4", "permission 4", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("resource role 1", "resource role 3", hardcodedUserAuthToken);
        authenticator.addEntitlementToUser("hardcodedUser", "resource role 5", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("resource role 1", "useAuthenticatorAPI", hardcodedUserAuthToken);
        authenticator.addEntitlementToRole("authenticatorAPIUserRole", "permission 3", hardcodedUserAuthToken);
        authenticator.addEntitlementToUser("hardcodedUser", "permission 5", hardcodedUserAuthToken);
        
        authenticator.printInventory();*/
    }
    
    /* Getters and Setters */
    
    public int getLineNum()
    {
        return lineNum;
    }
}

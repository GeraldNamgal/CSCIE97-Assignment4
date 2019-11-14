package com.cscie97.store.authentication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.cscie97.store.model.CommandProcessorException;


public class CommandProcessor
{
    /* Variables */
    
    private StoreAuthenticationService authenticator;
    private AuthToken hardcodedUserAuthToken;
    private int lineNum = 0;
    
    /* API Methods */

    /* *
     * Parses a string for valid CLI/DSL command syntax and calls corresponding Modeler method
     * @command The input string to be parsed
     */
    public void processCommand(String command)
    {               
        // Create Authenticator if doesn't already exist
        if (authenticator == null)
            authenticator = new Authenticator();
        
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
        // Create Authenticator if doesn't already exist
        if (authenticator == null)
            authenticator = new Authenticator();
        
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
     * Utility method that parses a string for valid DSL/CLI command syntax and calls Modeler methods
     * based on parsing result
     * @param input The line of string to be parsed
     */
    public void parseAndProcess(String input)
    {
        // TODO
        
        // Login CommandProcessor as the hardcoded User to get AuthToken for operating Authenticator methods
        hardcodedUserAuthToken = authenticator.obtainAuthToken(Authenticator.getHardcodedUserUsername(), Authenticator.getHardcodedUserPassword());
        
        // TODO: Do CLI script commands now to see if they work
        authenticator.definePermission("userAdmin", "User Administrator", "Create, Update, Delete Users", hardcodedUserAuthToken);
    }
}

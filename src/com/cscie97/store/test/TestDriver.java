/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.test;

import java.util.Scanner;

import com.cscie97.store.authentication.CommandProcessor;

/* *
 * Test driver class that contains main method that calls the Store Authenticator CommandProcessor class to exercise the Authenticator
 * and Ledger services
 */
public class TestDriver
{
    /* *
     * Without a script file name argument, main method will run manual commands (otherwise will parse a script
     * file if it is given)
     */
    public static void main(String[] args)
    {
        // Create a command processor
        CommandProcessor cp = new CommandProcessor();
        
        // If no file argument included on command line call processCommand method
        if (args.length == 0)
        {
            String str;    
            
            Scanner input = new Scanner(System.in);           
            
            System.out.print(getMenu());
            
            while (!(str = input.nextLine()).equalsIgnoreCase("q"))
            {   
                // Check if user inputted anything and call command processor if so
                if (str.length() > 0)
                    cp.processCommand(str); 
                
                System.out.println(); 
                
                //System.out.print(getMenu());
                System.out.print("Please enter a command ('q' to quit): ");
            }
            
            // Close scanner
            input.close();            
        }
        
        // If script file argument included on command line, call processCommandFile
        else if (args.length == 1)
        {
            // Call command processor with file name
            cp.processCommandFile(args[0]);       
        }    
    }
    
    /* *
     * Outputs a menu to stdout so user can see valid command inputs and their syntax
     */
    public static String getMenu()
    {
        // Initialize menu string
        String string = "";
        
        // Output list of commands
        string += "Commands and syntax (use quotes to group words as one entry) --\n";
        string += " 1.) define store <identifier> name <name> address <address>\n";
        string += " 2.) show store <identifier>\n";
        string += " 3.) define aisle <store_id>:<aisle_number> name <name> description <description> " 
                + "location (floor | store_room)\n";
        string += " 4.) show aisle <store_id>:<aisle_number>\n";
        string += " 5.) define shelf <store_id>:<aisle_number>:<shelf_id> name <name> level (high | " 
                + "medium | low) description <description> temperature (frozen | refrigerated | " 
                + "ambient | warm | hot) (default temperature is ambient)\n";
        string += " 6.) show shelf <store_id>:<aisle_number>:<shelf_id>\n";
        string += " 7.) define inventory <inventory_id> location <store_id>:<aisle_number>:<shelf_id> " 
                + "capacity <capacity> count <count> product <product_id>\n";
        string += " 8.) show inventory <inventory_id>\n";
        string += " 9.) update inventory <inventory_id> update_count <increment or decrement>\n";
        string += " 10.) define product <product_id> name <name> description <description> size " 
                + "<size> category <category> unit_price <unit_price> temperature (frozen | " 
                + "refrigerated | ambient | warm | hot) (default temperature is ambient)\n";
        string += " 11.) show product <product_id>\n";
        string += " 12.) define customer <customer_id> first_name <first_name> last_name <last_name> " 
                + "type (registered|guest) email_address <email> account <account_address>\n";
        string += " 13.) update customer <customer_id> location <store:aisle>\n";
        string += " 14.) show customer <customer_id>\n";
        string += " 15.) get_customer_basket <customer_id>\n";
        string += " 16.) add_basket_item <basket_id> product <product_id> item_count <count>\n";
        string += " 17.) remove_basket_item <basket_id> product <product_id> item_count <count>\n";
        string += " 18.) clear_basket <basket_id>\n";
        string += " 19.) show basket_items <basket_id>\n";
        string += " 20.) define device <device_id> name <name> type (speaker | robot | turnstile) "
                + "location <store>:<aisle>\n";
        string += " 21.) show device <device_id>\n";
        string += " 22.) create event <device_id> event <event_description>\n";
        string += " 23.) create command <device_id> message <command>\n";
        string += "\n";
        string += " Example command: define store store_1 name \"Harvard Square Store\" address \"1400 Mass Avenue,"
                + " Cambridge, MA 02138\"\n\n";
        string += " (Ledger and Authentication commands are also accepted)\n\n";
        string += "Please enter a command ('q' to quit): ";      
        
        return string;
    }
}

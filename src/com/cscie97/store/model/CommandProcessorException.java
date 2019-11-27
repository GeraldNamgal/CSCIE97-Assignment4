/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.model;

/* *
 * Handles exceptions thrown by the CommandProcessor class. Extends Java's Exception class
 */
@SuppressWarnings("serial")
public class CommandProcessorException extends java.lang.Exception
{
    /* API Variables */
    
    private String action;
    private String reason;
    private Integer lineNumber;
    
    /* Constructor */
    
    /* *
     * Creates a new CommandProcessorException for exceptions that don't output file numbers (e.g., manual commands)
     */
    public CommandProcessorException(String action, String reason)
    {
        this.action = action;
        this.reason = reason;        
    }
    
    /* *
     * Creates a new CommandProcessorException that takes in file line numbers (e.g. location of command file commands)
     * @param action Action performed when exception was thrown
     * @param reason Reason for the exception
     * @param lineNumber File's line number where exception was thrown
     */
    public CommandProcessorException(String action, String reason, Integer lineNumber)
    {
        this.action = action;
        this.reason = reason;
        this.lineNumber = lineNumber;
    }
    
    /* Method */
    
    /* *
     * Overwrites Exception class' getMessage method to suit CommandProcessor exceptions per requirements, i.e.,
     * to print an action and a reason to stdout
     */
    public String getMessage()
    {
        return "CommandProcessorException (Modeler) thrown --\n Action: " + action + "\n" + " Reason: " + reason + "\n";
    }
    
    /* *
     * Prints a message to stdout for exceptions thrown when reading from files (includes line number of exception occurrence)
     */
    public String getMessageLine()
    {
        return "CommandProcessorException (Modeler) thrown --\n Action: " + action + "\n" + " Reason: " + reason +
                "\n Line number: " + lineNumber + "\n";
    }
}

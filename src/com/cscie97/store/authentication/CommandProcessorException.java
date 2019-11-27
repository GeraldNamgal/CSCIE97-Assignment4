/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

/* *
 * Handles exceptions thrown by the CommandProcessor class in the store.authentication package. Extends Java's Exception class
 */
@SuppressWarnings("serial")
public class CommandProcessorException extends java.lang.Exception
{
    /* API Variables */
    
    private String action;
    private String reason;
    private Integer lineNumber;
    
    /* Constructor */
    
    // Two constructors; for exceptions that output file line numbers and those that don't
    public CommandProcessorException(String action, String reason)
    {
        this.action = action;
        this.reason = reason;        
    }
    
    public CommandProcessorException(String action, String reason, Integer lineNumber)
    {
        this.action = action;
        this.reason = reason;
        this.lineNumber = lineNumber;
    }
    
    /* Methods */
    
    public String getMessage()
    {
        return "CommandProcessorException (Authentication) thrown --\n Action: " + action + "\n" + " Reason: " + reason + "\n";
    }
    
    public String getMessageLine()
    {
        return "CommandProcessorException (Authentication) thrown --\n Action: " + action + "\n" + " Reason: " + reason +
                "\n Line number: " + lineNumber + "\n";
    }
}

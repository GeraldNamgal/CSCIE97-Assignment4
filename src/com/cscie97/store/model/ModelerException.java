/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

/* *
 * Handles exceptions thrown by the Modeler. Extends Java's exception class
 */
@SuppressWarnings("serial")
public class ModelerException extends java.lang.Exception
{
    /* API Variables */
    
    private String action;
    private String reason;
    
    /* Constructor */
    
    /* *
     * Creates a new ModelerException
     * @param action Action performed when exception was thrown
     * @param reason Reason for the exception
     */
    public ModelerException(String action, String reason)
    {
        this.action = action;
        this.reason = reason;
    }
    
    /* Methods */
    
    /* *
     * Overwrites Exception class' method to suit Modeler exceptions per requirements
     */
    public String getMessage()
    {
        return "ModelerException thrown --\n - Action: " + action + "\n" + " - Reason: " + reason + "\n";
    }    
}

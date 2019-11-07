/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.controller;

/* *
 * Handles exceptions thrown by the Controller. Extends Java's exception class
 */
@SuppressWarnings("serial")
public class ControllerException extends java.lang.Exception
{
    /* API Variables */
    
    private String action;
    private String reason;
    
    /* Constructor */
    
    /* *
     * Creates a new ControllerException
     * @param action Action performed when exception was thrown
     * @param reason Reason for the exception
     */
    public ControllerException(String action, String reason)
    {
        this.action = action;
        this.reason = reason;
    }
    
    /* Methods */
    
    /* *
     * Overwrites Exception class' method to suit Controller exceptions per requirements
     */
    public String getMessage()
    {
        return "ControllerException thrown --\n - Action: " + action + "\n" + " - Reason: " + reason + "\n";
    }
}

/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

/* *
 * Handles exceptions thrown by the Authenticator. Extends Java's exception class
 */
@SuppressWarnings("serial")
public class AuthenticatorException extends java.lang.Exception
{
    /* API Variables */
    
    private String action;
    private String reason;
    private String exception;
    
    /* Constructor */
    
    /* *
     * Creates a new AuthenticatorException
     * @param action Action performed when exception was thrown
     * @param reason Reason for the exception
     */
    public AuthenticatorException(String exception, String action, String reason)
    {
        this.exception = exception;
        this.action = action;
        this.reason = reason;
    }
    
    /* Methods */
    
    /* *
     * Overwrites Exception class' method to suit Authenticator exceptions per requirements
     */
    public String getMessage()
    {
        return exception + " thrown --\n - Action: " + action + "\n" + " - Reason: " + reason + "\n";
    }    
}

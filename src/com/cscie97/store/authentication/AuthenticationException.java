package com.cscie97.store.authentication;

@SuppressWarnings("serial")
public class AuthenticationException extends java.lang.Exception
{
    /* API Variables */
    
    private String action;
    private String reason;
    
    /* Constructor */
    
    /* *
     *
     */
    public AuthenticationException(String action, String reason)
    {
        this.action = action;
        this.reason = reason;
    }
    
    /* Methods */
    
    /* *
     * Overwrites Exception class' method to suit Authenticator exceptions per requirements
     */
    public String getMessage()
    {
        return "AuthenticationException thrown --\n - Action: " + action + "\n" + " - Reason: " + reason + "\n";
    }    
}

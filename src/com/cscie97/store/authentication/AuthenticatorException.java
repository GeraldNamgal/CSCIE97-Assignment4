package com.cscie97.store.authentication;

@SuppressWarnings("serial")
public class AuthenticatorException extends java.lang.Exception
{
    /* API Variables */
    
    private String action;
    private String reason;
    private String exception;
    
    /* Constructor */
    
    /* *
     *
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
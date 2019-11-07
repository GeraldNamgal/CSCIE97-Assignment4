package com.cscie97.store.authentication;

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

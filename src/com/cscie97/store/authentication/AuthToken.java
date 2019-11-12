package com.cscie97.store.authentication;

public class AuthToken
{
    /* Variables */
    
    private String id;
    private Boolean active;
    
    /* Constructor */
    
    // TODO: Add a lastUsed parameter and/or attribute
    public AuthToken(String id)
    {
        this.id = id;
        this.active = true;
    }
    
    /* Getters and Setters */
    
    public String getId()
    {
        return id;
    }

    public Boolean isActive()
    {
        return active;
    }

    public void setActive(Boolean trueOrFalse)
    {
        this.active = trueOrFalse;
    }   
}

package com.cscie97.store.authentication;

public class AuthToken
{
    /* Variables */
    
    private String id;
    private Boolean active;
    private User userOfAuthToken;
    private StoreAuthenticationService authenticator;    
    
    /* Constructor */
    
    // TODO: Add a lastUsed parameter and/or attribute
    public AuthToken(String id, User userOfAuthToken, Authenticator authenticator)
    {
        this.id = id;
        this.active = true;
        this.userOfAuthToken = userOfAuthToken;
        this.authenticator = authenticator;        
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

    public void setActive(Boolean trueOrFalse, AuthToken authToken)
    {
        // Check that given authToken has "updateAuthTokenValidity" Permission first
        if (authenticator.hasPermission("updateAuthTokenValidity", authToken))
            this.active = trueOrFalse;     
    }
    
    public User getUserOfAuthToken()
    {
        return userOfAuthToken;
    }
}

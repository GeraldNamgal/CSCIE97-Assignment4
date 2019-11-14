package com.cscie97.store.authentication;

public class AuthToken
{
    /* Variables */
    
    private String id;
    private Boolean active;
    private StoreAuthenticationService authenticator;
    
    /* Constructor */
    
    // TODO: Add a lastUsed parameter and/or attribute
    public AuthToken(String id, Authenticator authenticator)
    {
        this.id = id;
        this.active = true;
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
        // Check that given authToken has "updateAuthTokenValid" Permission first
        if (authenticator.hasPermission("updateAuthTokenValid", authToken))
            this.active = trueOrFalse;
        
        // Throw an AccessDenied exception if given authToken doesn't have the Permission
        else
        {
            try
            {             
                throw new AuthenticatorException("AccessDenied", "check for \"updateAuthTokenValid\" permission", "Auth Token does not have Permission");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());          
            }           
        }
    }   
}

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
    public AuthToken(String id, User userOfAuthToken, StoreAuthenticationService authenticator)
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

    public void setActive(Boolean trueOrFalse, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given authToken has "updateAuthTokenValidity" Permission first
        GetPermissionsVisitor getPermissionVisitor = authenticator.getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionVisitor != null) && getPermissionVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("update AuthToken validity")))
            this.active = trueOrFalse;     
    }
    
    public User getUserOfAuthToken()
    {
        return userOfAuthToken;
    }
}

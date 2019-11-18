package com.cscie97.store.authentication;

public class AuthTokenTuple
{
    /* Variables */
    
    private AuthToken authToken;
    private PermissionTuple permissionTuple;
    
    /* Constructor */
    
    public AuthTokenTuple(AuthToken authToken)
    {
        this.authToken = authToken;
        this.permissionTuple = new PermissionTuple();
    }
    
    /* Getters and Setters */
    
    public AuthToken getAuthToken()
    {
        return authToken;
    }
    
    public PermissionTuple getPermissionTuple()
    {
        return permissionTuple;
    }
}

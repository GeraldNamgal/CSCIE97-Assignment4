/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

/* *
 * Represents a "tuple" object that contains an AuthToken and a PermissionTuple for passing around AuthTokens and their
 * associated permissions, resources, and roles
 */
public class AuthTokenTuple
{
    /* Variables */
    
    private AuthToken authToken;
    private PermissionTuple permissionTuple;
    
    /* Constructors */
    
    public AuthTokenTuple(AuthToken authToken)
    {
        this.authToken = authToken;
        this.permissionTuple = new PermissionTuple();
    }
    
    public AuthTokenTuple(AuthToken authToken, String resourceId)
    {
        this.authToken = authToken;
        this.permissionTuple = new PermissionTuple();
        addResourceId(resourceId);
    }
    
    /* Methods */
    
    public void addResourceId(String resourceId)
    {
        permissionTuple.addResourceId(resourceId);
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

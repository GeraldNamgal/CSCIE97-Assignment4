package com.cscie97.store.authentication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthToken
{
    /* Variables */
    
    private Integer daysValid = 5;
    private String id;
    private Boolean active;
    private User userOfAuthToken;
    private String expirationDate;
    private String lastUsedDate;
    private StoreAuthenticationService authenticator;    
    
    /* Constructor */
   
    public AuthToken(String id, User userOfAuthToken, StoreAuthenticationService authenticator)
    {
        this.id = id;
        this.active = true;
        this.userOfAuthToken = userOfAuthToken;
        this.authenticator = authenticator;
        
        // Set expirationDate
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now().plusDays(daysValid);
        expirationDate = dtf.format(currentDateTime);
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

    public String getExpirationDate()
    {
        return expirationDate;
    }
    
    public void setExpirationDate(String expirationDate)
    {
        this.expirationDate = expirationDate;
    }

    public String getLastUsedDate()
    {
        return lastUsedDate;
    }

    public void setLastUsedDate(String lastUsedDate)
    {
        this.lastUsedDate = lastUsedDate;
    }    
}

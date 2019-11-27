/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

import java.util.LinkedHashMap;

/* *
 * Represents a user of the Store 24X7 System and their associated data. Implements Visitable (for usage in Visitor design pattern)
 */
public class User implements Visitable
{
    private String id;
    private String name;
    private LinkedHashMap<String, Credential> credentials;
    private LinkedHashMap<String, AuthToken> authTokens;
    private LinkedHashMap<String, Entitlement> entitlements;
    
    public User(String id, String name)
    {
        this.id = id;
        this.name = name;
        credentials = new LinkedHashMap<String, Credential>();
        authTokens = new LinkedHashMap<String, AuthToken>();
        entitlements = new LinkedHashMap<String, Entitlement>();
    }
    
    /* Methods */
    
    public void addCredential(Credential credential)
    {
        credentials.put(credential.getId(), credential);
    }
    
    public void addAuthToken(AuthToken authToken)
    {
        authTokens.put(authToken.getId(), authToken);
    }
    
    public void addEntitlement(Entitlement entitlement)
    {
        entitlements.put(entitlement.getId(), entitlement);
    }
    
    @Override
    public void acceptVisitor(Visitor visitor)
    {
        visitor.visitUser(this);
    }  
    
    /* Getters and Setters */
    
    public String getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public LinkedHashMap<String, Entitlement> getEntitlements()
    {
        return entitlements;
    }

    public LinkedHashMap<String, Credential> getCredentials()
    {
        return credentials;
    }  

    public LinkedHashMap<String, AuthToken> getAuthTokens()
    {
        return authTokens;
    }        
}

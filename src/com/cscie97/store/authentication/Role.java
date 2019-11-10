package com.cscie97.store.authentication;

import java.util.LinkedHashMap;

public class Role extends Entitlement
{
    /* Variables */
  
    protected LinkedHashMap<String, Entitlement> entitlements;
    
    /* Constructor */
    
    public Role(String id, String name, String description)
    {
        super(id, name, description);
        
        entitlements = new LinkedHashMap<String, Entitlement>();        
    }
    
    /* Methods */
    
    public void addEntitlement(Entitlement entitlement)
    {
        entitlements.put(entitlement.getId(), entitlement);
    }     

    /* Getters and Setters */
    
    public LinkedHashMap<String, Entitlement> getEntitlements()
    {
        return entitlements;
    }

    @Override
    public void acceptVistor(EntitlementVisitor visitor)
    {
        visitor.visitRole(this);
    }
}

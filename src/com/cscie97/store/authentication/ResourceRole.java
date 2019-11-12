package com.cscie97.store.authentication;

public class ResourceRole extends Role
{
    /* Variables */
    
    private Resource resource;
    
    /* Constructor */
    
    public ResourceRole(String id, String name, String description, Resource resource, Entitlement entitlement)
    {
        super(id, name, description);  
        
        this.resource = resource;
        entitlements.put(entitlement.getId(), entitlement);
    }
    
    // TODO: Make a constructor that doesn't take an Entitlement
    
    /* Getters and Setters */
    
    public Resource getResource()
    {
        return resource;
    }

    @Override
    public void acceptVistor(EntitlementVisitor visitor)
    {
        visitor.visitResourceRole(this);
    }
}

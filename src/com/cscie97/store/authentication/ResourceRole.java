package com.cscie97.store.authentication;

public class ResourceRole extends Role
{
    /* Variables */
    
    private Resource resource;
    
    /* Constructor */
    
    public ResourceRole(String id, String name, String description, Entitlement entitlement, Resource resource)
    {
        super(id, name, description);  
        
        this.resource = resource;
        entitlements.put(entitlement.getId(), entitlement);
    }
    
    /* Getters and Setters */
    
    public Resource getResource()
    {
        return resource;
    }

    @Override
    public void acceptVisitor(Visitor visitor)
    {
        visitor.visitResourceRole(this);
    }
}

package com.cscie97.store.authentication;

import java.util.LinkedHashMap;

public class ResourceRole extends BaseRole
{
    /* Variables */
    
    private Resource resource;
    private LinkedHashMap<String, BaseRole> baseRoles;
    
    /* Constructor */
    
    public ResourceRole(String id, String name, String description, Resource resource, BaseRole baseRole)
    {
        super(id, name, description);  
        
        this.resource = resource;
        baseRoles = new LinkedHashMap<String, BaseRole>();
        baseRoles.put(baseRole.getId(), baseRole);
    }
    
    /* Methods */
    
    public void addBaseRole(BaseRole baseRole)
    {
        baseRoles.put(baseRole.getId(), baseRole);
    }
    
    /* Getters and Setters */
    
    public Resource getResource()
    {
        return resource;
    }
    
    public LinkedHashMap<String, BaseRole> getBaseRoles()
    {
        return baseRoles;
    }

    @Override
    public void acceptVistor(EntitlementVisitor visitor)
    {
        // TODO Auto-generated method stub
        
        visitor.visitResourceRole(this);
    }
}

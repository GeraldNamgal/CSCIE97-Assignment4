package com.cscie97.store.authentication;

public abstract class Entitlement
{
    /* Variables */
    
    protected String id;
    protected String name;
    protected String description;    
    
    /* Constructor */
    
    public Entitlement(String id, String name, String description)
    {
        this.id = id;
        this.name = name;
        this.description = description;       
    }    
    
    // TODO: Do away with? -- public abstract Boolean hasPermission(String permissionId, String resourceId);
    
    /* Getters and Setters */
    
    public String getId()
    {
        return id;
    }    

    public String getName()
    {
        return name;
    }    

    public String getDescription()
    {
        return description;
    } 
}

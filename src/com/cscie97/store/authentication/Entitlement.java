package com.cscie97.store.authentication;

public abstract class Entitlement implements Visitable
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
    
    /* Method(s) */
    
    @Override
    public abstract void acceptVisitor(Visitor visitor);   
    
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

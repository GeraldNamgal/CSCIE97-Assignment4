package com.cscie97.store.authentication;

public abstract class BaseRole extends Entitlement
{
    public BaseRole(String id, String name, String description)
    {
        super(id, name, description);       
    }    
}

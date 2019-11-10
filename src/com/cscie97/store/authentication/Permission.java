package com.cscie97.store.authentication;

public class Permission extends Entitlement
{   
    public Permission(String id, String name, String description)
    {
        super(id, name, description);        
    }

    @Override
    public void acceptVistor(EntitlementVisitor visitor)
    {
        visitor.visitPermission(this);
    }   
}

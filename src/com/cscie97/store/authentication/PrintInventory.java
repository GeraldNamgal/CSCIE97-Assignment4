package com.cscie97.store.authentication;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PrintInventory implements EntitlementVisitor
{      
    /* VARIABLES */
    
    Integer tmpLevel = 0;
    Integer tabSpace = 4;
    
    /* API METHODS */

    @Override
    public void visitAuthenticator(Authenticator authenticator)
    {
        Integer level = tmpLevel;
        tmpLevel++;
        
        for (Entry<String, User> userEntry : authenticator.getUsers().entrySet())
        {
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.print("|");
            System.out.println("User: \"" + userEntry.getValue().getId() + "\"");
            userEntry.getValue().acceptVisitor(this);
        }
    }
    
    @Override
    public void visitUser(User user)
    {
        Integer level = tmpLevel;
        
        for (Entry<String, Entitlement> entitlementEntry : user.getEntitlements().entrySet())
        {       
            traverseTreeGetPermissions(entitlementEntry.getValue(), level);              
        }        
    }  
    
    @Override
    public void visitRole(Role role)
    {        
        System.out.println("Role: \"" + role.getId() + "\"");
    }
    
    @Override
    public void visitResourceRole(ResourceRole rRole)
    {
        System.out.println("ResourceRole: \"" + rRole.getId() + "\" (Resource: \"" + rRole.getResource().getId() + "\")");        
    }

    @Override
    public void visitPermission(Permission permission)
    {
        // TODO
        
        System.out.println("Permission: \"" + permission.getId() + "\"");        
    }
    
    /* UTILITY METHODS */
    
    public void traverseTreeGetPermissions(Visitable entitlement, Integer level)
    {
        for (int i = 0; i < level * tabSpace; i++)
            System.out.print(" ");
        System.out.print("|");
        
        // Call entitlement's acceptVisitor method
        entitlement.acceptVisitor(this);
                       
        // If current node is a Role, recurse
        if (entitlement.getClass().getName().endsWith(".Role") || entitlement.getClass().getName().endsWith(".ResourceRole"))
        {           
            Integer newLevel = level + 1;
            
            Role role = (Role) entitlement;            
            LinkedHashMap<String, Entitlement> entitlements = role.getEntitlements();
            for (Entry<String, Entitlement> entitlementEntry : entitlements.entrySet())
            {       
                traverseTreeGetPermissions(entitlementEntry.getValue(), newLevel);                
            }
        }              
    }      
}

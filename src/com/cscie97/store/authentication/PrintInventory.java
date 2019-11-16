package com.cscie97.store.authentication;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PrintInventory implements EntitlementVisitor
{      
    /* VARIABLES */
    
    Integer baseLevel = 0;
    Integer tabSpace = 4;
    Integer levelPtr;
    
    /* API METHODS */

    @Override
    public void visitAuthenticator(Authenticator authenticator)
    {
        Integer level = baseLevel;        
        
        System.out.println();
        
        for (Entry<String, User> userEntry : authenticator.getUsers().entrySet())
        {
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.print("|");
            System.out.println("User: id = \"" + userEntry.getValue().getId() + "\"");
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.println("       name = \"" + userEntry.getValue().getName() + "\"");
            userEntry.getValue().acceptVisitor(this);
        }
    }
    
    @Override
    public void visitUser(User user)
    {
        Integer level = baseLevel + 1;
        
        for (Entry<String, Credential> credentialEntry : user.getCredentials().entrySet())
        {
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.print("|");
            System.out.println("Credential: id = \"" + credentialEntry.getValue().getId() + "\"");
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.println("             type = \"" + credentialEntry.getValue().getType() + "\"");
        }
                    
        for (Entry<String, AuthToken> authTokenEntry : user.getAuthTokens().entrySet())
        {
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.print("|");
            System.out.println("AuthToken: id = \"" + authTokenEntry.getValue().getId() + "\"");
            for (int i = 0; i < level * tabSpace; i++)
                System.out.print(" ");
            System.out.println("            active = \"" + authTokenEntry.getValue().isActive() + "\"");
        }
        
        for (Entry<String, Entitlement> entitlementEntry : user.getEntitlements().entrySet())
        {       
            traverseTreeGetPermissions(entitlementEntry.getValue(), level);              
        }        
    }  
    
    @Override
    public void visitRole(Role role)
    {        
        System.out.println("Role: id = \"" + role.getId() + "\"");
    }
    
    @Override
    public void visitResourceRole(ResourceRole rRole)
    {
        System.out.println("ResourceRole: id = \"" + rRole.getId() + "\"");
        for (int i = 0; i < levelPtr * tabSpace; i++)
            System.out.print(" ");
        System.out.println("               resource.getId() = \"" + rRole.getResource().getId() + "\"");        
    }

    @Override
    public void visitPermission(Permission permission)
    {
        // TODO
        
        System.out.println("Permission: id = \"" + permission.getId() + "\"");        
    }
    
    /* UTILITY METHODS */
    
    public void traverseTreeGetPermissions(Visitable entitlement, Integer level)
    {
        levelPtr = level.intValue();
        
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

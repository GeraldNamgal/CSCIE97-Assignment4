package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class GetPermissions implements EntitlementVisitor
{
    /* VARIABLES */
    
    // Stores all the Permission id's of user and any associated ResourceRole resources 
    private LinkedHashMap<String, HashSet<String>> userPermissions;
    private ArrayList<String> resourceIds;
    
    /* CONSTRUCTOR */
    
    public GetPermissions()
    {
        userPermissions = new LinkedHashMap<String, HashSet<String>>();
    }
    
    /* API METHODS */

    @Override
    public void visitResourceRole(ResourceRole rRole)
    {
        // TODO Auto-generated method stub
        
        // Create new Resource list and copy old Resource list to new one
        ArrayList<String> newResourceIds = new ArrayList<String>();
        for (String resourceId : resourceIds)
        {
            newResourceIds.add(resourceId);
        }
        
        // Add new Resource to new Resource list
        newResourceIds.add(rRole.getResource().getId());    
        
        // Change resourceIds to new list
        resourceIds = newResourceIds;   
    }

    @Override
    public void visitPermission(Permission permission)
    {
        // TODO Auto-generated method stub
        
        // TODO: Debugging
        System.out.print(permission.getId() + " :");
        
        // Create HashSet pointer (for any resource id's associated with permission)
        HashSet<String> resourceIds;
        
        // If permission id encountered previously, get its HashSet
        if (userPermissions.containsKey(permission.getId()))            
            resourceIds = userPermissions.get(permission.getId());            
        
        // Else create a new HashSet for permission
        else            
            resourceIds = new HashSet<String>();            
        
        // Add resourceId to HashSet
        for (String resourceId : this.resourceIds)
        {         
            // TODO: Debugging
            System.out.print(" " + resourceId);
            
            resourceIds.add(resourceId);
        }    
        
        // TODO: Debugging
        System.out.println();
        
        // Add permission id and HashSet to userPermissions
        userPermissions.put(permission.getId(), resourceIds);
    }
    
    /* UTILITY METHODS */
    
    public void newTreeVisit()
    {
        resourceIds = new ArrayList<String>();
    }
    
    /* GETTERS AND SETTERS */
    
    public LinkedHashMap<String, HashSet<String>> getUserPermissions()
    {
        return userPermissions;
    }
}

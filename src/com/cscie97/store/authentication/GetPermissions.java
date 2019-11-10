package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class GetPermissions implements EntitlementVisitor
{
    /* VARIABLES */
         
    private LinkedHashMap<String, HashSet<String>> userPermissionIds; // Stores Permission id's of user and any associated ResourceRole resources
    private ArrayList<String> tmpResourceIds;
    
    /* CONSTRUCTOR */
    
    public GetPermissions()
    {
        userPermissionIds = new LinkedHashMap<String, HashSet<String>>();
    }
    
    /* API METHODS */

    @Override
    public void visitRole(Role role)
    {        
        // No action needed
    }
    
    @Override
    public void visitResourceRole(ResourceRole rRole)
    {
        // Create new Resource list and copy old Resource list to new one
        ArrayList<String> newTmpResourceIds = new ArrayList<String>();
        for (String resourceId : tmpResourceIds)
        {
            newTmpResourceIds.add(resourceId);
        }
        
        // Add new Resource to new Resource list
        newTmpResourceIds.add(rRole.getResource().getId());    
        
        // Change tmpResourceIds to new list
        tmpResourceIds = newTmpResourceIds;   
    }

    @Override
    public void visitPermission(Permission permission)
    {
        // TODO: Debugging
        System.out.print(permission.getId() + " :");
        
        // Create HashSet pointer (for any resource id's associated with permission)
        HashSet<String> resourceIds;
        
        // If Permission id encountered previously, get its HashSet
        if (userPermissionIds.containsKey(permission.getId()))            
            resourceIds = userPermissionIds.get(permission.getId());            
        
        // Else create a new HashSet for Permission id
        else            
            resourceIds = new HashSet<String>();            
        
        // Add Resource id to HashSet
        for (String tmpResourceId : tmpResourceIds)
        {         
            // TODO: Debugging
            System.out.print(" " + tmpResourceId);
            
            resourceIds.add(tmpResourceId);
        }    
        
        // TODO: Debugging
        System.out.println();
        
        // Add Permission id and Resource id HashSet to userPermissionIds
        userPermissionIds.put(permission.getId(), resourceIds);
    }
    
    /* UTILITY METHODS */
    
    /* *
     * Creates a new fresh ArrayList for traversing a new tree of Entitlements
     */
    public void newTreeVisit()
    {
        tmpResourceIds = new ArrayList<String>();
    }
    
    /* GETTERS AND SETTERS */
    
    public LinkedHashMap<String, HashSet<String>> getUserPermissionIds()
    {
        return userPermissionIds;
    }

    public ArrayList<String> getTmpResourceIds()
    {
        return tmpResourceIds;
    }

    public void setTmpResourceIds(ArrayList<String> tmpResourceIds)
    {
        this.tmpResourceIds = tmpResourceIds;
    }  
}

package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class GetPermissionVisitor implements Visitor
{
    /* VARIABLES */
   
    private Boolean hasPermission = false;
    private PermissionTuple permissionTupleToBeChecked;
    private ArrayList<PermissionTuple> permissionsTuples;
    private ArrayList<String> resourceIdsPtr;
    private ArrayList<String> roleIdsPtr;
    
    /* CONSTRUCTOR(S) */
        
    public GetPermissionVisitor(PermissionTuple permissionTupleToBeChecked)
    {
        this.permissionTupleToBeChecked = permissionTupleToBeChecked;
        permissionsTuples = new ArrayList<PermissionTuple>();
    }
    
    /* API METHODS */

    @Override
    public void visitAuthenticator(Authenticator authenticator)
    {
        // No action needed
    }
    
    @Override
    public void visitUser(User user)
    {
        for (Entry<String, Entitlement> entitlementEntry : user.getEntitlements().entrySet())
        {       
            traverseTreeGetPermissions(entitlementEntry.getValue(), new ArrayList<String>(), new ArrayList<String>());              
        }        
    }  
    
    @Override
    public void visitRole(Role role)
    {        
        // Create new Role list, copy old Role list to new one, and add new Role to new list
        ArrayList<String> newTmpRoleIds = new ArrayList<String>();
        for (String id : roleIdsPtr)
            newTmpRoleIds.add(id);
        newTmpRoleIds.add(role.getId());
        
        // Change tmpRoleIds to new list
        roleIdsPtr = newTmpRoleIds;
    }
    
    @Override
    public void visitResourceRole(ResourceRole rRole)
    {
        // Create new Resource list, copy old Resource list to new one, and add new Resource to new list
        ArrayList<String> newTmpResourceIds = new ArrayList<String>();
        for (String resourceId : resourceIdsPtr)
            newTmpResourceIds.add(resourceId);        
        newTmpResourceIds.add(rRole.getResource().getId());    
        
        // Change tmpResourceIds to new list
        resourceIdsPtr = newTmpResourceIds;   
    }

    @Override
    public void visitPermission(Permission permission)
    {
        /*// TODO: Debugging
        System.out.print(permission.getId() + " : [ ");*/
        
        // If Permission id is found 
        if (permission.getId().equals(permissionTupleToBeChecked.getPermissionId()))
        {
            // If Permission is associated with no resources
            if (resourceIdsPtr.isEmpty())
            {
                hasPermission = true;
                
                // Create new PermissionTuple
                PermissionTuple permissionTuple = new PermissionTuple(permission.getId());
                
                // Add associated resource id's to PermissionTuple
                for (String resourceId : resourceIdsPtr)                
                    permissionTuple.addResourceId(resourceId);
                
                // Add associated role id's to PermissionTuple
                for (String roleId : roleIdsPtr)
                    permissionTuple.addRoleId(roleId);
                
                // Add PermissionTuple to permissionsTuples list
                permissionsTuples.add(permissionTuple);
            }
            
            // If Permission is associated with a resource(s)            
            else
            {
                Boolean allResources = true;
                
                for (String resourceId : permissionTupleToBeChecked.getResourceIds())
                {
                    if (!resourceIdsPtr.contains(resourceId))
                    {
                        allResources = false;
                        break;
                    }
                }
                
                if (allResources == true)
                {
                    hasPermission = true;
                    
                    // Create new PermissionTuple
                    PermissionTuple permissionTuple = new PermissionTuple(permission.getId());
                    
                    // Add associated resource id's to PermissionTuple
                    for (String resourceId : resourceIdsPtr)                
                        permissionTuple.addResourceId(resourceId);
                    
                    // Add associated role id's to PermissionTuple
                    for (String roleId : roleIdsPtr)
                        permissionTuple.addRoleId(roleId);
                    
                    // Add PermissionTuple to permissionsTuples list
                    permissionsTuples.add(permissionTuple);
                }
            }
        }   
    }
    
    /* UTILITY METHODS */
    
    public void traverseTreeGetPermissions(Visitable entitlement, ArrayList<String> tmpResourceIds, ArrayList<String> tmpRoleIds)
    {       
        // Point to the list of Resource id's passed in
        resourceIdsPtr = tmpResourceIds;
        
        // Point to the associatedRoleId passed in
        roleIdsPtr = tmpRoleIds;
        
        // Call entitlement's acceptVisitor method
        entitlement.acceptVisitor(this);
        
        /*// TODO: Debugging
        if (entitlement.getClass().getName().endsWith(".Permission"))
        {
            for (String id : resourceIdsPtr)
                System.out.print(id + " ");
            System.out.print("] ");
            
            System.out.print("; [ ");
            for (String id : roleIdsPtr)
                System.out.print(id + " ");
            System.out.println("]");
        }*/
        
        // If current node is a Role, recurse
        if (entitlement.getClass().getName().endsWith(".Role") || entitlement.getClass().getName().endsWith(".ResourceRole"))
        {
            // Create a new ArrayList for the new Resource id list (for if new Resource was added)
            ArrayList<String> newTmpResourceIds = new ArrayList<String>();
            for (String id : resourceIdsPtr)
                newTmpResourceIds.add(id);
            
            // Create a new ArrayList for the new Role id list (for if new Role was added)
            ArrayList<String> newTmpRoleIds = new ArrayList<String>();
            for (String id : roleIdsPtr)
                newTmpRoleIds.add(id);
            
            Role role = (Role) entitlement;            
            LinkedHashMap<String, Entitlement> entitlements = role.getEntitlements();
            for (Entry<String, Entitlement> entitlementEntry : entitlements.entrySet())
            {
                // Recurse with new Resource id list as parameter if current node is a ResourceRole
                if (entitlement.getClass().getName().endsWith(".ResourceRole"))
                {        
                    traverseTreeGetPermissions(entitlementEntry.getValue(), newTmpResourceIds, tmpRoleIds);
                }
                
                else
                    traverseTreeGetPermissions(entitlementEntry.getValue(), tmpResourceIds, newTmpRoleIds);
            }
        }              
    }
    
    /* GETTERS AND SETTERS */
    
    public Boolean getHasPermission()
    {
        return hasPermission;
    }

    public PermissionTuple getPermissionTupleToBeChecked()
    {
        return permissionTupleToBeChecked;
    }  

    public ArrayList<PermissionTuple> getPermissionsTuples()
    {
        return permissionsTuples;
    }      
}

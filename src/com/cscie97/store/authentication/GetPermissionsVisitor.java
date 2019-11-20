package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class GetPermissionsVisitor implements Visitor
{
    /* VARIABLES */
 
    private User user;
    private ArrayList<PermissionTuple> permissionTuples;
    private ArrayList<String> resourceIdsPtr;
    private ArrayList<String> roleIdsPtr;
    
    /* CONSTRUCTOR(S) */
        
    public GetPermissionsVisitor(User user)
    {
        this.user = user;
        permissionTuples = new ArrayList<PermissionTuple>();
    }
    
    /* API METHODS */

    @Override
    public void visitAuthenticator(StoreAuthenticationService authenticator)
    {
        user.acceptVisitor(this);
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
        // Create new PermissionTuple
        PermissionTuple permissionTuple = new PermissionTuple(permission.getId());
        
        // Add associated resource id's to PermissionTuple
        for (String resourceId : resourceIdsPtr)                
            permissionTuple.addResourceId(resourceId);
        
        // Add associated role id's to PermissionTuple
        for (String roleId : roleIdsPtr)
            permissionTuple.addRoleId(roleId);
        
        // Add PermissionTuple to permissionTuples list
        permissionTuples.add(permissionTuple);    
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
    
    public Boolean hasPermission(PermissionTuple permissionTupleToBeChecked)
    {
        // Search through user's permissions
        for (PermissionTuple permissionTuple : permissionTuples)
        {
            // If Permission id is found 
            if (permissionTuple.getPermissionId().equals(permissionTupleToBeChecked.getPermissionId()))
            {
                // If Permission is associated with no resources
                if (permissionTuple.getResourceIds().isEmpty())
                {
                    return true;                    
                }
                
                // If Permission is associated with a resource(s)            
                else
                {
                    Boolean hasAllResources = true;
                    
                    for (String resourceId : permissionTupleToBeChecked.getResourceIds())
                    {
                        if (!permissionTuple.getResourceIds().contains(resourceId))
                        {
                            hasAllResources = false;
                            break;
                        }
                    }
                    
                    if (hasAllResources == true)
                    {
                        return true;                
                    }
                }
            }
        }
        
        // Throw exception if user does not have permission       
        try
        {
            throw new AuthenticatorException("AccessDeniedException", "check for \""+ permissionTupleToBeChecked.getPermissionId()
                + "\" permission", "user \"" + user.getId() + "\" does not have permission");
        }
        
        catch (AuthenticatorException exception)
        {
            System.out.println();
            System.out.print(exception.getMessage());
            return false;
        }       
    }
    
    /* GETTERS AND SETTERS */ 

    public ArrayList<PermissionTuple> getUserPermissions()
    {
        return permissionTuples;
    }      
}

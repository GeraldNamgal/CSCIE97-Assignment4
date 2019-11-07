package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Authenticator implements StoreAuthenticationService
{
    /* Variables */
    
    private final String INITIATOR_ACCOUNT_ID = "initiator";      
    private int suggestedId = 0;
    private HashSet<String> authTokenIdsUsed;
    private LinkedHashMap<String, User> users;
    LinkedHashMap<String, HashSet<String>> tmpUserPermissions;
   
    /* Constructor */
    
    public Authenticator()
    {      
        // Initialize list of Users
        users = new LinkedHashMap<String, User>();
        
        // Create an initiator User and add it to list of Users
        User initiator = new User(INITIATOR_ACCOUNT_ID, "Initiator");
        users.put(initiator.getId(), initiator);
        
        // Give initiator User a login (username and password) credential
        Credential credential = new Credential(initiator.getId(), "password", "password");
        initiator.addCredential(credential);
        
        // Create Permission to use Authenticator API methods
        Permission permission = new Permission("useAuthenticatorAPI", "Use Authenticator API", "Use any of the Authenticator API methods");
        
        // Create a Role for Authenticator API Users
        Role role = new Role("AuthenticatorApiUserRole", "Authenticator API User Role", "Has all permissions of an Authenticator API user");
        
        // Add Permission to Role
        role.addEntitlement(permission);
        
        // Add Role to initiator User
        initiator.addEntitlement(role);
        
        // Create a new HashSet to store and track used/processed Auth Token id's
        authTokenIdsUsed = new HashSet<String>();
        
        /* TODO: Testing... */
        
        Permission permission1 = new Permission("permissionId1", "name", "description");
        Permission permission2 = new Permission("permissionId2", "name", "description");
        Permission permission3 = new Permission("permissionId3", "name", "description");
        Permission permission4 = new Permission("permissionId4", "name", "description");
        Permission permission5 = new Permission("permissionId5", "name", "description");
        
        Role role1 = new Role("roleId1", "name", "description");
        Role role2 = new Role("roleId2", "name", "description");
        Role role3 = new Role("roleId3", "name", "description");
        Role role4 = new Role("roleId4", "name", "description");
        
        Resource store1 = new Resource("store1", "description");
        Resource store2 = new Resource("store2", "description");
        
        ResourceRole rRole1 = new ResourceRole("rRoleId1", "name", "description", store1, role3);
        ResourceRole rRole2 = new ResourceRole("rRoleId2", "name", "description", store1, role1);
        ResourceRole rRole3 = new ResourceRole("rRoleId3", "name", "description", store1, role4);
        ResourceRole rRole4 = new ResourceRole("rRoleId4", "name", "description", store2, role2);
        
        role1.addEntitlement(permission1);
        role1.addEntitlement(permission5);
        rRole2.addBaseRole(role1);
        role.addEntitlement(rRole2);
        role2.addEntitlement(permission2);
        role2.addEntitlement(permission1);
        rRole4.addBaseRole(role2);
        role.addEntitlement(rRole4);
        role3.addEntitlement(permission3);
        rRole1.addBaseRole(role3);
        initiator.addEntitlement(rRole1);
        role4.addEntitlement(permission4);
        rRole3.addBaseRole(role4);
        rRole1.addBaseRole(rRole3);
        
        LinkedHashMap<String, HashSet<String>> initiatorPermissions = getUserPermissions(initiator);
        
        // TODO: Debugging
        System.out.println();
        for (Entry<String, HashSet<String>> permissionEntry : initiatorPermissions.entrySet())
        {
            System.out.print(permissionEntry.getKey() + " :");
            for (String resourceId : permissionEntry.getValue())
            {
                System.out.print(" " + resourceId);
            }
            
            if (permissionEntry.getValue().size() == 0)
                System.out.print(" empty");
            
            System.out.println();
        }
    }
    
    public LinkedHashMap<String, HashSet<String>> getUserPermissions(User user)
    {
        // Get all the Permission id's of user and any associated ResourceRole resources        
        tmpUserPermissions = new LinkedHashMap<String, HashSet<String>>();        
        for (Entry<String, Entitlement> entitlementEntry : user.getEntitlements().entrySet())
        {
            ArrayList<String> tmpResourceIds = new ArrayList<String>();
            traverseTree(entitlementEntry.getValue(), tmpResourceIds);
        }   
        
        return tmpUserPermissions;
    }
    
    public void traverseTree(Entitlement entitlement, ArrayList<String> resourceIdList)
    {
        // Create pointer for default Resource list to pass
        ArrayList<String> resourceIdListToPass = resourceIdList;
        
        // Create new Resource list if new node is a ResoureRole
        if (entitlement.getClass().getName().endsWith(".ResourceRole"))
        {
            // Copy old Resource list to new one
            ArrayList<String> newResourceIdList = new ArrayList<String>();
            for (String resourceId : resourceIdList)
            {
                newResourceIdList.add(resourceId);
            }
            
            // Add new Resource to new Resource list
            ResourceRole rRole = (ResourceRole) entitlement;
            newResourceIdList.add(rRole.getResource().getId());
            
            // Change pointer toward new list
            resourceIdListToPass = newResourceIdList;
        }
        
        if (entitlement.getClass().getName().endsWith(".Permission"))
        {
            // TODO: Debugging
            System.out.print(entitlement.getId() + " :");          
            
            // Create HashSet pointer (for any resource id's associated with permission)
            HashSet<String> resourceIds;
            
            // If permission id encountered previously, get its HashSet
            if (tmpUserPermissions.containsKey(entitlement.getId()))            
                resourceIds = tmpUserPermissions.get(entitlement.getId());            
            
            // Else create a new HashSet for permission
            else            
                resourceIds = new HashSet<String>();            
            
            // Add resourceId to HashSet
            for (String resourceId : resourceIdListToPass)
            {             
                // TODO: Debugging
                System.out.print(" " + resourceId);
                
                resourceIds.add(resourceId);
            }
            
            // TODO: Debugging
            System.out.println();
            
            // Add permission id and HashSet to tmpUserPermissions
            tmpUserPermissions.put(entitlement.getId(), resourceIds);
        }
        
        if (entitlement.getClass().getName().endsWith(".Role"))
        {
            Role role = (Role) entitlement;
            LinkedHashMap<String, Entitlement> entitlements = role.getEntitlements();
            for (Entry<String, Entitlement> entitlementEntry : entitlements.entrySet())
            {
                traverseTree(entitlementEntry.getValue(), resourceIdListToPass);
            }
        }
        
        if (entitlement.getClass().getName().endsWith(".ResourceRole"))
        {
            ResourceRole rRole = (ResourceRole) entitlement;
            LinkedHashMap<String, BaseRole> baseRoles = rRole.getBaseRoles();
            for (Entry<String, BaseRole> rRoleEntry : baseRoles.entrySet())
            {
                traverseTree(rRoleEntry.getValue(), resourceIdListToPass);
            }
        }
    }
    
    /* Methods */
    
    @Override
    public Permission definePermission(String id, String name, String description)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Role defineRole(String id, String name, String description)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addPermission(String roleId, String permissionId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public User createUser(String userId, String userName)
    {
        // TODO: Check for duplicates before creating new User
        
        // Create a new User
        User user = new User(userId, userName);
        
        // Add new User to list of Users
        users.put(user.getId(), user);
        
        return user;
    }

    @Override
    public void addUserCredential(String userId, String type, String value)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addRoleToUser(String userId, String roleId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ResourceRole createResourceRole(String name, String roleId, String resourceId)
    {
        // TODO Auto-generated method stub
        
        // TODO: Make sure to check that same resource isn't repeated (or something?)
        
        return null;
    }

    @Override
    public void addResourceRoleToUser(String userId, String resourceRoleId)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public AuthToken obtainAuthToken(String username, String password)
    {
        // TODO Auto-generated method stub
        
        // TODO: Verify username and password with hash
        
        // Verify username exists
        if (!users.containsKey(username))
        {
            System.out.println("\nUsername not found");
            return null;
        }        
        
        // TODO: Check if an Auth Token already exists and send that instead of creating new one
        
        // If no existing Auth Token get new Auth Token id and create new AuthToken
        while (authTokenIdsUsed.contains(Integer.toString(suggestedId)))
            suggestedId++;        
        AuthToken authToken = new AuthToken(Integer.toString(suggestedId));
        
        // Add now-used Auth Token id to used id's list and increment suggestedId for next Auth Token
        authTokenIdsUsed.add(Integer.toString(suggestedId));
        suggestedId++;
        
        return authToken;
    }    
}

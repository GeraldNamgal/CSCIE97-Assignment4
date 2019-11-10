package com.cscie97.store.authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Authenticator implements StoreAuthenticationService
{
    /* VARIABLES */
    
    private final String INITIATOR_ACCOUNT_ID = "initiator";      
    private int suggestedId = 0;
    private HashSet<String> authTokenIdsUsed;
    private LinkedHashMap<String, User> users;
    private LinkedHashMap<String, HashSet<String>> tmpUserPermissions;
   
    /* CONSTRUCTOR */
    
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
    }  
    
    /* API METHODS */
    
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
    public AuthToken obtainAuthToken(String credentialId, String credentialValue)
    {
        // TODO Auto-generated method stub
        
        // TODO: Verify credentialId and credentialValue with hash (password type only?)        
      
        // Search through each User's Credentials for given credentialId and credentialValue combination
        Boolean found = false;
        User user = null;
        for (Entry<String, User> userEntry : users.entrySet())
        {    
            for (Entry<String, Credential> credentialEntry : userEntry.getValue().getCredentials().entrySet())
            {
                if (credentialEntry.getKey().equals(credentialId) && credentialEntry.getValue().getValue().equals(credentialValue))
                {
                    found = true;
                    break;
                }
            }
            
            if (found == true)
            {
                user = userEntry.getValue();
                break;
            }
        }
        
        // TODO: Throw Authentication Exception if credentialId and credentialValue weren't found
        if (found == false)
        {
            System.out.println("\nCredential id and/or credential value are invalid");
            return null;
        }
        
        // TODO: If User has a valid Auth Token, retrieve it
        AuthToken authToken;
        user.getAuthTokens();
        
        // TODO: If User has no Auth Tokens, create one
        while (authTokenIdsUsed.contains(Integer.toString(suggestedId)))
            suggestedId++;        
        authToken = new AuthToken(Integer.toString(suggestedId));
        
        // Add now-used Auth Token id to used id's list and increment suggestedId for next Auth Token
        authTokenIdsUsed.add(Integer.toString(suggestedId));
        suggestedId++;
        
        // Return Auth Token
        return authToken;
    }
    
    /* UTILITY METHODS */
    
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
        
        // Change Resource list if new node is a ResoureRole
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
        
        // If current node is a Permission
        if (entitlement.getClass().getName().endsWith(".Permission"))
        {      
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
                resourceIds.add(resourceId);
            }    
            
            // Add permission id and HashSet to tmpUserPermissions
            tmpUserPermissions.put(entitlement.getId(), resourceIds);
        }
        
        // Recurse for when current node is a Role
        if (entitlement.getClass().getName().endsWith(".Role"))
        {
            Role role = (Role) entitlement;
            LinkedHashMap<String, Entitlement> entitlements = role.getEntitlements();
            for (Entry<String, Entitlement> entitlementEntry : entitlements.entrySet())
            {
                traverseTree(entitlementEntry.getValue(), resourceIdListToPass);
            }
        }
        
        // Recurse for when current node is a ResourceRole
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
}

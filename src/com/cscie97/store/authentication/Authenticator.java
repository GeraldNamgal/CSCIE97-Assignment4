package com.cscie97.store.authentication;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Authenticator implements StoreAuthenticationService
{
    /* VARIABLES */
    
    private final String INITIATOR_ACCOUNT_ID = "initiatorUser";      
    private int suggestedId = 0;
    private HashSet<String> authTokenIdsUsed;
    private LinkedHashMap<String, User> users;
   
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
        Permission permission = new Permission("permission", "Use Authenticator API", "Use any of the Authenticator API methods");
        
        // Create a Role for Authenticator API Users
        Role role = new Role("role", "Authenticator API User Role", "Has all permissions of an Authenticator API user");
        
        // Add Permission to Role
        role.addEntitlement(permission);
        
        // Add Role to initiator User
        initiator.addEntitlement(role);
        
        // Create a new HashSet to store and track used/processed Auth Token id's
        authTokenIdsUsed = new HashSet<String>();
        
        /* TODO: Testing... */

        Permission permission1 = new Permission("permission 1", "name", "description");
        Permission permission2 = new Permission("permission 2", "name", "description");
        Permission permission3 = new Permission("permission 3", "name", "description");
        Permission permission4 = new Permission("permission 4", "name", "description");
        Permission permission5 = new Permission("permission 5", "name", "description");

        Role role1 = new Role("role 1", "name", "description");
        Role role2 = new Role("role 2", "name", "description");
        Role role3 = new Role("role 3", "name", "description");
        Role role4 = new Role("role 4", "name", "description");

        Resource store1 = new Resource("store 1", "description");
        Resource store2 = new Resource("store 2", "description");
        Resource store3 = new Resource("store 3", "description");

        ResourceRole rRole1 = new ResourceRole("resource role 1", "name", "description", store1, role3);
        ResourceRole rRole2 = new ResourceRole("resource role 2", "name", "description", store1, role1);
        ResourceRole rRole3 = new ResourceRole("resource role 3", "name", "description", store2, role4);
        ResourceRole rRole4 = new ResourceRole("resource role 4", "name", "description", store2, role2);
        ResourceRole rRole5 = new ResourceRole("resource role 5", "name", "description", store3, permission2);

        role1.addEntitlement(permission1);
        role1.addEntitlement(permission5);
        role.addEntitlement(rRole2);
        role2.addEntitlement(permission2);
        role2.addEntitlement(permission1);
        role.addEntitlement(rRole4);
        role3.addEntitlement(permission3);
        initiator.addEntitlement(rRole1);
        role4.addEntitlement(permission4);
        rRole1.addEntitlement(rRole3);
        rRole2.addEntitlement(permission4);
        initiator.addEntitlement(rRole5);
        rRole1.addEntitlement(permission);
        role.addEntitlement(permission3);
        initiator.addEntitlement(permission5);

        GetPermissions getPermissions = new GetPermissions(null, null, null);
        
        visitUserEntitlements(initiator, getPermissions);       
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
    public User createUser(String userId, String name)
    {
        // TODO: Check for duplicates before creating new User
        
        // Create a new User
        User user = new User(userId, name);
        
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

    @Override
    public void logout(AuthToken authToken)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void visitUserEntitlements(Visitable user, EntitlementVisitor visitor)
    {       
        user.acceptVistor(visitor);
    }     
}

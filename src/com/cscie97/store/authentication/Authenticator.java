package com.cscie97.store.authentication;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Authenticator implements StoreAuthenticationService
{
    /* VARIABLES */
    
    private static final String HARDCODED_USER_ID = "hardcodedUser";
    private static final String HARDCODED_USER_USERNAME = HARDCODED_USER_ID + "-pwd";
    private static final String HARDCODED_USER_PASSWORD = "password";
    private final String MY_AUTHTOKEN_ID = "authenticatorAuthTokenId";
    private int suggestedId = 0;
    private HashSet<String> authTokenIdsUsed;
    private LinkedHashMap<String, User> users;
    private AuthToken myAuthToken;
   
    /* CONSTRUCTOR */
    
    public Authenticator()
    {      
        // Initialize list of Users
        users = new LinkedHashMap<String, User>();
        
        // Create a hardcoded User, add it to list of Users, and give it Credentials
        User hardcodedUser = new User(HARDCODED_USER_ID, "Hardcoded User");
        users.put(hardcodedUser.getId(), hardcodedUser);
        Credential credential = new Credential(HARDCODED_USER_USERNAME, "password", HARDCODED_USER_PASSWORD);
        hardcodedUser.addCredential(credential);
        credential = new Credential(hardcodedUser.getId() + "-vp", "voiceprint", "--voice:" + hardcodedUser.getId() + "--");
        hardcodedUser.addCredential(credential);
        credential = new Credential(hardcodedUser.getId() + "-fp", "faceprint", "--face:" + hardcodedUser.getId() + "--");
        hardcodedUser.addCredential(credential);
        
        // Create Permission to use Authenticator API methods, and a Role for Authenticator API Users
        Permission permission = new Permission("useAuthenticatorAPI", "Use Authenticator API", "Use any of the Authenticator API methods");
        Role role = new Role("authenticatorAPIUserRole", "Authenticator API User Role", "Has all permissions of an Authenticator API user");
        
        // Add permission to role and role to hardcodedUser 
        role.addEntitlement(permission);
        hardcodedUser.addEntitlement(role);
        
        // Create a new HashSet to store and track used/processed Auth Token id's for managing Auth Tokens
        authTokenIdsUsed = new HashSet<String>();
        
        // Create a User for the Authenticator itself, add it to list of Users, and give it Credentials
        User authenticator = new User("authenticator", "The Authenticator");
        users.put(authenticator.getId(), authenticator);
        credential = new Credential(authenticator.getId() + "-pwd", "password", "password");
        authenticator.addCredential(credential);
        credential = new Credential(authenticator.getId() + "-vp", "voiceprint", "--voice:" + authenticator.getId() + "--");
        authenticator.addCredential(credential);
        credential = new Credential(authenticator.getId() + "-fp", "faceprint", "--face:" + authenticator.getId() + "--");
        authenticator.addCredential(credential);
        
        // Create permission to modify AuthToken's "active" attribute and give it to Authenticator User (only it has this special permission)
        Permission authTokenPermission = new Permission("updateAuthTokenValid", "Update Valid on Auth Token", "Has permission to validate/invalidate Auth Tokens");
        authenticator.addEntitlement(authTokenPermission);
                
        // Get Authenticator User its special Auth Token
        myAuthToken = new AuthToken(MY_AUTHTOKEN_ID, this);
        authenticator.addAuthToken(myAuthToken);
        
        
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
        hardcodedUser.addEntitlement(rRole1);
        role4.addEntitlement(permission4);
        rRole1.addEntitlement(rRole3);
        rRole2.addEntitlement(permission4);
        hardcodedUser.addEntitlement(rRole5);
        rRole1.addEntitlement(permission);
        role.addEntitlement(permission3);
        hardcodedUser.addEntitlement(permission5);
        
        // TODO: Testing
        GetPermissions getPermissions = new GetPermissions(null, null, null);        
        hardcodedUser.acceptVisitor(getPermissions);
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
      
        // Search through each User's Credentials for given credentialId and credentialValue combination
        Boolean foundCredential = false;
        User userOfCredential = null;
        for (Entry<String, User> userEntry : users.entrySet())
        {    
            for (Entry<String, Credential> credentialEntry : userEntry.getValue().getCredentials().entrySet())
            {
                // TODO: Verify credentialId and credentialValue with hash per requirements
                
                if (credentialEntry.getKey().equals(credentialId) && credentialEntry.getValue().getValue().equals(credentialValue))
                {
                    foundCredential = true;
                    break;
                }
            }
            
            if (foundCredential == true)
            {
                userOfCredential = userEntry.getValue();
                break;
            }
        }
        
        // Throw Authentication Exception if credentialId and/or credentialValue weren't found
        if (foundCredential == false)
        {
            try
            {
                throw new AuthenticatorException("AuthenticationException", "obtain Auth Token", "credential id and/or credential value are invalid");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }           
        }
        
        // If User has a valid Auth Token, retrieve it
        AuthToken authToken = null;
        for (Entry<String, AuthToken> authTokenEntry : userOfCredential.getAuthTokens().entrySet())
        {
            if (authTokenEntry.getValue().isActive() == true)
            {
                authToken = authTokenEntry.getValue();
            }
        }
        
        // If User has no Auth Tokens or valid ones, create one
        if (authToken == null)
        {
            while (authTokenIdsUsed.contains(Integer.toString(suggestedId)))
                suggestedId++;        
            authToken = new AuthToken(Integer.toString(suggestedId), this);
            
            // Add now-used Auth Token id to used id's list and increment suggestedId for next Auth Token
            authTokenIdsUsed.add(Integer.toString(suggestedId));
            suggestedId++;
            
            // Add newly created Auth Token to User's list of Auth Tokens
            userOfCredential.addAuthToken(authToken);
        }       
        
        // Return Auth Token
        return authToken;
    }

    @Override
    public void logout(AuthToken authToken)
    {
        // TODO Auto-generated method stub
        
        // Validate given Auth Token
        Boolean foundAuthToken = false;
        for (Entry<String, User> userEntry : users.entrySet())
        {            
            for (Entry<String, AuthToken> authTokenEntry : userEntry.getValue().getAuthTokens().entrySet())
            {
                if (authTokenEntry.getValue().equals(authToken))
                {
                    foundAuthToken = true;
                    break;
                }
            }    
        }
        
        // Throw InvalidAuthTokenException if Auth Token not found or null
        if ((foundAuthToken == false) || (authToken == null))
        {
            try
            {              
                throw new AuthenticatorException("InvalidAuthTokenException", "logout", "Auth Token not found; not logged out");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }           
        }
        
        // Invalidate Auth Token
        authToken.setActive(false, myAuthToken);
    }
        
    @Override
    public Boolean hasPermission(String permissionId, AuthToken authToken)
    {
        // TODO Auto-generated method stub  
        
        // Find User associated with Auth Token
        Boolean foundAuthToken = false;
        User userOfAuthToken = null;
        for (Entry<String, User> userEntry : users.entrySet())
        {            
            for (Entry<String, AuthToken> authTokenEntry : userEntry.getValue().getAuthTokens().entrySet())
            {
                if (authTokenEntry.getValue().equals(authToken))
                {
                    foundAuthToken = true;
                    break;
                }
            }
            
            if (foundAuthToken == true)
            {
                userOfAuthToken = userEntry.getValue();
                break;
            }
        }
        
        // Throw InvalidAuthTokenException if Auth Token not found 
        if (foundAuthToken == false)
        {
            try
            {
                // TODO: Change to InvalidAuthTokenException and other exceptions per requirements? (i.e., make the actual classes, not just strings)
                throw new AuthenticatorException("InvalidAuthTokenException", "check for \""+ permissionId +"\" permission", "Auth Token not found");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return false;
            }           
        }
        
        // Check if User of Auth Token has permissions
        GetPermissions getPermissions = new GetPermissions(permissionId);        
        userOfAuthToken.acceptVisitor(getPermissions);
        
        // TODO: Throw exception if User doesn't have the Permission
        
        return getPermissions.getHasPermission();
    }
    
    /* UTILITY METHODS */
    
    public static String getHardcodedUserUsername()
    {
        return HARDCODED_USER_USERNAME;
    }    
    
    public static String getHardcodedUserPassword()
    {
        return HARDCODED_USER_PASSWORD;
    }   
}

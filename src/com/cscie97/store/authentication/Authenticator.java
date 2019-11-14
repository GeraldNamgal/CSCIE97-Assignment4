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
    private LinkedHashMap<String, Entitlement> entitlements;
    private LinkedHashMap<String, Resource> resources;
   
    /* CONSTRUCTOR */
    
    public Authenticator()
    {      
        // Create list of Users, Entitlements, and Resources
        users = new LinkedHashMap<String, User>();
        entitlements = new LinkedHashMap<String, Entitlement>();
        resources = new LinkedHashMap<String, Resource>();
        
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
        Permission permission = new Permission("useAuthenticatorAPI", "Use Authenticator API", "Use any of the restricted Authenticator API methods");
        Role role = new Role("authenticatorAPIUserRole", "Authenticator API User Role", "Has all permissions of an Authenticator API user");
        
        // Add permission to role and role to hardcodedUser 
        role.addEntitlement(permission);
        hardcodedUser.addEntitlement(role);
        
        // Create a new HashSet to store and track used/processed AuthToken id's for managing AuthTokens
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
        Permission authTokenPermission = new Permission("updateAuthTokenValid", "Update Valid on AuthToken", "Has permission to validate/invalidate AuthTokens");
        authenticator.addEntitlement(authTokenPermission);
                
        // Get Authenticator User its special AuthToken
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
    }
    
    /* API METHODS */   
    
    @Override
    public Permission definePermission(String id, String name, String description, AuthToken authTokenForMethod)
    {
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;
        
        // Create Permission and add it to entitlements
        Permission permission = new Permission(id, name, description);
        entitlements.put(permission.getId(), permission);
        
        return permission;
    }

    @Override
    public Role defineRole(String id, String name, String description, AuthToken authTokenForMethod)
    {
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;
        
        // Create Role and add it to entitlements
        Role role = new Role(id, name, description);
        entitlements.put(role.getId(), role);
        
        return role;
    }

    @Override
    public void addPermissionToRole(String roleId, String permissionId, AuthToken authTokenForMethod)
    {
        // TODO 
        
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return;
        
        // Get given Permission and Role and add the Permission to the Role
        Entitlement permission = entitlements.get(permissionId);
        Role role = (Role) entitlements.get(roleId);
        role.addEntitlement(permission);
    }

    @Override
    public User createUser(String userId, String name, AuthToken authTokenForMethod)
    {       
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;
        
        // TODO (if have time): Check for duplicates before creating new User
        
        // Create a new User
        User user = new User(userId, name);
        
        // Add new User to list of Users
        users.put(user.getId(), user);
        
        return user;
    }

    @Override
    public void addUserCredential(String userId, String type, String value, AuthToken authTokenForMethod)
    {
        // TODO 
        
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return;
        
        // Create Credential
        Credential credential = null;        
        if(type.equals("password"))
            credential = new Credential(userId + "-pwd", type, value);
        if(type.equals("voiceprint"))
            credential = new Credential(userId + "-vp", type, value);
        if(type.equals("faceprint"))
            credential = new Credential(userId + "-fp", type, value);
                
        // Add Credential to User's Credentials
        if (credential != null)
            users.get(userId).addCredential(credential);
    }

    @Override
    public void addRoleToUser(String userId, String roleId, AuthToken authTokenForMethod)
    {
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return;
        
        // Get given Role and User and add the Role to the User
        Entitlement role = entitlements.get(roleId);
        users.get(userId).addEntitlement(role);
    }

    @Override
    public Resource createResource(String id, String description, AuthToken authTokenForMethod)
    {
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;        
        
        Resource resource = new Resource(id, description);
        resources.put(resource.getId(), resource);
        
        return resource;
    }
    
    @Override
    public ResourceRole createResourceRole(String id, String name, String description, String resourceId, String entitlementId, AuthToken authTokenForMethod)
    {
        // TODO 
        
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;
        
        // TODO (if have time): Make sure to check that same resource isn't repeated (or something?)        
        
        // Get given Resource and Entitlement to add to the ResourceRole
        Resource resource = resources.get(resourceId);
        Entitlement entitlement = entitlements.get(entitlementId);
        ResourceRole resourceRole = new ResourceRole(id, name, description, resource, entitlement);
        entitlements.put(resourceRole.getId(), resourceRole);
        
        return resourceRole;
    }

    @Override
    public void addResourceRoleToUser(String userId, String resourceRoleId, AuthToken authTokenForMethod)
    {
        // TODO
        
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return;
    }

    @Override
    public AuthToken obtainAuthToken(String credentialId, String credentialValue)
    {
        // TODO               
      
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
                throw new AuthenticatorException("AuthenticationException", "obtain AuthToken", "credential id and/or credential value are invalid");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }           
        }
        
        // If User has a valid AuthToken, retrieve it
        AuthToken authToken = null;
        for (Entry<String, AuthToken> authTokenEntry : userOfCredential.getAuthTokens().entrySet())
        {
            if (authTokenEntry.getValue().isActive() == true)
            {
                authToken = authTokenEntry.getValue();
            }
        }
        
        // If User has no AuthTokens or valid ones, create one
        if (authToken == null)
        {
            while (authTokenIdsUsed.contains(Integer.toString(suggestedId)))
                suggestedId++;        
            authToken = new AuthToken(Integer.toString(suggestedId), this);
            
            // Add now-used AuthToken id to used id's list and increment suggestedId for next AuthToken
            authTokenIdsUsed.add(Integer.toString(suggestedId));
            suggestedId++;
            
            // Add newly created AuthToken to User's list of AuthTokens
            userOfCredential.addAuthToken(authToken);
        }       
        
        // Return AuthToken
        return authToken;
    }

    @Override
    public void logout(AuthToken authToken)
    {
        // TODO 
        
        // Validate given AuthToken
        Boolean foundAuthToken = false;
        for (Entry<String, User> userEntry : users.entrySet())
        {            
            for (Entry<String, AuthToken> authTokenEntry : userEntry.getValue().getAuthTokens().entrySet())
            {
                if (authTokenEntry.getValue().equals(authToken) && (authTokenEntry.getValue().isActive() == true))
                {
                    foundAuthToken = true;
                    break;
                }
            }    
        }
        
        // Throw InvalidAuthTokenException if AuthToken not found or null
        if ((foundAuthToken == false) || (authToken == null))
        {
            try
            {              
                throw new AuthenticatorException("InvalidAuthTokenException", "logout", "Invalid AuthToken; not logged out");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }           
        }
        
        // Invalidate AuthToken
        authToken.setActive(false, myAuthToken);
    }
        
    @Override
    public Boolean hasPermission(String permissionId, AuthToken authToken)
    {
        // TODO   
        
        // Find AuthToken and User associated with it
        Boolean foundAuthToken = false;
        User userOfAuthToken = null;
        for (Entry<String, User> userEntry : users.entrySet())
        {            
            for (Entry<String, AuthToken> authTokenEntry : userEntry.getValue().getAuthTokens().entrySet())
            {
                if (authTokenEntry.getValue().equals(authToken) && (authTokenEntry.getValue().isActive() == true))
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
        
        // Throw InvalidAuthTokenException if AuthToken not found 
        if (foundAuthToken == false)
        {
            try
            {
                throw new AuthenticatorException("InvalidAuthTokenException", "check for \""+ permissionId +"\" permission", "Invalid AuthToken");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return false;
            }           
        }
        
        // Check if User of AuthToken has permission
        GetPermissions getPermissions = new GetPermissions(permissionId);        
        userOfAuthToken.acceptVisitor(getPermissions);
        
        // Throw exception if User doesn't have the Permission
        if (!getPermissions.getHasPermission())
        {
            try
            {
                throw new AuthenticatorException("AccessDeniedException", "check for \""+ permissionId +"\" permission", "User does not have permission");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());
                return false;
            }
        }
        
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
    
    // TODO: Delete later (for debugging purposes only)
    public AuthToken getMyAuthToken()
    {
        return myAuthToken;
    }
}

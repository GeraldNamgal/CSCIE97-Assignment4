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
        
        // Create Permission to use Authenticator API methods, and a Role for Authenticator API Users, and add both to entitlements list
        Permission permission = new Permission("useAuthenticatorAPI", "Use Authenticator API", "Use any of the restricted Authenticator API methods");
        entitlements.put(permission.getId(), permission);
        Role role = new Role("authenticatorAPIUserRole", "Authenticator API User Role", "Has all permissions of an Authenticator API user");
        entitlements.put(role.getId(), role);
        
        // Add permission to role and role to hardcodedUser 
        role.addEntitlement(permission);
        hardcodedUser.addEntitlement(role);       
        
        // Create a User for the Authenticator itself, add it to list of Users, and give it Credentials
        User authenticator = new User("authenticator", "The Authenticator");
        users.put(authenticator.getId(), authenticator);
        credential = new Credential(authenticator.getId() + "-pwd", "password", "password");
        authenticator.addCredential(credential);
        credential = new Credential(authenticator.getId() + "-vp", "voiceprint", "--voice:" + authenticator.getId() + "--");
        authenticator.addCredential(credential);
        credential = new Credential(authenticator.getId() + "-fp", "faceprint", "--face:" + authenticator.getId() + "--");
        authenticator.addCredential(credential);
        
        // Create permission to modify AuthToken's "active" attribute and add it to entitlements list 
        Permission authTokenPermission = new Permission("updateAuthTokenValid", "Update Valid on AuthToken", "Has permission to validate/invalidate AuthTokens");
        entitlements.put(authTokenPermission.getId(), authTokenPermission);
        
        // Give authTokenPermission to Authenticator User (only it has this special permission)
        authenticator.addEntitlement(authTokenPermission);
                
        // Get Authenticator User its special AuthToken
        myAuthToken = new AuthToken(MY_AUTHTOKEN_ID, this);
        authenticator.addAuthToken(myAuthToken);
        
        // Create a new HashSet to store and track used/processed AuthToken id's for managing AuthTokens
        authTokenIdsUsed = new HashSet<String>();
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
    public void addEntitlementToRole(String roleId, String entitlementId, AuthToken authTokenForMethod)
    {
        // TODO 
        
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return;
        
        // TODO (if have time): Check that given Permission and Role are valid objects         
        
        // Get given Permission and Role and add the Permission to the Role
        Entitlement entitlement = entitlements.get(entitlementId);
        Role role = (Role) entitlements.get(roleId);
        role.addEntitlement(entitlement);
    }

    @Override
    public User defineUser(String userId, String name, AuthToken authTokenForMethod)
    {       
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;
        
        // TODO (if have time): Check for duplicates before creating new User or nah (so can rewrite User easily)?
        
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
        
        // TODO (if have time): Check that given User is valid
        
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
    public void addEntitlementToUser(String userId, String entitlementId, AuthToken authTokenForMethod)
    {
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return;
        
        // TODO (if have time): Check that given User and Role are valid
        
        // Get given Role and User and add the Role to the User
        Entitlement entitlement = entitlements.get(entitlementId);
        users.get(userId).addEntitlement(entitlement);
    }

    @Override
    public Resource defineResource(String id, String description, AuthToken authTokenForMethod)
    {
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;        
        
        Resource resource = new Resource(id, description);
        resources.put(resource.getId(), resource);
        
        return resource;
    }
    
    @Override
    public ResourceRole defineResourceRole(String id, String name, String description, String entitlementId, String resourceId, AuthToken authTokenForMethod)
    {
        // TODO 
        
        // Check that given AuthToken has permission to access this method
        if (!hasPermission("useAuthenticatorAPI", authTokenForMethod))
            return null;
        
        // TODO (if have time): Check that given Resource and Entitlement are valid
        
        // Get given Resource and Entitlement to add to the ResourceRole
        Resource resource = resources.get(resourceId);
        Entitlement entitlement = entitlements.get(entitlementId);
        ResourceRole resourceRole = new ResourceRole(id, name, description, entitlement, resource);
        entitlements.put(resourceRole.getId(), resourceRole);
        
        return resourceRole;
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
                throw new AuthenticatorException("InvalidAuthTokenException", "logout", "invalid AuthToken; not logged out");
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
                throw new AuthenticatorException("InvalidAuthTokenException", "check for \""+ permissionId +"\" permission", "invalid AuthToken");
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
                throw new AuthenticatorException("AccessDeniedException", "check for \""+ permissionId +"\" permission", "user does not have permission");
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
    
    // TODO: For debugging (can delete later)
    public AuthToken getMyAuthToken()
    {
        return myAuthToken;
    }
    
    // TODO: For debugging (can delete later)
    public LinkedHashMap<String, Entitlement> getEntitlements()
    {
        return entitlements;
    }
}

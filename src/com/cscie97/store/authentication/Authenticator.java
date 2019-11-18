package com.cscie97.store.authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Authenticator implements StoreAuthenticationService, Visitable
{
    /* VARIABLES */

    private static final String HARDCODED_USER_USERNAME = "hardcodedUser";
    private static final String HARDCODED_USER_PASSWORD = "password";
    private final String MY_AUTHTOKEN_ID = "authenticatorAuthTokenId";
    private int suggestedId = 0;
    private HashSet<String> authTokenIdsUsed;
    private LinkedHashMap<String, User> users;
    private AuthToken myAuthToken;
    private LinkedHashMap<String, Entitlement> entitlements;
    private LinkedHashMap<String, Resource> resources;
    private LinkedHashMap<String, User> credentialUserIndexes;
   
    /* CONSTRUCTOR */
    
    public Authenticator()
    {      
        // Create list of Users, Entitlements, and Resources
        users = new LinkedHashMap<String, User>();
        entitlements = new LinkedHashMap<String, Entitlement>();
        resources = new LinkedHashMap<String, Resource>();
        credentialUserIndexes = new LinkedHashMap<String, User>();
        
        // Create a hardcoded User, add it to list of Users, and give it Credentials
        User hardcodedUser = new User("hardcodedUser", "Hardcoded User");
        users.put(hardcodedUser.getId(), hardcodedUser);
        Credential credential = new Credential(HARDCODED_USER_USERNAME, "password", hashCalculator(HARDCODED_USER_PASSWORD));
        hardcodedUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), hardcodedUser);
        credential = new Credential(hardcodedUser.getId(), "voiceprint", hashCalculator("--voice:" + hardcodedUser.getId() + "--"));
        hardcodedUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), hardcodedUser);
        credential = new Credential(hardcodedUser.getId(), "faceprint", hashCalculator("--face:" + hardcodedUser.getId() + "--"));
        hardcodedUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), hardcodedUser);
        
        // Create Permission to use Authenticator API methods, and a Role for Authenticator Admin Users, and add both to entitlements list
        Permission permission = new Permission("use Authenticator API", "Use Authenticator API", "Use any of the Authenticator API methods");
        entitlements.put(permission.getId(), permission);
        Role role = new Role("authenticator API Admin", "Authenticator API User Admin Role", "Has all permissions of an Authenticator API admin");
        entitlements.put(role.getId(), role);
        
        // Add permission to role and role to hardcodedUser 
        role.addEntitlement(permission);
        hardcodedUser.addEntitlement(role);       
        
        // Create a User for the Authenticator itself, add it to list of Users, and give it Credentials
        User authenticatorUser = new User("authenticator", "The Authenticator");
        users.put(authenticatorUser.getId(), authenticatorUser);
        credential = new Credential(authenticatorUser.getId(), "password", hashCalculator("password"));
        authenticatorUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), authenticatorUser);
        credential = new Credential(authenticatorUser.getId(), "voiceprint", hashCalculator("--voice:" + authenticatorUser.getId() + "--"));
        authenticatorUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), authenticatorUser);
        credential = new Credential(authenticatorUser.getId(), "faceprint", hashCalculator("--face:" + authenticatorUser.getId() + "--"));
        authenticatorUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), authenticatorUser);
        
        // Create permission to modify AuthToken's "active" attribute and add it to entitlements list 
        Permission authTokenPermission = new Permission("update AuthToken validity", "Update Valid on AuthToken", "Has permission to validate/invalidate AuthTokens");
        entitlements.put(authTokenPermission.getId(), authTokenPermission);
        
        // Give authTokenPermission to Authenticator User (only it has this special permission)
        authenticatorUser.addEntitlement(authTokenPermission);
                
        // Login authenticator (so service can modify AuthTokens)
        myAuthToken = new AuthToken(MY_AUTHTOKEN_ID, authenticatorUser, this);
        authenticatorUser.addAuthToken(myAuthToken);
        
        // Create a new HashSet to store and track used/processed AuthToken id's for managing AuthTokens
        authTokenIdsUsed = new HashSet<String>();
    }
    
    /* API METHODS */   
    
    @Override
    public Permission definePermission(String id, String name, String description, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return null;
                
        // Create Permission and add it to entitlements
        Permission permission = new Permission(id, name, description);
        entitlements.put(permission.getId(), permission);
        
        return permission;
    }

    @Override
    public Role defineRole(String id, String name, String description, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return null;
                
        // Create Role and add it to entitlements
        Role role = new Role(id, name, description);
        entitlements.put(role.getId(), role);
        
        return role;
    }

    @Override
    public void addEntitlementToRole(String roleId, String entitlementId, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return;
                
        // TODO (if have time): Check that given Permission and Role are valid objects         
        
        // Get given Permission and Role and add the Permission to the Role
        Entitlement entitlement = entitlements.get(entitlementId);
        Role role = (Role) entitlements.get(roleId);
        role.addEntitlement(entitlement);
    }

    @Override
    public User defineUser(String userId, String name, AuthTokenTuple authTokenTupleForMethod)
    {       
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return null;
                
        // TODO (if have time): Check for duplicates before creating new User or nah (so can rewrite User easily)?
        
        // Create a new User
        User user = new User(userId, name);
        
        // Add new User to list of Users
        users.put(user.getId(), user);
        
        return user;
    }

    @Override
    public void addUserCredential(String userId, String type, String value, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return;
                
        // TODO (if have time): Check that given User is valid
        
        // Create Credential
        Credential credential = new Credential(userId, type, hashCalculator(value));        
                
        // Add Credential to User's Credentials and to credentialUserIndexes list
        if (credential != null)
        {
            users.get(userId).addCredential(credential);
            credentialUserIndexes.put(credential.getId() + credential.getValue(), users.get(userId));
        }
    }

    @Override
    public void addEntitlementToUser(String userId, String entitlementId, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return;
                
        // TODO (if have time): Check that given User and Role are valid
        
        // Get given Role and User and add the Role to the User
        Entitlement entitlement = entitlements.get(entitlementId);
        users.get(userId).addEntitlement(entitlement);
    }

    @Override
    public Resource defineResource(String id, String description, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return null;
                
        Resource resource = new Resource(id, description);
        resources.put(resource.getId(), resource);
        
        return resource;
    }
    
    @Override
    public ResourceRole defineResourceRole(String id, String name, String description, String entitlementId, String resourceId, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
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
        // Get the User of the Credential
        User userOfCredential = credentialUserIndexes.get(credentialId + hashCalculator(credentialValue));       
        
        // Throw Authentication Exception if credentialId and/or credentialValue aren't found
        if (userOfCredential == null)
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
        
        // If User has no AuthTokens or no valid ones, create one
        if (authToken == null)
        {
            while (authTokenIdsUsed.contains(Integer.toString(suggestedId)))
                suggestedId++;        
            authToken = new AuthToken(Integer.toString(suggestedId), userOfCredential, this);
            
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
        // Throw InvalidAuthTokenException if AuthToken is null or inactive
        if ((authToken == null) || (authToken.isActive() == false))
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
        
        // TODO: Necessary? -- Also check if authToken.getUserOfAuthToken() == null ^
        
        // Invalidate AuthToken
        authToken.setActive(false, new AuthTokenTuple(myAuthToken));
    }
    
    @Override
    public void acceptVisitor(Visitor visitor)
    {        
        visitor.visitAuthenticator(this);
    }
    
    @Override
    public GetPermissionsVisitor getUserPermissions(AuthToken authToken)
    {
        // Throw InvalidAuthTokenException if AuthToken is null or inactive
        if (authToken == null || authToken.isActive().equals(false))
        {
            try
            {
                throw new AuthenticatorException("InvalidAuthTokenException", "get user permissions", "invalid AuthToken");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }           
        }
        
        // Get User associated with authToken
        User userOfAuthToken = authToken.getUserOfAuthToken();
        
        // TODO: Necessary? -- Throw PermissionException if user of AuthToken wasn't found
        if (userOfAuthToken == null)
        {
            try
            {
                throw new AuthenticatorException("InvalidAuthTokenException", "get user permissions", "user of AuthToken not found");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }           
        }
        
        // Check if User of AuthToken has permission
        GetPermissionsVisitor getPermissionsVisitor = new GetPermissionsVisitor(userOfAuthToken);        
        userOfAuthToken.acceptVisitor(getPermissionsVisitor);
        
        return getPermissionsVisitor;
    }  
    
    @Override
    public void printInventory()
    {
        PrintInventoryVisitor printInventory = new PrintInventoryVisitor();
        this.acceptVisitor(printInventory);
    }
    
    @Override
    public LinkedHashMap<String, User> getUsers()
    {
        return users;
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
   
    // Method: Creates a hash from String input (referenced https://www.baeldung.com/sha-256-hashing-java)
    public String hashCalculator(String originalString)
    {
        try
        {
            // Create message digest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Create hashed value
            byte[] encodedHash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));        
            
            // Convert hashed value from bytes to hexadecimal
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < encodedHash.length; i++)
            {
                String hex = Integer.toHexString(0xff & encodedHash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            
            // Return hash
            return hexString.toString();
        }
        
        catch (Exception exception)
        {
            exception.printStackTrace();    
            System.out.println(exception);            
            return null;
        }       
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

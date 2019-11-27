/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/* *
 * Authenticator class implements the StoreAuthentication and Visitable interfaces
 */
public class Authenticator implements StoreAuthenticationService, Visitable
{
    /* VARIABLES */

    private static final String HARDCODED_USER_USERNAME = "hardcodedUser-pwd";
    private static final String HARDCODED_USER_PASSWORD = "password";    
    private int suggestedId = 0;
    private HashSet<String> authTokenIdsUsed;
    private LinkedHashMap<String, User> users;
    private AuthToken myAuthToken;
    private LinkedHashMap<String, Entitlement> entitlements;
    private LinkedHashMap<String, Resource> resources;
    private LinkedHashMap<String, User> credentialUserIndexes;
    private Integer daysTimeLimit = 1;
   
    /* CONSTRUCTOR */
    
    /* *
     * Creates a new Authenticator. In its constructor, an initial hardcoded user is defined so that a client (e.g., the CommandProcessor)
     * can perform an initial login to obtain the permissions required to actually use the Authentication Service API methods since most
     * methods require an appropriate AuthToken to be used 
     * @param credentialUserIndexes The mapping from a User's credential id and value to the User object that contains their corresponding
     *                              Credential object
     */
    public Authenticator()
    {      
        // Create new Map of Users, Entitlements, and Resources managed by the Authentication Service
        users = new LinkedHashMap<String, User>();
        entitlements = new LinkedHashMap<String, Entitlement>();
        resources = new LinkedHashMap<String, Resource>();
        credentialUserIndexes = new LinkedHashMap<String, User>();
        
        // Create new HashSet to store and track used/processed AuthToken id's for managing AuthTokens
        authTokenIdsUsed = new HashSet<String>();
        
        // Create a hardcoded User, add it to list of Users, and give it Credentials
        User hardcodedUser = new User("hardcodedUser", "Hardcoded User");
        users.put(hardcodedUser.getId(), hardcodedUser);
        Credential credential = new Credential(HARDCODED_USER_USERNAME, "password", hashCalculator(HARDCODED_USER_PASSWORD));
        hardcodedUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), hardcodedUser);
        credential = new Credential(hardcodedUser.getId() + "-vp", "voiceprint", hashCalculator("--voice:" + hardcodedUser.getId() + "--"));
        hardcodedUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), hardcodedUser);
        credential = new Credential(hardcodedUser.getId() + "-fp", "faceprint", hashCalculator("--face:" + hardcodedUser.getId() + "--"));
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
        credential = new Credential(authenticatorUser.getId() + "-pwd", "password", hashCalculator("password"));
        authenticatorUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), authenticatorUser);
        credential = new Credential(authenticatorUser.getId() + "-vp", "voiceprint", hashCalculator("--voice:" + authenticatorUser.getId() + "--"));
        authenticatorUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), authenticatorUser);
        credential = new Credential(authenticatorUser.getId() + "-fp", "faceprint", hashCalculator("--face:" + authenticatorUser.getId() + "--"));
        authenticatorUser.addCredential(credential);
        credentialUserIndexes.put(credential.getId() + credential.getValue(), authenticatorUser);
        
        // Create permission to modify AuthToken's "active" attribute and add it to entitlements list 
        Permission authTokenPermission = new Permission("update AuthToken validity", "Update Valid on AuthToken", "Has permission to validate/invalidate AuthTokens");
        entitlements.put(authTokenPermission.getId(), authTokenPermission);
        
        // Give authTokenPermission to Authenticator User (only it has this special permission)
        authenticatorUser.addEntitlement(authTokenPermission);
                
        // Login authenticator (so it can modify AuthTokens) and save the AuthToken
        myAuthToken = login("authenticator-pwd", "password");       
    }
    
    /* API METHODS */   
    
    /* *
     * Defines a permission to access a particular behavior or aspect of the Store 24X7 System
     */
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

    /* *
     * Defines a role a user can have in relation to the Store 24X7 System
     */
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

    /* *
     * Adds an Entitlement (Role, ResourceRole, or Permission) to a Role (or ResourceRole)
     */
    @Override
    public void addEntitlementToRole(String roleId, String entitlementId, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return;
                      
        // Get given Permission and Role and add the Permission to the Role
        Entitlement entitlement = entitlements.get(entitlementId);
        Role role = (Role) entitlements.get(roleId);
        role.addEntitlement(entitlement);
    }

    /* *
     * Defines a user of the Store 24X7 System
     */
    @Override
    public User defineUser(String userId, String name, AuthTokenTuple authTokenTupleForMethod)
    {       
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return null;
        
        // Create a new User
        User user = new User(userId, name);
        
        // Add new User to list of Users
        users.put(user.getId(), user);
        
        return user;
    }

    /* *
     * Gives the user a credential (e.g., to login / obtain an AuthToken)
     */
    @Override
    public void addUserCredential(String userId, String type, String value, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return;
        
        // Create Credential
        Credential credential = null;
        if (type.equals("password"))
            credential = new Credential(userId + "-pwd", type, hashCalculator(value));
        if (type.equals("voiceprint"))
            credential = new Credential(userId + "-vp", type, hashCalculator(value));
        if (type.equals("faceprint"))
            credential = new Credential(userId + "-fp", type, hashCalculator(value));        
                
        // Add Credential to User's Credentials and to credentialUserIndexes list
        if (credential != null)
        {
            users.get(userId).addCredential(credential);
            credentialUserIndexes.put(credential.getId() + credential.getValue(), users.get(userId));
        }
    }

    /* *
     * Adds an Entitlement (Role, ResourceRole, or Permission) to a User
     */
    @Override
    public void addEntitlementToUser(String userId, String entitlementId, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return;
        
        // Get given Role and User and add the Role to the User
        Entitlement entitlement = entitlements.get(entitlementId);
        users.get(userId).addEntitlement(entitlement);
    }

    /* *
     * Defines a resource in the Store 24X7 System (e.g., for ResourceRoles)
     */
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
    
    /* *
     * Defines a ResourceRole
     * @param resourceId The id of the Resource to be associated with
     * @param entitlementId The id of the associated Entitlement (Role, ResourceRole, or Permission)
     */
    @Override
    public ResourceRole defineResourceRole(String id, String name, String description, String entitlementId, String resourceId, AuthTokenTuple authTokenTupleForMethod)
    {
        // Check that given AuthToken has permission to access this method
        GetPermissionsVisitor getPermissionsVisitor = getUserPermissions(authTokenTupleForMethod.getAuthToken());
        if ((getPermissionsVisitor == null) || !getPermissionsVisitor.hasPermission(authTokenTupleForMethod.getPermissionTuple().setPermissionId("use Authenticator API")))
            return null;
        
        // Get given Resource and Entitlement to add to the ResourceRole
        Resource resource = resources.get(resourceId);
        Entitlement entitlement = entitlements.get(entitlementId);
        ResourceRole resourceRole = new ResourceRole(id, name, description, entitlement, resource);
        entitlements.put(resourceRole.getId(), resourceRole);
        
        return resourceRole;
    }   
    
    /* *
     * Logs a User in
     * @param credentialId E.g., the username, voiceprint id, or faceprint id of the User
     * @param credentialValue E.g., the password, voiceprint, or faceprint of the User
     * @return An AuthToken
     */
    @Override
    public AuthToken login(String credentialId, String credentialValue)
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
        
        // If User has a valid AuthToken and it's not expired, retrieve it
        AuthToken authToken = null;
        for (Entry<String, AuthToken> authTokenEntry : userOfCredential.getAuthTokens().entrySet())
        {     
            // Check authToken's inactivity and expiration
            Boolean expirationPassed = checkIfExpired(authTokenEntry.getValue());
            Boolean inactivityElapsed = checkIfInactivityPassed(authTokenEntry.getValue());
            
            // If authToken is past expiration time or its inactivity time limit elapsed, invalidate it / log it out
            if (expirationPassed || inactivityElapsed)
                logout(authTokenEntry.getValue());
            
            // Otherwise, if authToken is valid, retrieve it
            else if (authTokenEntry.getValue().isActive() == true)
            {
                authToken = authTokenEntry.getValue();
                break;
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
        
        // Update AuthToken's lastUsedDate
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        authToken.setLastUsedDate(dtf.format(currentDateTime));
        
        // Return AuthToken
        return authToken;
    }

    /* *
     * Logs a User out (including invalidating their AuthToken)
     * @param authToken The AuthToken of the User to be invalidated
     */
    @Override
    public void logout(AuthToken authToken)
    {       
        // Throw InvalidAuthTokenException if AuthToken is null or inactive, or user isn't found
        if ((authToken == null) || (authToken.isActive() == false) || authToken.getUserOfAuthToken() == null)
        {
            try
            {              
                throw new AuthenticatorException("InvalidAuthTokenException", "logout", "invalid AuthToken");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return;
            }           
        }
        
        // Invalidate AuthToken
        authToken.setActive(false, new AuthTokenTuple(myAuthToken));
    }
    
    /* *
     * Implements the one Visitable interface method (for using Visitor design pattern)
     */
    @Override
    public void acceptVisitor(Visitor visitor)
    {        
        visitor.visitAuthenticator(this);
    }
    
    /* *
     * Retrieves a user's permissions
     * @param authToken The authToken of the user
     * @return A GetPermissionsVisitor object
     */
    @Override
    public GetPermissionsVisitor getUserPermissions(AuthToken authToken)
    {
        // If authToken's expiration date has passed or its lastUsedDate is more than allowed limit, throw exception
        if ((authToken != null) && (checkIfExpired(authToken) || checkIfInactivityPassed(authToken)))
        {
            // Inactivate/logout authToken
            logout(authToken);
            
            try
            {
                throw new AuthenticatorException("InvalidAuthTokenException", "get user permissions",
                        "AuthToken has expired or was too inactive; User \"" + authToken.getUserOfAuthToken() + "\" must login / renew AuthToken");
            }
            
            catch (AuthenticatorException exception)
            {
                System.out.println();
                System.out.print(exception.getMessage());      
                return null;
            }
        }
        
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
        
        // Update AuthToken's lastUsedDate
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        authToken.setLastUsedDate(dtf.format(currentDateTime));
        
        // Check if User of AuthToken has permission
        GetPermissionsVisitor getPermissionsVisitor = new GetPermissionsVisitor(userOfAuthToken);        
        this.acceptVisitor(getPermissionsVisitor);
        
        return getPermissionsVisitor;
    }  
    
    /* *
     * Prints to stdout objects and sub-objects of interest in the Authentication Service including Users, Credentials,
     * AuthTokens, and Entitlements 
     */
    @Override
    public void printInventory()
    {
        PrintInventoryVisitor printInventoryVisitor = new PrintInventoryVisitor();
        this.acceptVisitor(printInventoryVisitor);
    }
    
    /* *
     * Retrieves the Users in the Authentication Service
     */
    @Override
    public LinkedHashMap<String, User> getUsers()
    {
        return users;
    }
    
    /* UTILITY METHODS */
    
    /* *
     * Returns the username for the hardcoded user (used in initial startup of Authentication Service)
     */
    public static String getHardcodedUserUsername()
    {
        return HARDCODED_USER_USERNAME;
    }    
    
    /* *
     * Returns the password for the hardcoded user (used in initial startup of Authentication Service)
     */
    public static String getHardcodedUserPassword()
    {
        return HARDCODED_USER_PASSWORD;
    }
    
    /* *
     * Checks if an AuthToken is past its expiration date
     */
    public Boolean checkIfExpired(AuthToken authToken)
    {
        Boolean expirationPassed = false;        
        Date expirationDate = null;
        Date todaysDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        
        try
        {
            expirationDate = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss").parse(authToken.getExpirationDate());
        }
        
        catch (ParseException e)
        {
            e.printStackTrace();
        } 
        
        if ((todaysDate != null) && todaysDate.after(expirationDate))        
            expirationPassed = true;
        
        return expirationPassed;
    }
    
    /* *
     * Checks if an AuthToken is inactive for longer than it's allowed
     */
    public Boolean checkIfInactivityPassed(AuthToken authToken)
    {
        Boolean inactivityElapsed = false;
        Date lastUsedDate = null;
        Date todaysDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        
        try
        {
            lastUsedDate = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss").parse(authToken.getLastUsedDate());
        }
        
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        // referenced code from https://javarevisited.blogspot.com/2015/07/how-to-find-number-of-days-between-two-dates-in-java.html
        if (todaysDate != null)
        {
            long differenceInDays =  (todaysDate.getTime()-lastUsedDate.getTime())/86400000;
            
            if (Math.abs(differenceInDays) > daysTimeLimit)            
                inactivityElapsed = true;         
            
        }
        
        return inactivityElapsed;
    }
   
    /* *
     * Creates a hash from String input (referenced https://www.baeldung.com/sha-256-hashing-java)
     */
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
}

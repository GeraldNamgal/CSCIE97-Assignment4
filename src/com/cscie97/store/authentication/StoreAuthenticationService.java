package com.cscie97.store.authentication;

import java.util.LinkedHashMap;

public interface StoreAuthenticationService
{
    Permission definePermission(String id, String name, String description, AuthToken authTokenForMethod);
    Role defineRole(String id, String name, String description, AuthToken authTokenForMethod);    
    void addEntitlementToRole(String roleId, String entitlementId, AuthToken authTokenForMethod);
    void addEntitlementToUser(String userId, String roleId, AuthToken authTokenForMethod);    
    Resource defineResource(String id, String description, AuthToken authTokenForMethod);    
    ResourceRole defineResourceRole(String id, String name, String description, String entitlementId, String resourceId, AuthToken authTokenForMethod);    
    User defineUser(String id, String name, AuthToken authTokenForMethod);
    void addUserCredential(String userId, String type, String value, AuthToken authTokenForMethod);      
    AuthToken obtainAuthToken(String credentialId, String credentialValue);
    void logout(AuthToken authToken);
    Boolean hasPermission(String permissionId, AuthToken authToken);
    void printInventory();
    LinkedHashMap<String, User> getUsers();
    
    // TODO: For debugging (can delete later)
    AuthToken getMyAuthToken();
    LinkedHashMap<String, Entitlement> getEntitlements();
}

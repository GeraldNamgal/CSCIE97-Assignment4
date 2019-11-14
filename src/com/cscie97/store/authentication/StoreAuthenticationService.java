package com.cscie97.store.authentication;

public interface StoreAuthenticationService
{
    Permission definePermission(String id, String name, String description, AuthToken authTokenForMethod);
    Role defineRole(String id, String name, String description, AuthToken authTokenForMethod);
    void addPermissionToRole(String roleId, String permissionId, AuthToken authTokenForMethod);
    User createUser(String id, String name, AuthToken authTokenForMethod);
    void addUserCredential(String userId, String type, String value, AuthToken authTokenForMethod);
    void addRoleToUser(String userId, String roleId, AuthToken authTokenForMethod);
    Resource createResource(String id, String description, AuthToken authTokenForMethod);
    ResourceRole createResourceRole(String id, String name, String description, String resourceId, String entitlementId, AuthToken authTokenForMethod);
    void addResourceRoleToUser(String userId, String resourceRoleId, AuthToken authTokenForMethod);
    AuthToken obtainAuthToken(String credentialId, String credentialValue);
    void logout(AuthToken authToken);
    Boolean hasPermission(String permissionId, AuthToken authToken);
    
    // TODO: Delete later (for debugging purposes only)
    AuthToken getMyAuthToken();
}

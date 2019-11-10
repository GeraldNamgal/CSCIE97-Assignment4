package com.cscie97.store.authentication;

public interface StoreAuthenticationService
{
    Permission definePermission(String id, String name, String description);
    Role defineRole(String id, String name, String description);
    void addPermission(String roleId, String permissionId);
    User createUser(String id, String name);
    void addUserCredential(String userId, String type, String value);
    void addRoleToUser(String userId, String roleId);
    ResourceRole createResourceRole(String id, String roleId, String resourceId);
    void addResourceRoleToUser(String userId, String resourceRoleId);
    AuthToken obtainAuthToken(String credentialId, String credentialValue);
}

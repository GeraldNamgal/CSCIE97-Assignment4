/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

import java.util.LinkedHashMap;

/* *
 * Defines the interface API for the Store Authentication Service
 */
public interface StoreAuthenticationService
{
    Permission definePermission(String id, String name, String description, AuthTokenTuple authTokenTupleForMethod);
    Role defineRole(String id, String name, String description, AuthTokenTuple authTokenTupleForMethod);    
    void addEntitlementToRole(String roleId, String entitlementId, AuthTokenTuple authTokenTupleForMethod);
    void addEntitlementToUser(String userId, String roleId, AuthTokenTuple authTokenTupleForMethod);    
    Resource defineResource(String id, String description, AuthTokenTuple authTokenTupleForMethod);    
    ResourceRole defineResourceRole(String id, String name, String description, String entitlementId, String resourceId, AuthTokenTuple authTokenTupleForMethod);    
    User defineUser(String id, String name, AuthTokenTuple authTokenTupleForMethod);
    void addUserCredential(String userId, String type, String value, AuthTokenTuple authTokenTupleForMethod);      
    AuthToken login(String credentialId, String credentialValue);
    void logout(AuthToken authToken);
    GetPermissionsVisitor getUserPermissions(AuthToken authToken);
    void printInventory();
    
    // Getters and Setters
    LinkedHashMap<String, User> getUsers(); // Used in PrintInventory
}

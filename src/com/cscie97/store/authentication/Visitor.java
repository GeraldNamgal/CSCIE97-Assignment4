/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

/* *
 * Defines the Visitor interface, i.e., for Visitor design pattern usage
 */
public interface Visitor
{
    void visitAuthenticator(StoreAuthenticationService authenticator);
    void visitUser(User user);
    void visitRole(Role role);
    void visitResourceRole(ResourceRole rRole);
    void visitPermission(Permission permission);    
}

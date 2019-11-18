package com.cscie97.store.authentication;

public interface Visitor
{
    void visitAuthenticator(Authenticator authenticator);
    void visitUser(User user);
    void visitRole(Role role);
    void visitResourceRole(ResourceRole rRole);
    void visitPermission(Permission permission);    
}
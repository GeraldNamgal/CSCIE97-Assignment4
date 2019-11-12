package com.cscie97.store.authentication;

public interface EntitlementVisitor
{
    void visitRole(Role role);
    void visitResourceRole(ResourceRole rRole);
    void visitPermission(Permission permission);
    void visitUser(User user);
}

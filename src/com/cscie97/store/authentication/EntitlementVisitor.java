package com.cscie97.store.authentication;

public interface EntitlementVisitor
{
    void visitResourceRole(ResourceRole rRole);
    void visitPermission(Permission permission);
}

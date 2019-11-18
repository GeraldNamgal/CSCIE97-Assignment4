package com.cscie97.store.authentication;

import java.util.ArrayList;

public class PermissionTuple
{
    /* Variables */
    
    private String permissionId;
    private ArrayList<String> resourceIds;
    private ArrayList<String> roleIds;
    
    /* Constructors */
    
    public PermissionTuple(String permissionId)
    {
        this.permissionId = permissionId;
        resourceIds = new ArrayList<String>();
        roleIds = new ArrayList<String>();
    }
    
    public PermissionTuple()
    {
        resourceIds = new ArrayList<String>();
        roleIds = new ArrayList<String>();
    }
    
    /* Methods */
    
    public void addResourceId(String resourceId)
    {
        resourceIds.add(resourceId);
    }
    
    public void addRoleId(String roleId)
    {
        roleIds.add(roleId);
    }

    /* GETTERS AND SETTERS */
    
    public String getPermissionId()
    {
        return permissionId;
    }
    
    public PermissionTuple setPermissionId(String id)
    {
        permissionId = id;
        return this;
    }

    public ArrayList<String> getResourceIds()
    {
        return resourceIds;
    }

    public ArrayList<String> getRoleIds()
    {
        return roleIds;
    }    
}

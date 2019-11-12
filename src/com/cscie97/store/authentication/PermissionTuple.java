package com.cscie97.store.authentication;

import java.util.ArrayList;

public class PermissionTuple
{
    private String permissionId;
    private ArrayList<String> resourceIds;
    private ArrayList<String> roleIds;
    
    public PermissionTuple(String permissionId)
    {
        this.permissionId = permissionId;
    }
    
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

    public ArrayList<String> getResourceIds()
    {
        return resourceIds;
    }

    public ArrayList<String> getRoleIds()
    {
        return roleIds;
    }    
}

/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

import java.util.ArrayList;

/* *
 * Wraps a permission with its associated resources and roles in a container using their id's
 */
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

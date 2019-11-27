/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

/* *
 * Represents a ResourceRole, e.g., that Users can have. Extends Entitlement (so can be used in a Composite design pattern as a composite).
 * Restricts an entitlement to a particular resource
 */
public class ResourceRole extends Role
{
    /* Variables */
    
    private Resource resource;
    
    /* Constructor */
    
    public ResourceRole(String id, String name, String description, Entitlement entitlement, Resource resource)
    {
        super(id, name, description);  
        
        this.resource = resource;
        entitlements.put(entitlement.getId(), entitlement);
    }
    
    /* Getters and Setters */
    
    public Resource getResource()
    {
        return resource;
    }

    @Override
    public void acceptVisitor(Visitor visitor)
    {
        visitor.visitResourceRole(this);
    }
}

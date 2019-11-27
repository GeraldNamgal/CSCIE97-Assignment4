/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

/* *
 * Represents a permission, e.g., that Users can have. Extends Entitlement (so can be used in a Composite design pattern as a leaf)
 */
public class Permission extends Entitlement
{   
    public Permission(String id, String name, String description)
    {
        super(id, name, description);        
    }

    @Override
    public void acceptVisitor(Visitor visitor)
    {
        visitor.visitPermission(this);
    }   
}

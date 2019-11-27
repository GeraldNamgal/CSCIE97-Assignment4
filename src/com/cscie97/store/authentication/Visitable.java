/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.authentication;

/* *
 * Defines the Visitable interface, i.e., for Visitor design pattern usage
 */
public interface Visitable
{
    void acceptVisitor(Visitor visitor);
}

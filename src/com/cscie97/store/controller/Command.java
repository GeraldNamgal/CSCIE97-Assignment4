/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.controller;

import com.cscie97.store.model.Sensor;

/* *
 * Command class that represents the command event(s) classification gleaned from a specific type of update event.
 * It also represents the command in the Command design pattern
 */
public abstract class Command
{
    /* Variables */
    
    protected Sensor sourceDevice;    
    
    /* Constructor */
    
    public Command(Sensor sourceDevice)
    {
        this.sourceDevice = sourceDevice;               
    }
    
    /* Methods */
    
    /* *
     * Executes the actions for a Command
     */
    public abstract void execute();      
}

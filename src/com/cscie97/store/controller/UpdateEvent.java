/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.controller;

import com.cscie97.store.model.Sensor;

/* *
 * UpdateEvent class that represents the even that's passed from subject to observer in the Observer design
 * pattern. It comprises of the source device that created the event and what the event actually is in a
 * string array format
 *
 * Referenced https://www.vogella.com/tutorials/DesignPatternObserver/article.html
 */
public class UpdateEvent
{
    private Sensor sourceDevice;
    private String[] perceivedEvent;
    
    public UpdateEvent(Sensor sourceDevice, String[] eventToSend)
    {
        this.sourceDevice = sourceDevice;
        this.perceivedEvent = eventToSend;
    }

    /* Getters and Setters */
    
    public Sensor getSourceDevice()
    {
        return sourceDevice;
    }   

    public String[] getPerceivedEvent()
    {
        return perceivedEvent;
    }     
}
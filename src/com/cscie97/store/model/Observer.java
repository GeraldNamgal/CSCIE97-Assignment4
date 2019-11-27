/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.model;

import com.cscie97.store.controller.UpdateEvent;

/* *
 * Observer interface that represents the observer in the Observer design pattern. It has one method that
 * receives UpdateEvents from a subject it's registered with 
 * 
 * Referenced https://www.vogella.com/tutorials/DesignPatternObserver/article.html
 */
public interface Observer
{
    void update(UpdateEvent event);
}

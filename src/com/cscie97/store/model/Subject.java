/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 4
 */

package com.cscie97.store.model;

/* *
 * Subject interface that represents the subject in the Observer design pattern. It has three methods that
 * register, deregister, and notify Observers
 * 
 * Referenced https://www.vogella.com/tutorials/DesignPatternObserver/article.html 
 */
public interface Subject
{
    void registerObserver(Observer newObserver);
    void deregisterObserver(Observer observerToRemove);
    void notifyObservers(Sensor sourceDevice, String[] eventToSend);
}

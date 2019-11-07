/* *
 * Gerald Arocena
 * CSCI E-97
 * Professor: Eric Gieseke
 * Assignment 3
 */

package com.cscie97.store.model;

/* *
 * Customer class that represents a customer that shops at stores
 */
public class Customer
{
    /* API Variables */
    
    enum AgeGroup
    {
        adult, child;
    }
    
    public enum Type 
    { 
        registered, guest; 
    }
    
    private String id;
    private String firstName;
    private String lastName;
    private AgeGroup ageGroup;
    private Type type;
    private String emailAddress;
    private String account;
    private String location;
    private String timeLastSeen;
    
    /* Constructor */
    
    /* *
     * Creates a new Customer
     * @param type The type of customer (registered | guest)
     * @param account The blockchain address of the customer for billing
     */
    public Customer(String id, String firstName, String lastName, AgeGroup ageGroup, Type type, String emailAddress, String account)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ageGroup = ageGroup;
        this.type = type;
        this.emailAddress = emailAddress;
        this.account = account;
    } 
    
    /* Utility Methods */
    
    /* *
     *  Checks a string if it's a Type enum
     */
    public static boolean containsTypeEnum(String testString)
    {
        for (Type type : Type.values())
        {
            if (type.name().equals(testString))
            {
                return true;
            }
        }

        return false;
    }
    
    /* *
     *  Checks a string if it's an AgeGroup enum
     */
    public static boolean containsAgeGroupEnum(String testString)
    {
        for (AgeGroup ageGroup : AgeGroup.values())
        {
            if (ageGroup.name().equals(testString))
            {
                return true;
            }
        }

        return false;
    }

    /* Getters and Setters */
    
    public String getId() 
    {
        return id;
    }   

    public String getFirstName() 
    {
        return firstName;
    }
    
    public String getLastName() 
    {
        return lastName;
    }   

    public Type getType() 
    {
        return type;
    }   

    public String getEmailAddress() 
    {
        return emailAddress;
    }   

    public String getAccount() 
    {
        return account;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String storeAisleLoc)
    {
        this.location = storeAisleLoc;
    }

    public AgeGroup getAgeGroup()
    {
        return ageGroup;
    }

    public String getTimeLastSeen()
    {
        return timeLastSeen;
    }

    public void setTimeLastSeen(String timeLastSeen)
    {
        this.timeLastSeen = timeLastSeen;
    }    
}

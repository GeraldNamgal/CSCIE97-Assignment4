package com.cscie97.store.authentication;

public class Credential
{
    private String id;
    private String type;
    private Voiceprint voiceprint;
    private Faceprint faceprint;
    private Password password;
    
    // TODO: Store credential as a hash
    public Credential(String id, String type, String value)
    {
        this.id = id;
        this.type = type;
        
        if (type.equals("voiceprint"))
        {
            voiceprint = new Voiceprint(value);
        }
        
        if (type.equals("faceprint"))
        {
            faceprint = new Faceprint(value);
        }
        
        if (type.equals("password"))
        {
            password = new Password(value);
        }
    }
    
    public class Voiceprint
    {
        private String vp;
        
        private Voiceprint(String vp)
        {
            this.vp = vp;
        }

        public String getVp()
        {
            return vp;
        }       
    }
    
    public class Faceprint
    {
        private String fp;
        
        private Faceprint(String fp)
        {
            this.fp = fp;
        }
        
        public String getFp()
        {
            return fp;
        }
    }
    
    public class Password
    {
        private String pwd;
        
        private Password(String pwd)
        {
            this.pwd = pwd;
        }
        
        public String getPwd()
        {
            return pwd;
        }
    }
    
    /* Getters and Setters */
    
    public String getId()
    {
        return id;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getValue()
    {
        if (type.equals("password"))
        {
            return password.getPwd();
        }
            
        else if (type.equals("voiceprint"))
        {
            return voiceprint.getVp();
        }
                
        else if (type.equals("faceprint"))
        {
            return faceprint.getFp();
        }
        
        return null;
    }
}

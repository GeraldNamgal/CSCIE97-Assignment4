package com.cscie97.store.authentication;

public class Credential
{
    private String id;
    private String type;
    private VoicePrint voiceprint;
    private FacePrint faceprint;
    private Password password;
    
    // TODO: Store credential as a hash
    public Credential(String id, String type, String value)
    {
        this.id = id;
        this.type = type;
        
        if (type.equals("voiceprint"))
        {
            voiceprint = new VoicePrint(value);
        }
        
        if (type.equals("faceprint"))
        {
            faceprint = new FacePrint(value);
        }
        
        if (type.equals("password"))
        {
            password = new Password(value);
        }
    }
    
    public class VoicePrint
    {
        private String vp;
        
        private VoicePrint(String vp)
        {
            this.vp = vp;
        }

        public String getVp()
        {
            return vp;
        }       
    }
    
    public class FacePrint
    {
        private String fp;
        
        private FacePrint(String fp)
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
}

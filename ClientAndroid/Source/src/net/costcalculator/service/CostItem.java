/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

/**
 * This class represents business part of one record
 * in the database table 'cost_items'.
 * 
 * @author Aliaksei Plashchanski
 *
 */
public class CostItem
{
    public CostItem()
    {
        id_ = 0;
        version_ = 0;
        name_ = "";
        guid_ = "";
        creationTime_ = "";
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("id_=").append(id_).append("; ");
        sb.append("guid_=").append(guid_).append("; ");
        sb.append("name_=").append(name_).append("; ");
        sb.append("creationTime=").append(creationTime_).append("; ");
        sb.append("version=").append(version_).append("; ");
        
        return sb.toString();
    }
    
    public boolean isValid()
    {
        return name_.length() > 0 && guid_.length() > 0;
    }
    
    public void setName(String name)
    {
        name_ = name;
    }
    
    public String getName()
    {
        return name_;
    }

    public void setGuid(String guid)
    {
        this.guid_ = guid;
    }

    public String getGuid()
    {
        return guid_;
    }

    public int getId()
    {
        return id_;
    }
    
    public void setId(int id)
    {
        id_ = id;
    }
    
    public void setCreationTime(String creationTime_)
    {
        this.creationTime_ = creationTime_;
    }

    public String getCreationTime()
    {
        return creationTime_;
    }

    public void setVersion(int version_)
    {
        this.version_ = version_;
    }

    public int getVersion()
    {
        return version_;
    }

    private String name_;
    private String guid_;
    private String creationTime_;
    private int id_;
    private int version_;
}

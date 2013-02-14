/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import net.costcalculator.util.LOG;

import org.json.simple.JSONObject;

/**
 * This class represents expenses category.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CostItem
{
    public static final String JSON_OBJECT_SIG_NAME  = "json_object_id";
    public static final String JSON_OBJECT_SIG_VALUE = "cost_item";
    public static final String ID                    = "1";
    public static final String VERSION               = "2";
    public static final String NAME                  = "3";
    public static final String GUID                  = "4";
    public static final String CT                    = "5";

    public CostItem()
    {
        id_ = 0;
        version_ = 0;
        name_ = "";
        guid_ = "";
        creationTime_ = "";
    }
    
    public void resetId()
    {
        id_ = 0;
    }

    public boolean fromJSON(JSONObject json)
    {
        if (json == null)
            return false;
        try
        {
            Object o = null;
            o = json.get(JSON_OBJECT_SIG_NAME);
            if (o == null || !o.equals(JSON_OBJECT_SIG_VALUE))
                return false;

            o = json.get(ID);
            if (o != null)
                id_ = ((Long) o).intValue();

            o = json.get(VERSION);
            if (o != null)
                version_ = ((Long) o).intValue();

            o = json.get(NAME);
            if (o != null)
                name_ = (String) o;

            o = json.get(GUID);
            if (o != null)
                guid_ = (String) o;

            o = json.get(CT);
            if (o != null)
                creationTime_ = (String) o;
        }
        catch (Exception e)
        {
            LOG.E("CostItem:fromJSON failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON()
    {
        JSONObject obj = new JSONObject();
        obj.put(JSON_OBJECT_SIG_NAME, JSON_OBJECT_SIG_VALUE);
        obj.put(ID, id_);
        obj.put(VERSION, version_);
        if (name_.length() > 0)
            obj.put(NAME, name_);
        if (guid_.length() > 0)
            obj.put(GUID, guid_);
        if (creationTime_.length() > 0)
            obj.put(CT, creationTime_);

        return obj;
    }

    public String toString()
    {
        return toJSON().toJSONString();
    }

    /**
     * Check if all required fields are filled
     * 
     * @return true - required fields are filled, false - otherwise
     */
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
    private int    id_;
    private int    version_;
}

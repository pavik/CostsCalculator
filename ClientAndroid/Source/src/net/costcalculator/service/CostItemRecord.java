/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.Calendar;
import java.util.Date;

import net.costcalculator.util.LOG;

import org.json.simple.JSONObject;

/**
 * This class represents expenses items in the category CostItem.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CostItemRecord
{
    public static final String  JSON_OBJECT_SIG_NAME  = "json_object_id";
    public static final String  JSON_OBJECT_SIG_VALUE = "cost_item_record";
    public static final String ID                    = "1";
    public static final String SUM                   = "2";
    public static final String VERSION               = "3";
    public static final String CT                    = "4";
    public static final String GUID                  = "5";
    public static final String GROUPGUID             = "6";
    public static final String CURRENCY              = "7";
    public static final String TAG                   = "8";
    public static final String COMMENT               = "9";

    public CostItemRecord()
    {
        id_ = 0;
        sum_ = 0.0;
        version_ = 0;
        creationTime_ = Calendar.getInstance().getTime();
        guid_ = "";
        groupGuid_ = "";
        currency_ = "";
        tag_ = "";
        comment_ = "";
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

            o = json.get(SUM);
            if (o != null)
                sum_ = (Double) o;

            o = json.get(VERSION);
            if (o != null)
                version_ = ((Long) o).intValue();

            o = json.get(CT);
            if (o != null)
                creationTime_ = new Date((Long) o);

            o = json.get(GUID);
            if (o != null)
                guid_ = (String) o;

            o = json.get(GROUPGUID);
            if (o != null)
                groupGuid_ = (String) o;

            o = json.get(CURRENCY);
            if (o != null)
                currency_ = (String) o;

            o = json.get(TAG);
            if (o != null)
                tag_ = (String) o;

            o = json.get(COMMENT);
            if (o != null)
                comment_ = (String) o;
        }
        catch (Exception e)
        {
            LOG.E("CostItemRecord:fromJSON failed: " + e.getMessage());
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
        obj.put(SUM, sum_);
        obj.put(VERSION, version_);
        obj.put(CT, creationTime_.getTime());
        if (guid_.length() > 0)
            obj.put(GUID, guid_);
        if (groupGuid_.length() > 0)
            obj.put(GROUPGUID, groupGuid_);
        if (currency_.length() > 0)
            obj.put(CURRENCY, currency_);
        if (tag_.length() > 0)
            obj.put(TAG, tag_);
        if (comment_.length() > 0)
            obj.put(COMMENT, comment_);

        return obj;
    }

    public String toString()
    {
        return toJSON().toJSONString();
    }

    public void setId(long id)
    {
        this.id_ = id;
    }

    public long getId()
    {
        return id_;
    }

    public void setGuid(String guid)
    {
        this.guid_ = guid;
    }

    public String getGuid()
    {
        return guid_;
    }

    public void setGroupGuid(String groupGuid)
    {
        this.groupGuid_ = groupGuid;
    }

    public String getGroupGuid()
    {
        return groupGuid_;
    }

    public void setCreationTime(Date creationTime)
    {
        this.creationTime_ = creationTime;
    }

    public Date getCreationTime()
    {
        return creationTime_;
    }

    public void setSum(double sum)
    {
        this.sum_ = sum;
    }

    public double getSum()
    {
        return sum_;
    }

    public void setCurrency(String currency)
    {
        this.currency_ = currency;
    }

    public String getCurrency()
    {
        return currency_;
    }

    public void setTag(String tag)
    {
        this.tag_ = tag;
    }

    public String getTag()
    {
        return tag_;
    }

    public void setComment(String comment)
    {
        this.comment_ = comment;
    }

    public String getComment()
    {
        return comment_;
    }

    public void setVersion(int version)
    {
        this.version_ = version;
    }

    public int getVersion()
    {
        return version_;
    }

    /**
     * Check if all required fields are filled
     * 
     * @return true - required fields are filled, false - otherwise
     */
    public boolean isValid()
    {
        return guid_.length() > 0 && groupGuid_.length() > 0;
    }

    public void incVersion()
    {
        version_ += 1;
    }

    private long   id_;
    private String guid_;
    private String groupGuid_;
    private Date   creationTime_;
    private double sum_;
    private String currency_;
    private String tag_;
    private String comment_;
    private int    version_;
}

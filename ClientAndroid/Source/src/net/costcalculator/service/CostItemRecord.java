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

/**
 * This class represents business part of one record
 * in the database table 'cost_item_records'.
 * 
 * @author Aliaksei Plashchanski
 *
 */
public class CostItemRecord
{
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
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("id_=").append(id_).append("; ");
        sb.append("guid_=").append(guid_).append("; ");
        sb.append("groupGuid=").append(groupGuid_).append("; ");
        sb.append("creationTime=").append(creationTime_.toGMTString()).append("; ");
        sb.append("sum=").append(sum_).append("; ");
        sb.append("currency=").append(currency_).append("; ");
        sb.append("tag=").append(tag_).append("; ");
        sb.append("comment=").append(comment_).append("; ");
        sb.append("version=").append(version_).append("; ");
        
        return sb.toString();
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

    public boolean isValid()
    {
        return guid_.length() > 0 && groupGuid_.length() > 0;
    }
    
    public void incVersion()
    {
        version_ += 1;
    }
    
    private long id_;
    private String guid_;
    private String groupGuid_;
    private Date creationTime_;
    private double sum_;
    private String currency_;
    private String tag_;
    private String comment_;
    private int version_;
}

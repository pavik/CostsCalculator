/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.costcalculator.util.LOG;
import net.costcalculator.db.SQLiteDbProvider;
import net.costcalculator.db.SQLiteQueries;

/**
 * Provides interface to store/retrieve cost items from/to storage.
 * 
 * Usage:
 * <code>
 * CostItemsService s = new CostItemsService(context);
 * s.some_request();
 * //... some other requests
 * s.release();
 * s = null;
 * </code>
 * 
 * @author Aliaksei Plashchanski
 *
 */
public class CostItemsService
{
    public CostItemsService(Context context)
    {
        LOG.T("CostItemsService::CostItemsService");
        dbprovider_ = new SQLiteDbProvider(context);
    }
    
    public void release()
    {
        LOG.T("CostItemsService::release");
        if (dbprovider_ != null)
        {
            dbprovider_.close();
            dbprovider_ = null;
        }
    }
    
    public CostItem createCostItem(String name) throws Exception
    {
    	LOG.T("CostItemsService::createCostItem");
        if (name.length() == 0)
            throw new Exception("invalid argument: name");
        
        CostItem result = null;
        final String guid = UUID.randomUUID().toString();
        
        SQLiteDatabase db = null;
        Cursor row = null;
        try
        {
            db = dbprovider_.getWritableDatabase();
            db.execSQL(SQLiteQueries.INSERT_COST_ITEM, new Object[] {guid, name});
            row = db.rawQuery(SQLiteQueries.GET_COST_ITEM, new String[] {guid});
            if (row.moveToFirst())
                result = fromCursor(row);
            else
                throw new Exception("failed to get cost item by guid: " + guid);
        }
        finally
        {
            if (db != null)
                db.close();
            if (row != null)
                row.close();
        }
        
        return result;
    }

    public CostItemRecord saveCostItemRecord(CostItemRecord rec) throws Exception
    {
    	LOG.T("CostItemsService::saveCostItemRecord");
        if (!rec.isValid())
            throw new Exception("invalid argument: rec");
        
        SQLiteDatabase db = null;
        try
        {
            db = dbprovider_.getWritableDatabase();
            if (rec.getId() > 0)
            {
                int affected = db.update(SQLiteQueries.COST_ITEM_RECORDS, getContent(rec),
                    SQLiteQueries.COST_ITEM_RECORDS_U, new String[] {
                    Long.toString(rec.getId()), Integer.toString(rec.getVersion())});
                
                if (affected == 1)
                    rec.incVersion();
                else
                    throw new Exception("failed to update cost item record: " + rec.toString());
            }
            else
            {
                long id = db.insert(SQLiteQueries.COST_ITEM_RECORDS, null, getContent(rec));
                rec.setId(id);
            }
        }
        finally
        {
            if (db != null)
                db.close();
        }
        
        return rec;
    }
    
    public CostItemRecord getCostItemRecord(long id) throws Exception
    {
    	LOG.T("CostItemsService::getCostItemRecord");
        if (id <= 0)
            throw new Exception("invalid argument: id = " + id);
        
        CostItemRecord cir = null;
        Cursor ds = null;
        SQLiteDatabase db = null;
        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteQueries.GET_COST_ITEM_RECORD_BY_ID, new String[] {Long.toString(id)});
            
            if (ds.moveToFirst())
            {
                cir = new CostItemRecord();
                cir.setId(id);
                cir.setGuid(ds.getString(ds.getColumnIndex(SQLiteQueries.COL_CIR_GUID)));
                cir.setGroupGuid(ds.getString(ds.getColumnIndex(SQLiteQueries.COL_CIR_CI_GUID)));
                cir.setSum(ds.getDouble(ds.getColumnIndex(SQLiteQueries.COL_CIR_SUM)));
                cir.setCurrency(ds.getString(ds.getColumnIndex(SQLiteQueries.COL_CIR_CURRENCY)));
                cir.setComment(ds.getString(ds.getColumnIndex(SQLiteQueries.COL_CIR_COMMENT)));
                cir.setTag(ds.getString(ds.getColumnIndex(SQLiteQueries.COL_CIR_TAG)));
                cir.setVersion(ds.getInt(ds.getColumnIndex(SQLiteQueries.COL_CIR_AUDIT_VERSION)));
                cir.setCreationTime(new Date(
                        ds.getLong(ds.getColumnIndex(SQLiteQueries.COL_CIR_DATETIME))));
            }
            else
                throw new Exception("failed to get cost item record by id = " + id);
        }
        finally
        {
            if (db != null)
                db.close();
            if (ds != null)
                ds.close();
        }
        
        return cir;
    }
    
    public ArrayList<Long> getCostItemRecordIds(CostItem ci) throws Exception
    {
    	LOG.T("CostItemsService::getCostItemRecordIds");
        if (!ci.isValid())
            throw new Exception("invalid argument: ci = " + ci.toString());
        
        ArrayList<Long> ids = new ArrayList<Long>();
        Cursor ds = null;
        SQLiteDatabase db = null;
        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteQueries.COST_ITEM_RECORDS_IDS, new String[] {ci.getGuid()});
            
            if (ds.moveToFirst())
            {
                ids = new ArrayList<Long>(ds.getCount());
                final int col = ds.getColumnIndex(SQLiteQueries.COL_CIR_ID);
                do
                {
                    ids.add(ds.getLong(col));
                } while (ds.moveToNext());
            }
        }
        finally
        {
            if (db != null)
                db.close();
            if (ds != null)
                ds.close();
        }
        
        return ids;
    }
    
    public CostItemRecord createCostItemRecord(String groupGuid, Date creationTime, double sum)
    {
    	LOG.T("CostItemsService::createCostItemRecord");
    	
        CostItemRecord item = new CostItemRecord();
        item.setGuid(UUID.randomUUID().toString());
        item.setGroupGuid(groupGuid);
        item.setCreationTime(creationTime);
        item.setSum(sum);
        
        return item;
    }
    
    ContentValues getContent(CostItemRecord rec)
    {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteQueries.COL_CIR_GUID, rec.getGuid());
        cv.put(SQLiteQueries.COL_CIR_CI_GUID, rec.getGroupGuid());
        cv.put(SQLiteQueries.COL_CIR_DATETIME, rec.getCreationTime().getTime());
        cv.put(SQLiteQueries.COL_CIR_SUM, rec.getSum());
        cv.put(SQLiteQueries.COL_CIR_CURRENCY, rec.getCurrency());
        cv.put(SQLiteQueries.COL_CIR_TAG, rec.getTag());
        cv.put(SQLiteQueries.COL_CIR_COMMENT, rec.getComment());
        return cv;
    }
    
    public void deleteCostItem(CostItem item) throws Exception
    {
    	LOG.T("CostItemsService::deleteCostItem");
        if (item.getId() <= 0)
            throw new Exception("invalid argument: item = " + item.toString());
        
        SQLiteDatabase db = null;
        try
        {
            db = dbprovider_.getWritableDatabase();
            db.execSQL(SQLiteQueries.DEL_COST_ITEM, new Object[] {item.getId()});
        }
        finally
        {
            if (db != null)
                db.close();
        }
    }
    
    public ArrayList<CostItem> getAllCostItems()
    {
    	LOG.T("CostItemsService::getAllCostItems");
    	
        SQLiteDatabase db = null;
        Cursor ds = null;
        ArrayList<CostItem> result = new ArrayList<CostItem>();
        
        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteQueries.GET_ALL_COST_ITEMS, null);
        
            if (ds.moveToFirst())
            {
                do
                {
                    result.add(fromCursor(ds));
                } while (ds.moveToNext());
            }
        }
        finally
        {
            if (ds != null)
                ds.close();
            if (db != null)
                db.close();
        }

        return result;
    }
    
    public void udpateCostItemUseCount(CostItem item) throws Exception
    {
    	LOG.T("CostItemsService::udpateCostItemUseCount");
        if (item.getId() <= 0)
            throw new Exception("invalid argument: item = " + item);
        
        SQLiteDatabase db = null;
        try
        {
            db = dbprovider_.getWritableDatabase();
            db.execSQL(SQLiteQueries.INC_USE_COUNT, new Object[] {item.getId()});
        }
        finally
        {
            if (db != null)
                db.close();
        }
    }
    
    private CostItem fromCursor(Cursor c)
    {
        CostItem item = new CostItem();
        item.setGuid(c.getString(c.getColumnIndex(SQLiteQueries.COL_CI_GUID)));
        item.setName(c.getString(c.getColumnIndex(SQLiteQueries.COL_CI_NAME)));
        item.setId(c.getInt(c.getColumnIndex(SQLiteQueries.COL_CI_ID)));
        item.setCreationTime(c.getString(c.getColumnIndex(SQLiteQueries.COL_CI_CREATION_TIME)));
        item.setVersion(c.getInt(c.getColumnIndex(SQLiteQueries.COL_CI_DATA_VERSION)));

        return item;
    }
    
    private SQLiteDbProvider dbprovider_;    
}

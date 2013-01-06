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
import java.util.HashMap;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import net.costcalculator.util.LOG;
import net.costcalculator.db.SQLiteDbProvider;
import net.costcalculator.db.SQLiteDbQueries;

/**
 * Provides singleton instance to interface for storing/retrieving cost items
 * from/to storage.
 * 
 * <pre>
 * Usage:
 * {@code
 * // initialization
 * CostItemsService.createInstance(context);
 * 
 * // get instance
 * CostItemsService.instance().some_request();
 * 
 * // release instance
 * CostItemsService.releaseInstance();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CostItemsService
{
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
            db.execSQL(SQLiteDbQueries.INSERT_COST_ITEM, new Object[] { guid,
                    name });
            row = db.rawQuery(SQLiteDbQueries.GET_COST_ITEM_BY_GUID,
                    new String[] { guid });
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

    public CostItemRecord saveCostItemRecord(CostItemRecord rec)
            throws Exception
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
                int affected = db.update(
                        SQLiteDbQueries.COST_ITEM_RECORDS,
                        getContent(rec),
                        SQLiteDbQueries.COST_ITEM_RECORDS_U,
                        new String[] { Long.toString(rec.getId()),
                                Integer.toString(rec.getVersion()) });

                if (affected == 1)
                    rec.incVersion();
                else
                    throw new Exception("failed to update cost item record: "
                            + rec.toString());
            }
            else
            {
                long id = db.insert(SQLiteDbQueries.COST_ITEM_RECORDS, null,
                        getContent(rec));
                rec.setId(id);

                if (cirCountCache_ != null)
                {
                    Integer n = cirCountCache_.get(rec.getGroupGuid());
                    if (n == null)
                        n = 0;
                    cirCountCache_.put(rec.getGroupGuid(), n + 1);
                }
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
            ds = db.rawQuery(SQLiteDbQueries.GET_COST_ITEM_RECORD_BY_ID,
                    new String[] { Long.toString(id) });

            if (ds.moveToFirst())
            {
                cir = new CostItemRecord();
                cir.setId(id);
                cir.setGuid(ds.getString(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_GUID)));
                cir.setGroupGuid(ds.getString(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_CI_GUID)));
                cir.setSum(ds.getDouble(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_SUM)));
                cir.setCurrency(ds.getString(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_CURRENCY)));
                cir.setComment(ds.getString(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_COMMENT)));
                cir.setTag(ds.getString(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_TAG)));
                cir.setVersion(ds.getInt(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_AUDIT_VERSION)));
                cir.setCreationTime(new Date(ds.getLong(ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_DATETIME))));
            }
            else
                throw new Exception("failed to get cost item record by id = "
                        + id);
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
            ds = db.rawQuery(SQLiteDbQueries.COST_ITEM_RECORDS_IDS,
                    new String[] { ci.getGuid() });

            if (ds.moveToFirst())
            {
                ids = new ArrayList<Long>(ds.getCount());
                final int col = ds.getColumnIndex(SQLiteDbQueries.COL_CIR_ID);
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

    public CostItemRecord createCostItemRecord(String groupGuid,
            Date creationTime, double sum)
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
        cv.put(SQLiteDbQueries.COL_CIR_GUID, rec.getGuid());
        cv.put(SQLiteDbQueries.COL_CIR_CI_GUID, rec.getGroupGuid());
        cv.put(SQLiteDbQueries.COL_CIR_DATETIME, rec.getCreationTime()
                .getTime());
        cv.put(SQLiteDbQueries.COL_CIR_SUM, rec.getSum());
        cv.put(SQLiteDbQueries.COL_CIR_CURRENCY, rec.getCurrency());
        cv.put(SQLiteDbQueries.COL_CIR_TAG, rec.getTag());
        cv.put(SQLiteDbQueries.COL_CIR_COMMENT, rec.getComment());
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
            db.execSQL(SQLiteDbQueries.DEL_COST_ITEM,
                    new Object[] { item.getId() });
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
            ds = db.rawQuery(SQLiteDbQueries.GET_ALL_COST_ITEMS, null);

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

    public CostItem getCostItemById(long id) throws Exception
    {
        LOG.T("CostItemsService::getCostItemById");

        SQLiteDatabase db = null;
        Cursor ds = null;
        CostItem result = null;

        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.GET_COST_ITEM_BY_ID,
                    new String[] { Long.toString(id) });

            if (ds.moveToFirst())
                result = fromCursor(ds);
            else
                throw new Exception("failed to get cost item by id: " + id);
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
            db.execSQL(SQLiteDbQueries.INC_USE_COUNT,
                    new Object[] { item.getId() });
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
        item.setGuid(c.getString(c.getColumnIndex(SQLiteDbQueries.COL_CI_GUID)));
        item.setName(c.getString(c.getColumnIndex(SQLiteDbQueries.COL_CI_NAME)));
        item.setId(c.getInt(c.getColumnIndex(SQLiteDbQueries.COL_CI_ID)));
        item.setCreationTime(c.getString(c
                .getColumnIndex(SQLiteDbQueries.COL_CI_CREATION_TIME)));
        item.setVersion(c.getInt(c
                .getColumnIndex(SQLiteDbQueries.COL_CI_DATA_VERSION)));

        return item;
    }

    public HashMap<String, Integer> getCostItemRecordsCount()
    {
        LOG.T("CostItemsService::getCostItemRecordsCount");
        if (cirCountCache_ != null)
            return cirCountCache_;

        SQLiteDatabase db = null;
        Cursor ds = null;
        cirCountCache_ = new HashMap<String, Integer>();

        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.GET_COUNT_COST_ITEM_RECORDS, null);

            if (ds.moveToFirst())
            {
                int keyCol = ds.getColumnIndex(SQLiteDbQueries.COL_CIR_CI_GUID);
                int valCol = ds.getColumnIndex(SQLiteDbQueries.EXPR_CIR_COUNT);
                do
                {
                    cirCountCache_.put(ds.getString(keyCol), ds.getInt(valCol));
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

        return cirCountCache_;
    }

    public ArrayList<Date> getExpensesMonths()
    {
        LOG.T("CostItemsService::getExpensesMonths");

        ArrayList<Date> dates = getExpensesDates();
        ArrayList<Date> months = new ArrayList<Date>();

        int n = 0;
        for (int i = 0; i < dates.size(); ++i)
        {
            Date d = dates.get(i);
            d.setDate(1);
            d.setHours(0);
            d.setMinutes(0);
            d.setSeconds(0);
            if (i == 0)
            {
                months.add(d);
                ++n;
            }
            else if (!monthsAreEqual(months.get(n - 1), d))
            {
                months.add(d);
                ++n;
            }
        }

        return months;
    }

    public ArrayList<Date> getExpensesDates()
    {
        LOG.T("CostItemsService::getExpensesDates");

        SQLiteDatabase db = null;
        Cursor ds = null;
        ArrayList<Date> result = new ArrayList<Date>();

        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.GET_EXPENSES_DATES, null);

            if (ds.moveToFirst())
            {
                int i = 0;
                int colDatetime = ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_DATETIME);
                do
                {
                    Date d = new Date(ds.getLong(colDatetime));
                    if (i == 0)
                    {
                        result.add(d);
                        ++i;
                    }
                    else if (!datesAreEqual(result.get(i - 1), d))
                    {
                        result.add(d);
                        ++i;
                    }
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

    private boolean datesAreEqual(Date l, Date r)
    {
        return l.getYear() == r.getYear() && l.getMonth() == r.getMonth()
                && l.getDate() == r.getDate();
    }

    private boolean monthsAreEqual(Date l, Date r)
    {
        return l.getYear() == r.getYear() && l.getMonth() == r.getMonth();
    }

    public ArrayList<StatisticReportItem> getStatisticReport(Date from, Date to)
            throws Exception
    {
        LOG.T("CostItemsService::getStatisticReport");
        if (from == null || to == null)
            throw new Exception("invalid argument: from = "
                    + from.toLocaleString() + ", to = " + to.toLocaleString());

        SQLiteDatabase db = null;
        Cursor ds = null;
        ArrayList<StatisticReportItem> result = new ArrayList<StatisticReportItem>();

        try
        {
            Date dFrom = new Date(from.getYear(), from.getMonth(),
                    from.getDate(), 0, 0, 0);
            Date dTo = new Date(to.getYear(), to.getMonth(), to.getDate(), 23,
                    59, 59);

            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(
                    SQLiteDbQueries.GET_EXPENSES_STAT_FOR_PERIOD,
                    new String[] { Long.toString(dFrom.getTime()),
                            Long.toString(dTo.getTime()) });

            if (ds.moveToFirst())
            {
                int colGuid = ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_CI_GUID);
                int colCurrency = ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_CURRENCY);
                int colCount = ds
                        .getColumnIndex(SQLiteDbQueries.EXPR_CIR_COUNT);
                int colSum = ds.getColumnIndex(SQLiteDbQueries.EXPR_CIR_SUM);

                do
                {
                    StatisticReportItem item = new StatisticReportItem();
                    item.dateFrom = from;
                    item.dateTo = to;
                    item.guid = ds.getString(colGuid);
                    item.currency = ds.getString(colCurrency);
                    item.sum = ds.getDouble(colSum);
                    item.count = ds.getInt(colCount);
                    result.add(item);
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

    public static CostItemsService instance()
    {
        if (instance_ == null)
            LOG.E("instance_ is null");

        return instance_;
    }

    public static void createInstance(Context context)
    {
        if (instance_ != null)
            releaseInstance();
        instance_ = new CostItemsService(context);
    }

    public static void releaseInstance()
    {
        if (instance_ != null)
            instance_.release();
        instance_ = null;
    }

    private CostItemsService(Context context)
    {
        LOG.T("CostItemsService::CostItemsService");
        dbprovider_ = new SQLiteDbProvider(context);
    }

    private void release()
    {
        LOG.T("CostItemsService::release");
        if (dbprovider_ != null)
        {
            dbprovider_.close();
            dbprovider_ = null;
        }
    }

    private SQLiteDbProvider         dbprovider_;
    private HashMap<String, Integer> cirCountCache_;

    private static CostItemsService  instance_;
}

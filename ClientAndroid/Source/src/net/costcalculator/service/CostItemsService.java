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
 * Provides access to SQLite database.
 * 
 * <pre>
 * Usage:
 * {
 *     &#064;code
 *     // initialization
 *     CostItemsService s = CostItemsService(context);
 * 
 *     // use instance
 *     s.some_request();
 * 
 *     // release instance
 *     s.release();
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
                result = fromCursorCI(row);
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

    public CostItem getCostItemByGUID(String guid) throws Exception
    {
        LOG.T("CostItemsService::getCostItemByGUID");
        if (guid.length() == 0)
            throw new Exception("invalid argument: guid");

        CostItem result = null;
        SQLiteDatabase db = null;
        Cursor row = null;
        try
        {
            db = dbprovider_.getReadableDatabase();
            row = db.rawQuery(SQLiteDbQueries.GET_COST_ITEM_BY_GUID,
                    new String[] { guid });
            if (row.moveToFirst())
                result = fromCursorCI(row);
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

    public CostItemRecord getCostItemRecordByGUID(String guid) throws Exception
    {
        LOG.T("CostItemsService::getCostItemRecordByGUID");
        if (guid.length() == 0)
            throw new Exception("invalid argument: guid");

        CostItemRecord result = null;
        SQLiteDatabase db = null;
        Cursor row = null;
        try
        {
            db = dbprovider_.getReadableDatabase();
            row = db.rawQuery(SQLiteDbQueries.GET_COST_ITEM_RECORD_BY_GUID,
                    new String[] { guid });
            if (row.moveToFirst())
                result = fromCursorCIR(row);
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

    public void saveCostItem(CostItem ci) throws Exception
    {
        LOG.T("CostItemsService::saveCostItem");
        if (ci == null)
            throw new Exception("invalid argument: ci");
        if (ci.getGuid().length() == 0)
            throw new Exception("invalid argument: ci = " + ci.toString());

        SQLiteDatabase db = null;
        try
        {
            db = dbprovider_.getWritableDatabase();
            if (ci.getId() > 0)
            {
                int affected = db.update(SQLiteDbQueries.COST_ITEMS,
                        getContent(ci), SQLiteDbQueries.COST_ITEM_WHERE_BY_ID,
                        new String[] { Long.toString(ci.getId()) });
                if (affected != 1)
                    throw new Exception("failed to update cost item: "
                            + ci.toString());
            }
            else
                db.execSQL(SQLiteDbQueries.INSERT_COST_ITEM,
                        new Object[] { ci.getGuid(), ci.getName() });
        }
        finally
        {
            if (db != null)
                db.close();
        }
    }

    public void deleteCostItem(CostItem ci) throws Exception
    {
        LOG.T("CostItemsService::deleteCostItem");
        if (ci == null)
            throw new Exception("invalid argument: ci");

        if (ci.getId() > 0)
        {
            SQLiteDatabase db = null;
            try
            {
                db = dbprovider_.getWritableDatabase();
                int affected = db.delete(SQLiteDbQueries.COST_ITEMS,
                        SQLiteDbQueries.COST_ITEM_WHERE_BY_ID,
                        new String[] { Long.toString(ci.getId()) });
                if (affected != 1)
                    throw new Exception("failed to delete cost item by id: "
                            + ci.getId());
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
    }

    public void deleteCostItemRecords(CostItem ci) throws Exception
    {
        LOG.T("CostItemsService::deleteCostItemRecords");
        if (ci == null)
            throw new Exception("invalid argument: ci");

        if (ci.getGuid().length() > 0)
        {
            SQLiteDatabase db = null;
            try
            {
                db = dbprovider_.getWritableDatabase();
                int affected = db.delete(SQLiteDbQueries.COST_ITEM_RECORDS,
                        SQLiteDbQueries.COST_ITEM_RECORDS_WHERE_BY_CI_GUID,
                        new String[] { ci.getGuid() });
                LOG.D("affected: " + affected);
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
    }

    public void deleteCostItemRecord(CostItemRecord cir) throws Exception
    {
        LOG.T("CostItemsService::deleteCostItemRecord");
        if (cir == null)
            throw new Exception("invalid argument: cir");

        if (cir.getId() > 0)
        {
            SQLiteDatabase db = null;
            try
            {
                db = dbprovider_.getWritableDatabase();
                int affected = db.delete(SQLiteDbQueries.COST_ITEM_RECORDS,
                        SQLiteDbQueries.COST_ITEM_RECORDS_WHERE_BY_ID,
                        new String[] { Long.toString(cir.getId()) });
                if (affected != 1)
                    throw new Exception(
                            "failed to delete cost item record by id: "
                                    + cir.getId());
            }
            finally
            {
                if (db != null)
                    db.close();
            }
        }
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
                cir = fromCursorCIR(ds);
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

    /**
     * Return latest cost item record, by creation date, in the category
     * identified by id
     * 
     * @param id
     *            category identifier, cost item id.
     * @return CostItemRecord object or null if category does not contain any
     *         items
     * @throws Exception
     *             if id <= 0
     */
    public CostItemRecord getLatestCostItemRecordByDate(long id)
            throws Exception
    {
        LOG.T("CostItemsService::getLatestCostItemRecordByDate");
        if (id <= 0)
            throw new Exception("invalid argument: id = " + id);

        CostItemRecord cir = null;
        Cursor ds = null;
        SQLiteDatabase db = null;
        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.CIR_GET_LATEST_BY_DATETIME,
                    new String[] { Long.toString(id) });

            if (ds.moveToFirst())
                cir = fromCursorCIR(ds);
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

    ContentValues getContent(CostItem ci)
    {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteDbQueries.COL_CI_GUID, ci.getGuid());
        cv.put(SQLiteDbQueries.COL_CI_NAME, ci.getName());
        cv.put(SQLiteDbQueries.COL_CI_CREATION_TIME, ci.getCreationTime());
        return cv;
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
                    result.add(fromCursorCI(ds));
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

    public ArrayList<CostItemRecord> getAllCostItemRecords()
    {
        LOG.T("CostItemsService::getAllCostItemRecords");

        SQLiteDatabase db = null;
        Cursor ds = null;
        ArrayList<CostItemRecord> result = new ArrayList<CostItemRecord>();

        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.GET_ALL_COST_ITEM_RECORDS, null);

            if (ds.moveToFirst())
            {
                do
                {
                    result.add(fromCursorCIR(ds));
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

    public ArrayList<CostItemRecord> getAllCostItemRecords(CostItem ci)
            throws Exception
    {
        LOG.T("CostItemsService::getAllCostItemRecords");
        if (ci == null)
            throw new Exception("invalid argument: ci");

        ArrayList<CostItemRecord> cirList = new ArrayList<CostItemRecord>();
        ArrayList<Long> cirIds = getCostItemRecordIds(ci);
        for (int i = 0; i < cirIds.size(); ++i)
            cirList.add(getCostItemRecord(cirIds.get(i)));

        return cirList;
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
                result = fromCursorCI(ds);
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

    private CostItem fromCursorCI(Cursor c)
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

    private CostItemRecord fromCursorCIR(Cursor ds)
    {
        CostItemRecord cir = new CostItemRecord();

        cir.setId(ds.getLong(ds.getColumnIndex(SQLiteDbQueries.COL_CIR_ID)));
        cir.setGuid(ds.getString(ds
                .getColumnIndex(SQLiteDbQueries.COL_CIR_GUID)));
        cir.setGroupGuid(ds.getString(ds
                .getColumnIndex(SQLiteDbQueries.COL_CIR_CI_GUID)));
        cir.setSum(ds.getDouble(ds.getColumnIndex(SQLiteDbQueries.COL_CIR_SUM)));
        cir.setCurrency(ds.getString(ds
                .getColumnIndex(SQLiteDbQueries.COL_CIR_CURRENCY)));
        cir.setComment(ds.getString(ds
                .getColumnIndex(SQLiteDbQueries.COL_CIR_COMMENT)));
        cir.setTag(ds.getString(ds.getColumnIndex(SQLiteDbQueries.COL_CIR_TAG)));
        cir.setVersion(ds.getInt(ds
                .getColumnIndex(SQLiteDbQueries.COL_CIR_AUDIT_VERSION)));
        cir.setCreationTime(new Date(ds.getLong(ds
                .getColumnIndex(SQLiteDbQueries.COL_CIR_DATETIME))));

        return cir;
    }

    public HashMap<String, Integer> getCostItemRecordsCount()
    {
        LOG.T("CostItemsService::getCostItemRecordsCount");

        SQLiteDatabase db = null;
        Cursor ds = null;
        HashMap<String, Integer> cirCountCache = new HashMap<String, Integer>();

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
                    cirCountCache.put(ds.getString(keyCol), ds.getInt(valCol));
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

        return cirCountCache;
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

    public ArrayList<String> getAllDistinctCurrencies()
    {
        LOG.T("CostItemsService::getAllDistinctCurrencies");

        SQLiteDatabase db = null;
        Cursor ds = null;
        ArrayList<String> result = new ArrayList<String>();

        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.CIR_GET_ALL_DISTINCT_CURRENCIES,
                    null);

            if (ds.moveToFirst())
            {
                final int col = ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_CURRENCY);
                do
                {
                    result.add(ds.getString(col));
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

    public ArrayList<String> getAllDistinctComments()
    {
        LOG.T("CostItemsService::getAllDistinctComments");

        SQLiteDatabase db = null;
        Cursor ds = null;
        ArrayList<String> result = new ArrayList<String>();

        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.CIR_GET_ALL_DISTINCT_COMMENTS,
                    null);

            if (ds.moveToFirst())
            {
                final int col = ds
                        .getColumnIndex(SQLiteDbQueries.COL_CIR_COMMENT);
                do
                {
                    result.add(ds.getString(col));
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

    public ArrayList<String> getAllDistinctTags()
    {
        LOG.T("CostItemsService::getAllDistinctTags");

        SQLiteDatabase db = null;
        Cursor ds = null;
        ArrayList<String> result = new ArrayList<String>();

        try
        {
            db = dbprovider_.getReadableDatabase();
            ds = db.rawQuery(SQLiteDbQueries.CIR_GET_ALL_DISTINCT_TAGS, null);

            if (ds.moveToFirst())
            {
                final int col = ds.getColumnIndex(SQLiteDbQueries.COL_CIR_TAG);
                do
                {
                    result.add(ds.getString(col));
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

    public int moveCostItems(CostItem from, CostItem to)
    {
        LOG.T("CostItemsService::moveCostItems");
        if (from == null)
            throw new IllegalArgumentException("from is null");
        if (to == null)
            throw new IllegalArgumentException("to is null");

        SQLiteDatabase db = null;
        int result = 0;

        try
        {
            db = dbprovider_.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(SQLiteDbQueries.COL_CIR_CI_GUID, to.getGuid());
            result = db.update(SQLiteDbQueries.COST_ITEM_RECORDS, cv,
                    SQLiteDbQueries.COST_ITEM_RECORDS_WHERE_BY_CI_GUID,
                    new String[] { from.getGuid() });
            LOG.D("moveCostItems: " + result);
        }
        finally
        {
            if (db != null)
                db.close();
        }

        return result;
    }

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

    private SQLiteDbProvider dbprovider_;
}

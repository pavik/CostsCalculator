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

import net.costcalculator.db.SQLiteDbQueries;
import net.costcalculator.util.LOG;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Service provides static methods for encoding/decoding objects in json
 * 
 * <pre>
 * Usage:
 * {
 *     JSONSerializer.some_method();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class JSONSerializer
{
    @SuppressWarnings("unchecked")
    public static JSONArray getAllCostItemsJSON() throws Exception
    {
        LOG.T("JSONSerializer::getAllCostItemsJSON");

        JSONArray obj = new JSONArray();
        ArrayList<CostItem> ciList = CostItemsService.instance()
                .getAllCostItems();
        for (int i = 0; i < ciList.size(); ++i)
        {
            CostItem ci = ciList.get(i);
            ArrayList<CostItemRecord> cirList = CostItemsService.instance()
                    .getAllCostItemRecords(ci);
            obj.add(getCostItemJSON(ci, cirList));
        }

        return obj;
    }

    public static void importCostItemsFromJSON(String json)
            throws Exception
    {
        LOG.T("JSONSerializer::importCostItemsFromJSON");
        if (json.length() == 0)
            return;

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        if (obj != null)
        {
            JSONArray list = (JSONArray)obj;
            for (int i = 0; i < list.size(); ++i)
            {
                CostItem ci = new CostItem();
                ArrayList<CostItemRecord> cirList = new ArrayList<CostItemRecord>();
                getCostItem((JSONObject) list.get(i), ci, cirList);
                LOG.D("IMPORT: " + ci.getName());
                CostItemsService.instance().importCostItems(ci, cirList);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static JSONObject getCostItemRecordJSON(CostItemRecord cir)
            throws Exception
    {
        LOG.T("JSONSerializer::getCostItemRecordJSON");
        if (cir == null)
            throw new Exception("invalid argument: cir");

        JSONObject obj = new JSONObject();
        if (cir.getGuid().length() > 0)
            obj.put(SQLiteDbQueries.COL_CIR_GUID, cir.getGuid());

        if (cir.getGroupGuid().length() > 0)
            obj.put(SQLiteDbQueries.COL_CIR_CI_GUID, cir.getGroupGuid());

        obj.put(SQLiteDbQueries.COL_CIR_DATETIME, cir.getCreationTime()
                .getTime());
        obj.put(SQLiteDbQueries.COL_CIR_SUM, cir.getSum());

        if (cir.getCurrency().length() > 0)
            obj.put(SQLiteDbQueries.COL_CIR_CURRENCY, cir.getCurrency());

        if (cir.getTag().length() > 0)
            obj.put(SQLiteDbQueries.COL_CIR_TAG, cir.getTag());

        if (cir.getComment().length() > 0)
            obj.put(SQLiteDbQueries.COL_CIR_COMMENT, cir.getComment());

        obj.put(SQLiteDbQueries.COL_CIR_AUDIT_VERSION, cir.getVersion());

        return obj;
    }

    public static CostItemRecord getCostItemRecord(JSONObject obj)
            throws Exception
    {
        LOG.T("JSONSerializer::getCostItemRecord");
        if (obj == null)
            throw new Exception("invalid argument: obj");

        CostItemRecord cir = new CostItemRecord();

        Object guid = obj.get(SQLiteDbQueries.COL_CIR_GUID);
        if (guid != null)
            cir.setGuid(guid.toString());

        Object grGuid = obj.get(SQLiteDbQueries.COL_CIR_CI_GUID);
        if (grGuid != null)
            cir.setGroupGuid(grGuid.toString());

        Object time = obj.get(SQLiteDbQueries.COL_CIR_DATETIME);
        if (time != null)
            cir.setCreationTime(new Date((Long) time));

        Object sum = obj.get(SQLiteDbQueries.COL_CIR_SUM);
        if (sum != null)
            cir.setSum((Double) sum);

        Object currency = obj.get(SQLiteDbQueries.COL_CIR_CURRENCY);
        if (currency != null)
            cir.setCurrency(currency.toString());

        Object comment = obj.get(SQLiteDbQueries.COL_CIR_COMMENT);
        if (comment != null)
            cir.setComment(comment.toString());

        Object tag = obj.get(SQLiteDbQueries.COL_CIR_TAG);
        if (tag != null)
            cir.setTag(tag.toString());

        Object ver = obj.get(SQLiteDbQueries.COL_CIR_AUDIT_VERSION);
        if (ver != null)
            cir.setVersion((Integer) ver);

        return cir;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject getCostItemJSON(CostItem ci,
            ArrayList<CostItemRecord> cir) throws Exception
    {
        LOG.T("JSONSerializer::getCostItemJSON");
        if (ci == null)
            throw new Exception("invalid argument: ci");
        if (cir == null)
            throw new Exception("invalid argument: cir");

        JSONObject obj = new JSONObject();
        if (ci.getGuid().length() > 0)
            obj.put(SQLiteDbQueries.COL_CI_GUID, ci.getGuid());
        if (ci.getName().length() > 0)
            obj.put(SQLiteDbQueries.COL_CI_NAME, ci.getName());
        if (ci.getCreationTime().length() > 0)
            obj.put(SQLiteDbQueries.COL_CI_CREATION_TIME, ci.getCreationTime());

        obj.put(SQLiteDbQueries.COL_CI_DATA_VERSION, ci.getVersion());

        // fill array of records
        JSONArray records = new JSONArray();
        for (int i = 0; i < cir.size(); ++i)
            records.add(getCostItemRecordJSON(cir.get(i)));

        if (records.size() > 0)
            obj.put(RECORDS_ARRAY, records);

        return obj;
    }

    public static void getCostItem(JSONObject obj, CostItem ci,
            ArrayList<CostItemRecord> cir) throws Exception
    {
        LOG.T("JSONSerializer::getCostItem");
        if (obj == null)
            throw new Exception("invalid argument: obj");
        if (ci == null)
            throw new Exception("invalid argument: ci");
        if (cir == null)
            throw new Exception("invalid argument: cir");

        Object guid = obj.get(SQLiteDbQueries.COL_CI_GUID);
        if (guid != null)
            ci.setGuid(guid.toString());

        Object name = obj.get(SQLiteDbQueries.COL_CI_NAME);
        if (name != null)
            ci.setName(name.toString());

        Object time = obj.get(SQLiteDbQueries.COL_CI_CREATION_TIME);
        if (time != null)
            ci.setCreationTime(time.toString());

        Object ver = obj.get(SQLiteDbQueries.COL_CI_DATA_VERSION);
        if (ver != null)
            ci.setVersion((Integer) ver);

        Object records = obj.get(RECORDS_ARRAY);
        if (records != null)
        {
            JSONArray list = (JSONArray) records;
            for (int i = 0; i < list.size(); ++i)
                cir.add(getCostItemRecord((JSONObject) list.get(i)));
        }
    }

    private final static String RECORDS_ARRAY = "ci_records";
}

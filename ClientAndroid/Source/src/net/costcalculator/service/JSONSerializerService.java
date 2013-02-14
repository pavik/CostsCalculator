/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.ArrayList;
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
 *     JSONSerializerService.some_method();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class JSONSerializerService
{
    public static final String JSON_OBJECT_SIG_NAME         = "json_object_name";
    public static final String JSON_OBJECT_SIG_VAL          = "json_object_value";
    public static final String JSON_OBJECT_COST_ITEM        = "json_object_ci";
    public static final String JSON_OBJECT_COST_ITEM_RECORD = "json_object_cir";

    /**
     * Serialize all expenses into JSON format of the next structure: [
     * {"json_object_name":"json_object_ci", "json_object_value":[] },
     * {"json_object_name":"json_object_cir", "json_object_value":[] } ]
     * 
     * @return array of JSON objects
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static JSONArray getAllExpensesAsJSON() throws Exception
    {
        LOG.T("JSONSerializerService::getAllExpensesAsJSON");

        JSONArray array = new JSONArray();
        JSONObject ci = new JSONObject();
        ci.put(JSON_OBJECT_SIG_NAME, JSON_OBJECT_COST_ITEM);
        ci.put(JSON_OBJECT_SIG_VAL, getAllCostItemsAsJSON());
        JSONObject cir = new JSONObject();
        cir.put(JSON_OBJECT_SIG_NAME, JSON_OBJECT_COST_ITEM_RECORD);
        cir.put(JSON_OBJECT_SIG_VAL, getAllCostItemRecordsAsJSON());

        array.add(ci);
        array.add(cir);
        return array;
    }

    public static void getAllExpensesFromJSON(String json,
            ArrayList<CostItem> ciList, ArrayList<CostItemRecord> cirList)
            throws Exception
    {
        LOG.T("JSONSerializerService::getAllExpensesFromJSON");
        if (json.length() == 0)
            throw new Exception("invalid argument: json");
        if (cirList == null)
            throw new Exception("invalid argument: cirList");
        if (ciList == null)
            throw new Exception("invalid argument: ciList");

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        if (obj != null)
        {
            JSONArray list = (JSONArray) obj;
            {
                for (int i = 0; i < list.size(); ++i)
                {
                    JSONObject next = (JSONObject) list.get(i);
                    if (next != null)
                    {
                        Object type = next.get(JSON_OBJECT_SIG_NAME);
                        if (type != null)
                        {
                            if (type.equals(JSON_OBJECT_COST_ITEM))
                                getAllCostItemsFromJSON(
                                        next.get(JSON_OBJECT_SIG_VAL), ciList);
                            else if (type.equals(JSON_OBJECT_COST_ITEM_RECORD))
                                getAllCostItemRecordsFromJSON(
                                        next.get(JSON_OBJECT_SIG_VAL), cirList);
                            else
                                LOG.E("Undefined object type in JSON: "
                                        + type.toString());
                        }
                        else
                            LOG.E("Undefined JSON object at index: " + i
                                    + " - " + next);
                    }
                    else
                        LOG.E("Invalid JSON object at index: " + i + " - "
                                + list.get(i));
                }
            }
        }
        else
            LOG.E("Invalid JSON string: " + json);
    }

    @SuppressWarnings("unchecked")
    private static JSONArray getAllCostItemsAsJSON() throws Exception
    {
        LOG.T("JSONSerializerService::getAllCostItemsAsJSON");

        JSONArray obj = new JSONArray();
        ArrayList<CostItem> ciList = CostItemsService.instance()
                .getAllCostItems();
        for (int i = 0; i < ciList.size(); ++i)
            obj.add(ciList.get(i).toJSON());

        return obj;
    }

    @SuppressWarnings("unchecked")
    private static JSONArray getAllCostItemRecordsAsJSON() throws Exception
    {
        LOG.T("JSONSerializerService::getAllCostItemRecordsAsJSON");

        JSONArray obj = new JSONArray();
        ArrayList<CostItemRecord> cirList = CostItemsService.instance()
                .getAllCostItemRecords();
        for (int i = 0; i < cirList.size(); ++i)
            obj.add(cirList.get(i).toJSON());

        return obj;
    }

    private static void getAllCostItemsFromJSON(Object jsonArray,
            ArrayList<CostItem> ciList)
    {
        LOG.T("JSONSerializerService::getAllCostItemsFromJSON");
        if (jsonArray == null)
        {
            LOG.E("Invalid argument: jsonArray");
            return;
        }
        if (ciList == null)
        {
            LOG.E("Invalid argument: ciList");
            return;
        }

        JSONArray list = (JSONArray) jsonArray;
        {
            for (int i = 0; i < list.size(); ++i)
            {
                JSONObject next = (JSONObject) list.get(i);
                if (next != null)
                {
                    CostItem ci = new CostItem();
                    if (ci.fromJSON(next))
                    {
                        ci.resetId();
                        ciList.add(ci);
                    }
                    else
                        LOG.E("Failed to init cost item from json object: "
                                + next.toJSONString());
                }
                else
                    LOG.E("Invalid JSON object at index: " + i + " - "
                            + list.get(i));
            }
        }
    }

    private static void getAllCostItemRecordsFromJSON(Object jsonArray,
            ArrayList<CostItemRecord> cirList)
    {
        LOG.T("JSONSerializerService::getAllCostItemRecordsFromJSON");
        if (jsonArray == null)
        {
            LOG.E("Invalid argument: jsonArray");
            return;
        }
        if (cirList == null)
        {
            LOG.E("Invalid argument: cirList");
            return;
        }

        JSONArray list = (JSONArray) jsonArray;
        {
            for (int i = 0; i < list.size(); ++i)
            {
                JSONObject next = (JSONObject) list.get(i);
                if (next != null)
                {
                    CostItemRecord cir = new CostItemRecord();
                    if (cir.fromJSON(next))
                    {
                        cir.resetId();
                        cirList.add(cir);
                    }
                    else
                        LOG.E("Failed to init cost item from json object: "
                                + next.toJSONString());
                }
                else
                    LOG.E("Invalid JSON object at index: " + i + " - "
                            + list.get(i));
            }
        }
    }
}

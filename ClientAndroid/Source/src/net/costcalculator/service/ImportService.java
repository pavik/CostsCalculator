/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.ArrayList;
import java.util.HashSet;
import android.content.Context;

import net.costcalculator.util.LOG;

/**
 * Service provides static methods for importing expenses from json string.
 * 
 * <pre>
 * Usage:
 * {
 *     ImportService.some_method();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ImportService
{
    public static final int IMPORT_SIMPLE    = 0; // import only expenses which
                                                  // is not in database now,
                                                  // skip existing items
    public static final int IMPORT_OVERWRITE = 1; // import all expenses,
                                                  // overwrite existing items

    public static ImportStatistic importExpensesFromJSONString(Context c,
            String json, int importMode, ProgressCallback pc) throws Exception
    {
        LOG.T("ImportService::importExpensesFromJSONString");
        if (json.length() == 0)
            throw new IllegalArgumentException("json");

        ImportStatistic stat = new ImportStatistic();
        ArrayList<CostItem> ciList = new ArrayList<CostItem>();
        ArrayList<CostItemRecord> cirList = new ArrayList<CostItemRecord>();

        JSONSerializerService.getAllExpensesFromJSON(json, ciList, cirList);
        if (pc != null)
            pc.publishProgress(1, 3);

        importCostItems(c, ciList, importMode, stat);
        if (pc != null)
            pc.publishProgress(2, 3);

        importCostItemRecords(c, cirList, importMode, stat);
        if (pc != null)
            pc.publishProgress(3, 3);

        return stat;
    }

    private static void importCostItems(Context c, ArrayList<CostItem> ciList,
            int importMode, ImportStatistic stat)
    {
        LOG.T("ImportService::importCostItems");
        if (ciList == null)
        {
            LOG.E("Invalid argument: ciList");
            return;
        }
        if (stat == null)
        {
            LOG.E("Invalid argument: stat");
            return;
        }

        CostItemsService cis = new CostItemsService(c);
        stat.ci_total = ciList.size();
        for (int i = 0; i < ciList.size(); ++i)
        {
            CostItem ci = ciList.get(i);
            if (!ci.isValid())
            {
                stat.ci_errors++;
                continue;
            }
            try
            {
                CostItem dbci = cis.getCostItemByGUID(ci.getGuid());
                if (dbci == null)
                {
                    cis.saveCostItem(ci);
                    stat.ci_imported_new++;
                }
                else if (importMode == IMPORT_OVERWRITE)
                {
                    ci.setId(dbci.getId());
                    cis.saveCostItem(ci);
                    stat.ci_imported_overwritten++;
                }
                else if (importMode == IMPORT_SIMPLE)
                {
                    stat.ci_ignored_existent++;
                }
                else
                    throw new Exception("Invalid import mode");
            }
            catch (Exception e)
            {
                stat.ci_errors++;
                LOG.E("Exception: " + e.getMessage());
            }
        }
        cis.release();
    }

    private static void importCostItemRecords(Context c,
            ArrayList<CostItemRecord> cirList, int importMode,
            ImportStatistic stat)
    {
        LOG.T("ImportService::importCostItemRecords");
        if (cirList == null)
        {
            LOG.E("Invalid argument: cirList");
            return;
        }
        if (stat == null)
        {
            LOG.E("Invalid argument: stat");
            return;
        }

        CostItemsService cis = new CostItemsService(c);
        stat.cir_total = cirList.size();

        // get all cost items to fast check if cost item record
        // belogns to any cost item category
        ArrayList<CostItem> ciList = cis.getAllCostItems();
        HashSet<String> ciGuids = new HashSet<String>();
        for (int i = 0; i < ciList.size(); ++i)
            ciGuids.add(ciList.get(i).getGuid());

        for (int i = 0; i < cirList.size(); ++i)
        {
            CostItemRecord cir = cirList.get(i);
            if (!cir.isValid())
            {
                stat.cir_errors++;
                continue;
            }
            if (!ciGuids.contains(cir.getGroupGuid()))
            {
                stat.cir_errors++; // no category associated with item
                continue;
            }

            try
            {
                CostItemRecord dbcir = cis.getCostItemRecordByGUID(cir
                        .getGuid());
                if (dbcir == null)
                {
                    cis.saveCostItemRecord(cir);
                    stat.cir_imported_new++;
                }
                else if (importMode == IMPORT_OVERWRITE)
                {
                    cir.setId(dbcir.getId());
                    cis.saveCostItemRecord(cir);
                    stat.cir_imported_overwritten++;
                }
                else if (importMode == IMPORT_SIMPLE)
                {
                    stat.cir_ignored_existent++;
                }
                else
                    throw new Exception("Invalid import mode");
            }
            catch (Exception e)
            {
                stat.cir_errors++;
                LOG.E("Exception: " + e.getMessage());
            }
        }
        cis.release();
    }
}

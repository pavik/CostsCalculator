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

/**
 * Service provides static methods for importing expenses from external storage.
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

    public static ImportStatistic importExpensesFromJSONString(String json,
            int importMode, ProgressCallback pc) throws Exception
    {
        LOG.T("ImportService::importExpensesFromJSONString");
        if (json.length() == 0)
            throw new Exception("Invalid argument: json");

        ImportStatistic stat = new ImportStatistic();
        ArrayList<CostItem> ciList = new ArrayList<CostItem>();
        ArrayList<CostItemRecord> cirList = new ArrayList<CostItemRecord>();

        JSONSerializerService.getAllExpensesFromJSON(json, ciList, cirList);
        if (pc != null)
            pc.publishProgress(1, 3);

        importCostItems(ciList, importMode, stat);
        if (pc != null)
            pc.publishProgress(2, 3);

        importCostItemRecords(cirList, importMode, stat);
        if (pc != null)
            pc.publishProgress(3, 3);

        return stat;
    }

    private static void importCostItems(ArrayList<CostItem> ciList,
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
                CostItem dbci = CostItemsService.instance().getCostItemByGUID(
                        ci.getGuid());
                if (dbci == null)
                {
                    CostItemsService.instance().saveCostItem(ci);
                    stat.ci_imported_new++;
                }
                else if (importMode == IMPORT_OVERWRITE)
                {
                    ci.setId(dbci.getId());
                    CostItemsService.instance().saveCostItem(ci);
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
    }

    private static void importCostItemRecords(
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

        stat.cir_total = cirList.size();
        for (int i = 0; i < cirList.size(); ++i)
        {
            CostItemRecord cir = cirList.get(i);
            if (!cir.isValid())
            {
                stat.cir_errors++;
                continue;
            }
            try
            {
                CostItemRecord dbcir = CostItemsService.instance()
                        .getCostItemRecordByGUID(cir.getGuid());
                if (dbcir == null)
                {
                    CostItemsService.instance().saveCostItemRecord(cir);
                    stat.cir_imported_new++;
                }
                else if (importMode == IMPORT_OVERWRITE)
                {
                    cir.setId(dbcir.getId());
                    CostItemsService.instance().saveCostItemRecord(cir);
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
    }
}

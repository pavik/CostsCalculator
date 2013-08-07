/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.db;

/**
 * This class describes database tables and queries.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class SQLiteDbQueries
{
    // table 'cost_items'
    public static final String COST_ITEMS                         = "cost_items";

    // columns of 'cost_items'
    public static final String COL_CI_ID                          = "ci_id";
    public static final String COL_CI_GUID                        = "ci_guid";
    public static final String COL_CI_NAME                        = "ci_name";
    public static final String COL_CI_CREATION_TIME               = "ci_audit_ct";
    public static final String COL_CI_DATA_VERSION                = "ci_audit_ver";
    public static final String COL_CI_MODIFICATION_TIME           = "ci_audit_mt";

    // expressions columns
    public static final String EXPR_CI_MT                         = "ci_mt";

    // queries of 'cost_items'
    public static final String INSERT_COST_ITEM                   = "INSERT INTO cost_items(ci_guid, ci_name) VALUES(?, ?)";

    public static final String COST_ITEM_WHERE_BY_ID              = " ci_id = ? ";

    public static final String GET_COST_ITEM_BY_GUID              = "SELECT * FROM cost_items WHERE ci_guid = ?";

    public static final String DEL_COST_ITEM                      = "DELETE FROM cost_items WHERE ci_id = ?";

    public static final String GET_ALL_COST_ITEMS                 = "SELECT cost_items.*, COUNT(cost_item_records.cir_ci_guid) AS n FROM cost_items LEFT JOIN cost_item_records ON cost_items.ci_guid = cost_item_records.cir_ci_guid GROUP BY cost_items.ci_guid ORDER BY n DESC";

    public static final String GET_COST_ITEM_BY_ID                = "SELECT * FROM cost_items WHERE ci_id = ?";

    public static final String GET_LATEST_MODIFICATION_TIME       = "SELECT MAX(strftime('%s', ci_audit_mt) * 1000) AS ci_mt FROM cost_items";

    // ***************************************************************

    // table 'versions'
    public static final String INSERT_VERSION                     = "INSERT INTO versions (ver_number) VALUES(?)";

    // ***************************************************************

    // table 'cost_item_records'
    public static final String COST_ITEM_RECORDS                  = "cost_item_records";

    // columns of 'cost_item_records'
    public static final String COL_CIR_ID                         = "cir_id";
    public static final String COL_CIR_GUID                       = "cir_guid";
    public static final String COL_CIR_CI_GUID                    = "cir_ci_guid";
    public static final String COL_CIR_DATETIME                   = "cir_datetime";
    public static final String COL_CIR_SUM                        = "cir_sum";
    public static final String COL_CIR_CURRENCY                   = "cir_currency";
    public static final String COL_CIR_TAG                        = "cir_tag";
    public static final String COL_CIR_COMMENT                    = "cir_comment";
    public static final String COL_CIR_AUDIT_CT                   = "cir_audit_ct";
    public static final String COL_CIR_AUDIT_VERSION              = "cir_audit_version";
    public static final String COL_CIR_AUDIT_MT                   = "cir_audit_mt";

    // expressions columns
    public static final String EXPR_CIR_COUNT                     = "cir_count";
    public static final String EXPR_CIR_SUM                       = "cir_sum";
    public static final String EXPR_CIR_MT                        = "cir_mt";

    // update condition
    public static final String COST_ITEM_RECORDS_U                = "cir_id = ? AND cir_audit_version = ?";

    public static final String COST_ITEM_RECORDS_WHERE_BY_ID      = " cir_id = ? ";

    public static final String COST_ITEM_RECORDS_WHERE_BY_CI_GUID = " cir_ci_guid = ? ";

    // queries of 'cost_item_records'
    public static final String COST_ITEM_RECORDS_IDS              = "SELECT cir_id FROM cost_item_records WHERE cir_ci_guid = ? ORDER BY cir_datetime DESC";

    public static final String GET_ALL_COST_ITEM_RECORDS          = "SELECT * FROM cost_item_records";

    public static final String GET_COST_ITEM_RECORD_BY_ID         = "SELECT * FROM cost_item_records WHERE cir_id = ?";

    public static final String GET_COST_ITEM_RECORD_BY_GUID       = "SELECT * FROM cost_item_records WHERE cir_guid = ?";

    public static final String GET_COUNT_COST_ITEM_RECORDS        = "SELECT cir_ci_guid, COUNT(cir_ci_guid) AS cir_count FROM  cost_item_records GROUP BY (cir_ci_guid) ORDER BY cir_count DESC";

    public static final String GET_DISTINCT_EXPENSES_DATES        = "SELECT DISTINCT cir_datetime FROM cost_item_records ORDER BY cir_datetime DESC";

    public static final String GET_EXPENSES_STAT_FOR_PERIOD       = "SELECT cir_ci_guid, cir_currency, COUNT(*) AS cir_count, SUM(cir_sum) AS cir_sum FROM cost_item_records WHERE cir_datetime >= ? AND cir_datetime <= ? GROUP BY cir_ci_guid, cir_currency ORDER BY cir_sum DESC";

    public static final String CIR_GET_LATEST_BY_DATETIME         = "SELECT cost_item_records.* FROM cost_item_records JOIN cost_items ON cost_items.ci_guid = cost_item_records.cir_ci_guid WHERE cost_items.ci_id = ? ORDER BY cost_item_records.cir_datetime DESC LIMIT 1";

    public static final String CIR_GET_ALL_DISTINCT_CURRENCIES    = "SELECT DISTINCT cir_currency FROM cost_item_records";

    public static final String CIR_GET_ALL_DISTINCT_TAGS          = "SELECT DISTINCT cir_tag FROM cost_item_records";

    public static final String CIR_GET_ALL_DISTINCT_COMMENTS      = "SELECT DISTINCT cir_comment FROM cost_item_records";

    public static final String CIR_MOVE_COST_ITEM_RECORDS         = "UPDATE cost_item_records SET cir_ci_guid = ? WHERE cir_ci_guid = ?";

    public static final String CIR_GET_LATEST_MODIFICATION_TIME   = "SELECT MAX(strftime('%s', cir_audit_mt) * 1000) AS cir_mt FROM cost_item_records";

    public static final String CIR_GET_MIN_EXPENSES_DATE          = "SELECT MIN(cir_datetime) AS cir_datetime FROM cost_item_records";

    public static final String CIR_GET_MAX_EXPENSES_DATE          = "SELECT MAX(cir_datetime) AS cir_datetime FROM cost_item_records";
}

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
    public static final String TABLE_COST_ITEMS             = "CREATE TABLE cost_items ("
                                                                    + "ci_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE ,"
                                                                    + "ci_guid VARCHAR NOT NULL  UNIQUE ,"
                                                                    + "ci_name VARCHAR NOT NULL ,"
                                                                    + "ci_use_count INTEGER NOT NULL  DEFAULT 0,"
                                                                    + "ci_audit_ct DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                                                                    + "ci_audit_ver INTEGER NOT NULL DEFAULT 0)";

    public static final String TRIG_COST_ITEMS_AFTER_UPDATE = "CREATE TRIGGER cost_items_after_update AFTER UPDATE ON cost_items "
                                                                    + "BEGIN update cost_items set ci_audit_ver = ci_audit_ver + 1 "
                                                                    + "where ci_id = old.ci_id and old.ci_id = new.ci_id and old.ci_use_count = new.ci_use_count; "
                                                                    + "END";

    // columns of 'cost_items'
    public static final String COL_CI_ID                    = "ci_id";
    public static final String COL_CI_GUID                  = "ci_guid";
    public static final String COL_CI_NAME                  = "ci_name";
    public static final String COL_CI_CREATION_TIME         = "ci_audit_ct";
    public static final String COL_CI_DATA_VERSION          = "ci_audit_ver";

    // queries of 'cost_items'
    public static final String INSERT_COST_ITEM             = "INSERT INTO cost_items(ci_guid, ci_name) VALUES(?, ?)";

    public static final String GET_COST_ITEM_BY_GUID        = "SELECT * FROM cost_items WHERE ci_guid = ?";

    public static final String DEL_COST_ITEM                = "DELETE FROM cost_items WHERE ci_id = ?";

    public static final String INC_USE_COUNT                = "UPDATE cost_items SET ci_use_count = ci_use_count + 1 WHERE ci_id = ?";

    public static final String GET_ALL_COST_ITEMS           = "SELECT * FROM cost_items ORDER BY ci_use_count DESC";

    public static final String GET_COST_ITEM_BY_ID          = "SELECT * FROM cost_items WHERE ci_id = ?";

    // ***************************************************************

    // table 'versions'
    public static final String TABLE_VERSIONS               = "CREATE  TABLE versions ("
                                                                    + "ver_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                                                                    + "ver_number INTEGER NOT NULL  UNIQUE , "
                                                                    + "ver_audit_ct DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP)";

    // queries of 'versions'
    public static final String INSERT_VERSION               = "INSERT INTO versions (ver_number) VALUES(?)";

    // ***************************************************************

    // table 'cost_item_records'
    public static final String COST_ITEM_RECORDS            = "cost_item_records";

    public static final String TABLE_COST_ITEM_RECORDS      = "CREATE TABLE cost_item_records ("
                                                                    + "cir_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                                                                    + "cir_guid VARCHAR NOT NULL  UNIQUE , "
                                                                    + "cir_ci_guid VARCHAR NOT NULL, "
                                                                    + "cir_datetime DATETIME NOT NULL , "
                                                                    + "cir_sum DOUBLE NOT NULL , "
                                                                    + "cir_currency VARCHAR, "
                                                                    + "cir_tag VARCHAR, "
                                                                    + "cir_comment VARCHAR, "
                                                                    + "cir_audit_ct DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, "
                                                                    + "cir_audit_version INTEGER NOT NULL  DEFAULT 0)";

    // columns of 'cost_item_records'
    public static final String COL_CIR_ID                   = "cir_id";
    public static final String COL_CIR_GUID                 = "cir_guid";
    public static final String COL_CIR_CI_GUID              = "cir_ci_guid";
    public static final String COL_CIR_DATETIME             = "cir_datetime";
    public static final String COL_CIR_SUM                  = "cir_sum";
    public static final String COL_CIR_CURRENCY             = "cir_currency";
    public static final String COL_CIR_TAG                  = "cir_tag";
    public static final String COL_CIR_COMMENT              = "cir_comment";
    public static final String COL_CIR_AUDIT_CT             = "cir_audit_ct";
    public static final String COL_CIR_AUDIT_VERSION        = "cir_audit_version";

    // update condition
    public static final String COST_ITEM_RECORDS_U          = "cir_id = ? AND cir_audit_version = ?";

    // queries of 'cost_item_records'
    public static final String COST_ITEM_RECORDS_IDS        = "SELECT cir_id FROM cost_item_records WHERE cir_ci_guid = ? ORDER BY cir_datetime DESC";

    public static final String GET_COST_ITEM_RECORD_BY_ID   = "SELECT * FROM cost_item_records WHERE cir_id = ?";
}

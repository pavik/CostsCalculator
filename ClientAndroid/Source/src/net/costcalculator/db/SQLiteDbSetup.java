/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.db;

import net.costcalculator.util.LOG;

import android.database.sqlite.SQLiteDatabase;

/**
 * Class performs initial setup of sqlite database of the latest version.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class SQLiteDbSetup
{
    public SQLiteDbSetup(SQLiteDatabase db)
    {
        db_ = db;
    }

    public void setup()
    {
        LOG.T("SQLiteDbSetup::setup");

        db_.beginTransaction();
        try
        {
            db_.execSQL(SQLiteQueries.TABLE_COST_ITEMS);
            db_.execSQL(SQLiteQueries.TRIG_COST_ITEMS_AFTER_UPDATE);

            db_.execSQL(SQLiteQueries.TABLE_COST_ITEM_RECORDS);

            db_.execSQL(SQLiteQueries.TABLE_VERSIONS);
            db_.execSQL(SQLiteQueries.INSERT_VERSION,
                    new Object[] { SQLiteDbProvider.DATABASE_VERSION });

            db_.setTransactionSuccessful();
        }
        finally
        {
            db_.endTransaction();
        }
    }

    private SQLiteDatabase db_;
}

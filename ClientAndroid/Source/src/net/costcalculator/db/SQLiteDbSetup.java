/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.db;

import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import net.costcalculator.util.RawResources;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class performs initial setup of sqlite database of the latest version.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class SQLiteDbSetup
{
    public SQLiteDbSetup(SQLiteDatabase db, Context c)
    {
        db_ = db;
        context_ = c;
    }

    public void setup(int version)
    {
        LOG.T("SQLiteDbSetup::setup");

        db_.beginTransaction();
        try
        {
            final String sql = RawResources.getFileAsString(context_,
                    "createdb.sql");
            String[] queries = sql.split("\n");
            for (String q : queries)
            {
                final String qtrimed = q.trim();
                if (qtrimed.length() != 0)
                {
                    LOG.D(qtrimed);
                    db_.execSQL(qtrimed);
                }
            }
            db_.execSQL(SQLiteDbQueries.INSERT_VERSION,
                    new Object[] { version });
            db_.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
        finally
        {
            db_.endTransaction();
        }
    }

    private Context        context_;
    private SQLiteDatabase db_;
}

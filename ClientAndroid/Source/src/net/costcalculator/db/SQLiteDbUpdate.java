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
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class is responsible for update of sqlite database from the currently
 * installed version to the latest version.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class SQLiteDbUpdate
{
    public SQLiteDbUpdate(SQLiteDatabase db, Context c, int from, int to)
    {
        db_ = db;
        context_ = c;
        from_ = from;
        to_ = to;
    }

    @SuppressLint("DefaultLocale")
    public void update()
    {
        LOG.T("SQLiteDbUpdate::update");

        db_.beginTransaction();
        try
        {
            for (int i = from_; i < to_; ++i)
            {
                LOG.D(String.format("Update database from %d to %d", i, i + 1));
                final String updatescript = String.format("updatedb_%d_%d.sql",
                        i, i + 1);
                final String sql = RawResources.getFileAsString(context_,
                        updatescript);
                final String[] queries = sql.split("\n");
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
                        new Object[] { i + 1 });
            }
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
    private int            from_, to_;
}

/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.db;

import java.util.UUID;

import net.costcalculator.activity.R;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import net.costcalculator.util.RawResources;

import android.content.Context;
import android.content.res.Resources;
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

            // insert default categories
            final String query = "INSERT INTO cost_items(ci_guid, ci_name) VALUES('%s', '%s')";
            Resources r = context_.getResources();
            final String[] categories = new String[] {
                    r.getString(R.string.ci_food),
                    r.getString(R.string.ci_household),
                    r.getString(R.string.ci_clothes),
                    r.getString(R.string.ci_accommodation),
                    r.getString(R.string.ci_credit),
                    r.getString(R.string.ci_car),
                    r.getString(R.string.ci_health),
                    r.getString(R.string.ci_restaurant),
                    r.getString(R.string.ci_mobile),
                    r.getString(R.string.ci_entertainment),
                    r.getString(R.string.ci_other) };

            for (String cat : categories)
                db_.execSQL(String.format(query, UUID.randomUUID().toString(),
                        cat));

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

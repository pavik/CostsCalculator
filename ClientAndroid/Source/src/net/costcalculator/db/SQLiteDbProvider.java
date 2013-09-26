/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.db;

import net.costcalculator.util.LOG;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class provides access to the database to perform SQL queries.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class SQLiteDbProvider extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 3;

    public SQLiteDbProvider(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context_ = context;
    }

    public SQLiteDbProvider(Context context, String name,
            CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        context_ = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        LOG.T("SQLiteDbProvider::onCreate");

        SQLiteDbSetup s = new SQLiteDbSetup(db, context_);
        s.setup(DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int verFrom, int verTo)
    {
        LOG.T("SQLiteDbProvider::onUpgrade");

        SQLiteDbUpdate u = new SQLiteDbUpdate(db, context_, verFrom, verTo);
        u.update();
    }

    private Context             context_;
    private static final String DATABASE_NAME = "cost_calculator.db";
}

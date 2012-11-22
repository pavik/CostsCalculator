/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Class is responsible for update of sqlite database
 * from the currently installed version to the latest version.
 * 
 * @author Aliaksei Plashchanski
 *
 */
public class SQLiteDbUpdate
{
    public SQLiteDbUpdate(SQLiteDatabase db, int from, int to)
    {
        db_ = db;
    }

    public void update()
    {
        // TODO Selet latest version 'to' class, add table with version info
    }
    
    private SQLiteDatabase db_;
}

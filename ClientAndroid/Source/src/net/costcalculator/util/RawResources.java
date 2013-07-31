
package net.costcalculator.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class RawResources
{
    public static String getFileAsString(Context c, String filename)
            throws IOException
    {
        LOG.T("RawResources::getFileAsString");
        if (c == null || filename == null)
            throw new IllegalArgumentException("invalid input");

        InputStream is = c.getAssets().open(filename);
        byte[] content = new byte[is.available()];
        is.read(content);
        return new String(content);
    }
}

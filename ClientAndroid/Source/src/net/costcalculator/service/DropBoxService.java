/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import net.costcalculator.util.LOG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Service provides static methods for communicating with dropbox storage.
 * 
 * <pre>
 * Usage:
 * {
 *     DropBoxService.create(context);
 *     try
 *     {
 *         DropBoxService.instance().some_method();
 *     }
 *     catch (Exception e)
 *     {
 *         String error = DropBoxService.handleException(e);
 *     }
 * 
 *     DropBoxService.release();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class DropBoxService
{
    // static interface
    public static void create(Context c)
    {
        if (c == null)
            throw new IllegalArgumentException("context");

        release();
        instance_ = new DropBoxService(c);
    }

    public static void release()
    {
        if (instance_ != null)
        {
            instance_.destroy();
            instance_ = null;
        }
    }

    public static DropBoxService instance()
    {
        return instance_;
    }

    public static String appKey()
    {
        return APP_KEY;
    }

    public static void saveAccessToken(Context context, String accessToken)
    {
        if (context == null)
            throw new IllegalArgumentException("context");
        if (accessToken == null || accessToken.length() == 0)
            throw new IllegalArgumentException("accessToken");

        PreferencesService pref = new PreferencesService(context);
        pref.set(PreferencesService.ACCESS_TOKEN, accessToken);
    }

    public static void clearAccessToken(Context context)
    {
        if (context == null)
            throw new IllegalArgumentException("context");

        PreferencesService pref = new PreferencesService(context);
        pref.remove(PreferencesService.ACCESS_TOKEN);
    }

    public static boolean hasAccessToken(Context context)
    {
        if (context == null)
            throw new IllegalArgumentException("context");

        PreferencesService pref = new PreferencesService(context);
        return null != pref.get(PreferencesService.ACCESS_TOKEN);
    }

    public static void setupAlarm(Context context)
    {
        LOG.T("BackupConfigurationLogic::setupAlarm");
        if (context == null)
            throw new IllegalArgumentException("context");

        PreferencesService pref = new PreferencesService(context);
        String hourStr = pref.get(PreferencesService.BACKUP_INTERVAL);
        int hour = hourStr == null ? 0 : Integer.valueOf(hourStr);

        if (hour <= 0)
            LOG.D("Backup interval is not configured");
        else
        {
            LOG.D("Setting up alarm");

            AlarmManager am = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context,
                    BackupAlarmBroadcastReceiver.class);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0,
                    intent, 0);
            long repeatingms = hour * 3600 * 1000;
            LOG.D("repeatingms = " + repeatingms);
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + repeatingms, repeatingms,
                    pending);
        }
    }

    public static void deleteAlarm(Context context)
    {
        LOG.T("BackupConfigurationLogic::deleteAlarm");
        if (context == null)
            throw new IllegalArgumentException("context");

        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BackupAlarmBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent,
                0);
        am.cancel(pending);
    }

    public static String handleException(Exception ex)
    {
        String msg = ex.getLocalizedMessage();
        if (msg == null || msg.length() == 0)
            return ex.getMessage();
        else
            return msg;
    }

    // service interface
    public ArrayList<DropboxEntry> getFolderContent(String folder)
            throws DbxException
    {
        LOG.T("DropBoxService::getFolderContent");

        ListFolderResult r = api_.files().listFolder(folder);
        ArrayList<DropboxEntry> files = new ArrayList<DropboxEntry>();
        for (Metadata md : r.getEntries())
        {
            if (md instanceof FileMetadata)
            {
                FileMetadata fmd = (FileMetadata) md;
                DropboxEntry file = new DropboxEntry();
                file.name = fmd.getName();
                file.clientTime = fmd.getClientModified().toString();
                file.size = fmd.getSize();
                files.add(file);
            }
        }

        LOG.T("DropBoxService::getFolderContent -> return");
        return files;
    }

    public boolean uploadFile(String path, String file,
            final ProgressCallback pc) throws DbxException, IOException
    {
        LOG.T("DropBoxService::uploadFile");

        if (path.length() == 0)
            throw new IllegalArgumentException("path");
        if (file.length() == 0)
            throw new IllegalArgumentException("file");

        ByteArrayInputStream bis;
        try
        {
            byte[] utf8bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
            byte[] utf8data = file.getBytes("UTF-8");
            byte[] buffer = new byte[utf8bom.length + utf8data.length];
            for (int i = 0; i < utf8bom.length; ++i)
                buffer[i] = utf8bom[i];
            for (int i = 0; i < utf8data.length; ++i)
                buffer[utf8bom.length + i] = utf8data[i];
            bis = new ByteArrayInputStream(buffer);
        }
        catch (UnsupportedEncodingException e)
        {
            bis = new ByteArrayInputStream(file.getBytes());
        }

        api_.files().upload(path).uploadAndFinish(bis);

        LOG.T("DropBoxService::uploadFile -> return");
        return true;
    }

    public ByteArrayOutputStream downloadFile(String path,
            final ProgressCallback pc) throws DbxException, IOException
    {
        LOG.T("DropBoxService::downloadFile");

        if (path.length() == 0)
            throw new IllegalArgumentException("path");

        ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
        FileMetadata fmd = (FileMetadata) api_.files().getMetadata(path);
        api_.files().download(fmd.getPathLower(), fmd.getRev()).download(bos);

        LOG.T("DropBoxService::downloadFile -> return");
        return bos;
    }

    // private section
    private DropBoxService(Context c)
    {
        // We create a new AuthSession so that we can use the Dropbox API.
        DbxRequestConfig requestConfig = DbxRequestConfig
                .newBuilder("expenses")
                .withHttpRequestor(OkHttp3Requestor.INSTANCE).build();

        PreferencesService pref = new PreferencesService(c);
        String accessToken = pref.get(PreferencesService.ACCESS_TOKEN);

        if (accessToken == null)
            throw new IllegalStateException(
                    "DropBoxService: access token is not found");
        else
            api_ = new DbxClientV2(requestConfig, accessToken);
    }

    private void destroy()
    {
        api_ = null;
    }

    // members
    private DbxClientV2           api_;

    // instance
    private static DropBoxService instance_;

    // constant
    final static private String   APP_KEY = "ahtmcea8sy7m8bo";
}

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import net.costcalculator.util.LOG;

import android.content.Context;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

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
 *         String error = DropBoxService.instance().handleException(e);
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
    // singleton interface
    public static void create(Context c)
    {
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

    // service interface
    public DropboxAPI<AndroidAuthSession> getDropboxAPI()
    {
        return api_;
    }

    public ArrayList<String> getFolderContent(String folder)
            throws DropboxException
    {
        LOG.T("DropBoxService::getFolderContent");

        ArrayList<String> files = new ArrayList<String>();

        Entry dir = api_.metadata(folder.length() == 0 ? "/" : folder, 0, null,
                true, null);
        for (Entry e : dir.contents)
        {
            if (!e.isDir)
                files.add(e.fileName());
        }

        return files;
    }

    public boolean uploadFile(String path, String file,
            final ProgressCallback pc) throws Exception
    {
        LOG.T("DropBoxService::uploadFile");
        if (path.length() == 0)
            throw new Exception("invalid argument: path");
        if (file.length() == 0)
            throw new Exception("invalid argument: file");

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

        // By creating a request, we get a handle to the putFile operation,
        // so we can cancel it later if we want
        UploadRequest request = api_.putFileRequest(path, bis, bis.available(),
                null, new ProgressListener()
                {
                    @Override
                    public long progressInterval()
                    {
                        // Update the progress bar every half-second or so
                        return 500;
                    }

                    @Override
                    public void onProgress(long bytes, long total)
                    {
                        if (pc != null)
                            pc.publishProgress(bytes, total);
                    }
                });

        if (request != null)
            request.upload();

        return true;
    }

    public ByteArrayOutputStream downloadFile(String path,
            final ProgressCallback pc) throws Exception
    {
        LOG.T("DropBoxService::downloadFile");
        if (path.length() == 0)
            throw new Exception("invalid argument: path");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        api_.getFile(path, null, bos, new ProgressListener()
        {
            @Override
            public long progressInterval()
            {
                // Update the progress bar every half-second or so
                return 500;
            }

            @Override
            public void onProgress(long bytes, long total)
            {
                if (pc != null)
                    pc.publishProgress(bytes, total);
            }
        });

        return bos;
    }

    public void storeKeys(String key, String secret)
    {
        PreferencesService pref = new PreferencesService(context_);
        pref.set(PreferencesService.ACCESS_KEY_NAME, key);
        pref.set(PreferencesService.ACCESS_SECRET_NAME, secret);
    }

    public void clearKeys()
    {
        PreferencesService pref = new PreferencesService(context_);
        pref.remove(PreferencesService.ACCESS_KEY_NAME);
        pref.remove(PreferencesService.ACCESS_SECRET_NAME);
    }

    private DropBoxService(Context c)
    {
        context_ = c.getApplicationContext();
        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        api_ = new DropboxAPI<AndroidAuthSession>(session);
    }

    private void destroy()
    {
        api_ = null;
        context_ = null;
    }

    private AndroidAuthSession buildSession()
    {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        PreferencesService pref = new PreferencesService(context_);
        String key = pref.get(PreferencesService.ACCESS_KEY_NAME);
        String sec = pref.get(PreferencesService.ACCESS_SECRET_NAME);

        if (key != null && sec != null)
        {
            AccessTokenPair accessToken = new AccessTokenPair(key, sec);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
                    accessToken);
        }
        else
        {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

    public String handleException(Exception ex)
    {
        String error = null;
        if (ex instanceof DropboxUnlinkedException)
        {
            // This session wasn't authenticated properly or user unlinked
            error = "This app wasn't authenticated properly.";
        }
        else if (ex instanceof DropboxFileSizeException)
        {
            // File size too big to upload via the API
            error = "This file is too big to upload";
        }
        else if (ex instanceof DropboxPartialFileException)
        {
            // We canceled the operation
            error = "Upload canceled";
        }
        else if (ex instanceof DropboxServerException)
        {
            DropboxServerException e = (DropboxServerException) ex;
            // Server-side exception. These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._401_UNAUTHORIZED)
            {
                // Unauthorized, so we should unlink them. You may want to
                // automatically log the user out in this case.
            }
            else if (e.error == DropboxServerException._403_FORBIDDEN)
            {
                // Not allowed to access this
            }
            else if (e.error == DropboxServerException._404_NOT_FOUND)
            {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            }
            else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE)
            {
                // user is over quota
            }
            else
            {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            error = e.body.userError;
            if (error == null)
            {
                error = e.body.error;
            }
        }
        else if (ex instanceof DropboxIOException)
        {
            // Happens all the time, probably want to retry automatically.
            error = "Network error.  Try again.";
        }
        else if (ex instanceof DropboxParseException)
        {
            // Probably due to Dropbox server restarting, should retry
            error = "Dropbox error.  Try again.";
        }
        else if (ex instanceof DropboxException)
        {
            // Unknown error
            error = "Unknown error.  Try again.";
        }
        else
            return null; // can't handle this type of exception

        return error;
    }

    private Context                        context_;
    private DropboxAPI<AndroidAuthSession> api_;
    private static DropBoxService          instance_;
    // dropbox keys
    final static private String            APP_KEY     = "ahtmcea8sy7m8bo";
    final static private String            APP_SECRET  = "x2jwbg8j0f0w7nw";
    final static private AccessType        ACCESS_TYPE = AccessType.APP_FOLDER;
}

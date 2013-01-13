/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.io.ByteArrayOutputStream;

import net.costcalculator.service.JSONSerializer;
import net.costcalculator.util.LOG;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

/**
 * Logic is responsible for downloading a file in a background thread
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class DownloadFileLogic extends AsyncTask<Void, Long, Boolean>
{

    public DownloadFileLogic(Context context, DropboxAPI<?> api,
            String dropboxPath)
    {
        // We set the context this way so we don't accidentally leak activities
        context_ = context.getApplicationContext();

        api_ = api;
        path_ = dropboxPath;

        pd_ = new ProgressDialog(context);
        pd_.setMessage(path_);
        pd_.setTitle(context_.getResources()
                .getString(R.string.s_msg_uploading));
        pd_.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd_.setCancelable(false);
        pd_.show();
    }

    public String getFileContent()
    {
        if (bos_ != null)
            return new String(bos_.toByteArray());
        else
            return new String();
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            bos_ = new ByteArrayOutputStream();
            api_.getFile(path_, null, bos_, null);
            JSONSerializer.importCostItemsFromJSON(getFileContent());
            return true;
        }
        catch (DropboxUnlinkedException e)
        {
            // This session wasn't authenticated properly or user unlinked
            errorMessage_ = "This app wasn't authenticated properly.";
        }
        catch (DropboxFileSizeException e)
        {
            // File size too big to upload via the API
            errorMessage_ = "This file is too big to upload";
        }
        catch (DropboxPartialFileException e)
        {
            // We canceled the operation
            errorMessage_ = "Upload canceled";
        }
        catch (DropboxServerException e)
        {
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
            errorMessage_ = e.body.userError;
            if (errorMessage_ == null)
            {
                errorMessage_ = e.body.error;
            }
        }
        catch (DropboxIOException e)
        {
            // Happens all the time, probably want to retry automatically.
            errorMessage_ = "Network error.  Try again.";
        }
        catch (DropboxParseException e)
        {
            // Probably due to Dropbox server restarting, should retry
            errorMessage_ = "Dropbox error.  Try again.";
        }
        catch (DropboxException e)
        {
            // Unknown error
            errorMessage_ = "Unknown error.  Try again.";
        }
        catch (Exception e)
        {
            errorMessage_ = e.getLocalizedMessage();
            LOG.E(e.getMessage());
        }
        
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        pd_.dismiss();
        if (result)
        {
            showToast(context_.getResources().getString(
                    R.string.s_msg_dropbox_download_ok));
        }
        else
        {
            showToast(errorMessage_);
        }
    }

    private void showToast(String msg)
    {
        Toast error = Toast.makeText(context_, msg, Toast.LENGTH_LONG);
        error.show();
    }

    private DropboxAPI<?>        api_;
    private String               path_;

    private Context              context_;
    private final ProgressDialog pd_;

    private String               errorMessage_;
    ByteArrayOutputStream        bos_;
}

/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.io.ByteArrayInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

/**
 * Logic is responsible for uploading a file in a background thread
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class UploadFileLogic extends AsyncTask<Void, Long, Boolean>
{

    public UploadFileLogic(Context context, DropboxAPI<?> api,
            String dropboxPath, String file)
    {
        // We set the context this way so we don't accidentally leak activities
        context_ = context.getApplicationContext();

        fileLen_ = file.length();
        api_ = api;
        path_ = dropboxPath;
        file_ = file;

        pd_ = new ProgressDialog(context);
        pd_.setMax(100);
        pd_.setMessage(path_);
        pd_.setTitle(context_.getResources()
                .getString(R.string.s_msg_uploading));
        pd_.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd_.setProgress(0);
        pd_.setCancelable(false);
        pd_.show();
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            ByteArrayInputStream bis = new ByteArrayInputStream(
                    file_.getBytes());
            request_ = api_.putFileRequest(path_, bis, file_.length(), null,
                    new ProgressListener()
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
                            publishProgress(bytes);
                        }
                    });

            if (request_ != null)
            {
                request_.upload();
                return true;
            }

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
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress)
    {
        int percent = (int) (100.0 * (double) progress[0] / fileLen_ + 0.5);
        pd_.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        pd_.dismiss();
        if (result)
        {
            showToast(context_.getResources().getString(
                    R.string.s_msg_dropbox_backup_ok));
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
    private String               file_;

    private long                 fileLen_;
    private UploadRequest        request_;
    private Context              context_;
    private final ProgressDialog pd_;

    private String               errorMessage_;
}

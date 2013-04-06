/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import net.costcalculator.service.DropBoxService;
import net.costcalculator.service.ProgressCallback;
import net.costcalculator.util.LOG;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Task is responsible for downloading a file in a background thread. Task is
 * intended for using only from UI thread because show progress dialog to the
 * user.
 * 
 * <pre>
 * DownloadFileTask t = new DownloadFileTask(activity_context, path);
 * t.execute();
 * t.get();
 * t.getFile();
 * t.getErrors();
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class DownloadFileTask extends AsyncTask<Void, Long, Boolean> implements
        ProgressCallback
{
    public DownloadFileTask(Context context, String path,
            AsyncTaskCompletionHandler<DownloadFileTask> handler)
    {
        context_ = context;
        path_ = path;
        handler_ = handler;
        errors_ = new ArrayList<String>();
    }

    public ArrayList<String> getErrors()
    {
        return errors_;
    }

    @Override
    protected void onPreExecute()
    {
        pd_ = new ProgressDialog(context_);
        pd_.setMax(100);
        pd_.setMessage(path_);
        pd_.setTitle(context_.getResources().getString(
                R.string.s_msg_downloading));
        pd_.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd_.setProgress(0);
        pd_.setCancelable(false);
        pd_.show();
    }

    public String getPath()
    {
        return path_;
    }

    public String getFile()
    {
        String s = new String();
        if (bos_ != null)
        {
            byte[] buffer = bos_.toByteArray();
            if (buffer.length >= 3 && buffer[0] == (byte) 0xEF
                    && buffer[1] == (byte) 0xBB && buffer[2] == (byte) 0xBF)
            {
                try
                {
                    s = new String(buffer, 3, buffer.length - 3, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    s = new String(buffer);
                }
            }
            else
                s = new String(buffer);
        }

        return s;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            bos_ = DropBoxService.instance().downloadFile(path_, this);
            return true;
        }
        catch (Exception e)
        {
            String problem = DropBoxService.instance().handleException(e);
            if (problem != null)
                errors_.add(problem);
            else if (e.getLocalizedMessage() != null)
                errors_.add(e.getLocalizedMessage());
            else if (e.getMessage() != null)
                errors_.add(e.getMessage());
            else
                errors_.add(context_.getResources().getString(
                        R.string.s_err_unknown_error));
        }

        return false;
    }

    @Override
    public void publishProgress(long x, long total)
    {
        LOG.D("Download progress: " + x + " of " + total);
        publishProgress((long) (100.0 * (double) x / total));
    }

    @Override
    protected void onProgressUpdate(Long... progress)
    {
        pd_.setProgress(progress[0].intValue());
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        pd_.dismiss();
        if (handler_ != null)
            handler_.taskComplete(this);
    }

    @Override
    protected void onCancelled()
    {
        pd_.dismiss();
        if (handler_ != null)
            handler_.taskComplete(this);
    }

    private String                               path_;
    private Context                              context_;
    private ProgressDialog                       pd_;
    private ByteArrayOutputStream                bos_;
    private ArrayList<String>                    errors_;
    AsyncTaskCompletionHandler<DownloadFileTask> handler_;
}

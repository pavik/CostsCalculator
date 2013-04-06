/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.util.ArrayList;

import net.costcalculator.service.DropBoxService;
import net.costcalculator.service.ProgressCallback;
import net.costcalculator.util.LOG;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Task is responsible for uploading a file in a background thread. Task is
 * intended for using only from UI thread because show progress dialog to the
 * user.
 * 
 * <pre>
 * UploadFileTask t = new UploadFileTask(activity_context, path, file);
 * t.execute();
 * t.get();
 * t.getErrors();
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class UploadFileTask extends AsyncTask<Void, Long, Boolean> implements
        ProgressCallback
{

    public UploadFileTask(Context context, String path, String file,
            AsyncTaskCompletionHandler<UploadFileTask> handler)
    {
        context_ = context;
        errors_ = new ArrayList<String>();
        path_ = path;
        file_ = file;
        fileLen_ = file.length();
        handler_ = handler;
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
            return DropBoxService.instance().uploadFile(path_, file_, this);
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
        LOG.D("Upload progress: " + x + " of " + total);
        publishProgress(x);
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

    private String                             path_;
    private String                             file_;
    private long                               fileLen_;
    private Context                            context_;
    private ArrayList<String>                  errors_;
    private ProgressDialog                     pd_;
    AsyncTaskCompletionHandler<UploadFileTask> handler_;
}

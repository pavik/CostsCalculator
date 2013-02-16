/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.util.ArrayList;

import net.costcalculator.service.ImportService;
import net.costcalculator.service.ImportStatistic;
import net.costcalculator.service.ProgressCallback;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Task is responsible for importing expenses in a background thread. Task is
 * intended for using only from UI thread because shows progress dialog to the
 * user.
 * 
 * <pre>
 * ImportExpensesTask t = new ImportExpensesTask(activity_context);
 * t.execute(jsonstring);
 * ImportStatistic stat = t.get();
 * t.getErrors();
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ImportExpensesTask extends
        AsyncTask<String, Long, ImportStatistic> implements ProgressCallback
{
    public ImportExpensesTask(Context c, int importMode,
            AsyncTaskCompletionHandler<ImportExpensesTask> handler)
    {
        context_ = c;
        errors_ = new ArrayList<String>();
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
        pd_.setMessage("Dropbox");
        pd_.setTitle(context_.getResources()
                .getString(R.string.s_msg_uploading));
        pd_.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd_.setProgress(0);
        pd_.setCancelable(false);
        pd_.show();
    }

    @Override
    protected ImportStatistic doInBackground(String... params)
    {
        try
        {
            ImportStatistic stat = ImportService.importExpensesFromJSONString(
                    context_, params[0], importMode_, this);
            return stat;
        }
        catch (Exception e)
        {
            if (e.getLocalizedMessage() != null)
                errors_.add(e.getLocalizedMessage());
            else if (e.getMessage() != null)
                errors_.add(e.getMessage());
            else
                errors_.add(context_.getResources().getString(
                        R.string.s_err_unknown_error));
        }
        return null;
    }

    @Override
    public void publishProgress(long x, long total)
    {
        publishProgress((long) (100.0 * (double) x / total));
    }

    @Override
    protected void onProgressUpdate(Long... progress)
    {
        pd_.setProgress(progress[0].intValue());
    }

    @Override
    protected void onPostExecute(ImportStatistic result)
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

    private int                                    importMode_;
    private Context                                context_;
    private ProgressDialog                         pd_;
    private ArrayList<String>                      errors_;
    AsyncTaskCompletionHandler<ImportExpensesTask> handler_;
}

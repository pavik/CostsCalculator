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
import net.costcalculator.service.DropboxEntry;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Task is responsible for retrieving list of files in dropbox folders. Task is
 * intended for using only from UI thread because show progress dialog to the
 * user.
 * 
 * Usage:
 * 
 * <pre>
 * GetFolderContentTask t = new GetFolderContentTask(activity_context, completion_handler);
 * t.execute(param1, param2, ...);
 * t.get();
 * t.getErrors();
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class GetFolderContentTask extends
        AsyncTask<String, Long, ArrayList<DropboxEntry>>
{
    public GetFolderContentTask(Context context,
            AsyncTaskCompletionHandler<GetFolderContentTask> handler)
    {
        context_ = context;
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
        pd_.setMessage(context_.getResources().getString(
                R.string.s_msg_list_available_backups));
        pd_.setTitle("Dropbox");
        pd_.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd_.setCancelable(false);
        pd_.show();
    }

    @Override
    protected ArrayList<DropboxEntry> doInBackground(String... s)
    {
        ArrayList<DropboxEntry> list = new ArrayList<DropboxEntry>();
        for (int i = 0; i < s.length; ++i)
        {
            ArrayList<DropboxEntry> dir = new ArrayList<DropboxEntry>();
            try
            {
                dir = DropBoxService.instance().getFolderContent(s[i]);
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
            list.addAll(dir);
            if (isCancelled())
                break;
        }

        return list;
    }

    @Override
    protected void onProgressUpdate(Long... progress)
    {
    }

    @Override
    protected void onPostExecute(ArrayList<DropboxEntry> result)
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

    private Context                                          context_;
    private ProgressDialog                                   pd_;
    private ArrayList<String>                                errors_;
    private AsyncTaskCompletionHandler<GetFolderContentTask> handler_;
}

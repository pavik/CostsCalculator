/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.util.ArrayList;
import org.json.simple.JSONArray;

import net.costcalculator.service.DataFormatService;
import net.costcalculator.service.DropBoxService;
import net.costcalculator.service.DropboxEntry;
import net.costcalculator.service.ImportService;
import net.costcalculator.service.ImportStatistic;
import net.costcalculator.service.JSONSerializerService;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Logic is responsible for setup data on the view and handling user requests
 * 
 * <pre>
 * Usage:
 * {
 *     // create instance
 *     BackupLogic l = new BackupLogic(activity);
 * 
 *     // activity uses logic
 * 
 *     // destroy view
 *     l.release();
 *     l = null;
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class BackupLogic
{
    public BackupLogic(Activity a)
    {
        context_ = a;
        tvIntro_ = (TextView) context_.findViewById(R.id.tv_backup_intro);
        btnLinkUnlink = (Button) context_.findViewById(R.id.btn_link_dropbox);
        btnBackup = (Button) context_.findViewById(R.id.btn_backup_dropbox);
        btnRestore = (Button) context_.findViewById(R.id.btn_restore_dropbox);
        lvDropbox = (ListView) context_.findViewById(R.id.lv_drop_box_list);
        lvDropbox.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                    long id)
            {
                if (id >= 0)
                    selectEntryRequest(id);
            }
        });

        btnLinkUnlink.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                linkUnlinkRequest();
            }
        });
        btnBackup.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                backupRequest();
            }
        });
        btnRestore.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                readDirRequest();
            }
        });

        DropBoxService.create(context_);
        boolean isLinked = DropBoxService.instance().getDropboxAPI()
                .getSession().isLinked();
        updateView(isLinked);
    }

    public void release()
    {
        DropBoxService.release();
    }

    public void onActivityResume()
    {
        AndroidAuthSession session = DropBoxService.instance().getDropboxAPI()
                .getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful())
        {
            try
            {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                DropBoxService.instance().storeKeys(tokens.key, tokens.secret);
            }
            catch (IllegalStateException e)
            {
                showToast(e.getLocalizedMessage());
                LOG.E("Error authenticating: " + e.getLocalizedMessage());
            }
        }

        boolean isLinked = DropBoxService.instance().getDropboxAPI()
                .getSession().isLinked();
        updateView(isLinked);
        if (isLinked)
            readDirRequest();
    }

    public void selectEntryRequest(long id)
    {
        if (drobboxAdapter_ != null)
        {
            final String selectedFileName = drobboxAdapter_.getFileName(id);
            String message = context_.getResources().getString(
                    R.string.s_msg_confirm_restore)
                    + " " + selectedFileName;
            AlertDialog.Builder builder = new AlertDialog.Builder(context_);
            builder.setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    restoreRequest(selectedFileName);
                                }
                            }).setNegativeButton(R.string.cancel, null);

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void linkUnlinkRequest()
    {
        if (DropBoxService.instance().getDropboxAPI().getSession().isLinked())
        {
            DropBoxService.instance().getDropboxAPI().getSession().unlink();

            // Clear our stored keys
            DropBoxService.instance().clearKeys();
            updateView(false);
        }
        else
        {
            // Start the remote authentication
            DropBoxService.instance().getDropboxAPI().getSession()
                    .startAuthentication(context_);
        }
    }

    private void backupRequest()
    {
        try
        {
            String path = "/" + DataFormatService.getBackupFileNameNow();
            JSONArray list = JSONSerializerService
                    .getAllExpensesAsJSON(context_);
            if (list.size() == 0)
            {
                showToast(context_.getResources().getString(
                        R.string.s_msg_no_data_for_backuping));
                return;
            }

            String json = list.toJSONString();
            UploadFileTask t = new UploadFileTask(context_, path, json,
                    new AsyncTaskCompletionHandler<UploadFileTask>()
                    {
                        @Override
                        public void taskComplete(UploadFileTask task)
                        {
                            backupRequestComplete(task);
                        }
                    });
            t.execute();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
    }

    private void backupRequestComplete(UploadFileTask t)
    {
        try
        {
            if (t == null)
                throw new Exception("invalid argument: t");

            if (t.isCancelled())
            {
                showToast("Task was cancelled.");
                return;
            }

            if (t.get())
                showToast(context_.getResources().getString(
                        R.string.s_msg_dropbox_backup_ok));
            else
            {
                ArrayList<String> errors = t.getErrors();
                if (errors.size() > 0)
                    showToast(errors.get(0));
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
    }

    private void restoreRequest(String fileName)
    {
        if (fileName == null || fileName.length() == 0)
            return;

        try
        {
            DownloadFileTask dl = new DownloadFileTask(context_,
                    "/" + fileName,
                    new AsyncTaskCompletionHandler<DownloadFileTask>()
                    {
                        @Override
                        public void taskComplete(DownloadFileTask task)
                        {
                            downlowadRequestComplete(task);
                        }
                    });
            dl.execute();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
    }

    private void downlowadRequestComplete(DownloadFileTask task)
    {
        try
        {
            if (task == null)
                throw new Exception("invalid argument: task");

            if (task.isCancelled())
            {
                showToast("Task was cancelled.");
                return;
            }

            if (task.get())
            {
                // import
                final String json = task.getFile();
                ImportExpensesTask imTask = new ImportExpensesTask(context_,
                        ImportService.IMPORT_SIMPLE,
                        new AsyncTaskCompletionHandler<ImportExpensesTask>()
                        {
                            @Override
                            public void taskComplete(ImportExpensesTask task)
                            {
                                importRequestComplete(task);
                            }
                        });
                imTask.execute(json);
            }
            else
            {
                ArrayList<String> errors = task.getErrors();
                if (errors.size() > 0)
                    showToast(errors.get(0));
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
    }

    private void importRequestComplete(ImportExpensesTask task)
    {
        try
        {
            if (task == null)
                throw new Exception("invalid argument: task");

            if (task.isCancelled())
            {
                showToast("Task was cancelled.");
                return;
            }

            ImportStatistic stat = task.get();
            if (stat != null)
                showImportStatistic(stat);
            else
            {
                ArrayList<String> errors = task.getErrors();
                if (errors.size() > 0)
                    showToast(errors.get(0));
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
    }

    private void readDirRequest()
    {
        try
        {
            GetFolderContentTask task = new GetFolderContentTask(context_,
                    new AsyncTaskCompletionHandler<GetFolderContentTask>()
                    {
                        @Override
                        public void taskComplete(GetFolderContentTask task)
                        {
                            readDirRequestComplete(task);
                        }
                    });
            task.execute("/");
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
    }

    private void readDirRequestComplete(GetFolderContentTask task)
    {
        try
        {
            if (task == null)
                throw new Exception("invalid argument: task");

            if (task.isCancelled())
            {
                showToast("Task was cancelled.");
                return;
            }

            ArrayList<DropboxEntry> files = task.get();
            ArrayList<String> errors = task.getErrors();

            if (files.size() == 0)
            {
                if (errors.size() == 0)
                    showToast(context_.getResources().getString(
                            R.string.s_msg_no_backup_files_in_dropbox));
                else
                    showToast(errors.get(0));
            }
            else
            {
                drobboxAdapter_ = new DropboxAdapter(context_, files);
                lvDropbox.setAdapter(drobboxAdapter_);
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }
    }

    private void updateView(boolean isLinked)
    {
        tvIntro_.setVisibility(isLinked ? View.GONE : View.VISIBLE);
        btnBackup.setVisibility(isLinked ? View.GONE : View.GONE);
        btnRestore.setVisibility(isLinked ? View.GONE : View.GONE);
        lvDropbox.setVisibility(isLinked ? View.VISIBLE : View.GONE);

        btnLinkUnlink.setText(isLinked ? R.string.s_btn_logout_dropbox
                : R.string.s_btn_login_dropbox);
    }

    private void showImportStatistic(ImportStatistic stat)
    {
        final RelativeLayout rl = (RelativeLayout) context_.getLayoutInflater()
                .inflate(R.layout.dialog_import_statistic, null);

        final Dialog d = new Dialog(context_);
        ListView lv = (ListView) rl.findViewById(R.id.lv_items);
        lv.setAdapter(new ImportStatisticAdapter(context_, stat));
        Button ok = (Button) rl.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                d.dismiss();
            }
        });

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(rl);
        d.show();
    }

    private void showToast(String msg)
    {
        if (msg == null)
            return;
        Toast t = Toast.makeText(context_, msg, Toast.LENGTH_LONG);
        t.show();
    }

    private Activity       context_;
    private TextView       tvIntro_;
    private Button         btnLinkUnlink;
    private Button         btnBackup;
    private Button         btnRestore;
    private ListView       lvDropbox;
    private DropboxAdapter drobboxAdapter_;
}

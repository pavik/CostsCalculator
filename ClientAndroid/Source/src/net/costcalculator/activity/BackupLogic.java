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
import net.costcalculator.service.JSONSerializer;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
public class BackupLogic implements OnClickListener, OnItemClickListener,
        DialogInterface.OnClickListener
{
    public BackupLogic(Activity a)
    {
        context_ = a;
        tvIntro_ = (TextView) context_.findViewById(R.id.tv_backup_intro);
        btnLinkUnlink = (Button) context_.findViewById(R.id.btn_link_dropbox);
        btnBackup = (Button) context_.findViewById(R.id.btn_backup_dropbox);
        btnRestore = (Button) context_.findViewById(R.id.btn_restore_dropbox);
        lvDropbox = (ListView) context_.findViewById(R.id.lv_drop_box_list);
        View header = context_.getLayoutInflater().inflate(
                R.layout.view_list_header, null);
        TextView tvHeaderText = (TextView) header
                .findViewById(R.id.textViewListHeader);
        tvHeaderText.setText(R.string.s_dropbox_file_list);
        lvDropbox.addHeaderView(header);
        lvDropbox.setOnItemClickListener(this);

        btnLinkUnlink.setOnClickListener(this);
        btnBackup.setOnClickListener(this);
        btnRestore.setOnClickListener(this);

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        api_ = new DropboxAPI<AndroidAuthSession>(session);

        updateView();
    }

    public void release()
    {
    }

    public void onActivityResume()
    {
        AndroidAuthSession session = api_.getSession();

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
                storeKeys(tokens.key, tokens.secret);
            }
            catch (IllegalStateException e)
            {
                showToast(e.getLocalizedMessage());
                LOG.E("Error authenticating: " + e.getLocalizedMessage());
            }
        }
        updateView();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_link_dropbox:
            linkUnlinkRequest();
            break;
        case R.id.btn_backup_dropbox:
            backupRequest();
            break;
        case R.id.btn_restore_dropbox:
            readDirRequest();
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id)
    {
        if (drobboxDir_ != null)
        {
            selectedFileName_ = drobboxDir_.getItem(pos);
            String message = context_.getResources().getString(
                    R.string.s_msg_confirm_restore)
                    + " " + selectedFileName_;
            AlertDialog.Builder builder = new AlertDialog.Builder(context_);
            builder.setMessage(message).setCancelable(true)
                    .setPositiveButton(R.string.confirm, this)
                    .setNegativeButton(R.string.cancel, this);

            alert_ = builder.create();
            alert_.show();
        }
    }

    @Override
    public void onClick(DialogInterface di, int btn)
    {
        if (DialogInterface.BUTTON_POSITIVE == btn)
        {
            restoreRequest(selectedFileName_);
        }
    }

    private void linkUnlinkRequest()
    {
        if (api_.getSession().isLinked())
        {
            api_.getSession().unlink();

            // Clear our stored keys
            clearKeys();
            updateView();
        }
        else
        {
            // Start the remote authentication
            api_.getSession().startAuthentication(context_);
        }
    }

    private void backupRequest()
    {
        try
        {
            String path = "/" + DataFormatService.getBackupFileNameNow();
            JSONArray list = JSONSerializer.getAllCostItemsJSON();
            if (list.size() == 0)
            {
                showToast(context_.getResources().getString(
                        R.string.s_msg_no_data_for_backuping));
                return;
            }

            UploadFileLogic uploadLogic = new UploadFileLogic(context_, api_,
                    path, list.toJSONString());
            uploadLogic.execute();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
            showToast(e.getLocalizedMessage());
        }
        updateView();
    }

    private void restoreRequest(String fileName)
    {
        if (fileName == null || fileName.length() == 0)
            return;

        try
        {
            DownloadFileLogic dl = new DownloadFileLogic(context_, api_, "/"
                    + fileName);
            dl.execute();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
            showToast(e.getLocalizedMessage());
        }
        updateView();
    }

    private void readDirRequest()
    {
        try
        {
            Entry dir = api_.metadata("/", 0, null, true, null);
            ArrayList<String> files = new ArrayList<String>();
            for (Entry e : dir.contents)
            {
                if (!e.isDir)
                    files.add(e.fileName());
            }

            if (files.size() == 0)
            {
                showToast(context_.getResources().getString(
                        R.string.s_msg_no_backup_files_in_dropbox));
                return;
            }

            drobboxDir_ = new ArrayAdapter<String>(context_,
                    android.R.layout.simple_list_item_1, files);
            lvDropbox.setAdapter(drobboxDir_);
        }
        catch (DropboxException e)
        {
            ErrorHandler.handleException(e, context_);
            showToast(e.getLocalizedMessage());
        }
        updateView();
    }

    private void updateView()
    {
        boolean isLinked = api_.getSession().isLinked();

        tvIntro_.setVisibility(isLinked ? View.GONE : View.VISIBLE);
        btnBackup.setVisibility(isLinked ? View.VISIBLE : View.GONE);
        btnRestore.setVisibility(isLinked ? View.VISIBLE : View.GONE);
        lvDropbox.setVisibility(isLinked ? View.VISIBLE : View.GONE);

        btnLinkUnlink.setText(isLinked ? R.string.s_btn_logout_dropbox
                : R.string.s_btn_login_dropbox);
    }

    private void showToast(String msg)
    {
        Toast error = Toast.makeText(context_, msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     * 
     * @return Array of [access_key, access_secret], or null if none stored
     */
    private String[] getKeys()
    {
        SharedPreferences prefs = context_.getSharedPreferences(
                ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null)
        {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        }
        else
        {
            return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret)
    {
        // Save the access key for later
        SharedPreferences prefs = context_.getSharedPreferences(
                ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys()
    {
        SharedPreferences prefs = context_.getSharedPreferences(
                ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession()
    {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null)
        {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
                    accessToken);
        }
        else
        {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

    private AlertDialog                    alert_;
    private Activity                       context_;
    private TextView                       tvIntro_;
    private Button                         btnLinkUnlink;
    private Button                         btnBackup;
    private Button                         btnRestore;
    private ListView                       lvDropbox;
    // access to dropbox API
    private DropboxAPI<AndroidAuthSession> api_;

    private String                         selectedFileName_;
    private ArrayAdapter<String>           drobboxDir_;
    // dropbox keys
    final static private String            APP_KEY            = "ahtmcea8sy7m8bo";
    final static private String            APP_SECRET         = "x2jwbg8j0f0w7nw";

    final static private AccessType        ACCESS_TYPE        = AccessType.APP_FOLDER;

    final static private String            ACCOUNT_PREFS_NAME = "expenses_options";
    final static private String            ACCESS_KEY_NAME    = "ACCESS_KEY";
    final static private String            ACCESS_SECRET_NAME = "ACCESS_SECRET";
}

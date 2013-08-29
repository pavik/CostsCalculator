
package net.costcalculator.activity;

import java.util.Calendar;
import java.util.Date;

import net.costcalculator.service.CostItemsService;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.service.DropBoxService;
import net.costcalculator.service.JSONSerializerService;
import net.costcalculator.service.PreferencesService;
import net.costcalculator.util.LOG;

import org.json.simple.JSONArray;

import com.dropbox.client2.exception.DropboxIOException;

import android.content.Context;

public class BackupService
{
    /**
     * Perform backup if since latest backup time there are any insert/update
     * modifications in the expenses, skip backup if there were no modifications
     * or only delete modifications were made.
     * 
     * @param context
     * @throws Exception
     */
    public static void backupExpensesDropbox(Context context) throws Exception
    {
        LOG.T("BackupService::backupExpensesDropbox");

        // check if we should do automatic backup
        PreferencesService s = new PreferencesService(context);
        String latestBackupStr = s
                .get(PreferencesService.LATEST_DROPBOX_BACKUP_TIME);
        if (latestBackupStr != null)
        {
            long latestBackupTime = Long.parseLong(latestBackupStr);
            CostItemsService cis = new CostItemsService(context);
            long latestModificationTime = cis.getLatestModificationTime();
            cis.release();
            if (latestModificationTime == 0)
            {
                LOG.D("Dropbox backup skipped - no expenses found");
                return;
            }
            LOG.D(new Date(latestBackupTime).toLocaleString());
            LOG.D(new Date(latestModificationTime).toLocaleString());
            if (latestBackupTime >= latestModificationTime)
            {
                LOG.D("Dropbox backup skipped - no modifications found since latest backup");
                return;
            }
        }

        boolean release = false;
        if (DropBoxService.instance() == null)
        {
            DropBoxService.create(context);
            release = true;
        }

        try
        {
            final long currentTime = Calendar.getInstance().getTimeInMillis();
            JSONArray list = JSONSerializerService
                    .getAllExpensesAsJSON(context);
            String path = "/" + DataFormatService.getBackupFileNameNow();

            final int BACKUP_SUCCESS = -1;
            int attempt = 3;
            while (attempt > 0)
            {
                try
                {
                    DropBoxService.instance().uploadFile(path,
                            list.toJSONString(), null);
                    attempt = BACKUP_SUCCESS;
                }
                catch (DropboxIOException e)
                {
                    --attempt;
                    Thread.sleep(10000); // wait 10 seconds
                    LOG.D("Trying to upload backup, attempt " + attempt);
                }
            }
            if (attempt == BACKUP_SUCCESS)
                s.set(PreferencesService.LATEST_DROPBOX_BACKUP_TIME,
                        Long.toString(currentTime));
        }
        finally
        {
            if (release)
                DropBoxService.release();
        }
    }
}

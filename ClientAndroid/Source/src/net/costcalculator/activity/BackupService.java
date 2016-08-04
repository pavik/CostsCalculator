
package net.costcalculator.activity;

import java.util.Calendar;

import net.costcalculator.service.CostItemsService;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.service.DropBoxService;
import net.costcalculator.service.JSONSerializerService;
import net.costcalculator.service.PreferencesService;
import net.costcalculator.util.LOG;

import org.json.simple.JSONArray;

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

        PreferencesService s = new PreferencesService(context);
        String accessToken = s.get(PreferencesService.ACCESS_TOKEN);
        if (accessToken == null)
        {
            LOG.E("Dropbox account is not configured.");
            return;
        }

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
            else if (latestBackupTime >= latestModificationTime)
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
                catch (Exception e)
                {
                    --attempt;
                    LOG.E(e.getMessage());
                    LOG.D("Trying to upload backup, attempts remain: " + attempt);
                    Thread.sleep(1000); // wait 1 second
                }
            }
            
            if (attempt == BACKUP_SUCCESS)
                s.set(PreferencesService.LATEST_DROPBOX_BACKUP_TIME,
                        Long.toString(currentTime));
        }
        catch (Exception ex)
        {
            LOG.E(ex.getMessage());
        }
        finally
        {
            if (release)
                DropBoxService.release();
        }
        
        LOG.T("BackupService::backupExpensesDropbox return");
    }
}

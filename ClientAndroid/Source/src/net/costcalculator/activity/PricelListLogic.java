/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.util.ArrayList;

import net.costcalculator.dialog.AlertDialog;
import net.costcalculator.dialog.DialogConfirmListener;
import net.costcalculator.dialog.MenuDialog;
import net.costcalculator.dialog.MenuItemClickedListener;
import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemRecord;
import net.costcalculator.service.CostItemRecordsAdapter;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Logic is responsible for setup data on the view and handling user requests
 * 
 * <pre>
 * Usage:
 * {
 *     // create instance
 *     PricelListLogic l = new PricelListLogic(activity, costItemId);
 * 
 *     // activity uses logic
 * 
 *     // destroy logic
 *     l.release();
 *     l = null;
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class PricelListLogic implements DialogConfirmListener,
        MenuItemClickedListener
{
    public PricelListLogic(FragmentActivity a, long costItemId)
            throws Exception
    {
        LOG.T("PricelListLogic::PricelListLogic");

        activity_ = a;
        costItemId_ = costItemId;
        adapter_ = new CostItemRecordsAdapter(activity_, costItemId);
        activity_.setTitle(adapter_.getCostItemName());

        // initialize price list
        ListView lv = (ListView) activity_.findViewById(R.id.lv_price_list);
        View header = activity_.getLayoutInflater().inflate(
                R.layout.view_list_header, null);
        TextView tvHeaderText = (TextView) header
                .findViewById(R.id.textViewListHeader);
        tvHeaderText.setText(R.string.s_history_of_expenses);
        lv.addHeaderView(header);
        lv.setAdapter(adapter_);
        lv.setOnItemLongClickListener(new OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
                    long id)
            {
                if (pos > 0)
                {
                    contextMenuRequest(pos - 1);
                    return true;
                }
                else
                    return false;
            }
        });

        // support screen rotation when dialog is visible
        rebindListenersForActiveFragments();
    }

    public void release()
    {
        LOG.T("PricelListLogic::release()");

        activity_ = null;
        if (adapter_ != null)
        {
            adapter_.release();
            adapter_ = null;
        }
    }

    public void okRequest(long r)
    {
        adapter_.newCostItemRecord(r);
    }

    public void cancelRequest()
    {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_EDIT_ITEM)
        {
            if (resultCode == Activity.RESULT_OK && adapter_ != null
                    && data != null)
            {
                long id = data.getLongExtra(
                        ActivityEditPrice.EXTRA_COST_ITEM_RECORD_ID, 0);
                if (id > 0)
                    adapter_.updateCostItemRecord(id);
                else
                    LOG.E("PricelListLogic::onActivityResult - invalid activity result");
            }
        }
    }

    @Override
    public void onMenuItemClicked(int dialogid, String name, int pos, int param)
    {
        if (DIALOG_ID_MENU_ACTION == dialogid)
        {
            switch (pos)
            {
            case 0:
                editMenuRequest(param);
                break;
            case 1:
                moveMenuRequest(param);
                break;
            case 2:
                deleteMenuRequest(param);
                break;
            }
        }
        else if (DIALOG_ID_MENU_SELITEM == dialogid)
            confirmMoveRequest(param, pos);
    }

    @Override
    public void onConfirm(int dialogid, String text, int param)
    {
        if (DIALOG_ID_ALERT_DEL == dialogid)
            deleteItemRequest(param);
        else if (DIALOG_ID_ALERT_MOVE == dialogid)
        {
            int pos = (param & 0x0000FFFF);
            int expensePos = (param & 0xFFFF0000) >> 16;
            moveExpensesRequest(pos, expensePos);
        }
    }

    private void contextMenuRequest(int pos)
    {
        if (pos >= 0 && pos < adapter_.getCount())
        {
            CostItemRecord cir = adapter_.getCostItemRecord(pos);
            MenuDialog md = new MenuDialog();
            md.setDialogId(DIALOG_ID_MENU_ACTION);
            md.setParam(pos);
            md.setHeader(DataFormatService.formatPrice(cir.getSum()) + " "
                    + cir.getCurrency());
            Resources r = activity_.getResources();
            String[] items = new String[] { r.getString(R.string.edit),
                    r.getString(R.string.move), r.getString(R.string.delete) };
            int[] icons = new int[] { R.drawable.ic_edit_2, R.drawable.ic_move,
                    R.drawable.ic_delete };
            md.setItems(items);
            md.setIcons(icons);
            md.setItemClickedListener(this);
            md.show(activity_.getSupportFragmentManager(), TAG_MENU_DIALOG);
        }
    }

    private void editMenuRequest(int pos)
    {
        if (pos >= 0 && pos < adapter_.getCount())
        {
            Intent intent = new Intent(activity_, ActivityEditPrice.class);
            intent.putExtra(ActivityEditPrice.EXTRA_COST_ITEM_RECORD_ID,
                    adapter_.getCostItemRecord(pos).getId());
            intent.putExtra(ActivityEditPrice.EXTRA_COST_ITEM_ID, costItemId_);
            activity_.startActivityForResult(intent, REQUEST_EDIT_ITEM);
        }
    }

    private void deleteMenuRequest(int pos)
    {
        if (pos >= 0 && pos < adapter_.getCount())
        {
            CostItemRecord cir = adapter_.getCostItemRecord(pos);
            final String rawWarn = activity_.getResources().getString(
                    R.string.warning_del_category);
            final String formattedWarn = String.format(
                    rawWarn,
                    DataFormatService.formatPrice(cir.getSum()) + " "
                            + cir.getCurrency());

            AlertDialog ad = new AlertDialog();
            ad.setDialogId(DIALOG_ID_ALERT_DEL);
            ad.setHeaderId(R.string.warning);
            ad.setText(formattedWarn);
            ad.setParam(pos);
            ad.setIconId(R.drawable.ic_delete_large);
            ad.setConfirmListener(this);
            ad.show(activity_.getSupportFragmentManager(), TAG_ALERT_DIALOG);
        }
    }

    private void moveMenuRequest(int pos)
    {
        try
        {
            if (pos >= 0 && pos < adapter_.getCount())
            {
                CostItemRecord cir = adapter_.getCostItemRecord(pos);
                String s = activity_.getResources().getString(
                        R.string.s_move_item_to);

                MenuDialog md = new MenuDialog();
                md.setDialogId(DIALOG_ID_MENU_SELITEM);
                md.setParam(pos);
                md.setHeader(String.format(
                        s,
                        DataFormatService.formatPrice(cir.getSum()) + " "
                                + cir.getCurrency()));
                ArrayList<CostItem> expenses = adapter_.getAllCostItems();
                String[] items = new String[expenses.size()];
                int[] icons = new int[items.length];
                for (int i = 0; i < items.length; ++i)
                {
                    items[i] = expenses.get(i).getName();
                    icons[i] = R.drawable.ic_folder_small;
                }
                md.setItems(items);
                md.setIcons(icons);
                md.setItemClickedListener(this);
                md.show(activity_.getSupportFragmentManager(), TAG_MENU_DIALOG);
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void deleteItemRequest(int pos)
    {
        try
        {
            if (pos >= 0 && pos < adapter_.getCount())
                adapter_.deletePosition(pos);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void confirmMoveRequest(int pos, int expensePos)
    {
        try
        {
            ArrayList<CostItem> expenses = adapter_.getAllCostItems();
            if (pos >= 0 && pos < adapter_.getCount() && expensePos >= 0
                    && expensePos < expenses.size())
            {
                CostItem ci = expenses.get(expensePos);
                CostItemRecord cir = adapter_.getCostItemRecord(pos);
                final String rawWarn = activity_.getResources().getString(
                        R.string.warning_move_item);
                final String formattedWarn = String.format(
                        rawWarn,
                        DataFormatService.formatPrice(cir.getSum()) + " "
                                + cir.getCurrency(), ci.getName());

                AlertDialog ad = new AlertDialog();
                ad.setDialogId(DIALOG_ID_ALERT_MOVE);
                ad.setHeaderId(R.string.warning);
                ad.setText(formattedWarn);
                ad.setIconId(R.drawable.ic_move_large);
                ad.setConfirmListener(this);
                ad.setParam(pos | (expensePos << 16));
                ad.show(activity_.getSupportFragmentManager(), TAG_ALERT_DIALOG);
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void moveExpensesRequest(int pos, int expensePos)
    {
        try
        {
            ArrayList<CostItem> expenses = adapter_.getAllCostItems();
            if (pos >= 0 && pos < adapter_.getCount() && expensePos >= 0
                    && expensePos < expenses.size())
            {
                CostItem ci = expenses.get(expensePos);
                CostItemRecord cir = adapter_.getCostItemRecord(pos);
                adapter_.moveExpense(cir, ci);
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void rebindListenersForActiveFragments()
    {
        Fragment f = null;

        f = activity_.getSupportFragmentManager().findFragmentByTag(
                TAG_MENU_DIALOG);
        if (f != null && f instanceof MenuDialog)
        {
            MenuDialog md = (MenuDialog) f;
            md.setItemClickedListener(this);
        }
        f = activity_.getSupportFragmentManager().findFragmentByTag(
                TAG_ALERT_DIALOG);
        if (f != null && f instanceof net.costcalculator.dialog.AlertDialog)
        {
            net.costcalculator.dialog.AlertDialog ad = (net.costcalculator.dialog.AlertDialog) f;
            ad.setConfirmListener(this);
        }
    }

    private long                   costItemId_;
    private FragmentActivity       activity_;
    private CostItemRecordsAdapter adapter_;

    private static final int       REQUEST_EDIT_ITEM      = 0x100;
    private static final String    TAG_MENU_DIALOG        = "fragment_menu_dialog";
    private static final String    TAG_ALERT_DIALOG       = "fragment_alert_dialog";
    private static final int       DIALOG_ID_ALERT_DEL    = 1;
    private static final int       DIALOG_ID_ALERT_MOVE   = 2;
    private static final int       DIALOG_ID_MENU_ACTION  = 3;
    private static final int       DIALOG_ID_MENU_SELITEM = 4;
}

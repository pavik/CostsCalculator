/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.activity.R;
import net.costcalculator.dialog.AlertDialog;
import net.costcalculator.dialog.DialogConfirmListener;
import net.costcalculator.dialog.MenuItemClickedListener;
import net.costcalculator.dialog.EditTextDialog;
import net.costcalculator.dialog.MenuDialog;
import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemAdapter;
import net.costcalculator.service.CostItemAdapterMainView;
import net.costcalculator.service.CostItemsService;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * Logic is responsible for setup data on the view and handling user requests
 * 
 * <pre>
 * Usage:
 * {
 *     // create instance
 *     ExpenseItemsLogic l = new ExpenseItemsLogic(activity);
 * 
 *     // activity uses view
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
public class ExpenseItemsLogic implements DialogConfirmListener,
        MenuItemClickedListener
{
    public static final String TAG_EDIT_DLG     = "fragment_edit_dialog";
    public static final String TAG_MENU_DIALOG  = "fragment_menu_dialog";
    public static final String TAG_ALERT_DIALOG = "fragment_alert_dialog";

    public ExpenseItemsLogic(FragmentActivity a) throws Exception
    {
        LOG.T("ExpenseItemsLogic::ExpenseItemsLogic");

        activity_ = a;
        gridView_ = (GridView) activity_.findViewById(R.id.gridExpenseItems);
        if (gridView_ == null)
            throw new Exception("findViewById failed, id = "
                    + R.id.gridExpenseItems);

        cis_ = new CostItemsService(activity_);
        viewbuilder_ = new CostItemAdapterMainView(activity_,
                cis_.getCostItemRecordsCount());
        adapter_ = new CostItemAdapter(activity_, viewbuilder_);
        gridView_.setAdapter(adapter_);
        gridView_.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id)
            {
                Intent intent = new Intent(activity_, PriceListActivity.class);
                intent.putExtra(PriceListActivity.COST_ITEM_ID, id);
                activity_.startActivity(intent);
            }
        });
        gridView_
                .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> av, View v,
                            int pos, long id)
                    {
                        if (id > 0)
                        {
                            categoryMenuRequest(pos);
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
        LOG.T("ExpenseItemsLogic::release()");

        activity_ = null;
        gridView_ = null;
        if (adapter_ != null)
            adapter_.release();
        adapter_ = null;
        if (cis_ != null)
            cis_.release();
        cis_ = null;
        viewbuilder_ = null;
    }

    public void refreshView()
    {
        viewbuilder_.setCounts(cis_.getCostItemRecordsCount());
        adapter_.reload();
    }

    public EditTextDialog createEditTextDialog(boolean newcategory)
    {
        EditTextDialog etd = new EditTextDialog();
        etd.setDialogId(newcategory ? DIALOG_ID_NEW : DIALOG_ID_EDIT);
        etd.setHeaderId(newcategory ? R.string.new_expense_item
                : R.string.change_category);
        etd.setHint(R.string.et_expense_item_name_hint);
        etd.setMaxLen(30);
        etd.setConfirmListener(this);
        return etd;
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
        {
            confirmMoveRequest(param, pos);
        }
    }

    @Override
    public void onConfirm(int dialogid, String param1, int param2)
    {
        if (DIALOG_ID_EDIT == dialogid)
        {
            if (param1 != null && param2 > 0)
                editExpenseCategory(param1, param2);
        }
        else if (DIALOG_ID_NEW == dialogid)
        {
            if (param1 != null)
                addExpenseCategory(param1);
        }
        else if (DIALOG_ID_ALERT_DEL == dialogid)
        {
            if (param2 >= 0)
                deleteExpenseCategory(param2);
        }
        else if (DIALOG_ID_ALERT_MOVE == dialogid)
        {
            int from = (param2 & 0x0000FFFF);
            int to = (param2 & 0xFFFF0000) >> 16;
            if (from >= 0 && to >= 0)
                moveExpensesRequest(from, to);
        }
    }

    private void categoryMenuRequest(final int pos)
    {
        if (pos >= 0 && pos < adapter_.getCount())
        {
            CostItem ci = adapter_.getCostItem(pos);
            MenuDialog md = new MenuDialog();
            md.setDialogId(DIALOG_ID_MENU_ACTION);
            md.setParam(pos);
            md.setHeader(ci.getName());
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

    private void editMenuRequest(final int pos)
    {
        if (pos >= 0 && pos < adapter_.getCount())
        {
            EditTextDialog etd = createEditTextDialog(false);
            etd.setParam(pos);
            CostItem ci = adapter_.getCostItem(pos);
            etd.setText(ci.getName());
            etd.show(activity_.getSupportFragmentManager(), TAG_EDIT_DLG);
        }
    }

    private void deleteMenuRequest(final int pos)
    {
        if (pos >= 0 && pos < adapter_.getCount())
        {
            CostItem ci = adapter_.getCostItem(pos);
            final String rawWarn = activity_.getResources().getString(
                    R.string.warning_del_category);
            final String formattedWarn = String.format(rawWarn, ci.getName());

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

    private void moveMenuRequest(final int posFrom)
    {
        try
        {
            if (posFrom >= 0 && posFrom < adapter_.getCount())
            {
                CostItem ci = adapter_.getCostItem(posFrom);
                String s = activity_.getResources().getString(
                        R.string.s_move_to);

                MenuDialog md = new MenuDialog();
                md.setDialogId(DIALOG_ID_MENU_SELITEM);
                md.setParam(posFrom);
                md.setHeader(String.format(s, ci.getName()));
                String[] items = new String[adapter_.getCount()];
                int[] icons = new int[adapter_.getCount()];
                for (int i = 0; i < items.length; ++i)
                {
                    items[i] = adapter_.getCostItem(i).getName();
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

    private void deleteExpenseCategory(int pos)
    {
        try
        {
            adapter_.deletePosition(pos);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void confirmMoveRequest(int posFrom, int posTo)
    {
        if (posFrom >= 0 && posTo >= 0 && posFrom < adapter_.getCount()
                && posTo < adapter_.getCount())
        {
            CostItem ciFrom = adapter_.getCostItem(posFrom);
            CostItem ciTo = adapter_.getCostItem(posTo);
            final String rawWarn = activity_.getResources().getString(
                    R.string.warning_move_category);
            final String formattedWarn = String.format(rawWarn,
                    ciFrom.getName(), ciTo.getName());

            AlertDialog ad = new AlertDialog();
            ad.setDialogId(DIALOG_ID_ALERT_MOVE);
            ad.setHeaderId(R.string.warning);
            ad.setText(formattedWarn);
            ad.setIconId(R.drawable.ic_move_large);
            ad.setConfirmListener(this);
            ad.setParam(posFrom | (posTo << 16));
            ad.show(activity_.getSupportFragmentManager(), TAG_ALERT_DIALOG);
        }
    }

    private void moveExpensesRequest(int fromCat, int toCat)
    {
        try
        {
            adapter_.moveExpenses(fromCat, toCat);
            viewbuilder_.setCounts(cis_.getCostItemRecordsCount());
            adapter_.refreshView();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void addExpenseCategory(String name)
    {
        try
        {
            adapter_.addNewCostItem(name);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void editExpenseCategory(String name, int pos)
    {
        try
        {
            adapter_.changeName(name, pos);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void rebindListenersForActiveFragments()
    {
        Fragment f = activity_.getSupportFragmentManager().findFragmentByTag(
                TAG_EDIT_DLG);
        if (f != null && f instanceof EditTextDialog)
        {
            EditTextDialog etd = (EditTextDialog) f;
            etd.setConfirmListener(this);
        }
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

    private GridView                gridView_;
    private FragmentActivity        activity_;
    private CostItemAdapter         adapter_;
    private CostItemAdapterMainView viewbuilder_;
    private CostItemsService        cis_;
    private static final int        DIALOG_ID_EDIT         = 1;
    private static final int        DIALOG_ID_NEW          = 2;
    private static final int        DIALOG_ID_ALERT_DEL    = 3;
    private static final int        DIALOG_ID_ALERT_MOVE   = 4;
    private static final int        DIALOG_ID_MENU_ACTION  = 5;
    private static final int        DIALOG_ID_MENU_SELITEM = 6;
}

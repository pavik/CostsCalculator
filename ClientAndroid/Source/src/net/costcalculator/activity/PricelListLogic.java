/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemAdapter;
import net.costcalculator.service.CostItemAdapterSimpleView;
import net.costcalculator.service.CostItemRecord;
import net.costcalculator.service.CostItemRecordsAdapter;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
public class PricelListLogic
{
    public PricelListLogic(Activity a, long costItemId) throws Exception
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
                if (id > 0)
                {
                    contextMenuRequest(id);
                    return true;
                }
                else
                    return false;
            }
        });
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

    private void contextMenuRequest(final long id)
    {
        CostItemRecord cir = adapter_.getCostItemRecord(id);

        final RelativeLayout menu = (RelativeLayout) activity_
                .getLayoutInflater().inflate(R.layout.dialog_expense_cat_menu,
                        null);

        TextView header = (TextView) menu
                .findViewById(R.id.tvDlgExpenseCatMenu);
        header.setText(DataFormatService.formatPrice(cir.getSum()) + " "
                + cir.getCurrency());

        final Dialog d = new Dialog(activity_);
        d.setCanceledOnTouchOutside(true);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(menu);

        LinearLayout edit = (LinearLayout) menu
                .findViewById(R.id.menu_edit_layout);
        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                d.dismiss();
                editMenuRequest(id);
            }
        });

        LinearLayout delete = (LinearLayout) menu
                .findViewById(R.id.menu_del_layout);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                d.dismiss();
                deleteMenuRequest(id);
            }
        });

        LinearLayout move = (LinearLayout) menu
                .findViewById(R.id.menu_move_layout);
        move.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                d.dismiss();
                moveMenuRequest(id);
            }
        });

        d.show();
    }

    private void editMenuRequest(long id)
    {
        Intent intent = new Intent(activity_, ActivityEditPrice.class);
        intent.putExtra(ActivityEditPrice.EXTRA_COST_ITEM_RECORD_ID, id);
        intent.putExtra(ActivityEditPrice.EXTRA_COST_ITEM_ID, costItemId_);
        activity_.startActivityForResult(intent, REQUEST_EDIT_ITEM);
    }

    private void deleteMenuRequest(final long id)
    {
        CostItemRecord cir = adapter_.getCostItemRecord(id);
        final String rawWarn = activity_.getResources().getString(
                R.string.warning_del_category);
        final String formattedWarn = String.format(
                rawWarn,
                DataFormatService.formatPrice(cir.getSum()) + " "
                        + cir.getCurrency());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
        builder.setMessage(formattedWarn)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                deleteItemRequest(id);
                            }
                        }).setNegativeButton(R.string.cancel, null)
                .setIcon(R.drawable.ic_delete_large).setTitle(R.string.warning);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void moveMenuRequest(final long id)
    {
        try
        {
            CostItemRecord cir = adapter_.getCostItemRecord(id);
            final RelativeLayout rl = (RelativeLayout) activity_
                    .getLayoutInflater().inflate(
                            R.layout.dialog_select_expense_cat, null);

            TextView header = (TextView) rl.findViewById(R.id.tv_move_to);
            String s = activity_.getResources().getString(
                    R.string.s_move_item_to);
            header.setText(String.format(
                    s,
                    DataFormatService.formatPrice(cir.getSum()) + " "
                            + cir.getCurrency()));

            final Dialog d = new Dialog(activity_);
            ListView lv = (ListView) rl.findViewById(R.id.lv_categories);
            lv.setAdapter(new CostItemAdapter(activity_,
                    new CostItemAdapterSimpleView(activity_)));
            lv.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int posTo,
                        long catId)
                {
                    if (id > 0)
                        confirmMoveRequest(id, catId, d);
                }
            });

            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(rl);
            d.show();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void deleteItemRequest(long id)
    {
        try
        {
            adapter_.deletePosition(id);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void confirmMoveRequest(final long id, final long catId,
            final Dialog d)
    {
        try
        {
            CostItem ci = adapter_.getCostItem(catId);
            CostItemRecord cir = adapter_.getCostItemRecord(id);
            final String rawWarn = activity_.getResources().getString(
                    R.string.warning_move_item);
            final String formattedWarn = String.format(
                    rawWarn,
                    DataFormatService.formatPrice(cir.getSum()) + " "
                            + cir.getCurrency(), ci.getName());

            AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
            builder.setMessage(formattedWarn)
                    .setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which)
                                {
                                    d.dismiss();
                                    moveExpensesRequest(id, catId);
                                }
                            }).setNegativeButton(R.string.cancel, null)
                    .setIcon(R.drawable.ic_move_large)
                    .setTitle(R.string.warning);

            AlertDialog alert = builder.create();
            alert.show();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void moveExpensesRequest(long id, long catId)
    {
        try
        {
            adapter_.moveExpenses(id, catId);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private long                   costItemId_;
    private Activity               activity_;
    private CostItemRecordsAdapter adapter_;

    private int                    REQUEST_EDIT_ITEM = 0x100;
}

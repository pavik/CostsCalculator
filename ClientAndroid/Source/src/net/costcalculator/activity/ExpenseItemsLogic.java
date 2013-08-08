/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.activity.R;
import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemAdapter;
import net.costcalculator.service.CostItemAdapterMainView;
import net.costcalculator.service.CostItemAdapterSimpleView;
import net.costcalculator.service.CostItemsService;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

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
public class ExpenseItemsLogic
{
    public ExpenseItemsLogic(Activity a) throws Exception
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

    public void onActivityRestart()
    {
        viewbuilder_.setCounts(cis_.getCostItemRecordsCount());
        adapter_.reload();
    }

    public void newExpenseCategoryRequest()
    {
        final RelativeLayout newItemView = (RelativeLayout) activity_
                .getLayoutInflater().inflate(R.layout.dialog_new_expense_item,
                        null);
        final Dialog d = new Dialog(activity_);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(newItemView);

        Button confirm = (Button) newItemView.findViewById(R.id.btn_confirm);
        Button cancel = (Button) newItemView.findViewById(R.id.btn_cancel);
        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                d.dismiss();
                EditText editName = (EditText) newItemView
                        .findViewById(R.id.et_expense_item_name);
                addExpenseCategory(editName.getText().toString().trim());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                d.dismiss();
            }
        });
        d.show();
    }

    private void addExpenseCategory(String name)
    {
        try
        {
            if (name.length() > 0)
                adapter_.addNewCostItem(name);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void categoryMenuRequest(final int pos)
    {
        CostItem ci = adapter_.getCostItem(pos);
        final RelativeLayout menu = (RelativeLayout) activity_
                .getLayoutInflater().inflate(R.layout.dialog_expense_cat_menu,
                        null);

        TextView header = (TextView) menu
                .findViewById(R.id.tvDlgExpenseCatMenu);
        header.setText(ci.getName());

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
                editMenuRequest(pos);
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
                deleteMenuRequest(pos);
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
                moveMenuRequest(pos);
            }
        });

        d.show();
    }

    private void editMenuRequest(final int pos)
    {
        final RelativeLayout newItemView = (RelativeLayout) activity_
                .getLayoutInflater().inflate(R.layout.dialog_new_expense_item,
                        null);
        final Dialog d = new Dialog(activity_);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(newItemView);

        TextView tvHeader = (TextView) newItemView
                .findViewById(R.id.tvDlgNewExpenseCat);
        tvHeader.setText(R.string.change_category);

        CostItem ci = adapter_.getCostItem(pos);
        final EditText editName = (EditText) newItemView
                .findViewById(R.id.et_expense_item_name);
        editName.setText(ci.getName());
        Button confirm = (Button) newItemView.findViewById(R.id.btn_confirm);
        Button cancel = (Button) newItemView.findViewById(R.id.btn_cancel);
        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                d.dismiss();
                try
                {
                    adapter_.changeName(editName.getText().toString().trim(),
                            pos);
                }
                catch (Exception e)
                {
                    ErrorHandler.handleException(e, activity_);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                d.dismiss();
            }
        });
        d.show();
    }

    private void deleteMenuRequest(final int pos)
    {
        CostItem ci = adapter_.getCostItem(pos);
        final String rawWarn = activity_.getResources().getString(
                R.string.warning_del_category);
        final String formattedWarn = String.format(rawWarn, ci.getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
        builder.setMessage(formattedWarn)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                deleteCategoryRequest(pos);
                            }
                        }).setNegativeButton(R.string.cancel, null)
                .setIcon(R.drawable.ic_delete_large).setTitle(R.string.warning);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void moveMenuRequest(final int posFrom)
    {
        try
        {
            CostItem ci = adapter_.getCostItem(posFrom);
            final RelativeLayout rl = (RelativeLayout) activity_
                    .getLayoutInflater().inflate(
                            R.layout.dialog_select_expense_cat, null);

            TextView header = (TextView) rl.findViewById(R.id.tv_move_to);
            String s = activity_.getResources().getString(R.string.s_move_to);
            header.setText(String.format(s, ci.getName()));

            final Dialog d = new Dialog(activity_);
            ListView lv = (ListView) rl.findViewById(R.id.lv_categories);
            lv.setAdapter(new CostItemAdapter(activity_,
                    new CostItemAdapterSimpleView(activity_)));
            lv.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int posTo,
                        long id)
                {
                    if (id > 0)
                        confirmMoveRequest(posFrom, id, d);
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

    private void deleteCategoryRequest(int pos)
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

    private void confirmMoveRequest(final int posFrom, final long id,
            final Dialog d)
    {
        CostItem ciFrom = adapter_.getCostItem(posFrom);
        CostItem ciTo = adapter_.getCostItem(id);
        final String rawWarn = activity_.getResources().getString(
                R.string.warning_move_category);
        final String formattedWarn = String.format(rawWarn, ciFrom.getName(),
                ciTo.getName());

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
                                moveExpensesRequest(posFrom, id);
                            }
                        }).setNegativeButton(R.string.cancel, null)
                .setIcon(R.drawable.ic_move_large).setTitle(R.string.warning);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void moveExpensesRequest(int fromCat, long id)
    {
        try
        {
            adapter_.moveExpenses(fromCat, id);
            viewbuilder_.setCounts(cis_.getCostItemRecordsCount());
            adapter_.refreshView();
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private GridView                gridView_;
    private Activity                activity_;
    private CostItemAdapter         adapter_;
    private CostItemAdapterMainView viewbuilder_;
    private CostItemsService        cis_;
}

/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.activity.R;
import net.costcalculator.service.CostItemAdapter;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

        adapter_ = new CostItemAdapter(activity_);
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
    }

    public void release()
    {
        LOG.T("ExpenseItemsLogic::release()");

        activity_ = null;
        gridView_ = null;
        if (adapter_ != null)
            adapter_.release();
        adapter_ = null;
    }

    public void onActivityRestart()
    {
        adapter_.notifyDataSetChanged();
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
                EditText editName = (EditText) newItemView
                        .findViewById(R.id.et_expense_item_name);
                addExpenseCategory(editName.getText().toString().trim());
                d.dismiss();
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

    private GridView        gridView_;
    private Activity        activity_;
    private CostItemAdapter adapter_;
}

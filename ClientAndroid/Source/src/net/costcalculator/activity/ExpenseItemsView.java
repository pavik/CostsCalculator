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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * View is responsible for displaying available cost categories
 * 
 * Usage: <code>
 * // create instance
 * ExpenseItemsView view = new ExpenseItemsView(activity);
 * 
 * // activity uses view
  * 
 * // destroy view
 * view.release();
 * view = null;
 * </code>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ExpenseItemsView implements OnItemClickListener,
        DialogInterface.OnClickListener, View.OnClickListener
{
    public ExpenseItemsView(Activity a) throws Exception
    {
        activity_ = a;
        gridView_ = (GridView) activity_.findViewById(R.id.gridExpenseItems);
        if (gridView_ == null)
            throw new Exception("findViewById failed, id = "
                    + R.id.gridExpenseItems);

        adapter_ = new CostItemAdapter(activity_);
        gridView_.setAdapter(adapter_);
        gridView_.setOnItemClickListener(this);
    }

    public void release()
    {
        activity_ = null;
        gridView_ = null;
        alert_ = null;
        if (adapter_ != null)
            adapter_.release();
        adapter_ = null;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id)
    {
        LOG.T("onItemClick");
        // Intent intent = new Intent(activity_, CostItemActivity.class);
        // intent.putExtra(CostItemActivity.COST_ITEM_ID, id);
        // activity_.startActivity(intent);
    }

    @Override
    public void onClick(DialogInterface di, int btn)
    {
        if (DialogInterface.BUTTON_POSITIVE == btn)
        {
            try
            {
                EditText edit = (EditText) alert_
                        .findViewById(R.id.et_expense_item_name);
                adapter_.addNewCostItem(
                        edit.getText().toString());
            }
            catch (Exception e)
            {
                ErrorHandler.handleException(e, activity_);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.new_expense_item:
            showNewExpenseItemView();
            break;
        }
    }

    private void showNewExpenseItemView()
    {
        RelativeLayout newItemView = (RelativeLayout) activity_
                .getLayoutInflater().inflate(R.layout.dialog_new_expense_item, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
        builder.setView(newItemView);
        builder.setMessage(R.string.new_expense_item).setCancelable(false)
                .setPositiveButton(R.string.confirm, this)
                .setNegativeButton(R.string.cancel, this);

        alert_ = builder.create();
        alert_.show();
    }

    private AlertDialog alert_;
    private GridView    gridView_;
    private Activity    activity_;
    private CostItemAdapter adapter_;
}

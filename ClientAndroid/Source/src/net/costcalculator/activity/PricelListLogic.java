/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import net.costcalculator.service.CostItemRecordsAdapter;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Logic is responsible for setup data on the view and handling user requests
 * 
 * <pre>
 * Usage:
 * {
 *     // create instance
 *     PricelListLogic l = new PricelListLogic(activity);
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
public class PricelListLogic implements OnClickListener
{
    public PricelListLogic(Activity a, long costItemId) throws Exception
    {
        LOG.T("PricelListView::PricelListView()");

        viewsVisibility_ = false;
        activity_ = a;
        adapter_ = new CostItemRecordsAdapter(activity_, costItemId);
        activity_.setTitle(adapter_.getCostItemName());

        // initialize price list
        ListView lv = (ListView) activity_.findViewById(R.id.lv_price_list);
        lv.setAdapter(adapter_);

        // set save button onClick callback
        btnSave_ = (Button) activity_.findViewById(R.id.btn_save_price);
        btnSave_.setOnClickListener(this);

        // find other views
        tvPrice_ = (TextView) activity_.findViewById(R.id.tv_price);
        tvDate_ = (TextView) activity_.findViewById(R.id.tv_date);

        etPrice_ = (EditText) activity_.findViewById(R.id.et_price);
        etCurrency_ = (EditText) activity_.findViewById(R.id.et_currency);
        etComment_ = (EditText) activity_.findViewById(R.id.et_comment);
        etTag_ = (EditText) activity_.findViewById(R.id.et_tag);
        etDate_ = (EditText) activity_.findViewById(R.id.et_date);
        etDate_.setEnabled(false);

        initViewsContent();
        showViews();
    }

    public void release()
    {
        LOG.T("PricelListView::release()");

        activity_ = null;
        if (adapter_ != null)
        {
            adapter_.release();
            adapter_ = null;
        }
    }

    @Override
    public void onClick(View v)
    {
        try
        {
            switch (v.getId())
            {
            case R.id.btn_save_price:
                if (viewsVisibility_)
                    saveNewPrice();
                else
                    showViews();
                break;
            }
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, activity_);
        }
    }

    private void saveNewPrice() throws Exception
    {
        String sumStr = etPrice_.getText().toString().trim();
        if (sumStr.length() == 0)
        {
            showPriceWarning();
            return;
        }

        double sum = Double.parseDouble(sumStr);
        String comment = etComment_.getText().toString().trim();
        String tag = etTag_.getText().toString().trim();
        String currency = etCurrency_.getText().toString().trim();

        adapter_.addNewCostItemRecord(priceRecordDate_, sum, comment, currency,
                tag);

        initViewsContent();
        hideViews();
    }

    private void showPriceWarning()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
        builder.setMessage(R.string.price_warning).setPositiveButton(
                R.string.close, null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initViewsContent()
    {
        etPrice_.setText("");
        etCurrency_.setText(Currency.getInstance(Locale.getDefault()).getCurrencyCode());

        priceRecordDate_ = Calendar.getInstance().getTime();
        etDate_.setText(priceRecordDate_.toLocaleString());

        etTag_.setText("");
        etComment_.setText("");
    }

    private void showViews()
    {
        tvPrice_.setVisibility(View.VISIBLE);
        etPrice_.setVisibility(View.VISIBLE);
        etCurrency_.setVisibility(View.VISIBLE);
        tvDate_.setVisibility(View.VISIBLE);
        etDate_.setVisibility(View.VISIBLE);
        etTag_.setVisibility(View.VISIBLE);
        etComment_.setVisibility(View.VISIBLE);

        viewsVisibility_ = true;
        btnSave_.setText(R.string.save_label);
        etPrice_.requestFocus();
    }

    private void hideViews()
    {
        etComment_.setVisibility(View.GONE);
        etTag_.setVisibility(View.GONE);
        etDate_.setVisibility(View.GONE);
        tvDate_.setVisibility(View.GONE);
        etCurrency_.setVisibility(View.GONE);
        etPrice_.setVisibility(View.GONE);
        tvPrice_.setVisibility(View.GONE);

        viewsVisibility_ = false;
        btnSave_.setText(R.string.new_record_label);
    }

    private Date                   priceRecordDate_;

    private TextView               tvPrice_, tvDate_;
    private EditText               etPrice_, etCurrency_, etDate_, etComment_,
            etTag_;
    private Button                 btnSave_;
    boolean                        viewsVisibility_;

    private Activity               activity_;
    private CostItemRecordsAdapter adapter_;
}

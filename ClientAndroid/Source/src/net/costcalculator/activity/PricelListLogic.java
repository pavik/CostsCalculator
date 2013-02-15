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

import net.costcalculator.service.AutocompleteService;
import net.costcalculator.service.CostItemRecord;
import net.costcalculator.service.CostItemRecordsAdapter;
import net.costcalculator.service.CostItemsService;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TextView.OnEditorActionListener;

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
        LOG.T("PricelListLogic::PricelListLogic");

        viewsVisibility_ = false;
        activity_ = a;
        adapter_ = new CostItemRecordsAdapter(activity_, costItemId);
        activity_.setTitle(adapter_.getCostItemName());
        currency_ = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        CostItemRecord cir = CostItemsService.instance()
                .getLatestCostItemRecordByDate(costItemId);
        if (cir != null && cir.getCurrency().length() > 0)
            currency_ = cir.getCurrency();

        // initialize price list
        ListView lv = (ListView) activity_.findViewById(R.id.lv_price_list);
        View header = activity_.getLayoutInflater().inflate(
                R.layout.view_list_header, null);
        TextView tvHeaderText = (TextView) header
                .findViewById(R.id.textViewListHeader);
        tvHeaderText.setText(R.string.s_history_of_expenses);
        lv.addHeaderView(header);
        lv.setAdapter(adapter_);

        // set save button onClick callback
        btnSave_ = (Button) activity_.findViewById(R.id.btn_save_price);
        btnSave_.setOnClickListener(this);

        // find other views
        tvPrice_ = (TextView) activity_.findViewById(R.id.tv_price);
        tvDate_ = (TextView) activity_.findViewById(R.id.tv_date);
        tvDateStr_ = (TextView) activity_.findViewById(R.id.tv_date_string);
        tvTimeStr_ = (TextView) activity_.findViewById(R.id.tv_time_string);

        etPrice_ = (EditText) activity_.findViewById(R.id.et_price);
        etCurrency_ = (AutoCompleteTextView) activity_
                .findViewById(R.id.et_currency);
        etComment_ = (AutoCompleteTextView) activity_
                .findViewById(R.id.et_comment);
        etTag_ = (AutoCompleteTextView) activity_.findViewById(R.id.et_tag);

        etCurrency_.setAdapter(AutocompleteService
                .createCurrenciesAdapter(activity_));
        etComment_.setAdapter(AutocompleteService
                .createCommentsAdapter(activity_));
        etTag_.setAdapter(AutocompleteService.createTagsAdapter(activity_));

        etCurrency_.setThreshold(1);
        etComment_.setThreshold(1);
        etTag_.setThreshold(1);

        etPrice_.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event)
            {
                switch (actionId)
                {
                case EditorInfo.IME_ACTION_NEXT:
                    boolean result = false;
                    TextView v1 = (TextView) v.focusSearch(View.FOCUS_RIGHT);
                    if (v1 != null)
                        result = v1.requestFocus(View.FOCUS_RIGHT);
                    else if (!result)
                    {
                        v1 = (TextView) v.focusSearch(View.FOCUS_DOWN);
                        if (v1 != null)
                            result = v1.requestFocus(View.FOCUS_DOWN);
                    }
                    if (!result)
                        v.onEditorAction(actionId);
                    break;

                default:
                    v.onEditorAction(actionId);
                    break;
                }
                return true;
            }
        });

        tvDateStr_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if (priceRecordDate_ == null)
                    priceRecordDate_ = Calendar.getInstance().getTime();
                DatePickerDialog dpd = new DatePickerDialog(activity_,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth)
                            {
                                dateChanged(year, monthOfYear, dayOfMonth);
                            }
                        }, priceRecordDate_.getYear(), priceRecordDate_
                                .getMonth(), priceRecordDate_.getDate());
                dpd.show();
            }
        });

        tvTimeStr_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (priceRecordDate_ == null)
                    priceRecordDate_ = Calendar.getInstance().getTime();
                TimePickerDialog tpd = new TimePickerDialog(activity_,
                        new TimePickerDialog.OnTimeSetListener()
                        {

                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute)
                            {
                                timeChanged(hourOfDay, minute);
                            }
                        }, priceRecordDate_.getHours(), priceRecordDate_
                                .getMinutes(), false);
                tpd.show();
            }
        });

        showViews();
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
        currency_ = currency;

        adapter_.addNewCostItemRecord(priceRecordDate_, sum, comment, currency,
                tag);

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
        etCurrency_.setText(currency_);

        priceRecordDate_ = Calendar.getInstance().getTime();
        updateDateOnView();
        updateTimeOnView();

        etTag_.setText("");
        etComment_.setText("");
    }

    private void dateChanged(int year, int monthOfYear, int dayOfMonth)
    {
        priceRecordDate_.setYear(year);
        priceRecordDate_.setMonth(monthOfYear);
        priceRecordDate_.setDate(dayOfMonth);
        updateDateOnView();
    }

    private void timeChanged(int hourOfDay, int minute)
    {
        priceRecordDate_.setHours(hourOfDay);
        priceRecordDate_.setMinutes(minute);
        updateTimeOnView();
    }

    private void updateDateOnView()
    {
        String date = DataFormatService.formatDate(priceRecordDate_);
        SpannableString dateSS = new SpannableString(date);
        dateSS.setSpan(new UnderlineSpan(), 0, date.length(), 0);
        tvDateStr_.setText(dateSS);
    }

    private void updateTimeOnView()
    {
        String time = DataFormatService.formatTime(priceRecordDate_);
        SpannableString timeSS = new SpannableString(time);
        timeSS.setSpan(new UnderlineSpan(), 0, time.length(), 0);
        tvTimeStr_.setText(timeSS);
    }

    private void showViews()
    {
        initViewsContent();
        tvPrice_.setVisibility(View.VISIBLE);
        etPrice_.setVisibility(View.VISIBLE);
        etCurrency_.setVisibility(View.VISIBLE);
        tvDate_.setVisibility(View.VISIBLE);
        tvDateStr_.setVisibility(View.VISIBLE);
        tvTimeStr_.setVisibility(View.VISIBLE);
        etTag_.setVisibility(View.VISIBLE);
        etComment_.setVisibility(View.VISIBLE);

        viewsVisibility_ = true;
        btnSave_.setText(R.string.save_label);

        InputMethodManager inputManager = (InputMethodManager) activity_
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etPrice_, InputMethodManager.SHOW_FORCED);
    }

    private void hideViews()
    {
        etComment_.setVisibility(View.GONE);
        etTag_.setVisibility(View.GONE);
        tvDateStr_.setVisibility(View.GONE);
        tvTimeStr_.setVisibility(View.GONE);
        tvDate_.setVisibility(View.GONE);
        etCurrency_.setVisibility(View.GONE);
        etPrice_.setVisibility(View.GONE);
        tvPrice_.setVisibility(View.GONE);

        viewsVisibility_ = false;
        btnSave_.setText(R.string.new_record_label);

        InputMethodManager inputManager = (InputMethodManager) activity_
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = activity_.getCurrentFocus();
        if (v != null)
            inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private Date                   priceRecordDate_;

    private AutoCompleteTextView   etCurrency_, etComment_, etTag_;
    private TextView               tvPrice_, tvDate_, tvDateStr_, tvTimeStr_;
    private EditText               etPrice_;
    private Button                 btnSave_;
    boolean                        viewsVisibility_;

    private String                 currency_;
    private Activity               activity_;
    private CostItemRecordsAdapter adapter_;
}

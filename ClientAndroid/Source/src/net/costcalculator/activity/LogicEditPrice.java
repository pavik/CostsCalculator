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
import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemRecord;
import net.costcalculator.service.CostItemsService;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
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
 *     LogicEditPrice l = new LogicEditPrice(fragment, view, cirId, ciId);
 * 
 *     // logic usage
 * 
 *     // logic destroy
 *     l.release();
 *     l = null;
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class LogicEditPrice
{
    public LogicEditPrice(FragmentEditPrice f, View v, long cirId, long ciId)
    {
        fragment_ = f;
        view_ = v;
        cirId_ = cirId;
        ciId_ = ciId;
        cis_ = new CostItemsService(f.getActivity());
        priceRecordDate_ = Calendar.getInstance().getTime();

        CostItemRecord cir = null;
        try
        {
            if (cirId > 0)
            {
                cir = cis_.getCostItemRecord(cirId_);
                priceRecordDate_ = cir.getCreationTime();
            }
        }
        catch (Exception e)
        {
            LOG.E("LogicEditPrice::LogicEditPrice - getCostItemRecord "
                    + cirId_);
            LOG.E(e.getMessage());
        }

        CostItem ci = null;
        try
        {
            ci = cis_.getCostItemById(ciId_);
            fragment_.getActivity().setTitle(ci.getName());
            ciGuid_ = ci.getGuid();
        }
        catch (Exception e)
        {
            LOG.E("LogicEditPrice::LogicEditPrice - getCostItemById " + ciId_);
            LOG.E(e.getMessage());
        }

        currency_ = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        try
        {
            CostItemRecord currCir = cis_.getLatestCostItemRecordByDate(ciId_);
            if (currCir != null)
                currency_ = currCir.getCurrency();
        }
        catch (Exception e)
        {
            LOG.E("LogicEditPrice::LogicEditPrice - getLatestCostItemRecordByDate "
                    + ciId_);
            LOG.E(e.getMessage());
        }

        // set save button onClick callback
        btnSave_ = (Button) view_.findViewById(R.id.btn_save_price);
        btnSave_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                saveRequest();
            }
        });

        // set cancel button onClick callback
        btnCancel_ = (Button) view_.findViewById(R.id.btn_cancel);
        btnCancel_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                cancelRequest();
            }
        });

        // set new record button onClick callback
        btnNew_ = (Button) view_.findViewById(R.id.btn_new);
        btnNew_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                newRecordRequest();
            }
        });

        // find all views
        tvDateStr_ = (TextView) view_.findViewById(R.id.tv_date_string);
        tvTimeStr_ = (TextView) view_.findViewById(R.id.tv_time_string);
        tvAmount_ = (TextView) view_.findViewById(R.id.tv_price);
        tvDate_ = (TextView) view_.findViewById(R.id.tv_date);

        etPrice_ = (EditText) view_.findViewById(R.id.et_price);
        etCurrency_ = (AutoCompleteTextView) view_
                .findViewById(R.id.et_currency);
        etComment_ = (AutoCompleteTextView) view_.findViewById(R.id.et_comment);
        etTag_ = (AutoCompleteTextView) view_.findViewById(R.id.et_tag);

        etCurrency_.setAdapter(AutocompleteService
                .createCurrenciesAdapter(fragment_.getActivity()));
        etComment_.setAdapter(AutocompleteService
                .createCommentsAdapter(fragment_.getActivity()));
        etTag_.setAdapter(AutocompleteService.createTagsAdapter(fragment_
                .getActivity()));

        etCurrency_.setThreshold(1);
        etComment_.setThreshold(1);
        etTag_.setThreshold(1);

        if (cir != null)
        {
            etPrice_.setText(DataFormatService.formatPrice(cir.getSum()));
            etCurrency_.setText(cir.getCurrency());
            etComment_.setText(cir.getComment());
            etTag_.setText(cir.getTag());
        }
        else
            etCurrency_.setText(currency_);

        updateDateOnView();
        updateTimeOnView();

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

                Calendar c = Calendar.getInstance();
                c.setTime(priceRecordDate_);
                DatePickerDialog dpd = new DatePickerDialog(fragment_
                        .getActivity(),
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth)
                            {
                                dateChanged(year, monthOfYear, dayOfMonth);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                                .get(Calendar.DATE));
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

                Calendar c = Calendar.getInstance();
                c.setTime(priceRecordDate_);
                TimePickerDialog tpd = new TimePickerDialog(fragment_
                        .getActivity(),
                        new TimePickerDialog.OnTimeSetListener()
                        {

                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute)
                            {
                                timeChanged(hourOfDay, minute);
                            }
                        }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true);
                tpd.show();
            }
        });
        showViews();
    }

    public void release()
    {
        if (cis_ != null)
        {
            cis_.release();
            cis_ = null;
        }
    }

    public void showViews()
    {
        btnNew_.setVisibility(View.GONE);
        btnSave_.setVisibility(View.VISIBLE);
        btnCancel_.setVisibility(View.VISIBLE);
        tvAmount_.setVisibility(View.VISIBLE);
        tvDate_.setVisibility(View.VISIBLE);
        etPrice_.setVisibility(View.VISIBLE);
        etCurrency_.setVisibility(View.VISIBLE);
        tvDateStr_.setVisibility(View.VISIBLE);
        tvTimeStr_.setVisibility(View.VISIBLE);
        etTag_.setVisibility(View.VISIBLE);
        etComment_.setVisibility(View.VISIBLE);

        etPrice_.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) fragment_
                .getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(etPrice_, InputMethodManager.SHOW_FORCED);
    }

    public void hideViews()
    {
        btnNew_.setVisibility(View.VISIBLE);
        btnSave_.setVisibility(View.GONE);
        btnCancel_.setVisibility(View.GONE);
        tvAmount_.setVisibility(View.GONE);
        tvDate_.setVisibility(View.GONE);
        etComment_.setVisibility(View.GONE);
        etTag_.setVisibility(View.GONE);
        tvDateStr_.setVisibility(View.GONE);
        tvTimeStr_.setVisibility(View.GONE);
        etCurrency_.setVisibility(View.GONE);
        etPrice_.setVisibility(View.GONE);
        hideIME();
    }

    private void hideIME()
    {
        InputMethodManager inputManager = (InputMethodManager) fragment_
                .getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = fragment_.getActivity().getCurrentFocus();
        if (v != null)
            inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void saveRequest()
    {
        if (ciGuid_ == null)
        {
            LOG.E("LogicEditPrice::saveRequest - ciGuid_ is null");
            fragment_.cancelRequest();
            return;
        }

        try
        {
            String sumStr = DataFormatService.eraseAllCommasAndSpaces(etPrice_
                    .getText().toString().trim());
            if (sumStr.length() == 0)
            {
                showPriceWarning();
                return;
            }

            double sum = Double.parseDouble(sumStr);
            String comment = etComment_.getText().toString().trim();
            String tag = etTag_.getText().toString().trim();
            currency_ = etCurrency_.getText().toString().trim();

            CostItemRecord cir = null;
            if (cirId_ > 0)
                cir = cis_.getCostItemRecord(cirId_);
            else
                cir = cis_.createCostItemRecord(ciGuid_, priceRecordDate_, sum);

            cir.setSum(sum);
            cir.setCreationTime(priceRecordDate_);
            cir.setCurrency(currency_);
            cir.setTag(tag);
            cir.setComment(comment);

            hideIME();
            cis_.saveCostItemRecord(cir);
            fragment_.okRequest(cir.getId());
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, fragment_.getActivity());
        }
    }

    private void cancelRequest()
    {
        hideIME();
        fragment_.cancelRequest();
    }

    private void newRecordRequest()
    {
        initViewsContent();
        showViews();
    }

    private void showPriceWarning()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                fragment_.getActivity());
        builder.setMessage(R.string.price_warning).setPositiveButton(
                R.string.close, null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void dateChanged(int year, int monthOfYear, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(priceRecordDate_);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DATE, dayOfMonth);
        priceRecordDate_ = c.getTime();
        updateDateOnView();
    }

    private void timeChanged(int hourOfDay, int minute)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(priceRecordDate_);
        c.set(Calendar.HOUR, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        priceRecordDate_ = c.getTime();
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

    private void initViewsContent()
    {
        etPrice_.setText("");
        etTag_.setText("");
        etComment_.setText("");
        etCurrency_.setText(currency_);

        priceRecordDate_ = Calendar.getInstance().getTime();
        updateDateOnView();
        updateTimeOnView();
    }

    private FragmentEditPrice fragment_;
    private View              view_;
    private CostItemsService  cis_;
    private AutoCompleteTextView etCurrency_, etComment_, etTag_;
    private TextView             tvDateStr_, tvTimeStr_, tvAmount_, tvDate_;
    private EditText             etPrice_;
    private Button               btnSave_, btnCancel_, btnNew_;
    private Date                 priceRecordDate_;
    private long                 ciId_, cirId_;
    private String               currency_, ciGuid_;
}

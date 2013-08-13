
package net.costcalculator.activity;

import java.util.Calendar;
import java.util.Date;

import net.costcalculator.service.DataFormatService;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class StatisticFragmentCustom extends StatisticFragment
{
    public StatisticFragmentCustom()
    {
        from_ = new Date();
        to_ = new Date();
        days_ = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        layoutid_ = R.layout.view_statistic_report_custom;
        View view = super.onCreateView(inflater, container, savedInstanceState);
        dateFrom_ = (TextView) view.findViewById(R.id.tv_date_from);
        dateTo_ = (TextView) view.findViewById(R.id.tv_date_to);
        daysCount_ = (EditText) view.findViewById(R.id.et_days_count);
        daysCount_.setText(Integer.toString(days_));
        daysCount_.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView arg0, int actionId,
                    KeyEvent arg2)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT)
                    daysCountEntered();
                return false;
            }
        });

        setDate(dateFrom_, from_);
        setDate(dateTo_, to_);

        dateFrom_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if (from_ == null)
                    from_ = Calendar.getInstance().getTime();

                Calendar c = Calendar.getInstance();
                c.setTime(from_);
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth)
                            {
                                dateFromChanged(year, monthOfYear, dayOfMonth);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                                .get(Calendar.DATE));
                dpd.show();
            }
        });

        dateTo_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if (to_ == null)
                    to_ = Calendar.getInstance().getTime();

                Calendar c = Calendar.getInstance();
                c.setTime(to_);
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth)
                            {
                                dateToChanged(year, monthOfYear, dayOfMonth);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                                .get(Calendar.DATE));
                dpd.show();
            }
        });

        return view;
    }

    @Override
    public void initialize(Context c)
    {
        adapter_ = new AdvancedStatisticAdapter(c, from_, to_, period_, days_,
                costitems_, expensesdates_);
    }

    private void setDate(TextView tv, Date d)
    {
        String date = DataFormatService.formatDate(d);
        SpannableString dateSS = new SpannableString(date);
        dateSS.setSpan(new UnderlineSpan(), 0, date.length(), 0);
        tv.setText(dateSS);
    }

    private void dateFromChanged(int year, int monthOfYear, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(from_);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DATE, dayOfMonth);
        from_ = c.getTime();
        setDate(dateFrom_, from_);
        adapter_.setCustomInterval(from_, to_, period_, days_, expensesdates_);
    }

    private void dateToChanged(int year, int monthOfYear, int dayOfMonth)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(to_);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DATE, dayOfMonth);
        to_ = c.getTime();
        setDate(dateTo_, to_);
        adapter_.setCustomInterval(from_, to_, period_, days_, expensesdates_);
    }

    private void daysCountEntered()
    {
        try
        {
            int interval = Integer.parseInt(daysCount_.getText().toString());
            if (interval != days_)
            {
                days_ = interval;
                adapter_.setCustomInterval(from_, to_, period_, days_,
                        expensesdates_);
            }
        }
        catch (Exception e)
        {
            daysCount_.setText(Integer.toString(days_));
        }
    }

    private Date from_, to_;
    private int  days_;
    private TextView dateFrom_, dateTo_;
    private EditText daysCount_;
}

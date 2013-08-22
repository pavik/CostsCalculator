
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import net.costcalculator.adapter.StatisticDetailsAdapter;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.DialogInterface;

public class StatisticDetailsActivity extends Activity
{
    public static final String EXTRA_DATE_FROM  = "date_from"; // long
    public static final String EXTRA_DATE_TO    = "date_to";   // long
    public static final String EXTRA_GUIDTAG    = "guidtag";   // String[]
    public static final String EXTRA_CATNAME    = "catname";   // String[]
    public static final String EXTRA_CAT_OR_TAG = "cat_or_tag"; // boolean

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        LOG.T("StatisticDetailsActivity::onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_details);

        long fromms = getIntent().getLongExtra(EXTRA_DATE_FROM, 0);
        long toms = getIntent().getLongExtra(EXTRA_DATE_TO, 0);
        guidtag_ = getIntent().getStringArrayExtra(EXTRA_GUIDTAG);
        boolean catortag = getIntent().getBooleanExtra(EXTRA_CAT_OR_TAG, true);
        final String[] catname = getIntent().getStringArrayExtra(EXTRA_CATNAME);
        if (fromms == 0 || toms == 0 || guidtag_ == null || catname == null)
            throw new IllegalArgumentException(
                    "missing required extra for intent");

        guidtag_ = removeduplicates(guidtag_);
        final String[] titles = removeduplicates(catname);
        Date from = new Date(fromms);
        Date to = new Date(toms);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(DataFormatService.formatCustom(from, to));

        selectedItems_ = new ArrayList<Integer>();
        for (int i = 0; i < guidtag_.length; ++i)
            selectedItems_.add(i);

        Button btn = (Button) findViewById(R.id.btn_select_items);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                MultiSelectionDialog d = new MultiSelectionDialog(
                        StatisticDetailsActivity.this,
                        R.string.label_select_category, titles, selectedItems_);
                d.setOnConfirmListener(new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String[] values = new String[selectedItems_.size()];
                        for (int i = 0; i < selectedItems_.size(); ++i)
                            values[i] = guidtag_[selectedItems_.get(i)];
                        adapter_.setFilter(values);
                    }
                });
                d.show();
            }
        });

        ListView items = (ListView) findViewById(R.id.lv_report);
        adapter_ = new StatisticDetailsAdapter(this, from, to, guidtag_,
                catortag);
        items.setAdapter(adapter_);
    }

    @Override
    protected void onRestart()
    {
        LOG.T("StatisticDetailsActivity::onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        LOG.T("StatisticDetailsActivity::onDestroy");
        super.onDestroy();
    }

    private String[] removeduplicates(String[] src)
    {
        HashSet<String> table = new HashSet<String>(Arrays.asList(src));
        String[] dest = new String[table.size()];
        for (int i = 0, j = 0; i < src.length; ++i)
        {
            if (table.contains(src[i]))
            {
                dest[j++] = src[i];
                table.remove(src[i]);
            }
        }
        return dest;
    }

    private String[]                guidtag_;
    private ArrayList<Integer>      selectedItems_;
    private StatisticDetailsAdapter adapter_;
}

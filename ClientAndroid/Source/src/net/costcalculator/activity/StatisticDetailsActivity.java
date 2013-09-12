
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import com.google.analytics.tracking.android.EasyTracker;

import net.costcalculator.adapter.StatisticDetailsAdapter;
import net.costcalculator.dialog.MultiSelectionConfirmListener;
import net.costcalculator.dialog.MultiSelectionDialog;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticDetailsActivity extends FragmentActivity implements
        MultiSelectionConfirmListener
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

        if (savedInstanceState != null)
            selectedItems_ = savedInstanceState.getIntegerArrayList("a1");
        else
        {
            selectedItems_ = new ArrayList<Integer>();
            for (int i = 0; i < guidtag_.length; ++i)
                selectedItems_.add(i);
        }

        Button btn = (Button) findViewById(R.id.btn_select_items);
        btn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                MultiSelectionDialog d = new MultiSelectionDialog();
                d.setHeaderId(R.string.label_select_category);
                d.setItems(titles);
                d.setSelectedItems(selectedItems_);
                d.setConfirmListener(StatisticDetailsActivity.this);
                d.show(getSupportFragmentManager(), TAG_MULTISEL_DLG);
            }
        });

        adapter_ = new StatisticDetailsAdapter(this, from, to, guidtag_,
                catortag);
        String[] values = new String[selectedItems_.size()];
        for (int i = 0; i < selectedItems_.size(); ++i)
            values[i] = guidtag_[selectedItems_.get(i)];
        adapter_.setFilter(values);

        ListView items = (ListView) findViewById(R.id.lv_report);
        items.setAdapter(adapter_);

        rebindListenersForActiveFragments();
    }

    @Override
    public void onMultiSelectionConfirmed(int dialogid,
            ArrayList<Integer> selectedItems, int param)
    {
        selectedItems_.clear();
        selectedItems_.addAll(selectedItems);
        String[] values = new String[selectedItems_.size()];
        for (int i = 0; i < selectedItems_.size(); ++i)
            values[i] = guidtag_[selectedItems_.get(i)];
        adapter_.setFilter(values);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (outState != null && selectedItems_ != null)
            outState.putIntegerArrayList("a1", selectedItems_);
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

    private void rebindListenersForActiveFragments()
    {
        Fragment f = null;

        f = getSupportFragmentManager().findFragmentByTag(TAG_MULTISEL_DLG);
        if (f != null && f instanceof MultiSelectionDialog)
        {
            MultiSelectionDialog md = (MultiSelectionDialog) f;
            md.setConfirmListener(this);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    private String[]                guidtag_;
    private ArrayList<Integer>      selectedItems_;
    private StatisticDetailsAdapter adapter_;
    private static final String     TAG_MULTISEL_DLG = "fragment_multi_selection";
}

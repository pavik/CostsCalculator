
package net.costcalculator.activity;

import java.util.ArrayList;

import net.costcalculator.dialog.MultiSelectionConfirmListener;
import net.costcalculator.dialog.MultiSelectionDialog;
import net.costcalculator.util.LOG;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SliderActivity extends FragmentActivity implements
        OnTouchListener, MultiSelectionConfirmListener
{
    public static final String EXTRA_FRAGMENTS = "fragments";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        LOG.T("SliderActivity::onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_activity);

        fragments_ = getIntent().getStringArrayExtra(EXTRA_FRAGMENTS);
        if (fragments_ == null)
            throw new IllegalArgumentException(
                    "illegal call of SliderActivity, missing extra: "
                            + EXTRA_FRAGMENTS);

        tvHeader_ = (TextView) findViewById(R.id.tv_header);
        ImageView btnLeft = (ImageView) findViewById(R.id.img_header_arrow_left);
        btnLeft.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                current_ -= 1;
                showfragment();
            }
        });
        btnLeft.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View arg0)
            {
                showSliderMenu();
                return false;
            }
        });

        ImageView btnRight = (ImageView) findViewById(R.id.img_header_arrow_right);
        btnRight.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                current_ += 1;
                showfragment();
            }
        });
        btnRight.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View arg0)
            {
                showSliderMenu();
                return false;
            }
        });
        if (savedInstanceState != null)
            current_ = savedInstanceState.getInt("SliderActivity1");

        rebindListenersForActiveFragments();
    }

    @Override
    protected void onDestroy()
    {
        LOG.T("SliderActivity::onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            onTouchDown(event.getRawX(), event.getRawY());
        else if (event.getAction() == MotionEvent.ACTION_UP)
            onTouchUp(event.getRawX(), event.getRawY());
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            onTouchDown(event.getRawX(), event.getRawY());
        else if (event.getAction() == MotionEvent.ACTION_UP)
            onTouchUp(event.getRawX(), event.getRawY());
        return super.onTouchEvent(event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (outState != null)
            outState.putInt("SliderActivity1", current_);
    }

    protected SliderFragment getFragment(int index)
    {
        return null;
    }

    protected String getHeaderTitle(int index)
    {
        return "title"; // should be overridden by derived class
    }

    protected String[] getFragmentTitles()
    {
        return null; // should be overridden by derived class
    }

    protected void showfragment()
    {
        replaceview();
    }

    private void onTouchDown(float x, float y)
    {
        touchDownX_ = x;
        touchDownY_ = y;
    }

    private void onTouchUp(float x, float y)
    {
        float touchUpX = x;
        float touchUpY = y;
        float a = Math.abs(touchDownX_ - touchUpX);
        float b = Math.abs(touchDownY_ - touchUpY);
        double c = Math.sqrt(a * a + b * b);
        if (c < 20.0)
            return;
        else if (a / c > sin70_)
        {
            current_ += (touchDownX_ < touchUpX ? -1 : 1);
            showfragment();
        }
    }

    private void replaceview()
    {
        if (fragments_ != null && fragments_.length > 0)
        {
            if (current_ < 0)
                current_ = fragments_.length - 1;
            else if (current_ >= fragments_.length)
                current_ = 0;

            SliderFragment f = null;
            final String FTAG = String.format("fragment_%d", current_);
            Fragment retainfragment = getSupportFragmentManager()
                    .findFragmentByTag(FTAG);

            LOG.D("SliderActivity::replaceview - retainfragment: "
                    + retainfragment);

            if (retainfragment != null
                    && retainfragment instanceof SliderFragment)
                f = (SliderFragment) retainfragment;

            LOG.D("SliderActivity::replaceview - fragment reusing: "
                    + (f != null));

            if (f == null)
            {
                f = getFragment(current_);
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.sliderfragment, f, FTAG).commit();
            }
            f.setPageListener(this);
            tvHeader_.setText(getHeaderTitle(current_));
        }
    }

    private void showSliderMenu()
    {
        final String[] titles = getFragmentTitles();
        if (titles != null)
        {
            selectedFragment_ = new ArrayList<Integer>();
            selectedFragment_.add(current_);
            MultiSelectionDialog d = new MultiSelectionDialog();
            d.setHeaderId(R.string.label_select_statistic);
            d.setItems(titles);
            d.setSelectedItems(selectedFragment_);
            d.setSingleSelection();
            d.setConfirmListener(this);
            d.show(getSupportFragmentManager(), TAG_MULTISEL_DLG);
        }
    }

    @Override
    public void onMultiSelectionConfirmed(int dialogid,
            ArrayList<Integer> selectedItems, int param)
    {
        selectedFragment_.clear();
        selectedFragment_.addAll(selectedItems);
        if (selectedFragment_.size() > 0
                && selectedFragment_.get(0) != current_)
        {
            current_ = selectedFragment_.get(0);
            showfragment();
        }
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

    protected String[]          fragments_;
    private ArrayList<Integer>  selectedFragment_;
    private TextView            tvHeader_;
    private int                 current_         = 0;
    private float               touchDownX_;
    private float               touchDownY_;
    private static final double sin70_           = Math.sin(Math
                                                         .toRadians(70.0));
    private static final String TAG_MULTISEL_DLG = "fragment_multi_selection";
}

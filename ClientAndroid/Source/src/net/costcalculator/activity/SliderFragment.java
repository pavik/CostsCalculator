
package net.costcalculator.activity;

import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

public class SliderFragment extends Fragment
{
    public void setPageListener(OnTouchListener listener)
    {
        pagelistener_ = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        LOG.T("SliderFragment::onCreate");
        super.onDestroy();
    }

    @Override
    public void onDestroy()
    {
        LOG.T("SliderFragment::onDestroy");
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        LOG.T("SliderFragment::onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView()
    {
        LOG.T("SliderFragment::onDestroyView");
        super.onDestroyView();
    }

    protected OnTouchListener pagelistener_;
}


package net.costcalculator.activity;

import android.support.v4.app.Fragment;
import android.view.View.OnTouchListener;

public class SliderFragment extends Fragment
{
    public void setPageListener(OnTouchListener listener)
    {
        pagelistener_ = listener;
    }

    protected OnTouchListener pagelistener_;
}

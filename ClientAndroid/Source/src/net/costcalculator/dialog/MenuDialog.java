
package net.costcalculator.dialog;

import net.costcalculator.activity.R;
import net.costcalculator.adapter.SimpleListAdapter;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuDialog extends BaseDialog
{
    public MenuDialog()
    {
        LOG.T("MenuDialog::MenuDialog");

        items_ = new String[0];
        icons_ = new int[0];
        click_ = null;
    }

    public void setItems(String[] items)
    {
        if (items != null)
            items_ = items;
    }

    public void setIcons(int[] icons)
    {
        if (icons != null)
            icons_ = icons;
    }

    public void setItemClickedListener(MenuItemClickedListener l)
    {
        click_ = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        LOG.T("MenuDialog::onCreateView");
        restoreInstanceState(savedInstanceState);

        RelativeLayout view = (RelativeLayout) inflater.inflate(
                R.layout.dialog_popup_menu, container);
        TextView header = (TextView) view.findViewById(R.id.tv_header);
        ListView menu = (ListView) view.findViewById(R.id.lv_menu_items);

        if (getHeaderId() > 0)
            header.setText(getHeaderId());
        if (getHeader() != null && getHeader().length() > 0)
            header.setText(getHeader());

        menu.setAdapter(new SimpleListAdapter(inflater, items_, icons_));
        menu.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos,
                    long id)
            {
                MenuDialog.this.dismiss();
                if (pos >= 0 && pos < items_.length && click_ != null)
                    click_.onMenuItemClicked(getDialogId(), items_[pos], pos,
                            getParam());
            }
        });

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.saveInstance(outState);
        if (outState != null)
        {
            outState.putIntArray(_1, icons_);
            outState.putStringArray(_2, items_);
        }
    }

    private void restoreInstanceState(Bundle inState)
    {
        super.restoreInstance(inState);
        if (inState != null)
        {
            icons_ = inState.getIntArray(_1);
            items_ = inState.getStringArray(_2);
        }
    }

    private String[]        items_;
    private int[]           icons_;
    MenuItemClickedListener click_;
}

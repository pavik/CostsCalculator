
package net.costcalculator.activity;

import java.util.ArrayList;

import net.costcalculator.adapter.MultiSelectionAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MultiSelectionDialog
{
    public MultiSelectionDialog(Activity a, int titleid, String[] items,
            ArrayList<Integer> selectedItems)
    {
        if (a == null || items == null || selectedItems == null)
            throw new IllegalArgumentException("invalid input");
        a_ = a;
        titleid_ = titleid;
        items_ = items;
        selectedItems_ = selectedItems;
        singlechoice_ = false;
    }

    public void setOnConfirmListener(
            android.content.DialogInterface.OnClickListener onClickListener)
    {
        onClickListener_ = onClickListener;
    }

    public void setSingleSelection()
    {
        singlechoice_ = true;
    }

    public void show()
    {
        final RelativeLayout layout = (RelativeLayout) a_.getLayoutInflater()
                .inflate(R.layout.dialog_multi_selection, null);
        TextView header = (TextView) layout.findViewById(R.id.tv_title);
        header.setText(titleid_);

        final Dialog d = new Dialog(a_);
        d.setCanceledOnTouchOutside(true);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(layout);

        Button bok = (Button) layout.findViewById(R.id.btn_confirm);
        bok.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                d.dismiss();
                if (onClickListener_ != null)
                    onClickListener_.onClick(d, -1);
            }
        });

        Button bcancel = (Button) layout.findViewById(R.id.btn_cancel);
        bcancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                d.dismiss();
            }
        });

        ListView list = (ListView) layout.findViewById(R.id.lv_items);
        final MultiSelectionAdapter adapter = new MultiSelectionAdapter(a_,
                items_, selectedItems_, singlechoice_);
        list.setAdapter(adapter);

        CheckBox selall = (CheckBox) layout.findViewById(R.id.btn_checkbox);
        if (singlechoice_)
            selall.setVisibility(View.GONE);
        else
        {
            if (selectedItems_.size() == items_.length)
                selall.setChecked(true);
            selall.setOnCheckedChangeListener(new OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked)
                {
                    adapter.setAll(isChecked);
                }
            });
        }
        d.show();
    }

    private Activity                        a_;
    private int                             titleid_;
    private String[]                        items_;
    private ArrayList<Integer>              selectedItems_;
    private boolean                         singlechoice_;
    private DialogInterface.OnClickListener onClickListener_;

}

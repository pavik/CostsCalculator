
package net.costcalculator.dialog;

import java.util.ArrayList;

import net.costcalculator.activity.R;
import net.costcalculator.adapter.MultiSelectionAdapter;
import net.costcalculator.util.LOG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class MultiSelectionDialog extends BaseDialog
{
    public MultiSelectionDialog()
    {
        LOG.T("MultiSelectionDialog::MultiSelectionDialog");

        items_ = new String[0];
        selectedItems_ = new ArrayList<Integer>();
        singlechoice_ = false;
        confirm_ = null;
        cancel_ = null;
    }

    public void setConfirmListener(MultiSelectionConfirmListener l)
    {
        confirm_ = l;
    }

    public void setCancelListener(DialogCancelListener l)
    {
        cancel_ = l;
    }

    public void setItems(String[] items)
    {
        items_ = items;
    }

    public void setSelectedItems(ArrayList<Integer> selItems)
    {
        selectedItems_.clear();
        selectedItems_.addAll(selItems);
    }

    public void setSingleSelection()
    {
        singlechoice_ = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        LOG.T("MultiSelectionDialog::onCreateView");
        restoreInstanceState(savedInstanceState);

        View view = inflater
                .inflate(R.layout.dialog_multi_selection, container);
        TextView header = (TextView) view.findViewById(R.id.tv_title);
        if (getHeaderId() > 0)
            header.setText(getHeaderId());
        if (getHeader() != null && getHeader().length() > 0)
            header.setText(getHeader());

        ListView list = (ListView) view.findViewById(R.id.lv_items);
        final MultiSelectionAdapter adapter = new MultiSelectionAdapter(
                getActivity(), items_, selectedItems_, singlechoice_);
        list.setAdapter(adapter);

        final CheckBox selall = (CheckBox) view.findViewById(R.id.btn_checkbox);
        if (singlechoice_)
            selall.setVisibility(View.GONE);
        else
        {
            if (selectedItems_.size() == items_.length)
                selall.setChecked(true);
            selall.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View arg0)
                {
                    adapter.setAll(selall.isChecked());
                }
            });
            adapter.setSelAllButton(selall);
        }

        Button confirm = (Button) view.findViewById(R.id.btn_confirm);
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                MultiSelectionDialog.this.dismiss();
                if (confirm_ != null)
                    confirm_.onMultiSelectionConfirmed(getDialogId(),
                            selectedItems_, getParam());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MultiSelectionDialog.this.dismiss();
                if (cancel_ != null)
                    cancel_.onCancel(getDialogId());
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
            outState.putStringArray(_1, items_);
            outState.putBoolean(_2, singlechoice_);
            outState.putIntegerArrayList(_3, selectedItems_);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        LOG.T("MultiSelectionDialog::onCreate");
        super.onDestroy();
    }

    @Override
    public void onDestroy()
    {
        LOG.T("MultiSelectionDialog::onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        LOG.T("MultiSelectionDialog::onDestroyView");
        super.onDestroyView();
    }

    private void restoreInstanceState(Bundle inState)
    {
        super.restoreInstance(inState);
        if (inState != null)
        {
            items_ = inState.getStringArray(_1);
            singlechoice_ = inState.getBoolean(_2);
            selectedItems_ = inState.getIntegerArrayList(_3);
        }
    }

    private String[]              items_;
    private ArrayList<Integer>    selectedItems_;
    private boolean               singlechoice_;
    MultiSelectionConfirmListener confirm_;
    DialogCancelListener          cancel_;
}

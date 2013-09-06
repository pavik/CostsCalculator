
package net.costcalculator.dialog;

import net.costcalculator.activity.R;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditTextDialog extends BaseDialog
{
    public EditTextDialog()
    {
        LOG.T("EditTextDialog::EditTextDialog");

        hintid_ = 0;
        maxlen_ = 0;
        confirm_ = null;
        cancel_ = null;
    }

    public void setHint(int hint)
    {
        hintid_ = hint;
    }

    public void setMaxLen(int len)
    {
        maxlen_ = len;
    }

    public void setConfirmListener(DialogConfirmListener l)
    {
        confirm_ = l;
    }

    public void setCancelListener(DialogCancelListener l)
    {
        cancel_ = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        LOG.T("EditTextDialog::onCreateView");
        restoreInstanceState(savedInstanceState);

        RelativeLayout view = (RelativeLayout) inflater.inflate(
                R.layout.dialog_edit_text, container);
        TextView header = (TextView) view.findViewById(R.id.tv_header);
        final EditText editName = (EditText) view.findViewById(R.id.et_editbox);
        Button confirm = (Button) view.findViewById(R.id.btn_confirm);
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);

        if (getHeaderId() > 0)
            header.setText(getHeaderId());
        if (getHeader() != null && getHeader().length() > 0)
            header.setText(getHeader());
        if (getTextId() > 0)
            editName.setText(getTextId());
        if (getText() != null && getText().length() != 0)
            editName.setText(getText());
        if (hintid_ > 0)
            editName.setHint(hintid_);
        if (maxlen_ > 0)
        {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(maxlen_);
            editName.setFilters(filterArray);
        }
        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                EditTextDialog.this.dismiss();
                if (confirm_ != null)
                    confirm_.onConfirm(getDialogId(), editName.getText()
                            .toString().trim(), getParam());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditTextDialog.this.dismiss();
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
            outState.putInt(_1, hintid_);
            outState.putInt(_2, maxlen_);
        }
    }

    private void restoreInstanceState(Bundle inState)
    {
        super.restoreInstance(inState);
        if (inState != null)
        {
            hintid_ = inState.getInt(_1);
            maxlen_ = inState.getInt(_2);
        }
    }

    private int           hintid_, maxlen_;
    DialogConfirmListener confirm_;
    DialogCancelListener  cancel_;
}

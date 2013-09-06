
package net.costcalculator.dialog;

import net.costcalculator.activity.R;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlertDialog extends BaseDialog
{
    public AlertDialog()
    {
        iconid_ = -1;
        confirm_ = null;
        cancel_ = null;
    }

    public void setIconId(int id)
    {
        iconid_ = id;
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
        LOG.T("AlertDialog::onCreateView");
        restoreInstanceState(savedInstanceState);

        RelativeLayout view = (RelativeLayout) inflater.inflate(
                R.layout.dialog_alert, container);
        TextView header = (TextView) view.findViewById(R.id.tv_header);
        TextView label = (TextView) view.findViewById(R.id.tv_label);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        Button confirm = (Button) view.findViewById(R.id.btn_confirm);
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);

        if (getHeaderId() > 0)
            header.setText(getHeaderId());
        if (getHeader() != null && getHeader().length() > 0)
            header.setText(getHeader());
        if (getTextId() > 0)
            label.setText(getTextId());
        if (getText() != null && getText().length() != 0)
            label.setText(getText());
        if (iconid_ > 0)
            icon.setImageResource(iconid_);
        else
            icon.setVisibility(View.GONE);

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                AlertDialog.this.dismiss();
                if (confirm_ != null)
                    confirm_.onConfirm(getDialogId(), "", getParam());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.this.dismiss();
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
            outState.putInt(_1, iconid_);
    }

    private void restoreInstanceState(Bundle inState)
    {
        super.restoreInstance(inState);
        if (inState != null)
            iconid_ = inState.getInt(_1);
    }

    int                   iconid_;
    DialogConfirmListener confirm_;
    DialogCancelListener  cancel_;
}


package net.costcalculator.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class BaseDialog extends DialogFragment
{
    public BaseDialog()
    {
        setDialogId(-1);
        param_ = headerid_ = textid_ = -1;
        text_ = null;
        header_ = null;
    }

    public void setDialogId(int dialogid)
    {
        this.dialogid_ = dialogid;
    }

    public int getDialogId()
    {
        return dialogid_;
    }

    public void setParam(int param)
    {
        this.param_ = param;
    }

    public int getParam()
    {
        return param_;
    }

    public void setTextId(int textid)
    {
        this.textid_ = textid;
    }

    public int getTextId()
    {
        return textid_;
    }

    public void setHeaderId(int headerid)
    {
        this.headerid_ = headerid;
    }

    public int getHeaderId()
    {
        return headerid_;
    }

    public void setHeader(String header)
    {
        this.header_ = header;
    }

    public String getHeader()
    {
        return header_;
    }

    public void setText(String text)
    {
        this.text_ = text;
    }

    public String getText()
    {
        return text_;
    }

    protected void saveInstance(Bundle outState)
    {
        if (outState != null)
        {
            outState.putInt("_1", dialogid_);
            outState.putInt("_2", param_);
            outState.putInt("_3", headerid_);
            outState.putInt("_4", textid_);
            outState.putString("_5", text_);
            outState.putString("_6", header_);
        }
    }

    protected void restoreInstance(Bundle inState)
    {
        if (inState != null)
        {
            dialogid_ = inState.getInt("_1");
            param_ = inState.getInt("_2");
            headerid_ = inState.getInt("_3");
            textid_ = inState.getInt("_4");
            text_ = inState.getString("_5");
            header_ = inState.getString("_6");
        }
    }

    private int dialogid_;
    private int param_, headerid_, textid_;
    private String text_, header_;
    protected static final String _1 = "1", _2 = "2", _3 = "3", _4 = "4",
            _5 = "5", _6 = "6", _7 = "7", _8 = "8", _9 = "9";
}

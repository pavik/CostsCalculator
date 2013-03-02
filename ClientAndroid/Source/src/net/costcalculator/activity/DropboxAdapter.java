
package net.costcalculator.activity;

import java.util.ArrayList;

import net.costcalculator.service.DropboxEntry;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DropboxAdapter extends BaseAdapter
{
    public DropboxAdapter(Activity c, ArrayList<DropboxEntry> files)
    {
        context_ = c;
        files_ = files;
        BYTES = c.getResources().getString(R.string.bytes);
    }

    public String getFileName(long id)
    {
        return files_.get((int) id).name;
    }

    @Override
    public int getCount()
    {
        return files_.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return files_.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return pos;
    }

    static class ViewHolder
    {
        public TextView tvName;
        public TextView tvSize;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        View item = null;

        if (convertView == null)
        {
            LayoutInflater inflater = context_.getLayoutInflater();
            item = inflater.inflate(R.layout.view_dropbox_entry, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.tvName = (TextView) item.findViewById(R.id.tv_name);
            vh.tvSize = (TextView) item.findViewById(R.id.tv_size);
            item.setTag(vh);
        }
        else
        {
            item = convertView;
        }

        ViewHolder vh = (ViewHolder) item.getTag();
        DropboxEntry e = files_.get(pos);
        vh.tvName.setText(e.name);
        vh.tvSize.setText(Long.toString(e.size) + " " + BYTES);

        return item;
    }

    private Activity                context_;
    private ArrayList<DropboxEntry> files_;
    private final String            BYTES;
}

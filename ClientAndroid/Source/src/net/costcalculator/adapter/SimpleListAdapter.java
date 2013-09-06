
package net.costcalculator.adapter;

import net.costcalculator.activity.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleListAdapter extends BaseAdapter
{
    public SimpleListAdapter(LayoutInflater inflater, String[] items,
            int[] icons)
    {
        if (inflater == null || items == null)
            throw new IllegalArgumentException("invalid input");
        if (icons != null && icons.length != 0 && icons.length != items.length)
            throw new IllegalArgumentException("invalid icons length");
        inflater_ = inflater;
        items_ = items;
        icons_ = icons;
    }

    @Override
    public int getCount()
    {
        return items_.length;
    }

    @Override
    public Object getItem(int pos)
    {
        return items_[pos];
    }

    @Override
    public long getItemId(int pos)
    {
        return pos;
    }

    static class ViewHolder
    {
        public ImageView icon;
        public TextView  label;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        View item = null;

        if (convertView == null)
        {
            item = inflater_
                    .inflate(R.layout.view_list_img_text, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.icon = (ImageView) item.findViewById(R.id.icon);
            vh.label = (TextView) item.findViewById(R.id.label);
            item.setTag(vh);
        }
        else
            item = convertView;

        ViewHolder vh = (ViewHolder) item.getTag();
        vh.label.setText(items_[pos]);
        if (icons_ != null && icons_.length > 0)
        {
            vh.icon.setImageResource(icons_[pos]);
            vh.icon.setVisibility(View.VISIBLE);
        }
        else
            vh.icon.setVisibility(View.GONE);

        return item;
    }

    private String[] items_;
    private int[]    icons_;
    LayoutInflater   inflater_;
}

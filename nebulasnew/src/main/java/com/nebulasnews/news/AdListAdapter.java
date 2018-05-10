package com.nebulasnews.news;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nebulas.io.ui.list.adapter.BaseListAdapter;
import com.nebulas.io.util.TimeUtils;
import com.nebulasnews.R;
import com.squareup.picasso.Picasso;

/**
 */

class AdListAdapter extends BaseListAdapter<Ad> {
    public AdListAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_ad_item, null);
            holder = new ViewHolder();
            holder.title= convertView.findViewById(R.id.title);
            holder.desc = convertView.findViewById(R.id.desc);
            holder.from = convertView.findViewById(R.id.from);
            holder.img = convertView.findViewById(R.id.img);
            holder.time = convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final Ad ad = getItem(position);
        holder.desc.setText(ad.getDesc());
        holder.title.setText(ad.getTitle());
        holder.time.setText(ad.getTime());
        holder.from.setText(ad.getFrom());
        holder.from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "已经复制地址", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label",ad.getFrom());
                clipboard.setPrimaryClip(clip);

            }
        });

        if (!TextUtils.isEmpty(ad.getUrl()) && ad.getUrl().startsWith("http")) {
            Picasso.with(mContext).load(ad.getUrl()).resize(1000,600).centerCrop().into(holder.img);
            holder.img.setVisibility(View.VISIBLE);
        }else{
            holder.img.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView title;
        TextView desc;
        TextView url;
        ImageView img;
        TextView from;
        TextView time;
    }
}

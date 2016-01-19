package com.vietdms.mobile.dmslauncher.CustomAdapter;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.vietdms.mobile.dmslauncher.R;

/**
 * @author alessandro.balocco
 */
public class DialogAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public DialogAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
                view = layoutInflater.inflate(R.layout.dialog_menu_custom, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            case 0:
                viewHolder.textView.setText(context.getString(R.string.check_in));
                viewHolder.imageView.setImageResource(R.drawable.checkin_btn);
                break;
            case 1:
                viewHolder.textView.setText(context.getString(R.string.check_out));
                viewHolder.imageView.setImageResource(R.drawable.checkout_btn);
                break;
            default:
                viewHolder.textView.setText(context.getString(R.string.cancel));
                viewHolder.imageView.setImageResource(R.drawable.back_btn);
                break;
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}

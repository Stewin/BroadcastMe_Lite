package ch.hslu.mobpro.projekt.broadcastmelite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] Ids;

    public MenuAdapter(Context context, String[] objects) {
        super(context, R.layout.drawer_list_item, objects);
        this.context = context;
        this.Ids = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.drawer_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.text1);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        int id = Integer.parseInt(Ids[position]);
        int imageFile = MainMenu.getById(id).IconFile;
        viewHolder.text.setText(MainMenu.getById(id).Name);
        return rowView;
    }

    static class ViewHolder {
        public ImageView image;
        TextView text;
    }

}
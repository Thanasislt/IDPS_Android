package securityteam.ece.uowm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<Packet> {
    // View lookup cache
    private static class ViewHolder {
        TextView count;
        TextView source;
        TextView destination;
        TextView protocol;
    }

    public UsersAdapter(Context context, ArrayList<Packet> users) {
        super(context, R.layout.table_row, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
            Packet packet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.table_row, parent, false);
                viewHolder.count = (TextView) convertView.findViewById(R.id.count);
            viewHolder.source = (TextView) convertView.findViewById(R.id.source);
            viewHolder.destination = (TextView) convertView.findViewById(R.id.destination);
            viewHolder.protocol = (TextView) convertView.findViewById(R.id.protocol);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
            viewHolder.count.setText(String.valueOf(packet.getCount()));
        viewHolder.source.setText(String.valueOf(packet.getSource()));
        viewHolder.destination.setText(String.valueOf(packet.getDestination()));
        viewHolder.protocol.setText(String.valueOf(packet.getProtocol()));
        // Return the completed view to render on screen
        return convertView;
    }
}

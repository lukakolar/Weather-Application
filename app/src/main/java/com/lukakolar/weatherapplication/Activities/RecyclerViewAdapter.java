package com.lukakolar.weatherapplication.Activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lukakolar.weatherapplication.Entity.CityWeatherObject;
import com.lukakolar.weatherapplication.R;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<CityWeatherObject> values;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtHeader;
        TextView txtFooter;
        public View layout;

        ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.recycler_view_city_name);
            txtFooter = (TextView) v.findViewById(R.id.recycler_view_temperature);
        }
    }

    private int getSortedPosition(CityWeatherObject item){
        for (int i = 0; i < values.size(); i++) {
            if (item.name.compareToIgnoreCase(values.get(i).name) > 0) {
                continue;
            }
            return i;
        }
        return values.size();
    }

    void add(CityWeatherObject item) {
        int sortedPosition = getSortedPosition(item);
        values.add(sortedPosition, item);
        notifyItemInserted(sortedPosition);
    }

    void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    RecyclerViewAdapter(Context context, List<CityWeatherObject> values) {
        this.values = values;
        this.context = context;
    }

    void refresh(List<CityWeatherObject> values) {
        this.values = values;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CityWeatherObject item = values.get(position);
        holder.layout.setTag(item);
        holder.txtHeader.setText(item.name);

        String degrees_celsius = context.getResources().getString(R.string.degrees_celsius);
        String refreshToGetTemperature = context.getResources().getString(R.string.activity_main_refresh_to_get_temperature);

        if (item.temperature != null) {
            String text = item.temperature + " " + degrees_celsius;
            holder.txtFooter.setText(text);
        } else {
            holder.txtFooter.setText(refreshToGetTemperature);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityWeatherObject item = (CityWeatherObject) v.getTag();
                MainActivity mainActivity = ((MainActivity) context);
                mainActivity.onCitySelected(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}


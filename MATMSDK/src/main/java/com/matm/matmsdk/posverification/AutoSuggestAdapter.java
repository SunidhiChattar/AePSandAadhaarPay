package com.matm.matmsdk.posverification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

import isumatm.androidsdk.equitas.R;

public class AutoSuggestAdapter extends ArrayAdapter<String> implements Filterable {
    private List<ModelBankSearch> mlistData;
    private int itemLayout;

    public AutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mlistData = new ArrayList<>();
        itemLayout = resource;

    }

    public void setData(List<ModelBankSearch> list) {
        mlistData.clear();
        mlistData.addAll(list);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView strName = (TextView) view.findViewById(R.id.bank_name_suggestion);
        strName.setText(mlistData.get(position).bank_name);
        return view;
    }


    @Override
    public int getCount() {
        return mlistData.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mlistData.get(position).bank_name;
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    public ModelBankSearch getObject(int position) {
        return mlistData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mlistData;
                    filterResults.count = mlistData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}

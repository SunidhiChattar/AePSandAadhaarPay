package com.matm.matmsdk.aepsmodule.unifiedaeps;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.matm.matmsdk.Utils.SdkConstants;

import org.w3c.dom.Text;

import java.util.ArrayList;

import isumatm.androidsdk.equitas.R;


public class UnifiedStatementListAdapter extends RecyclerView.Adapter<UnifiedStatementListAdapter.RecyclerViewHolder> {
    private ArrayList<UnifiedTxnStatusModel.MiniStatement> list = new ArrayList<>();
    private Context context;
    private int selectedPosition = -1;
    private ProgressDialog pd;
    private TransactionType transactionType;

    public UnifiedStatementListAdapter(Context context, ArrayList<UnifiedTxnStatusModel.MiniStatement> list, ProgressDialog pd) {
        this.list = list;
        this.context = context;
        this.pd = pd;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView date_txt;
        private TextView amount_txt;
        private TextView remark_txt;
        private TextView crdr_txt;

        RecyclerViewHolder(View view) {
            super(view);
            date_txt = view.findViewById(R.id.date_txt);
            amount_txt = view.findViewById(R.id.amount_txt);
            remark_txt = view.findViewById(R.id.remark_txt);
            crdr_txt = view.findViewById(R.id.crdr_txt);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int layout = R.layout.statement_list_items;
        if (SdkConstants.statementItem != 0) {
            layout = SdkConstants.statementItem;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        final RecyclerViewHolder mViewHolder = new RecyclerViewHolder(v);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int pos) {
        try {
            if (list.size() != 0) {
                if (list.get(pos).getDate() == null) {
                    holder.date_txt.setText("");
                } else {
                    holder.date_txt.setText(list.get(pos).getDate().toString());
                }
                if (list.get(pos).getType() == null) {
                    holder.remark_txt.setText("");
                } else {
                    holder.remark_txt.setText(list.get(pos).getType());
                }
                if (list.get(pos).getDebitCredit() == null) {
                } else {
                    String type = list.get(pos).getDebitCredit().toString();
                    transactionType = new TransactionType(DebitCredit.valueOf(type));
                }
                double amount = list.get(pos).getAmount();

                holder.crdr_txt.setText(transactionType.transactionType());
                if (holder.crdr_txt.getText().toString().equalsIgnoreCase("Cr ")) {
                    holder.crdr_txt.setTextColor(context.getResources().getColor(R.color.green));
                } else {
                    holder.crdr_txt.setTextColor(context.getResources().getColor(R.color.red));
                }
                holder.amount_txt.setText(amount + "");

                pd.hide();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            pd.hide();
            Log.v("StatementList Aeps", e.toString().trim());
        }

    }


    @Override
    public int getItemCount() {
        int size;
        size = 0;
        try {
            if (list.size() != 0) {
                size = list.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    //Delete the selected position from the arrayList
    public void deleteSelectedPosition() {
        if (selectedPosition != -1) {
            list.remove(selectedPosition);
            selectedPosition = -1;//after removing selectedPosition set it back to -1
            notifyDataSetChanged();
        }
    }
}

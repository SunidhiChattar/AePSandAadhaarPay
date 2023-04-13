package com.matm.matmsdk.posverification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matm.matmsdk.posverification.responsemodel.BankDetailsEntity;

import java.util.ArrayList;

import isumatm.androidsdk.equitas.R;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountListViewHolder> {

    Context AccountListActivity;
    private ArrayList<BankDetailsEntity> fetchAccountResponseModelArrayList = new ArrayList<>();


    public AccountListAdapter(Context AccountListActivity, ArrayList<BankDetailsEntity> FetchAccountResponseModels) {
        this.AccountListActivity = AccountListActivity;
        this.fetchAccountResponseModelArrayList = FetchAccountResponseModels;

    }

    @NonNull
    @Override
    public AccountListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_list_row, parent, false);

//        AccountListViewHolder accountListViewHolder = new AccountListViewHolder(view);
        AccountListViewHolder accountListViewHolder = new AccountListViewHolder(view);
        return accountListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountListViewHolder holder, int position) {
        holder.tvBeneName.setText(fetchAccountResponseModelArrayList.get(position).getAccountholdername());
        holder.tvBankName.setText(fetchAccountResponseModelArrayList.get(position).getBankname());
        holder.tvAcNo.setText(fetchAccountResponseModelArrayList.get(position).getAccountnumber());

    }

    @Override
    public int getItemCount() {
        return(fetchAccountResponseModelArrayList != null ? fetchAccountResponseModelArrayList.size() : 0);
        //return fetchAccountResponseModelArrayList.size();
    }



    public class AccountListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBeneName,tvBankName,tvAcNo;


        public AccountListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBeneName = itemView.findViewById(R.id.tv_benename);
            tvBankName = itemView.findViewById(R.id.tv_bankname);
            tvAcNo = itemView.findViewById(R.id.tv_AcNo);

        }
    }

}

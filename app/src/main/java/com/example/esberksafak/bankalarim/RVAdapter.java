package com.example.esberksafak.bankalarim;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by EsberkSafak on 3.8.2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AccountViewHolder> {
    List<Account> accounts = null;
    Context context;
    private MainActivity.OnNoDataAvailable onNoDataAvailable;

    public RVAdapter(List<Account> accounts, Context context, MainActivity.OnNoDataAvailable mCallback) {
        this.accounts = accounts;
        this.context = context;
        this.onNoDataAvailable = mCallback;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        AccountViewHolder pvh = new AccountViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final AccountViewHolder accountViewHolder, final int i) {
        accountViewHolder.bankName.setText(accounts.get(i).getBankName() + "");
        accountViewHolder.customerNo.setText(accounts.get(i).getCustomerNo() + "");

        if(i%2==0){
            accountViewHolder.cv.setBackgroundColor(Color.parseColor("#795548"));
        }
        else{
            accountViewHolder.cv.setBackgroundColor(Color.parseColor("#5D4037"));

        }

        accountViewHolder.copy.setOnClickListener(new View.OnClickListener() {  // COPY
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(accounts.get(i).getCustomerNo());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Müşteri No", accounts.get(i).getCustomerNo());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(context, "Kopyalandı", Toast.LENGTH_SHORT).show();
            }
        });


        accountViewHolder.share.setOnClickListener(new View.OnClickListener() {  //SHARE
            @Override
            public void onClick(View v) {
                String shareBody = accounts.get(i).getBankName() + " hesabımın müşteri numarası : " + accounts.get(i).getCustomerNo() + "\n";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Müşteri numaranı paylaş"));
            }
        });


        accountViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {  // CV LONG PRESS
            @Override
            public boolean onLongClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Emin misin?")
                        .setContentText("Kayıt geri kurtarılamayacak!")
                        .setConfirmText("Evet,sil!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                final Realm realm = Realm.getInstance(context);
                                realm.beginTransaction();
                                final RealmResults<Account> result = realm.where(Account.class)
                                        .equalTo("customerNo", accounts.get(i).getCustomerNo())
                                        .findAll();
                                result.remove(0);
                                accounts.remove(i);
                                notifyDataSetChanged();
                                if (accounts.size() == 0) {
                                    onNoDataAvailable.noDataAvailable();
                                }
                                realm.commitTransaction();
                                Toast.makeText(context, "Silindi", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return (accounts != null) ? accounts.size() : 0;
    }


    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cv)
        CardView cv;
        @Bind(R.id.bankName)
        TextView bankName;
        @Bind(R.id.customerNo)
        TextView customerNo;
        @Bind(R.id.copy)
        Button copy;
        @Bind(R.id.share)
        Button share;


        AccountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}

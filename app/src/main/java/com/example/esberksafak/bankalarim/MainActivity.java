package com.example.esberksafak.bankalarim;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.rv)
    RecyclerView recyclerView;

    @Bind(R.id.addAccountText)
    TextView addText;

    EditText bankNameDialog;
    EditText customerNoDialog;

    ArrayList<Account> accounts=new ArrayList<>();
    Realm realm ;

    public interface OnNoDataAvailable{
        void noDataAvailable();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // Bütün veritabanını al.
        realm= Realm.getInstance(MainActivity.this);
        realm.setAutoRefresh(true);
        RealmQuery<Account> query = realm.where(Account.class);
        RealmResults<Account> result = query.findAll();
        if(result.size()==0){
            recyclerView.setVisibility(View.GONE);
            addText.setVisibility(View.VISIBLE);
        }
        else{  // Boş değilse listeye ekle.
            for (int i = 0; i < result.size(); i++) {
                accounts.add(result.get(i));
            }
        }
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        RVAdapter adapter = new RVAdapter(accounts,MainActivity.this,noDataAvailable  ); // Listeyi parametre geç
        recyclerView.setAdapter(adapter);


    }

    @OnClick(R.id.fab)
    public void addAccount(View view){
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View addAcount = li.inflate(R.layout.add_acount, null);

        bankNameDialog = (EditText) addAcount.findViewById(R.id.bankNameDialog);
        bankNameDialog.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        customerNoDialog= (EditText) addAcount.findViewById(R.id.customerNoDialog);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(addAcount);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Kaydet",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String bankName =bankNameDialog.getText().toString();
                                String customerNo = customerNoDialog.getText().toString();

                                if(bankName.isEmpty() || customerNo.isEmpty())Toast.makeText(MainActivity.this,"Lütfen geçerli bilgiler girin",Toast.LENGTH_SHORT).show();
                                else{
                                    Account account = new Account(bankName,customerNo);
                                    accounts.add(account);  // Listeye ekle.

                                    // Veritabanına ekle.
                                    realm.beginTransaction();
                                    realm.copyToRealm(account);
                                    realm.commitTransaction();

                                    recyclerView.setVisibility(View.VISIBLE);
                                    addText.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Eklendi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setNegativeButton("Vazgeç",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private OnNoDataAvailable noDataAvailable = new OnNoDataAvailable() {
        @Override
        public void noDataAvailable() {
            recyclerView.setVisibility(View.GONE);
            addText.setVisibility(View.VISIBLE);
        }
    };


}

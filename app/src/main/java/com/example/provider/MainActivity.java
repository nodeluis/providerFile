package com.example.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity{
    RecyclerView rec;
    adapterrecycler adp;
    LinearLayoutManager ln;
    Button btn;

    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rec=findViewById(R.id.recycler);
        ln=new LinearLayoutManager(this);
        adp=new adapterrecycler(this);
        rec.setLayoutManager(ln);
        rec.setAdapter(adp);

        search=findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            Handler handler = new android.os.Handler();
            Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(!s.equals("")){
                            searchText(s);
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });

        btn=findViewById(R.id.add);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MainActivity.this,newproduct.class);
                startActivity(in);
            }
        });
    }

    private void searchText(Editable s) {
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams rq=new RequestParams();
        rq.put("text",s);
        client.post(ip.ip,rq,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    for (int i=0;i<response.length();i++){
                        JSONObject obj=response.getJSONObject(i);
                        adp.add(new item(obj.getString("name"),obj.getString("_id")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
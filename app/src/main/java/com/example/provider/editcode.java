package com.example.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class editcode extends AppCompatActivity implements View.OnClickListener {
    ImageView code_img,img_img;
    Button code,img,send;
    EditText codigo,costo,precio,cantidad,costo_total,total;

    String code_path;
    String img_path;

    String id="";

    int con=0;


    private final static int CODE_PERMISSION = 107;
    private final static int IMAGE_RESULT1 = 200;
    private final static int IMAGE_RESULT2 = 201;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcode);

        code_img=findViewById(R.id.edit_code);
        img_img=findViewById(R.id.edit_img);

        code=findViewById(R.id.edit_capture_code);
        img=findViewById(R.id.edit_capture_img);
        send=findViewById(R.id.edit_send);

        codigo=findViewById(R.id.edit_codigo);
        costo=findViewById(R.id.edit_costo);
        precio=findViewById(R.id.edit_precio);
        cantidad=findViewById(R.id.edit_cantidad);
        costo_total =findViewById(R.id.edit_costo_total);
        total=findViewById(R.id.edit_total);

        Bundle b=getIntent().getExtras();
        id=b.getString("id");

        getData();

        code.setOnClickListener(this);
        img.setOnClickListener(this);
        send.setOnClickListener(this);

        code.setVisibility(View.GONE);
        img.setVisibility(View.GONE);
        send.setVisibility(View.GONE);
        if(reviewPermissions()){
            code.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
        }

    }

    private void getData() {
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(ip.ip+"",null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Glide.with(editcode.this).load(response.getString("code")).into(code_img);
                    Glide.with(editcode.this).load(response.getString("img")).into(img_img);
                    codigo.setText(response.getString("codigo"));
                    costo.setText(response.getString("costo"));
                    precio.setText(response.getString("precio"));
                    cantidad.setText(response.getString("cantidad"));
                    costo_total.setText(response.getString("costo_total"));
                    total.setText(response.getString("total"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.edit_capture_code){
            startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT1);
        }
        if(v.getId()==R.id.edit_capture_img){
            startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT2);
        }
        if(v.getId()==R.id.edit_send){
            Toast.makeText(this, "enviando", Toast.LENGTH_SHORT).show();
            sendData();
        }
    }

    private void sendData() {
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams req=new RequestParams();
        req.put("id",id);
        req.put("codigo",codigo.getText().toString());
        req.put("costo",costo.getText().toString());
        req.put("precio",precio.getText().toString());
        req.put("cantidad",cantidad.getText().toString());
        req.put("costo_total",costo_total.getText().toString());
        req.put("total",total.getText().toString());
        client.post("https://servicemercadoxpress.",req,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Toast.makeText(editcode.this, ""+response.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent in=new Intent(editcode.this,MainActivity.class);
                    startActivity(in);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "oneplus.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_RESULT1) {

                String filePath = getImageFilePath(data,1);
                if (filePath != null) {
                    sendCodeimg(filePath);
                }
            }

            if (requestCode == IMAGE_RESULT2) {

                String filePath = getImageFilePath(data,2);
                if (filePath != null) {
                    sendImgimg(filePath);
                }
            }

        }
    }

    private void sendImgimg(String url) {
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams rq=new RequestParams();
        rq.put("id",id);
        try {
            rq.put("",new File(url));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(ip.ip,rq,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Glide.with(editcode.this).load(response.getString("url")).into(img_img);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendCodeimg(String url) {
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams rq=new RequestParams();
        rq.put("id",id);
        try {
            rq.put("",new File(url));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(ip.ip,rq,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Glide.with(editcode.this).load(response.getString("url")).into(code_img);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getImageFromFilePath(Intent data,int n) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) {
            if(n==1){
                code_path=getCaptureImageOutputUri().getPath();
                return code_path;
            }else{
                img_path=getCaptureImageOutputUri().getPath();
                return img_path;
            }
        }
        else {
            if(n==1){
                code_path=getPathFromURI(data.getData());
                return code_path;
            }else{
                img_path=getPathFromURI(data.getData());
                return img_path;
            }
        }

    }

    public String getImageFilePath(Intent data, int n) {
        return getImageFromFilePath(data,n);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("code_img_",code_path);
        outState.putString("img_img_",img_path);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        code_path = savedInstanceState.getString("code_img_");
        img_path = savedInstanceState.getString("img_img_");
        if (code_path != null) {
            Bitmap selectedImage = BitmapFactory.decodeFile(code_path);
            code_img.setImageBitmap(selectedImage);
        }
        if (img_path != null) {
            Bitmap selectedImage = BitmapFactory.decodeFile(img_path);
            img_img.setImageBitmap(selectedImage);
        }
    }

    private boolean reviewPermissions() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        this.requestPermissions(new String [] {Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CODE_PERMISSION);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (CODE_PERMISSION == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                code.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
            }
        }
    }
}
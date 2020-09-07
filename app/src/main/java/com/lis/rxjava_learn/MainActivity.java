package com.lis.rxjava_learn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    String url = "http://a3.att.hudong.com/14/75/01300000164186121366756803686.jpg";
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.image);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo： 步骤二
                Observable.just(url)
                        // todo: 步骤三
                        .map(new Function<String, Bitmap>() {
                            @Override
                            public Bitmap apply(String s) throws Throwable {
                                URL url = new URL(s);
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setConnectTimeout(3_000);
                                int responseCode = httpURLConnection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    return bitmap;
                                }
                                return null;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Observer<Bitmap>() {
                                    //订阅开始
                                    // todo: 步骤一
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {
                                        // 加载loading等
                                        mProgressDialog = new ProgressDialog(MainActivity.this);
                                        mProgressDialog.setTitle("loading");
                                        mProgressDialog.show();
                                    }

                                    // todo: 步骤四
                                    @Override
                                    public void onNext(@NonNull Bitmap bitmap) {
                                        imageView.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        if (mProgressDialog != null) {
                                            mProgressDialog.dismiss();
                                        }
                                    }

                                    // todo: 步骤五
                                    @Override
                                    public void onComplete() {
                                        //关闭loading
                                        if (mProgressDialog != null) {
                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });
            }
        });
    }
}

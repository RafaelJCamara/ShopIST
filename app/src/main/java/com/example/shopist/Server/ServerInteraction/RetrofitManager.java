package com.example.shopist.Server.ServerInteraction;

import android.content.Context;

import com.example.shopist.BuildConfig;
import com.example.shopist.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;

    public RetrofitManager(Context context){
        initRetrofit(context);
    }

    private void initRetrofit(Context context){
        //instantiate retrofit settings
        retrofit  = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(generateSecureOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    private OkHttpClient generateSecureOkHttpClient(Context context) {
        // Create a simple builder for our http client, this is only por example purposes
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        if(!BuildConfig.BUILD_TYPE.equals("debug")) {
            try {

                // Here you may wanna add some headers or custom setting for your builder

                // Get the file of our certificate
                InputStream caFileInputStream = context.getResources().openRawResource(R.raw.certificate);

                // We're going to put our certificates in a Keystore
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(caFileInputStream, BuildConfig.CERTIFICATE_PASSWORD.toCharArray());

                // Create a KeyManagerFactory with our specific algorithm our our public keys
                // Most of the cases is gonna be "X509"
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
                keyManagerFactory.init(keyStore, BuildConfig.CERTIFICATE_PASSWORD.toCharArray());

                // Create a SSL context with the key managers of the KeyManagerFactory
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

                // Create the TrustManagerFactory and get the X509TrustManager
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                // Set the sslSocketFactory to our builder
                httpClientBuilder
                        .sslSocketFactory(sslContext.getSocketFactory(), trustManager);

                httpClientBuilder
                        .addNetworkInterceptor(chain -> {
                            Request original = chain.request();

                            Request request = original.newBuilder()
                                    .addHeader("Connection", "close")
                                    .method(original.method(), original.body())
                                    .build();

                            return chain.proceed(request);
                        });

            } catch (Exception e) {
                System.err.println("Unable to get certificate information, going HTTP");
            }

        }

        return httpClientBuilder.build();
    }

    public RetrofitInterface accessRetrofitInterface(){
        return this.retrofitInterface;
    }

}

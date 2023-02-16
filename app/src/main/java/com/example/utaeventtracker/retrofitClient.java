//package com.example.utaeventtracker;
//
//import com.google.android.gms.common.api.Api;
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class retrofitClient {
//        private static retrofitClient instance = null;
//        private Api myApi;
//
//        private retrofitClient() {
//            Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            myApi = retrofit.create(Api.class);
//        }
//
//        public static synchronized retrofitClient getInstance() {
//            if (instance == null) {
//                instance = new retrofitClient();
//            }
//            return instance;
//        }
//
//        public Api getMyApi() {
//            return myApi;
//        }
//}

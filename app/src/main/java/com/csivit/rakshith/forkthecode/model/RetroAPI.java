package com.csivit.rakshith.forkthecode.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public class RetroAPI {

    public interface NetworkService {

        @FormUrlEncoded
        @POST("login")
        Observable<JsonObject> login(
                @Field("username") String username,
                @Field("password") String password
        );

        /*
        [
            {
                teamname: alsjd;jhfawe f
                user1: 15bed5430
                user2: 15lsg5440
                user3: 15wla6306 / null
            },
            {
                teamname: alsjd;jhfawe f
                user1: 15bed5430
                user2: 15lsg5440
                user3: 15wla6306 / null
            }
        ]
         */
        @FormUrlEncoded
        @POST("getteams")
        Observable<JsonArray> getTeams();

        @FormUrlEncoded
        @POST("createteam")
        Observable<JsonObject> createTeam(
                @Field("teamname") String teamName,
                @Field("password") String password
        );

        @FormUrlEncoded
        @POST("jointeam")
        Observable<JsonObject> joinTeam(
                @Field("teamname") String teamName,
                @Field("password") String password
        );

        @FormUrlEncoded
        @POST("getquestion")
        Observable<JsonObject> getQuestion(
                @Field("questionid") String questionId
        );

        @FormUrlEncoded
        @POST("answer")
        Observable<JsonObject> answer(
                @Field("questionid") String questionID,
                @Field("answer") String answer
        );
    }

    private static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            Request request = new Request.Builder()
                    .addHeader("Authorization", "JWT " + Data.AuthToken)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        }
    };

    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

    public static final NetworkService NetworkCalls = new Retrofit.Builder()
            .baseUrl(Constants.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(client)
            .build()
            .create(NetworkService.class);
}

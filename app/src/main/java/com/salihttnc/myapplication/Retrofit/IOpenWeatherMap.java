package com.salihttnc.myapplication.Retrofit;

import com.salihttnc.myapplication.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface IOpenWeatherMap {
    //@GET("weather")
    //Observable<WeatherResult> getWeatherByLating(@Query("lat") String lat,

    @GET("weather")
    Observable<WeatherResult> getWeatherByLating(@Query("lat") String lat,
                                                 @Query("lon") String lgn,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);



}

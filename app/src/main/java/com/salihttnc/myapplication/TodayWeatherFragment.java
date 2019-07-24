package com.salihttnc.myapplication;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.salihttnc.myapplication.Common.Common;
import com.salihttnc.myapplication.Model.WeatherResult;
import com.salihttnc.myapplication.Retrofit.IOpenWeatherMap;
import com.salihttnc.myapplication.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    ImageView img_weather;
    TextView txt_city_name,txt_humidity,txt_sunrise,txt_sunset,txt_pressure,txt_temperature,txt_description,txt_date_time,txt_wind,txt_geo_coord,txt_tarih;
    LinearLayout weather_panel;
    ProgressBar loading;
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;




    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if(instance==null)
            instance=new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {

        compositeDisposable=new CompositeDisposable();
        Retrofit retroif=RetrofitClient.getInstance();
        mService=retroif.create(IOpenWeatherMap.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);
        img_weather=(ImageView)itemView.findViewById(R.id.img_weather);
        txt_city_name=(TextView)itemView.findViewById(R.id.txt_city_name);
        txt_tarih=(TextView)itemView.findViewById(R.id.textView2);
        txt_temperature=(TextView)itemView.findViewById(R.id.txt_temperature);
        txt_description=(TextView)itemView.findViewById(R.id.txt_description);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        txt_tarih.setText(date);
        getWeatherInformation();

        return itemView;

    }


    private void getWeatherInformation() {


        compositeDisposable.add(mService.getWeatherByLating(String.valueOf(Common.current_location.latitude),
                String.valueOf(Common.current_location.longitude),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        //resim yükleme yeri
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);

                        txt_city_name.setText(weatherResult.getName());
                        txt_description.setText(new StringBuffer("Hava Durumu : ")
                                .append(weatherResult.getName()).toString());
                        txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                                .append("°C").toString());

                        //görüntüleme paneli








                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })

        );

    }


}
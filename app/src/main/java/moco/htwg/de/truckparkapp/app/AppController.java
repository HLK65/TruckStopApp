package moco.htwg.de.truckparkapp.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Sebastian Thümmel on 31.12.2017.
 */

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private static AppController appController;

    private RequestQueue requestQueue;

    @Override
    public void onCreate(){
        super.onCreate();
        appController = this;
    }

    public static synchronized AppController getInstance(){
        return appController;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag) ? TAG: tag);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequest(Object tag){
        if(requestQueue != null){
            requestQueue.cancelAll(tag);
        }
    }

}

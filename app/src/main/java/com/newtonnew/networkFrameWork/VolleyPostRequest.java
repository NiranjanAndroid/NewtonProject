package com.newtonnew.networkFrameWork;

import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sandeepgautam on 23/07/16.
 */
public class VolleyPostRequest extends Request<NetworkResponse> {
    private final Response.Listener<Object> mListener;
    private HashMap<String, String> mParams;
    private Activity mActivity;


    public VolleyPostRequest(Response.Listener listener, String url, HashMap<String, String> params, Activity activity) {
        super(Method.POST, url, (Response.ErrorListener) listener);
        mListener = listener;
        mParams = params;
        mActivity = activity;

        setRetryPolicy(new DefaultRetryPolicy(300000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse networkResponse) {
        int responseCode = networkResponse.statusCode;
        String response = new String();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            response = new String(networkResponse.data);

        }
        mListener.onResponse(response);
    }

}

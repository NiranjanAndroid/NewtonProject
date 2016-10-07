package com.newtonnew.networkFrameWork;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by happay on 6/10/16.
 */
public class VolleyDeleteRequest extends Request<NetworkResponse> {
    private final Response.Listener<Object> mListener;
    private String mUrl;

    public VolleyDeleteRequest(Response.Listener listner, String url) {
        super(Method.DELETE, url, (Response.ErrorListener) listner);
        mListener = listner;
        mUrl = url;

        setRetryPolicy(new DefaultRetryPolicy(300000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public String getUrl() {
        return mUrl;
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

        String response = new String(networkResponse.data);

        mListener.onResponse(response);

    }
}


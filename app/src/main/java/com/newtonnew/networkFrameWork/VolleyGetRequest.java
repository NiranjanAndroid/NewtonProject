package com.newtonnew.networkFrameWork;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by sandeepgautam on 23/07/16.
 */
public class VolleyGetRequest extends Request<NetworkResponse> {
    private final Response.Listener<Object> mListener;
    private String mUrl;

    public VolleyGetRequest(Response.Listener listner, String url) {
        super(Method.GET, url, (Response.ErrorListener) listner);
        mListener = listner;
        mUrl = url;
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


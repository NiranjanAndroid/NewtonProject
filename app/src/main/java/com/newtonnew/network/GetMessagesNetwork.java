package com.newtonnew.network;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.volley.VolleyError;
import com.newtonnew.Constants.AppConstants;
import com.newtonnew.Models.ResponseModel;
import com.newtonnew.interfaces.Callback;
import com.newtonnew.networkFrameWork.VolleyGetRequest;
import com.newtonnew.networkFrameWork.VolleyRequestQueue;

/**
 * Created by happay on 6/10/16.
 */
public class GetMessagesNetwork implements com.android.volley.Response.Listener, com.android.volley.Response.ErrorListener {
    private final String mFELIZ_URL = "http://192.168.1.105:8088";
    private Activity mActivity;
    private ResponseModel mResponse;
    private Callback mCallBack;
    private int mEventType = 0;
    protected ProgressDialog mProgressDialog;


    public GetMessagesNetwork(Activity activity, int eventType) {
        mActivity = activity;
        mCallBack = (Callback) activity;
        mEventType = eventType;

        VolleyRequestQueue.getInstance(mActivity).addToRequestQueue(new VolleyGetRequest(this
                , AppConstants.BASE_URL + "/api/message/"));

    }

    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        mResponse = new ResponseModel();
        mResponse.setRespStr("Something Went Wrong");
        mResponse.setResCode(400);
        mCallBack.setResultData(mResponse, mEventType);
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        mResponse = new ResponseModel();
        mResponse.setRespStr((String) response);
        mResponse.setResCode(200);
        mCallBack.setResultData(mResponse, mEventType);
    }
}

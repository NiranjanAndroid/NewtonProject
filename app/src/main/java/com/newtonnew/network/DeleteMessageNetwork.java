package com.newtonnew.network;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.volley.VolleyError;
import com.newtonnew.Constants.AppConstants;
import com.newtonnew.Models.ResponseModel;
import com.newtonnew.activities.MessageDetailActivity;
import com.newtonnew.interfaces.Callback;
import com.newtonnew.networkFrameWork.VolleyDeleteRequest;
import com.newtonnew.networkFrameWork.VolleyRequestQueue;

/**
 * Created by happay on 6/10/16.
 */
public class DeleteMessageNetwork implements com.android.volley.Response.Listener, com.android.volley.Response.ErrorListener {

    private Activity mActivity;
    private Callback mCallBack;
    private ResponseModel mResponse;
    private int mEventType = 0;
    protected ProgressDialog mProgressDialog;


    public DeleteMessageNetwork(Activity activity, int eventType, String id) {
        mActivity = activity;
        mCallBack = (Callback) activity;
        mEventType = eventType;

        if (mActivity instanceof MessageDetailActivity)
            mProgressDialog = ProgressDialog.show(mActivity, "Please Wait", null, false);

        VolleyRequestQueue.getInstance(mActivity).addToRequestQueue(new VolleyDeleteRequest(this
                , AppConstants.BASE_URL + "/api/message/" + id));

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
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
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
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mCallBack.setResultData(mResponse, mEventType);
    }
}

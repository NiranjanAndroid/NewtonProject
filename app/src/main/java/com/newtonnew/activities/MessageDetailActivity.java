package com.newtonnew.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newtonnew.Constants.AppConstants;
import com.newtonnew.Models.MessagesModel;
import com.newtonnew.Models.ResponseModel;
import com.newtonnew.R;
import com.newtonnew.interfaces.Callback;
import com.newtonnew.network.DeleteMessageNetwork;
import com.newtonnew.network.GetMessageNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageDetailActivity extends AppCompatActivity implements Callback, View.OnClickListener {
    private MessagesModel message;

    private TextView text_tittle;
    private TextView text_paticipants;
    private TextView text_sender;
    private TextView text_body;
    private ImageView image_star;
    private TextView text_sender_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(AppConstants.MESSAGE)) {
            message = getIntent().getParcelableExtra(AppConstants.MESSAGE);
        } else {
            Toast.makeText(getApplicationContext(), "SomeThing Went Wrong", Toast.LENGTH_LONG).show();
            finish();
        }

        image_star = (ImageView) findViewById(R.id.image_star);
        text_tittle = (TextView) findViewById(R.id.text_tittle);
        text_paticipants = (TextView) findViewById(R.id.text_participants);
        text_sender = (TextView) findViewById(R.id.text_sender);
        text_body = (TextView) findViewById(R.id.text_body);
        text_sender_email = (TextView) findViewById(R.id.text_sender_email);

        if (message.getBody() == null) {
            setMessage();
            new GetMessageNetwork(this, AppConstants.GET_MESSAGE, message.getMessage_id());
        } else {
            setMessage();
        }
        image_star.setOnClickListener(this);

    }

    private void setMessage() {
        if (message.getParticipants() != null) {
            try {
                JSONArray participants = new JSONArray(message.getParticipants());

                JSONObject participantObj = participants.getJSONObject(0);
                String participant = participantObj.getString("name");
                String participant_email = participantObj.getString("email");
                for (int i = 0; i < participants.length(); i++) {
                    if (i > 0) {

                        participantObj = participants.getJSONObject(i);
                        participant = participant + ", " + participantObj.getString("name");
                        if (i == participants.length() - 1) {
                            participant_email = participant_email + " & " + participantObj.getString("email");

                        } else {
                            participant_email = participant_email + ", " + participantObj.getString("email");
                        }
                    }
                }

                text_sender.setText(participant);

                participant = participant + " & " + "me";

                text_paticipants.setText(participant);
                text_sender_email.setText(participant_email);

            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        if (message.getSubject() != null) {
            text_tittle.setText(message.getSubject());
        }

        setStared();

        if (message.getBody() != null) {
            text_body.setText(message.getBody());
        }
    }

    @Override
    public void setResultData(Object object, int eventType) {
        if (eventType == AppConstants.GET_MESSAGE) {
            if (((ResponseModel) object).getResCode() == 200) {
                JSONObject messageObj = null;
                try {
                    messageObj = new JSONObject(((ResponseModel) object).getRespStr());
                    message.setMessage_id(messageObj.getString("id"));
                    message.setStared(messageObj.getBoolean("isStarred"));
                    message.setRead(messageObj.getBoolean("isRead"));
                    message.setParticipants(messageObj.getString("participants"));
                    message.setPreview(messageObj.getString("preview"));
                    message.setSubject(messageObj.getString("subject"));
                    message.setBody(messageObj.getString("body"));

                    setMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (eventType == AppConstants.DELETE_MESSAGE) {
            if (((ResponseModel) object).getResCode() == 200) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message, menu);
        MenuItem menu_delete = menu.findItem(R.id.action_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_delete) {
            new DeleteMessageNetwork(MessageDetailActivity.this, AppConstants.DELETE_MESSAGE, message.getMessage_id());
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        message.setRead(true);
        intent.putExtra(AppConstants.MESSAGE, message);
        setResult(RESULT_CANCELED, intent);

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.image_star) {
            message.setStared(!message.isStared());
            setStared();
        }
    }

    private void setStared() {
        if (message.isStared()) {
            image_star.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            image_star.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }
}

package com.newtonnew.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.newtonnew.Constants.AppConstants;
import com.newtonnew.Models.MessagesModel;
import com.newtonnew.R;
import com.newtonnew.activities.MessageDetailActivity;
import com.newtonnew.activities.MessagesActivity;
import com.newtonnew.network.DeleteMessageNetwork;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by happay on 6/10/16.
 */
public class MessagesAdapter extends RecyclerView.Adapter {
    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    Activity mActivity;
    ArrayList<MessagesModel> mMessages;

    int Colors[] = {R.color.logo_blue, R.color.logo_green, R.color.logo_pink, R.color.logo_orange};

    List<MessagesModel> itemsPendingRemoval;
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<MessagesModel, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    public int postionDeleted;

    public MessagesAdapter(Activity activity, ArrayList<MessagesModel> messages) {
        itemsPendingRemoval = new ArrayList<>();
        mActivity = activity;
        mMessages = messages;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text_participants;
        TextView text_subject;
        TextView text_preview;
        RelativeLayout rl_item;
        Button undoButton;
        ImageView image_stared;
        ImageView image_circle;

        public MainViewHolder(View itemView) {
            super(itemView);
            text_participants = (TextView) itemView.findViewById(R.id.text_participants);
            text_subject = (TextView) itemView.findViewById(R.id.text_subject);
            text_preview = (TextView) itemView.findViewById(R.id.text_preview);
            rl_item = (RelativeLayout) itemView.findViewById(R.id.rl_item);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
            image_stared = (ImageView) itemView.findViewById(R.id.image_stared);
            image_circle = (ImageView) itemView.findViewById(R.id.image_circle);

            image_stared.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.image_stared) {

                mMessages.get(getAdapterPosition()).setStared(!mMessages.get(getAdapterPosition()).isStared());
                notifyItemChanged(getAdapterPosition());

            } else {
                mMessages.get(getAdapterPosition()).setRead(true);

                ((MessagesActivity) mActivity).positonClicked = getAdapterPosition();

                Intent intent = new Intent(mActivity, MessageDetailActivity.class);
                intent.putExtra(AppConstants.MESSAGE, mMessages.get(getAdapterPosition()));
                mActivity.startActivityForResult(intent, AppConstants.MESSAGE_INTENT);
            }
        }
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meassage, parent, false);
        MainViewHolder mainViewHolder = new MainViewHolder(view);
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MainViewHolder mvh = (MainViewHolder) holder;

        final MessagesModel message = mMessages.get(position);

        if (itemsPendingRemoval.contains(message)) {
            // we need to show the "undo" state of the row
            mvh.itemView.setBackgroundColor(Color.RED);
            mvh.rl_item.setVisibility(View.GONE);
            mvh.undoButton.setVisibility(View.VISIBLE);
            mvh.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(message);
                    pendingRunnables.remove(message);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(message);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(mMessages.indexOf(message));
                }
            });
        } else {
            // we need to show the "normal" state
            mvh.itemView.setBackgroundColor(Color.WHITE);
            mvh.rl_item.setVisibility(View.VISIBLE);
            mvh.undoButton.setVisibility(View.GONE);
            mvh.undoButton.setOnClickListener(null);
            String participant = "";
            if (message.getParticipants() != null) {
                try {
                    JSONArray participants = new JSONArray(message.getParticipants());

                    participant = participants.getString(0);

                    for (int i = 0; i < participants.length(); i++) {
                        if (i > 0) {
                            if (i < participants.length()) {
                                participant = participant + " & " + participants.getString(i);
                            } else {
                                participant = participant + ", " + participants.getString(i);
                            }
                        }
                    }
                    mvh.text_participants.setText(participant);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }

            if (message.getSubject() != null) {
                mvh.text_subject.setText(message.getSubject());
            }

            if (message.getPreview() != null) {
                mvh.text_preview.setText(message.getPreview());
            }
            if (message.isStared()) {
                mvh.image_stared.setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                mvh.image_stared.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
            if (message.isRead()) {
                mvh.rl_item.setBackgroundColor(mActivity.getResources().getColor(R.color.info_grey));
                mvh.text_participants.setTypeface(null, Typeface.NORMAL);
            } else {
                mvh.rl_item.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
                mvh.text_participants.setTypeface(null, Typeface.BOLD);
            }
            TextDrawable drawable1 = TextDrawable.builder()
                    .buildRound(String.valueOf(participant.charAt(0)), mActivity.getResources().getColor(Colors[position % Colors.length]));

            mvh.image_circle.setImageDrawable(drawable1);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void pendingRemoval(int position) {
        final MessagesModel item = mMessages.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(mMessages.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        MessagesModel item = mMessages.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (mMessages.contains(item)) {
            postionDeleted = position;
            new DeleteMessageNetwork(mActivity, AppConstants.DELETE_MESSAGE, mMessages.get(position).getMessage_id());
        }
    }

    public boolean isPendingRemoval(int position) {
        MessagesModel item = mMessages.get(position);
        return itemsPendingRemoval.contains(item);
    }
}



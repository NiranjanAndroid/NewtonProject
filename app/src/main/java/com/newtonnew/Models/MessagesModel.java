package com.newtonnew.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by happay on 6/10/16.
 */
public class MessagesModel implements Parcelable {

    private String message_id;
    private String subject;
    private String participants;
    private String preview;
    private boolean isStared;
    private boolean isRead;
    private String body;

    public MessagesModel() {
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public boolean isStared() {
        return isStared;
    }

    public void setStared(boolean stared) {
        isStared = stared;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message_id);
        parcel.writeString(subject);
        parcel.writeString(participants);
        parcel.writeString(preview);
        parcel.writeByte((byte) (isStared ? 1 : 0));
        parcel.writeByte((byte) (isRead ? 1 : 0));
        parcel.writeString(body);
    }

    protected MessagesModel(Parcel in) {
        message_id = in.readString();
        subject = in.readString();
        participants = in.readString();
        preview = in.readString();
        isStared = in.readByte() != 0;
        isRead = in.readByte() != 0;
        body = in.readString();
    }

    public static final Creator<MessagesModel> CREATOR = new Creator<MessagesModel>() {
        @Override
        public MessagesModel createFromParcel(Parcel in) {
            return new MessagesModel(in);
        }

        @Override
        public MessagesModel[] newArray(int size) {
            return new MessagesModel[size];
        }
    };
}

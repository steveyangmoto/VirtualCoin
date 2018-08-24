package com.elitemobiletechnology.jobcoin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable{
    private String timestamp;
    private String fromAddress;
    private String toAddress;
    private String amount;

    public Transaction(){

    }

    public Transaction(Parcel in){
        String[] data = new String[4];
        in.readStringArray(data);
        this.timestamp = data[0];
        this.fromAddress = data[1];
        this.toAddress = data[2];
        this.amount = data[3];
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.timestamp,
                this.fromAddress,
                this.toAddress,
                this.amount
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}

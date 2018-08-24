package com.elitemobiletechnology.jobcoin.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable{
    private String balance;
    private String userId;
    private Transaction[] transactions;

    public UserInfo(Parcel in){
        balance = in.readString();
        userId = in.readString();
        int size = in.readInt();
        transactions = new Transaction[size];
        in.readTypedArray(transactions,Transaction.CREATOR);
    }

    public String getBalance() {
        return balance;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(balance);
        dest.writeString(userId);
        dest.writeInt(transactions.length);
        dest.writeTypedArray(transactions,flags);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}

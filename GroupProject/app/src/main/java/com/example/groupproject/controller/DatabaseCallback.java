package com.example.groupproject.controller;

import android.content.Context;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class DatabaseCallback {
    private Context context;

    public DatabaseCallback(Context context) {
        this.context = context;
    }
    public Context getContext(){
        return this.context;
    }
    public abstract void run(List<Object> dataList);

    public abstract void successlistener(Boolean success);
}
package com.comiftouch.jeasyfinance.model.api.actions;

import okhttp3.Call;

import java.io.IOException;
import java.util.ArrayList;

public interface CollectionCallBack<T> {
    void onSusses(int code, String message, ArrayList<T> collection);

    void onError(int code, String message);

    void onFailed(Call call, IOException e);
}
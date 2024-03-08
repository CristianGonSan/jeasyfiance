package com.comiftouch.jeasyfinance.model.api.actions;

import okhttp3.Call;

import java.io.IOException;

public interface BooleanCallBack {
    void onSusses(int code);

    void onError(int code, String message);

    void onFailed(Call call, IOException e);
}
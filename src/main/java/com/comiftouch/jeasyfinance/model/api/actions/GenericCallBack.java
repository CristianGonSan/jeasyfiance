package com.comiftouch.jeasyfinance.model.api.actions;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.Call;

import java.io.IOException;

public interface GenericCallBack {
    void onSusses(int code, JsonNode jsonNode);

    void onError(int code, String message);

    void onFailed(Call call, IOException e);
}

package com.comiftouch.jeasyfinance.model.api.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class APIManager {
    protected final OkHttpClient clientHttp;
    protected String apiUrl;
    protected String token;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    protected boolean processing;

    public APIManager(OkHttpClient okHttpClient, String apiUrl, String token) {
        this.clientHttp = okHttpClient;
        this.apiUrl = apiUrl;
        this.token = token;
    }

    public <T> void makeRequest(APIData apiData, Class<T> type, CollectionCallBack<T> collectionCallBack) {
        Request request = buildRequest(apiData);

        clientHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                processing = false;
                collectionCallBack.onFailed(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                processing = false;
                if (response.body() != null) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    String message = jsonNode.get("message").asText();

                    if (response.isSuccessful()) {
                        JsonNode content = jsonNode.get("content");

                        int rows = content.get("row_count").asInt();
                        JsonNode data = content.get("data");

                        ArrayList<T> collection = new ArrayList<>();

                        for (int i = 0; i < rows; i++) {
                            T objet = objectMapper.convertValue(data.get(i), type);
                            collection.add(objet);
                        }

                        collectionCallBack.onSusses(response.code(), message, collection);
                    } else {
                        collectionCallBack.onError(response.code(), message);
                    }
                }
                response.body().close();
                response.close();
            }
        });
    }

    public void makeRequest(APIData apiData, GenericCallBack genericCallBack) {
        Request request = buildRequest(apiData);

        clientHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                processing = false;
                genericCallBack.onFailed(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                processing = false;

                if (response.body() != null) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    if (response.isSuccessful()) {
                        genericCallBack.onSusses(response.code(), jsonNode);
                    } else {
                        genericCallBack.onError(response.code(), jsonNode.get("message").asText());
                    }
                }
                response.body().close();
                response.close();
            }
        });
    }

    public void makeRequest(APIData apiData, BooleanCallBack booleanCallBack) {
        Request request = buildRequest(apiData);

        clientHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                processing = false;
                booleanCallBack.onFailed(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                processing = false;

                if (response.body() != null) {
                    String responseBody = response.body().string();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    if (response.isSuccessful()) {
                        booleanCallBack.onSusses(response.code());
                    } else {
                        booleanCallBack.onError(response.code(), jsonNode.get("message").asText());
                    }
                }
                response.body().close();
                response.close();
            }
        });
    }

    private Request buildRequest(APIData apiData) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(apiUrl);
        if (token != null) requestBuilder.addHeader("TOKEN", token);

        if (apiData.isUseJson()) {
            try {
                RequestBody requestBody = RequestBody.create(
                        objectMapper.writeValueAsString(apiData.getData()),
                        MediaType.parse("application/json; charset=utf-8"));

                requestBuilder.post(requestBody);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            FormBody.Builder formBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : apiData.getData().entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }

            requestBuilder.post(formBuilder.build());
        }
        processing = true;
        return requestBuilder.build();
    }

    public OkHttpClient getClientHttp() {
        return clientHttp;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isProcessing() {
        return processing;
    }

    public static class APIData {
        private final HashMap<String, String> data = new HashMap<>();
        private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        private boolean useJson;

        public APIData() {
        }

        public APIData(boolean useJson) {
            this.useJson = useJson;
        }

        public APIData add(String key, String value) {
            data.put(key, value);
            return this;
        }

        public APIData add(String key, double value) {
            data.put(key, decimalFormat.format(value));
            return this;
        }

        public APIData add(String key, boolean value) {
            data.put(key, String.valueOf(value));
            return this;
        }

        public APIData add(String key, Object value) {
            try {
                data.put(key, objectMapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public HashMap<String, String> getData() {
            return data;
        }

        public boolean isUseJson() {
            return useJson;
        }

        public void setUseJson(boolean useJson) {
            this.useJson = useJson;
        }

        @Override
        public String toString() {
            return "APIData{" +
                    "data=" + data +
                    ", useJson=" + useJson +
                    '}';
        }
    }

}

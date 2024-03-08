package com.comiftouch.jeasyfinance.model.api.session;

import com.comiftouch.jeasyfinance.controller.DependencyContainer;
import com.comiftouch.jeasyfinance.model.api.actions.APIManager;
import com.comiftouch.jeasyfinance.model.api.actions.BooleanCallBack;
import com.comiftouch.jeasyfinance.model.api.actions.GenericCallBack;
import com.comiftouch.jeasyfinance.model.api.dataclass.UserSystem;
import com.comiftouch.jeasyfinance.model.config.PropertiesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private static Session session;

    private UserSystem userSystem;
    private String token;

    private OkHttpClient clientHttp;

    private final APIManager apiManager;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final PropertiesManager propertiesManager;

    private boolean active;
    private boolean activeSession;


    public static Session getInstance() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    private Session() {
        propertiesManager = DependencyContainer.getInstance().getDependency(PropertiesManager.class);

        clientHttp = createClient();
        apiManager = new APIManager(clientHttp, propertiesManager.getStringProperty("apiUrl"), null);

        active = true;
    }

    public synchronized void login(String username, String userPassword, BooleanCallBack booleanCallBack) {
        if (activeSession) {
            System.out.println("Ya hay una sesión activa");
            return;
        }

        if (username == null || userPassword == null) {
            throw new NullPointerException();
        }

        APIManager.APIData apiData = new APIManager.APIData(true);

        apiData.add("group", "session")
                .add("operation", "start")
                .add("username", username)
                .add("user_password", userPassword);

        apiManager.makeRequest(apiData, new GenericCallBack() {
            @Override
            public void onSusses(int code, JsonNode jsonNode) {
                JsonNode content = jsonNode.get("content");

                userSystem = objectMapper.convertValue(content.get("user_system"), UserSystem.class);
                token = content.get("token").asText();
                apiManager.setToken(token);
                activeSession = true;
                booleanCallBack.onSusses(code);
            }

            @Override
            public void onError(int code, String message) {
                activeSession = false;
                booleanCallBack.onError(code, message);
            }

            @Override
            public void onFailed(Call call, IOException e) {
                activeSession = false;
                booleanCallBack.onFailed(call, e);
            }
        });
    }

    public synchronized void logout(BooleanCallBack booleanCallBack) {
        if (activeSession) {

            APIManager.APIData apiData = new APIManager.APIData(true);

            apiData.add("group", "session")
                    .add("operation", "end");

            apiManager.makeRequest(apiData, new BooleanCallBack() {
                @Override
                public void onSusses(int code) {
                    activeSession = false;
                    booleanCallBack.onSusses(code);
                }

                @Override
                public void onError(int code, String message) {
                    booleanCallBack.onError(code, message);
                }

                @Override
                public void onFailed(Call call, IOException e) {
                    booleanCallBack.onFailed(call, e);
                }
            });
        }
    }

    public synchronized void destroy() {
        if (active) {
            if (clientHttp != null) clientHttp.dispatcher().executorService().shutdown();
            clientHttp = null;
            active = false;
        }
    }

    public synchronized UserSystem getUserSystem() {
        return userSystem;
    }

    public synchronized String getToken() {
        return token;
    }

    public synchronized OkHttpClient getClientHttp() {
        return clientHttp;
    }

    @Override
    public String toString() {
        return "Session{" +
                "userSystem=" + userSystem +
                ", token='" + token + '\'' +
                ", clientHttp=" + clientHttp +
                ", active=" + active +
                ", activeSession=" + activeSession +
                '}';
    }

    private synchronized OkHttpClient createClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(
                propertiesManager.getIntProperty( "connectTimeout"),
                java.util.concurrent.TimeUnit.SECONDS);
        builder.readTimeout(
                propertiesManager.getIntProperty("readTimeout"),
                java.util.concurrent.TimeUnit.SECONDS);

        builder.cookieJar(new CookieJar() {
            final List<Cookie> cookies = new ArrayList<>();

            @Override
            public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
                this.cookies.addAll(cookies);
            }

            @Override
            public @NotNull List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                return cookies;
            }
        });

        return builder.build();
    }

    public synchronized boolean isActiveSession() {
        return activeSession;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized boolean isProcessing() {
        return apiManager.isProcessing();
    }
}


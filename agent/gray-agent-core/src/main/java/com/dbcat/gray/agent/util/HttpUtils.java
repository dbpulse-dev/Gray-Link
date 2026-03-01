package com.dbcat.gray.agent.util;


import com.dbcat.gray.agent.core.logging.api.ILog;
import com.dbcat.gray.agent.core.logging.api.LogManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class HttpUtils {

    private static ILog log = LogManager.getLogger(HttpUtils.class);

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static <T> T request(String uri, Object request, Type typeOfT) throws Exception {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(3000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "*/*");
        try {
            try (OutputStream op = conn.getOutputStream();) {
                String dataJson = GSON.toJson(request);
                op.write(dataJson.getBytes("UTF-8"));
            }
            String resultString = null;
            try (InputStream in = conn.getInputStream();) {
                resultString = copyToString(in, Charset.forName("utf-8"));
            }
            if (StringUtil.isEmpty(resultString)) {
                return null;
            }
            return GSON.fromJson(resultString, typeOfT);
        } catch (Throwable e) {
            log.error("上报信息失败: " + uri, e);
            throw e;
        }
    }

    private static String copyToString(InputStream in, Charset charset) throws IOException {
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        InputStreamReader is = new InputStreamReader(in, charset);
        char[] buffer = new char[4096];
        int charsRead;
        while ((charsRead = is.read(buffer)) != -1) {
            out.append(buffer, 0, charsRead);
        }
        return out.toString();
    }
}

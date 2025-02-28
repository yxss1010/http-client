package icu.yxss10110;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class GetRequest {
    private static final int MAX_RETRIES = 3; // 最大重试次数
    public static void main( String[] args )
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        String url = "http://127.0.0.1:4523/m1/5699385-5380464-default/test-data/csv";
        Request request = new Request.Builder().url(url).build();
        try{
            String result = executeGetWithRetry(okHttpClient, request, MAX_RETRIES);
            System.out.println("GET 请求响应数据: " + result);
        } catch (IOException e) {
            System.err.println("GET 请求最终失败: " + e.getMessage());
        }
    }
    /**
     * 带重试机制的 GET 同步请求方法
     */
    private static String executeGetWithRetry(OkHttpClient client, Request request, int retriesLeft) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string(); // 返回响应数据
            } else {
                if (retriesLeft > 0) {
                    System.out.println("GET 请求失败，状态码: " + response.code() + "，剩余重试次数: " + retriesLeft);
                    return executeGetWithRetry(client, request, retriesLeft - 1); // 递归重试
                } else {
                    throw new IOException("GET 请求最终失败，状态码: " + response.code());
                }
            }
        } catch (IOException e) {
            if (retriesLeft > 0) {
                System.out.println("GET 请求失败，剩余重试次数: " + retriesLeft);
                return executeGetWithRetry(client, request, retriesLeft - 1); // 递归重试
            } else {
                throw e; // 抛出异常
            }
        }
    }
}

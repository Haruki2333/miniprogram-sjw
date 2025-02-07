package com.haruki.cloud.interfaces.deepseek.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.haruki.cloud.interfaces.deepseek.bean.DeepseekMessage;
import com.haruki.cloud.interfaces.deepseek.bean.DeepseekRequest;
import com.haruki.cloud.interfaces.deepseek.bean.DeepseekResponse;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeepseekApi {
    // 从环境变量获取API密钥，建议不要硬编码，如未配置环境变量则使用默认值，此处请替换为你的API密钥
    private static final String API_KEY = "sk-b64a35aa881c4e1eb32ccbfcf517512e";
    
    // 维护对话历史
    private List<DeepseekMessage> messageHistory;
    
    public DeepseekApi() {
        this.messageHistory = new ArrayList<>();
        // 添加系统角色消息
        this.messageHistory.add(new DeepseekMessage("system", "You are a helpful assistant"));
    }
    
    /**
     * 发送新的问题并获取回答，同时维护对话历史
     * @param userQuestion 用户的问题
     * @return 模型的回答
     */
    public String chat(String userQuestion) {
        // 添加用户新的问题到历史记录
        messageHistory.add(new DeepseekMessage("user", userQuestion));
        
        // 调用API获取回答
        String response = callDeepseekChatWithHistory();
        
        // 如果成功获取到回答，将助手的回答也添加到历史记录
        if (!response.startsWith("请求失败") && !response.startsWith("请求出现异常")) {
            messageHistory.add(new DeepseekMessage("assistant", response));
        }
        
        return response;
    }
    
    /**
     * 清空对话历史，开始新的对话
     */
    public void clearHistory() {
        messageHistory.clear();
        messageHistory.add(new DeepseekMessage("system", "You are a helpful assistant"));
    }
    
    /**
     * 底层调用Deepseek接口的方法，使用完整的对话历史
     */
    private String callDeepseekChatWithHistory() {
        // 创建HTTP客户端
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        // 构建请求体：使用完整的对话历史
        DeepseekRequest deepseekRequest = new DeepseekRequest(
            messageHistory.toArray(new DeepseekMessage[0]),
            "deepseek-chat",
            2048,
            1,
            1.0,
            0,
            0
        );

        // 使用Gson将请求对象序列化为JSON字符串
        String requestBodyJson = new Gson().toJson(deepseekRequest);

        // 构建请求体
        RequestBody body = RequestBody.create(mediaType, requestBodyJson);

        // 构造HTTP请求，设置必要的请求头
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                DeepseekResponse responseObj = new Gson().fromJson(responseBody, DeepseekResponse.class);
                return responseObj.getChoices()[0].getMessage().getContent();
            } else {
                return "请求失败。响应码：" + response.code() + "，消息：" + response.message();
            }
        } catch (IOException e) {
            return "请求出现异常：" + e.getMessage();
        }
    }
}

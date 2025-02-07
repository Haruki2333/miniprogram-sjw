package com.haruki.cloud.interfaces.deepseek.bean;

public class DeepseekRequest {
    private DeepseekMessage[] messages; // 对话消息列表，包含角色和内容的对象数组
    private String model; // 使用的模型名称（如：deepseek-chat/deepseek-r1）
    private int max_tokens; // 生成结果的最大token数（默认4096，最大8192）
    private double temperature; // 采样温度（0-2），值越高输出越随机，值越低输出越确定
    private double top_p; // 核采样阈值（0-1），仅保留概率质量在top_p内的token
    private double frequency_penalty; // 频率惩罚系数（-2.0-2.0），正值降低重复用词
    private double presence_penalty; // 存在惩罚系数（-2.0-2.0），正值降低重复主题
    private ResponseFormat response_format = new ResponseFormat("text"); // 响应格式设置（默认text，可选json_object）
    private Object stop = null; // 停止序列（字符串或数组），遇到指定字符时停止生成
    private boolean stream = false; // 是否启用流式传输（true时实时返回token）
    private Object stream_options = null; // 流式传输选项配置（包含include_usage等参数）
    private Object tools = null; // 工具调用列表（函数调用等扩展功能配置）
    private String tool_choice = "none";// 工具选择模式（none/auto/指定工具）
    private boolean logprobs = false; // 是否返回输出token的对数概率
    private Object top_logprobs = null; // 返回每个位置最可能token的数量（整数或null）

    // 更新后的构造函数参数
    public DeepseekRequest(DeepseekMessage[] messages, String model, int max_tokens, double temperature,
            double top_p, double frequency_penalty, double presence_penalty) {
        this.messages = messages;
        this.model = model;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
        this.top_p = top_p;
        this.frequency_penalty = frequency_penalty;
        this.presence_penalty = presence_penalty;
    }

    // 新增响应格式内部类
    public static class ResponseFormat {
        private String type;
        
        public ResponseFormat(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
    }

    // Getters and Setters (添加所有字段的getter和setter)
    // ... 此处省略getter和setter方法，建议使用IDE自动生成
}
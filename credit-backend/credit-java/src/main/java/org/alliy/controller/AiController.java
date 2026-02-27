package org.alliy.controller;

import org.alliy.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/ai")
public class AiController {

    private final String COZE_API_URL = "https://api.coze.cn/v3/chat";
    private final String PAT = "Bearer pat_CIbvwX0C1VXgmfvkpThqoxcUNKdIdtHCja1GdC6MINaYJOxOkZJLScynH5qrBvx6";
    private final String BOT_ID = "7609964584581644338";

    @PostMapping("/chat")
    public Result chatWithAcademicAi(@RequestBody Map<String, Object> data) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", PAT);

        // --- 数据极致压缩处理 ---
        String name = (String) data.get("name");
        List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("record");
        String question = (String) data.get("question");

        StringBuilder recordStr = new StringBuilder();
        for (Map<String, Object> r : records) {
            // 过滤掉 BLOB 详情等无关数据，仅提取：名，权重，分
            recordStr.append("[").append(r.get("chineseName"))
                    .append(", w=").append(r.get("weight"))
                    .append(", s=").append(r.get("score"))
                    .append("] ");
        }

        String finalPrompt = String.format(
                "背景:学生[%s]。15门加权成绩清单: %s。在此基础上严谨分析回答(不要只给我建议问题): %s",
                name, recordStr.toString(), question
        );

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("bot_id", BOT_ID);
        body.put("user_id", "admin_" + data.get("id"));
        body.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", finalPrompt);
        userMsg.put("content_type", "text");
        messages.add(userMsg);
        body.put("additional_messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        Result r = new Result();
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(COZE_API_URL, entity, Map.class);
            // 关键策略：强制后端缓冲 2 秒，确保同步接口拿到底层答案
            Thread.sleep(2000);

            r.setCode(200);
            r.setData(response.getBody());
            System.out.println("🤖 云端核心响应详情: " + response.getBody());
            return r;
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("对话接口繁忙，建议再次提交查询：" + e.getMessage());
            return r;
        }
    }
}
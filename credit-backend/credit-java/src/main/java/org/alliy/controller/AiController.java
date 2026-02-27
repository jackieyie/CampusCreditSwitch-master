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

    private final String PAT = "Bearer pat_CIbvwX0C1VXgmfvkpThqoxcUNKdIdtHCja1GdC6MINaYJOxOkZJLScynH5qrBvx6";
    private final String BOT_ID = "7609964584581644338";
    private final String COZE_API_URL = "https://api.coze.cn/v3/chat";
    private final String RETRIEVE_URL = "https://api.coze.cn/v3/chat/retrieve";
    private final String MESSAGE_LIST_URL = "https://api.coze.cn/v3/chat/message/list";

    @PostMapping("/chat")
    public Result chatWithAcademicAi(@RequestBody Map<String, Object> data) {
        System.out.println("\n[1. 接收到学生咨询]: " + data.get("name"));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", PAT);

        // 构造 Prompt 数据 (JSON 字符串化)
        String name = (String) data.get("name");
        List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("record");
        String question = (String) data.get("question");

        // 构造请求体
        Map<String, Object> body = new HashMap<>();
        body.put("bot_id", BOT_ID);
        body.put("user_id", "stu_" + data.get("id"));
        body.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", String.format("我是%s，我的成绩数据是：%s。我的问题是：%s", name, records, question));
        userMsg.put("content_type", "text");
        messages.add(userMsg);
        body.put("additional_messages", messages);

        try {
            // 步骤1：发起 Chat
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(COZE_API_URL, entity, Map.class);
            Map chatInfo = (Map) ((Map) response.getBody()).get("data");
            String chatId = (String) chatInfo.get("id");
            String convId = (String) chatInfo.get("conversation_id");

            // 步骤2：轮询状态
            String status = "in_progress";
            int attempts = 0;
            while ("in_progress".equals(status) && attempts < 25) {
                Thread.sleep(1200);
                String url = String.format("%s?chat_id=%s&conversation_id=%s", RETRIEVE_URL, chatId, convId);
                ResponseEntity<Map> poll = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
                status = (String) ((Map) poll.getBody().get("data")).get("status");
                System.out.println("-> 状态检查: " + status);
                attempts++;
            }

            // 步骤3：提取回答
            if ("completed".equals(status)) {
                String msgUrl = String.format("%s?chat_id=%s&conversation_id=%s", MESSAGE_LIST_URL, chatId, convId);
                ResponseEntity<Map> msgResp = restTemplate.exchange(msgUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
                List<Map> msgList = (List<Map>) msgResp.getBody().get("data");

                for (Map m : msgList) {
                    if ("answer".equals(m.get("type"))) {
                        String content = (String) m.get("content");
                        System.out.println("[成功获取 AI 回复]");
                        Result r = new Result();
                        r.setCode(200);
                        r.setData(content);
                        return r;
                    }
                }
            }

            Result fail = new Result();
            fail.setCode(500);
            fail.setMsg("教授正在处理其他事务，请稍后再试。");
            return fail;

        } catch (Exception e) {
            e.printStackTrace();
            Result err = new Result();
            err.setCode(500);
            err.setMsg("系统故障：" + e.getMessage());
            return err;
        }
    }
}
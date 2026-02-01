package org.alliy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.alliy.mapper.AccountMapper;
import org.alliy.pojo.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin // 必须加，解决前端8080访问后端的跨域问题
public class LoginController {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 1. 验证码接口：对接前端 getCodeImgPath 方法
     */
    @GetMapping("/codepath")
    public Map getCode() {
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        // 这里的逻辑先写死，保证前端不报错能显示
        data.put("token", UUID.randomUUID().toString()); // 随机生成一个token
        // 这里给一个占位图，你也可以找个真实验证码图片的URL
        // 在 LoginController.java 里修改这一行
        // 在 LoginController.java 里修改这一行
        data.put(
                "codeImgPath",
                "https://dummyimage.com/110x44/cccccc/000000&text=CODE&rand=" + UUID.randomUUID()
        );



        res.put("code", 200);
        res.put("data", data);
        return res;
    }

    /**
     * 2. 登录接口：对接前端 submitForm 方法
     */
    @PostMapping("/login")
    public Map login(@RequestBody Map<String, String> loginForm, HttpServletResponse response) {
        String username = loginForm.get("username"); // 前端传的是 username
        String password = loginForm.get("password");

        // 简单模拟验证码校验（前端要求长度为5，我们这里就不真校验了）

        // 查询数据库
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("id", username).eq("password", password);
        Account user = accountMapper.selectOne(wrapper);

        Map<String, Object> res = new HashMap<>();
        if (user != null) {
            // 登录成功
            // 关键：前端 Login.vue 88行需要从 header 里拿 authorization
            String jwtToken = "fake-jwt-token-for-" + username;
            response.setHeader("authorization", jwtToken);
            // 必须暴露这个 header，否则前端 JS 拿不到它
            response.setHeader("Access-Control-Expose-Headers", "authorization");

            res.put("code", 200);
            res.put("msg", "登录成功");
            return res;
        } else {
            // 登录失败
            res.put("code", 400);
            res.put("msg", "用户名或密码错误");
            return res;
        }
    }
}
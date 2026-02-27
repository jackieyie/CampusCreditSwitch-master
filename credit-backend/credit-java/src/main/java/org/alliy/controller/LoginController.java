package org.alliy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.alliy.mapper.AccountMapper;
import org.alliy.mapper.StudentMapper; // 【新增】引入 StudentMapper
import org.alliy.pojo.Account;
import org.alliy.pojo.Student; // 【新增】引入 Student 实体
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; // 【建议】引入事务
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private StudentMapper studentMapper; // 【新增】注入 StudentMapper

    @PostMapping("/login")
    public Map login(@RequestBody Map<String, String> loginForm, HttpServletResponse response) {
        // ... 登录逻辑保持不变 ...
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        Account user = accountMapper.selectOne(new QueryWrapper<Account>().eq("id", username).eq("password", password));

        Map<String, Object> res = new HashMap<>();
        if (user != null) {
            String jwtToken = "token-for-" + username;
            response.setHeader("authorization", jwtToken);
            response.setHeader("Access-Control-Expose-Headers", "authorization");
            res.put("code", 200);
            res.put("msg", "登录成功");
            res.put("role", user.getRole());
            return res;
        } else {
            res.put("code", 400);
            res.put("msg", "用户名或密码错误");
            return res;
        }
    }

    /**
     * 修改后的注册方法：双表同步插入
     */
    @PostMapping("/register")
    @Transactional // 【重要】开启事务，确保两张表要么都成功，要么都失败
    public Map register(@RequestBody Map<String, String> regForm) {
        String username = regForm.get("username");
        String password = regForm.get("password");

        Map<String, Object> res = new HashMap<>();
        try {
            // 1. 检查账号是否已存在
            if (accountMapper.selectById(username) != null) {
                res.put("code", 400);
                res.put("msg", "该账号已存在");
                return res;
            }

            Integer userId = Integer.parseInt(username);

            // 2. 向 account 表插入数据
            Account newAccount = new Account();
            newAccount.setId(userId);
            newAccount.setPassword(password);
            newAccount.setRole("student"); // 默认注册为学生
            accountMapper.insert(newAccount);

            // 3. 【核心改进】同步向 student 表插入一条初始记录
            Student newStudent = new Student();
            newStudent.setId(userId);
            newStudent.setName("新同学_" + userId); // 设置一个默认姓名，防止页面空白
            newStudent.setGender("未设置");
            newStudent.setAcademy("待分配学院");
            newStudent.setMajor("待分配专业");
            studentMapper.insert(newStudent);

            res.put("code", 200);
            res.put("msg", "注册成功，已自动创建个人档案");
        } catch (NumberFormatException e) {
            res.put("code", 400);
            res.put("msg", "学号必须为纯数字");
        } catch (Exception e) {
            res.put("code", 500);
            res.put("msg", "注册异常：" + e.getMessage());
        }
        return res;
    }
}
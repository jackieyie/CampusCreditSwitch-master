package org.alliy.controller;

import org.alliy.Result;
import org.alliy.mapper.AccountMapper;
import org.alliy.pojo.Account;
import org.alliy.pojo.Teacher;
import org.alliy.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService service;

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 获取单个教师详情
     */
    @GetMapping("/info/{id}")
    public Result getInfo(@PathVariable Integer id) {
        Result r = new Result();
        Teacher teacher = service.getById(id); // MyBatis-Plus 原生方法
        if (teacher != null) {
            Account account = accountMapper.selectById(id);
            if (account != null) {
                teacher.setPassword(account.getPassword()); // 暂存
            }
            r.setCode(200);
            r.setData(teacher);
        } else {
            r.setCode(404);
            r.setMsg("未找到教职信息");
        }
        return r;
    }

    /**
     * 更新教师档案（及密码）
     */
    @PutMapping
    public Result update(@RequestBody Teacher teacher) {
        // 1. 更新教师表资料 (mail)
        service.updateById(teacher);

        // 2. 处理密码更新
        if (teacher.getPassword() != null && !teacher.getPassword().isEmpty()) {
            Account account = new Account();
            account.setId(teacher.getId());
            account.setPassword(teacher.getPassword());
            accountMapper.updateById(account);
        }

        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }
}
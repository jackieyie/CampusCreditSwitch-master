package org.alliy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.alliy.Result;
import org.alliy.mapper.AccountMapper; // 【确保引入了账户Mapper】
import org.alliy.mapper.CourseMapper;
import org.alliy.mapper.TeacherMapper;
import org.alliy.pojo.Account;
import org.alliy.pojo.Course;
import org.alliy.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin // 🏆 第一防线：处理 8080 与 8081 的所有跨域交互
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    /* ----------------- 🔐 账号与授权管理逻辑 (解决 405 问题的核心区) ----------------- */

    /**
     * 1. 拉取授权清单 (GET)
     */
    @GetMapping("/users")
    public Result getAllAccounts() {
        Result r = new Result();
        r.setCode(200);
        r.setData(accountMapper.selectList(null));
        return r;
    }

    /**
     * 2. 【新增】：通用修改账户接口 (PUT) - 解决 AdminInfo.vue 的 405 错误
     * 前端 AdminInfo 调用的是 /admin/users
     */
    @PutMapping("/users")
    public Result updateAccountData(@RequestBody Account account) {
        Result r = new Result();
        if(accountMapper.updateById(account) > 0) {
            r.setCode(200);
            r.setMsg("系统密匙已全线更新成功");
        }
        return r;
    }

    /**
     * 3. 【新增】：专项角色权限重构 (PUT) - 对接 AdminUsers.vue
     */
    @PutMapping("/users/role")
    public Result updateRole(@RequestBody Account acc) {
        Result r = new Result();
        if(accountMapper.updateById(acc) > 0) {
            r.setCode(200);
            r.setMsg("身份标识已成功重组");
        }
        return r;
    }

    /**
     * 4. 【新增】：管理员重置用户密码 (PUT) - 对接 AdminUsers.vue
     */
    @PutMapping("/users/password")
    public Result forceUpdatePass(@RequestBody Account acc) {
        Result r = new Result();
        if(accountMapper.updateById(acc) > 0) {
            r.setCode(200);
            r.setMsg("旧密令已强制废弃");
        }
        return r;
    }

    /**
     * 5. 获取账号个人详细详情
     */
    @GetMapping("/accounts/info/{id}")
    public Result getAccountDetail(@PathVariable String id) {
        Result r = new Result();
        r.setData(accountMapper.selectById(id));
        r.setCode(200);
        return r;
    }

    /* ----------------- 📚 课程资源管理相关接口 ----------------- */

    @GetMapping("/teachers")
    public Result getAllTeachers() {
        Result r = new Result();
        r.setCode(200);
        r.setData(teacherMapper.selectList(null));
        return r;
    }

    @GetMapping("/courses")
    public Result getAllCourses() {
        Result r = new Result();
        r.setCode(200);
        r.setData(courseMapper.selectList(null));
        return r;
    }

    @PostMapping("/courses")
    public Result addCourse(@RequestBody Course course) {
        Result r = new Result();
        if (courseMapper.insert(course) > 0) { r.setCode(200); }
        return r;
    }

    @PutMapping("/courses")
    public Result updateCourse(@RequestBody Course course) {
        Result r = new Result();
        if (courseMapper.updateById(course) > 0) { r.setCode(200); }
        return r;
    }

    @DeleteMapping("/courses/{id}")
    public Result deleteCourse(@PathVariable String id) {
        Result r = new Result();
        if (courseService.deleteCourseWithRelation(id)) { r.setCode(200); }
        return r;
    }
}
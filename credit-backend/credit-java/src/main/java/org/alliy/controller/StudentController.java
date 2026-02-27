package org.alliy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper; // 🏆 【补全】：必须引入这个
import org.alliy.Result;
import org.alliy.mapper.AccountMapper;
import org.alliy.mapper.StudentCourseMapper; // 🏆 【补全】：引入关联Mapper
import org.alliy.pojo.Account;
import org.alliy.pojo.StuCourse;      // 🏆 【补全】：引入关联实体类
import org.alliy.pojo.Student;
import org.alliy.service.CourseService;
import org.alliy.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/students", produces = "application/json;charset=utf-8")
public class StudentController {

    @Autowired
    private StudentService service;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper; // 🏆 【修复点1】：确保依赖注入存在

    /**
     * 获取所有学生列表
     */
    @GetMapping
    public Result getAll() {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(service.selectAll());
        return r;
    }

    /**
     * 根据学号获取单个学生详细信息
     */
    @GetMapping("/info/{id}")
    public Result getStudentInfo(@PathVariable Integer id) {
        Result r = new Result();
        Student student = service.selectBySid(id);

        if (student != null) {
            Account account = accountMapper.selectById(id);
            if (account != null) {
                student.setPassword(account.getPassword());
            }
            r.setCode(200);
            r.setMsg("查询成功");
            r.setData(student);
        } else {
            r.setCode(404);
            r.setMsg("未找到该学生信息");
        }
        return r;
    }

    /**
     * 根据姓名进行模糊查询
     */
    @GetMapping("/search/{name}")
    public Result getByName(@PathVariable String name) {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        if (name == null || "".equals(name)) {
            r.setData(service.selectAll());
        } else {
            List<Student> students = service.selectByName(name);
            r.setData(students);
        }
        return r;
    }

    /**
     * 修改个人资料：同时更新资料表和密码表
     */
    @PutMapping
    public Result updateStudent(@RequestBody Student student) {
        Result r = new Result();
        try {
            service.updateByStudent(student);
            if (student.getPassword() != null && !student.getPassword().trim().isEmpty()) {
                Account account = new Account();
                account.setId(student.getId());
                account.setPassword(student.getPassword());
                accountMapper.updateById(account);
            }
            r.setCode(200);
            r.setMsg("个人资料变更已存档");
            r.setData(service.selectAll());
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("资料保存失败：" + e.getMessage());
        }
        return r;
    }

    /**
     * 注销学生档案
     */
    @DeleteMapping
    public Result deleteOne(@RequestBody Student student) {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("学籍信息已销号");
        service.deleteByStudent(student);
        r.setData(service.selectAll());
        return r;
    }

    /**
     * 管理端手动增加学生
     */
    @PostMapping
    public Result addOneStudent(@RequestBody Student student) {
        Result r = new Result();
        if (service.addByStudent(student)) {
            r.setCode(200);
            r.setMsg("新生建档完成");
        } else {
            r.setCode(500);
            r.setMsg("建档失败");
        }
        return r;
    }

    /**
     * 查询选修某门课的所有学生（老师端“学生名册”调用）
     */
    @GetMapping("/class/{courseId}")
    public Result getStudentsByCourse(@PathVariable String courseId) {
        Result r = new Result();
        try {
            List<Student> students = service.selectByCourseId(courseId);
            r.setCode(200);
            r.setData(students);
            r.setMsg("教学班名册拉取成功");
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("无法加载班级名单");
        }
        return r;
    }

    /**
     * 学生自助“加入学习”选课接口
     * 对齐前端：this.$axios.post('/students/pickCourse/学号/课号')
     */
    @PostMapping("/pickCourse/{studentId}/{courseId}")
    public Result pick(@PathVariable Integer studentId, @PathVariable String courseId) {
        // 🏆 【修复点2】：对齐项目通用的 new Result 风格，避免符号报错
        Result r = new Result();
        try {
            // 防重复校验：
            QueryWrapper<StuCourse> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId).eq("course_id", courseId);
            if (studentCourseMapper.selectOne(wrapper) != null) {
                r.setCode(400);
                r.setMsg("科目已在清单中");
                return r;
            }

            // 存入选课记录
            StuCourse sc = new StuCourse();
            sc.setStudentId(studentId);
            sc.setCourseId(courseId);
            // 注意：sc.setScore 在初始化时保持为 null（正在修读）

            studentCourseMapper.insert(sc);
            r.setCode(200);
            r.setMsg("已正式建立选课修读关联关系");
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("入库失败：" + e.getMessage());
        }
        return r;
    }
}
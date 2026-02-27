package org.alliy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.alliy.Result;
import org.alliy.mapper.CourseMapper; // 1. 【核心：确认导入了这个Mapper】
import org.alliy.pojo.Course;
import org.alliy.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/courses", produces = "application/json;charset=utf-8")
public class CourseController {

    @Autowired
    private CourseService service;

    // 2. 【核心修改】：在这里注入 courseMapper
    @Autowired
    private CourseMapper courseMapper;

    /**
     * 【老师端功能】：根据老师 ID 查询他带的所有课程
     * 路径：GET /courses/teacher/2001
     */
    @GetMapping("/teacher/{teacherId}")
    public Result getCoursesByTeacher(@PathVariable Integer teacherId) {
        Result r = new Result();
        try {
            // 使用 QueryWrapper 查出 teacher_id 匹配的数据
            QueryWrapper<Course> wrapper = new QueryWrapper<>();
            wrapper.eq("teacher_id", teacherId);

            List<Course> list = courseMapper.selectList(wrapper);

            r.setData(list);
            r.setCode(200);
            r.setMsg("老师授课信息拉取成功");
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("查询异常");
        }
        return r;
    }

    /**
     * 【学生端功能】：根据学号获取个人的修读课程
     */
    @GetMapping("/my/{studentId}")
    public Result getMyCourses(@PathVariable Integer studentId) {
        Result r = new Result();
        try {
            r.setData(service.selectByStudentId(studentId));
            r.setCode(200);
            r.setMsg("学生课程加载成功");
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("加载个人课程失败");
        }
        return r;
    }

    /**
     * 获取全部课程库
     */
    @GetMapping
    public Result getAll() {
        Result r = new Result();
        try {
            List<Course> courses = service.selectAll();
            r.setCode(200);
            r.setData(courses != null ? courses : new ArrayList<>());
            r.setMsg("全部课程拉取成功");
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("加载失败");
        }
        return r;
    }
}
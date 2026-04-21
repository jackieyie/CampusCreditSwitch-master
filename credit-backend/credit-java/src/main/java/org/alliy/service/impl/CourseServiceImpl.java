package org.alliy.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.alliy.mapper.CourseMapper;
import org.alliy.mapper.StudentCourseMapper; // 【新增】引入关联表的 Mapper
import org.alliy.pojo.Course;
import org.alliy.pojo.StuCourse; // 【新增】引入对应的关联实体
import org.alliy.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 【核心】用于事务控制

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper mapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper; // 【新增】注入

    @Override
    public List<Course> selectByStudentId(Integer studentId) {
        return mapper.selectByStudentId(studentId);
    }

    @Override
    public List<Course> selectAll() {
        return mapper.selectList(null);
    }

    /**
     * 【核心改动】：实现级联注销课程逻辑
     * 1. 自动清除 student_course 表中涉及该课程的所有学生修读记录
     * 2. 删除 course 表中对应的科目资源
     */
    @Override
    @Transactional // 开启事务处理，确保数据删除的同步性
    public boolean deleteCourseWithRelation(String courseId) {
        // 第一步：构建删除条件，针对关联表（student_course）
        QueryWrapper<StuCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId); // 这里的 course_id 要对齐你数据库中的真实列名

        // 执行关联记录删除
        studentCourseMapper.delete(wrapper);

        // 第二步：执行课程主表的物理删除动作
        int result = mapper.deleteById(courseId);

        return result > 0;
    }
}
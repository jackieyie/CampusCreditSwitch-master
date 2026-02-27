package org.alliy.service;

import org.alliy.pojo.Course;
import java.util.List;

/**
 * 课程业务逻辑接口定义
 */
public interface CourseService {

    // 1. 根据学生ID查询修读课程（带成绩）
    List<Course> selectByStudentId(Integer studentId);

    // 2. 获取所有课程清单
    List<Course> selectAll();

    /**
     * 3. 【核心新增】：级联删除课程
     * 只有在这里声明了，AdminController 才能调用到这个方法
     * @param courseId 需要删除的课程编号
     * @return 操作是否成功
     */
    boolean deleteCourseWithRelation(String courseId);
}
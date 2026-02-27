package org.alliy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.alliy.pojo.StuCourse; // 这里的名字以你的关联表实体类为准
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface StudentCourseMapper extends BaseMapper<StuCourse> {

    // 【关键代码】：增加根据课程ID删除全部选课记录的方法
    @Delete("DELETE FROM student_course WHERE course_id = #{courseId}")
    int deleteByCourseId(String courseId);
}
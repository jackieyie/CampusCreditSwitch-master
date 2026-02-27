package org.alliy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.alliy.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    // 【核心改动】：使用连表查询。s 为学生表，sc 为你刚建的选课关联表
    @Select("SELECT s.* FROM student s " +
            "INNER JOIN student_course sc ON s.id = sc.student_id " +
            "WHERE sc.course_id = #{courseId}")
    List<Student> selectStudentsByCourseId(String courseId);
}
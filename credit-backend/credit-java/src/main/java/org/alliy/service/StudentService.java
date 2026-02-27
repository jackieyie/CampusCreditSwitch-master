package org.alliy.service;

import org.alliy.pojo.Student;
import java.util.List;

public interface StudentService {
    // 【关键新增】：根据 ID 查询单个学生
    Student selectBySid(Integer id);

    List<Student> selectAll();
    boolean updateByStudent(Student student);
    boolean deleteByStudent(Student student);
    List<Student> selectByName(String name);
    boolean addByStudent(Student student);

    // 业务逻辑方法
    boolean submit2Teacher(Student student, String major);
    List<Student> showStudentListByTeacherId(Integer teacherId, List<Student> oldStudentList);

    List<Student> selectByCourseId(String courseId);
}
package org.alliy.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.alliy.mapper.StudentMapper;
import org.alliy.pojo.Student;
import org.alliy.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 【实现新增】：根据 ID 获取学生对象
     */
    @Override
    public Student selectBySid(Integer id) {
        return studentMapper.selectById(id);
    }

    @Override
    public List<Student> selectAll() {
        return studentMapper.selectList(null);
    }

    @Override
    public boolean updateByStudent(Student student) {
        // MyBatis-Plus 根据实体 ID 自动更新非空字段
        return studentMapper.updateById(student) > 0;
    }

    @Override
    public boolean deleteByStudent(Student student) {
        return studentMapper.deleteById(student.getId()) > 0;
    }

    @Override
    public List<Student> selectByName(String name) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.like("name", name); // 模糊查询
        return studentMapper.selectList(wrapper);
    }

    @Override
    public boolean addByStudent(Student student) {
        return studentMapper.insert(student) > 0;
    }

    /**
     * 业务方法：提交给老师审核（示例逻辑）
     */
    @Override
    public boolean submit2Teacher(Student student, String major) {
        // 这里通常涉及修改学生状态或在申请表中插入记录
        student.setMajor(major);
        return studentMapper.updateById(student) > 0;
    }

    /**
     * 业务方法：根据教师 ID 筛选学生列表
     */
    @Override
    public List<Student> showStudentListByTeacherId(Integer teacherId, List<Student> oldStudentList) {
        // 示例逻辑：筛选出专业与该老师匹配的学生
        // 实际开发中通常使用 SQL 关联查询更好，这里演示内存过滤
        return oldStudentList.stream()
                .filter(s -> s.getMajor() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> selectByCourseId(String courseId) {
        // 逻辑：查询所有在关联表中存在的、且 cid 为对应课号的学生
        // 建议在 StudentMapper.xml 里写 JOIN 语句，效率最高
        return studentMapper.selectStudentsByCourseId(courseId);
    }
}
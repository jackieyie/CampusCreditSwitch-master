package org.alliy.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@TableName("teacher")
@Data
public class Teacher {
    @TableId
    private Integer id;       // 教师工号
    private String name;      // 教师姓名
    private String mail;      // 办公邮箱
    private String major;     // 所属院系/专业

    // 【新增/保留字段】由账户表关联获得，用于个人信息页的显示和修改
    // 标记为 exist = false，代表该字段不属于 teacher 数据库表
    @TableField(exist = false)
    private String password;

    // 逻辑关联字段：一名老师负责的多门课程
    @TableField(exist = false)
    private List<Course> courses;

    // 逻辑关联字段：该教师指导的所有学生
    @TableField(exist = false)
    private List<Student> students;
}
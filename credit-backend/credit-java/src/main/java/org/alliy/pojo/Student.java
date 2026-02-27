package org.alliy.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student")
public class Student {
    @TableId
    private Integer id;      // 学号
    private String name;    // 姓名
    private String gender;  // 性别
    private String grade;   // 年级
    private Integer age;    // 年龄
    private String mail;    // 电子邮件
    private String tel;     // 【新增】手机号
    private String academy; // 所在学院
    private String major;   // 专业班级
    private String card;    // 身份证号/卡号

    // 映射到数据库时忽略，但在前后端传输时使用，用于显示和修改密码
    @TableField(exist = false)
    private String password;
}
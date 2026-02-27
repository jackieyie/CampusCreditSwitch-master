package org.alliy.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_course") // 严格对应数据库表名
public class StuCourse {
    @TableId
    private Integer id;       // 主键
    private Integer studentId; // 学生ID
    private String courseId;  // 课程ID
    private Double score;      // 成绩
}
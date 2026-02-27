package org.alliy.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course")
public class Course {
    @TableId
    private String id;
    private String chineseName;
    private String englishName;
    private String detail;
    private Double credits;
    private Double weight;

    // 🎖️ 【核心：生命槽】必须加这三行
    @TableField(exist = false) // 表示数据库 course 表里没有这一列
    private Double score;      // 这个变量专门用来装 XML 里 sc.score 的值
}
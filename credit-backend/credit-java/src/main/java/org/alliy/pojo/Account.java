package org.alliy.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("account")
@Data
public class Account {
    @TableId
    private Integer id;       // 账号（学号/工号）
    private String password;  // 密码
    private String role;      // 角色：student, teacher, admin
}
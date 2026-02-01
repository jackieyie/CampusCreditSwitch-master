package org.alliy.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("account")
@Data
public class Account {
    @TableId
    private Integer id;
    private String password;
}

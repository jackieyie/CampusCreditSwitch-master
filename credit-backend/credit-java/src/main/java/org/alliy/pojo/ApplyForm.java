package org.alliy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("apply_form")
public class ApplyForm {

    @TableId(type = IdType.AUTO) // 重点：这个 id 必须设置成自动增长
    private Integer id;

    private Integer studentId; // 重点：这里新增一个专门存学号的字段

    private String name;
    private String major;
    private String academy;
    private Double credits;
    private String mail;
    private Date date;
    private Integer preStatus;
    private Integer processStatus;
    private String result;
}
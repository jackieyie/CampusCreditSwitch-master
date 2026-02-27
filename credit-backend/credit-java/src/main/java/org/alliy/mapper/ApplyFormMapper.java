package org.alliy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.alliy.pojo.ApplyForm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学分申请表的数据库操作接口
 * 继承 BaseMapper 即可获得标准的 增删改查 功能
 */
@Mapper
public interface ApplyFormMapper extends BaseMapper<ApplyForm> {
}
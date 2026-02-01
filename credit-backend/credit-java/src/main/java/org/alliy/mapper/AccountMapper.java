package org.alliy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.alliy.pojo.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}

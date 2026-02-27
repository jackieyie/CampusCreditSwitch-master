package org.alliy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.alliy.pojo.Teacher;

/**
 * 继承 MyBatis-Plus 的 IService 接口可以获得大量的内置方法
 * 如 getById, updateById, list 等
 */
public interface TeacherService extends IService<Teacher> {

    // 如果你有特殊的根据工号查询逻辑，可以在这里定义
    Teacher selectByTid(Integer id);
}
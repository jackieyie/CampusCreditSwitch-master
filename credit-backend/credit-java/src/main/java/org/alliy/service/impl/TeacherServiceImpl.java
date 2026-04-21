package org.alliy.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.alliy.mapper.TeacherMapper;
import org.alliy.pojo.Teacher;
import org.alliy.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 手写实现根据工号获取信息的方法
     */
    @Override
    public Teacher selectByTid(Integer id) {
        return teacherMapper.selectById(id);
    }
}
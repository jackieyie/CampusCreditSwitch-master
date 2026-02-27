package org.alliy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.alliy.Result;
import org.alliy.mapper.ApplyFormMapper;
import org.alliy.pojo.ApplyForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/appliers") // 设置根路径
public class ApplierController {

    @Autowired
    private ApplyFormMapper applyFormMapper; // 注入刚才建好的Mapper

    /**
     * 1. 提交新申请
     * 前端 StudentApply.vue 在第 3 步结束时调用
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody ApplyForm form) {
        Result r = new Result();
        try {
            // 自动填充后台字段
            form.setDate(new Date());        // 设置提交日期为当前系统时间
            form.setProcessStatus(0);       // 设置处理状态为：0（待审核）

            // 执行插入数据库操作
            int result = applyFormMapper.insert(form);

            if(result > 0) {
                r.setCode(200);
                r.setMsg("您的学分申请已入库，系统编号：" + form.getId());
            } else {
                r.setCode(500);
                r.setMsg("数据存入失败，请稍后重试");
            }
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("异常：" + e.getMessage());
            e.printStackTrace();
        }
        return r;
    }

    /**
     * 2. 查询所有申请记录 (提供给管理端使用)
     * 后续你在写“审核页面”时会用到这个接口
     */
    @GetMapping("/students")
    public Result getAllStudent() {
        Result r = new Result();
        try {
            // 从数据库 apply_form 表中查出所有记录，并按日期倒序排（新的在前面）
            QueryWrapper<ApplyForm> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("date");
            List<ApplyForm> list = applyFormMapper.selectList(wrapper);

            r.setData(list);
            r.setCode(200);
            r.setMsg("获取申请记录成功");
        } catch (Exception e) {
            r.setCode(500);
            r.setMsg("加载失败");
        }
        return r;
    }

    /**
     * 3. 撤回/物理删除某条申请
     * 用于系统维护或用户取消操作
     */
    @DeleteMapping("/remove/{id}")
    public Result removeApply(@PathVariable Integer id) {
        Result r = new Result();
        if(applyFormMapper.deleteById(id) > 0) {
            r.setCode(200);
            r.setMsg("记录已清除");
        } else {
            r.setCode(404);
            r.setMsg("未找到该记录");
        }
        return r;
    }

    // 在 ApplierController.java 中添加/更新 handle 接口
    @PutMapping("/handle")
    public Result handleApplication(@RequestBody ApplyForm form) {
        Result r = new Result();
        // MyBatis-Plus 会根据 id 自动更新传入的非空字段 (processStatus 和 result)
        if(applyFormMapper.updateById(form) > 0) {
            r.setCode(200);
            r.setMsg("审核操作已成功入库");
        } else {
            r.setCode(500);
            r.setMsg("处理失败，请刷新后重试");
        }
        return r;
    }
}
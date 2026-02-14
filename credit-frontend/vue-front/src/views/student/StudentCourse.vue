<template>
  <div class="course-page-container">
    <!-- 页头提示 -->
    <div class="header-box">
      <el-alert
        title="本列表仅展示您当前正在修读或已完成认证的课程详情"
        type="info"
        show-icon
        :closable="false">
      </el-alert>
      <h3 class="page-title"><i class="el-icon-collection"></i> 我修读的课程列表</h3>
    </div>

    <!-- 课程列表表格 -->
    <el-table
      v-loading="loading"
      ref="courseTable"
      :data="tableData"
      style="width: 100%; border-radius: 8px;"
      border
      stripe
      highlight-current-row
      :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 'bold', textAlign: 'center' }"
      :cell-style="{ textAlign: 'center' }"
    >
      <el-table-column type="index" label="序号" width="70"></el-table-column>
      <el-table-column prop="id" label="课程编号" width="160"></el-table-column>
      <el-table-column prop="chineseName" label="课程名称" min-width="200"></el-table-column>
      <el-table-column prop="englishName" label="英文名" min-width="220"></el-table-column>
      <el-table-column prop="credits" label="学分" width="100">
        <template slot-scope="scope">
          <el-tag size="small" type="success" effect="dark">{{ scope.row.credits }} 分</el-tag>
        </template>
      </el-table-column>

      <!-- 操作栏 -->
      <el-table-column label="详情管理" width="180"> 
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            round
            icon="el-icon-search"
            @click="handleShowDetail(scope.row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 课程详情弹窗 -->
    <el-dialog
      title="课程具体详情"
      :visible.sync="detailVisible"
      width="450px"
      center
      class="course-dialog"
    >
      <div v-if="selectedCourse" class="detail-content">
        <p><strong>🔹 课程中文：</strong> {{ selectedCourse.chineseName }}</p>
        <p><strong>🔹 Course EN：</strong> {{ selectedCourse.englishName }}</p>
        <p><strong>🔹 认证学分：</strong> {{ selectedCourse.credits }}</p>
        <el-divider><i class="el-icon-reading"></i> 课程简介</el-divider>
        <div class="course-detail-body">
          {{ selectedCourse.detail || '暂无详细介绍' }}
        </div>
      </div>
      <span slot="footer">
        <el-button type="primary" @click="detailVisible = false">确 认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "StudentCourse",
  data() {
    return {
      tableData: [], // 后端返回的真实数据列表
      loading: false, // 表格加载动画
      detailVisible: false, // 控制详情弹窗
      selectedCourse: null, // 当前选中的那一行课程
    };
  },
  mounted() {
    // 页面加载即初始化
    this.getMyCourses();
  },
  methods: {
    /**
     * 从后端获取个人课程信息
     * 请求路径示例: http://localhost:8081/courses/my/1001
     */
    getMyCourses() {
      // 获取当前登录学号
      const userId = localStorage.getItem("username");
      if (!userId) {
          this.$message.warning("登录身份失效，请重新登录");
          this.$router.push('/login');
          return;
      }

      this.loading = true;
      this.$axios.get(`/courses/my/${userId}`).then((res) => {
        if (res.data.code === 200) {
          this.tableData = res.data.data;
          console.log("课程获取成功:", this.tableData);
        } else {
          this.$message.info(res.data.msg || "暂未选修任何课程");
        }
      }).catch(err => {
        console.error("加载异常", err);
      }).finally(() => {
        this.loading = false;
      });
    },

    /**
     * 处理显示详情逻辑
     */
    handleShowDetail(row) {
      this.selectedCourse = row; // 记录行数据
      this.detailVisible = true;  // 打开弹窗
    }
  },
};
</script>

<style scoped>
.course-page-container {
  padding: 30px;
}
.header-box {
  margin-bottom: 25px;
}
.page-title {
  margin-top: 20px;
  color: #303133;
  font-weight: 800;
  border-left: 5px solid #409EFF;
  padding-left: 15px;
}
.detail-content p {
  margin-bottom: 15px;
  color: #606266;
  font-size: 15px;
}
.course-detail-body {
  background-color: #fcfcfc;
  border: 1px solid #eeeeee;
  border-radius: 4px;
  padding: 15px;
  line-height: 1.8;
  color: #909399;
  text-indent: 2em;
}
/* 对话框标题样式美化 */
/deep/ .el-dialog__title {
  font-weight: bold;
}
</style>
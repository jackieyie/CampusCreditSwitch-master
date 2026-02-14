<template>
  <div class="class-container">
    <el-card class="class-card" shadow="never">
      <!-- 页头：显示授课统计 -->
      <div slot="header" class="clearfix">
        <span class="title">🏛️ 授课档案与教学班管理</span>
        <el-tag type="info" size="medium" style="margin-left:20px; font-weight: bold;">
          当前负责：{{ tableData.length }} 门课程
        </el-tag>
      </div>

      <!-- 授课列表表格 -->
      <el-table
        :data="tableData"
        border
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f8fafc', color: '#1e3a8a', fontWeight: '800' }"
      >
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="id" label="课程编号" width="160" align="center"></el-table-column>
        <el-table-column prop="chineseName" label="中文课程名称" min-width="180" align="center"></el-table-column>
        <el-table-column prop="englishName" label="English Name" min-width="200" align="center"></el-table-column>
        
        <el-table-column prop="credits" label="对应学分" width="100" align="center">
            <template slot-scope="scope">
                <span style="font-weight: 800;">{{ scope.row.credits }} pts</span>
            </template>
        </el-table-column>

        <el-table-column label="教学维护" width="180" align="center">
          <template slot-scope="scope">
            <!-- 关键功能：查看本课学生名单 -->
            <el-button 
                type="primary" 
                size="mini" 
                icon="el-icon-user" 
                class="academic-btn"
                @click="viewClassStudents(scope.row)">
                学生名册
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生名册 弹窗 (下钻功能) -->
    <el-dialog :title="dialogTitle" :visible.sync="studentListVisible" width="600px">
      <el-table :data="studentList" border size="small" stripe>
        <el-table-column prop="id" label="学号" width="120" align="center"></el-table-column>
        <el-table-column prop="name" label="学生姓名" width="100" align="center"></el-table-column>
        <el-table-column prop="academy" label="所属学院" align="center"></el-table-column>
        <el-table-column prop="major" label="专业班级" align="center"></el-table-column>
      </el-table>
      <span slot="footer">
        <el-button @click="studentListVisible = false" size="small">关闭窗口</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "TeacherClasses",
  data() {
    return {
      tableData: [], // 存储该教师的课程列表
      studentListVisible: false,
      studentList: [], // 存储某门课下的学生名单
      dialogTitle: "",
      loading: false
    };
  },
  mounted() {
    this.getMyTaughtCourses();
  },
  methods: {
    // 1. 获取该老师名下的所有课程
    getMyTaughtCourses() {
      const tid = localStorage.getItem("username");
      if (!tid) return;

      // 假设后端 CourseController 提供了根据老师 ID 查课的接口
      this.$axios.get(`/courses/teacher/${tid}`).then(res => {
        if (res.data.code === 200) {
          this.tableData = res.data.data;
        }
      });
    },

    // 2. 查看某一门课的学生（连表查询逻辑）
    viewClassStudents(row) {
      this.dialogTitle = `教学班：${row.chineseName} - 选课名册`;
      this.studentListVisible = true;
      
      // 向后端请求：查出所有选了 cid = row.id 的学生信息
      this.$axios.get(`/students/class/${row.id}`).then(res => {
        if (res.data.code === 200) {
          this.studentList = res.data.data;
        }
      });
    }
  },
};
</script>

<style scoped>
.class-container { padding: 30px; }
.class-card { border-radius: 8px; border: 1px solid #d1d9e4; }
.title { font-weight: 800; font-size: 18px; color: #1e3a8a; }

/* 延续严肃的深蓝色按钮样式 */
.academic-btn {
    background-color: #1e3a8a !important;
    border-radius: 4px;
    font-weight: 700;
    border: none;
}
.academic-btn:hover { opacity: 0.85; }

/deep/ .el-dialog { border-radius: 10px; }
</style>
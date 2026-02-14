<template>
  <div class="academic-container">
    <el-card class="academic-card" shadow="never">
      <!-- 统一的动态标题页头 -->
      <div slot="header" class="card-header">
        <div class="title-group">
          <span class="main-title">🏛️ 全校标准化教学科目资源中心</span>
          <span class="perspective-hint">[ 当前授权角色: {{ roleDisplayName }} ]</span>
        </div>
      </div>

      <!-- 检索筛选工具 -->
      <div class="filter-toolbar">
          <el-input 
            v-model="search" 
            placeholder="搜索课程编号、名称或详细关键字..." 
            style="width: 400px;" 
            clearable 
            prefix-icon="el-icon-search">
          </el-input>
      </div>

      <!-- 全员共享的大表格 -->
      <el-table :data="filteredData" border stripe class="professional-table">
        <el-table-column prop="id" label="代码编号" width="130" align="center"></el-table-column>
        <el-table-column prop="chineseName" label="科目中文全称" min-width="180" align="center"></el-table-column>
        <el-table-column prop="credits" label="标准学分" width="90" align="center"></el-table-column>
        
        <!-- 学分认证系数：AI逻辑的核心支撑 -->
        <el-table-column label="认定系数" width="100" align="center">
            <template slot-scope="scope">
                <el-tag size="mini" type="info" style="font-weight: 800;">x{{scope.row.weight || 1.0}}</el-tag>
            </template>
        </el-table-column>

        <!-- 🎭 【权限渲染中枢】：根据身份展示按钮组 -->
        <el-table-column label="执行操作/资源状态" width="220" align="center" fixed="right">
          <template slot-scope="scope">
            
            <!-- 情况1：我是【管理员】 -> 提供修正数据的入口 -->
            <div v-if="userRole === 'admin'">
                <el-button size="mini" type="primary" class="thick-btn" @click="$router.push('/admin/courses')">调整基础参数</el-button>
            </div>

            <!-- 情况2：我是【教师】 -> 系统判断这门课是不是我上的 -->
            <div v-else-if="userRole === 'teacher'">
                <el-tag v-if="scope.row.teacherId == myUserId" type="success" effect="dark">这是我的授课班</el-tag>
                <el-tag v-else type="info">归属于其他讲师</el-tag>
            </div>

            <!-- 情况3：我是【学生】 -> 加入我的课表或标记已选 -->
            <div v-else-if="userRole === 'student'">
                <el-button 
                    v-if="!checkIfEnrolled(scope.row.id)" 
                    type="primary" 
                    size="mini" 
                    icon="el-icon-plus"
                    class="thick-btn"
                    @click="handleEnroll(scope.row)">
                    申请加入学习
                </el-button>
                <el-tag v-else type="success" effect="plain" icon="el-icon-check">已存入仪表盘</el-tag>
            </div>

          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "CourseContainer",
  data() {
    return {
      allGlobalCourses: [],
      alreadyEnrolledIds: [], // 用于标记学生已选过的课程ID
      userRole: localStorage.getItem("userRole"), 
      myUserId: localStorage.getItem("username"),
      search: "",
    }
  },
  computed: {
    // 动态生成角色头衔文字
    roleDisplayName() {
        const roles = { admin: '行政资源维护官', teacher: '课程执行讲师', student: '学生学术中心' };
        return roles[this.userRole] || '外访查看';
    },
    // 前端即时搜索
    filteredData() {
        return this.allGlobalCourses.filter(c => 
            (c.chineseName && c.chineseName.includes(this.search)) || (c.id && c.id.includes(this.search))
        );
    }
  },
  mounted() {
    this.fetchInitialData();
    if(this.userRole === 'student') {
        this.fetchUserCurrentEnrollments();
    }
  },
  methods: {
    // 拉取全校唯一的科目库总表
    fetchInitialData() {
      this.$axios.get("/courses").then(res => { this.allGlobalCourses = res.data.data; });
    },

    // 检查哪些课程已经是选修状态，并进行UI反馈
    fetchUserCurrentEnrollments() {
        this.$axios.get(`/courses/my/${this.myUserId}`).then(res => {
            this.alreadyEnrolledIds = res.data.data.map(c => c.id);
        });
    },

    checkIfEnrolled(courseId) {
        return this.alreadyEnrolledIds.includes(courseId);
    },

    // 【重要核心业务】：提交学生“申请学习”指令
    handleEnroll(row) {
        this.$confirm(`确认为您本人[${this.myUserId}] 开启科目《${row.chineseName}》的认证之旅吗？`, '入库提示')
        .then(() => {
            // 调用之前编写好的 pick 接口
            this.$axios.post(`/students/pickCourse/${this.myUserId}/${row.id}`).then(res => {
                if(res.data.code === 200) {
                    this.$message.success("申请成功：学籍关联已建立。");
                    this.fetchUserCurrentEnrollments(); // 静默同步一下按钮状态
                } else {
                    this.$message.error(res.data.msg);
                }
            });
        });
    }
  }
}
</script>

<style scoped>
.academic-container { padding: 40px; }
.academic-card { border-radius: 4px; border: 1px solid #dcdfe6; }
.main-title { font-weight: 800; font-size: 18px; color: #1e3a8a; }
.perspective-hint { margin-left: 15px; font-weight: 700; color: #7f8c8d; }
.filter-toolbar { margin-bottom: 25px; }
.thick-btn { font-weight: 800; border-radius: 4px; border: none; background: #1e3a8a !important; color: #fff; }
.professional-table { border-radius: 4px; box-shadow: 0 4px 10px rgba(0,0,0,0.02); }
/deep/ .el-table__header { line-height: 20px; }
</style>
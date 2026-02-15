<template>
  <div class="academic-container">
    <el-card class="academic-card" shadow="never">
      <!-- 统一的动态标题页头 -->
      <div slot="header" class="card-header">
        <div class="title-group">
          <span class="main-title">🏛️ 全校标准化教学科目资源中心</span>
          <span class="perspective-hint">[ 访问视图: {{ roleDisplayName }} ]</span>
        </div>
      </div>

      <!-- 检索筛选工具 -->
      <div class="filter-toolbar">
          <el-input 
            v-model="search" 
            placeholder="通过 ID 或 中文名 全文检索..." 
            style="width: 420px;" 
            clearable 
            prefix-icon="el-icon-search">
          </el-input>
      </div>

      <!-- 数据主体表格 -->
      <el-table :data="filteredData" border stripe class="professional-table">
        <el-table-column prop="id" label="资产编号" width="130" align="center"></el-table-column>
        <el-table-column prop="chineseName" label="科目全称" min-width="180" align="center"></el-table-column>
        <el-table-column prop="credits" label="认证学分" width="90" align="center">
            <template slot-scope="scope">
                <strong style="color:#1e3a8a">{{scope.row.credits}}</strong>
            </template>
        </el-table-column>
        
        <el-table-column label="认定系数" width="100" align="center">
            <template slot-scope="scope">
                <el-tag size="mini" type="info" style="font-weight: 900;">x{{scope.row.weight || 1.0}}</el-tag>
            </template>
        </el-table-column>

        <!-- 🎭 权限动作渲染 -->
        <el-table-column label="资源状态/指令执行" width="220" align="center" fixed="right">
          <template slot-scope="scope">
            
            <!-- 情况1：我是管理员 - 修改为【不跳转】的原地编辑模式 -->
            <div v-if="userRole === 'admin'">
                <el-button 
                    size="mini" 
                    type="primary" 
                    icon="el-icon-setting"
                    class="thick-btn" 
                    @click="handleQuickEdit(scope.row)">
                    调整基础参数
                </el-button>
            </div>

            <!-- 情况2：我是教师 - 展示所属权 -->
            <div v-else-if="userRole === 'teacher'">
                <el-tag v-if="scope.row.teacherId == myUserId" type="success" effect="dark" class="weight-label">我的主讲科目</el-tag>
                <el-tag v-else type="info" class="weight-label">其他科目资源</el-tag>
            </div>

            <!-- 情况3：我是学生 - 绑定加入逻辑 -->
            <div v-else-if="userRole === 'student'">
                <el-button 
                    v-if="!checkIfEnrolled(scope.row.id)" 
                    type="success" 
                    size="mini" 
                    icon="el-icon-plus"
                    class="thick-btn"
                    @click="handleEnroll(scope.row)">
                    申请加入认证
                </el-button>
                <el-tag v-else type="primary" effect="plain" class="weight-label">
                    <i class="el-icon-check"></i> 已在仪表盘
                </el-tag>
            </div>

          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 🔑 [新增逻辑]: 管理员专用的公共资源微调对话框 (避免了页面跳动) -->
    <el-dialog title="课程中心：公共科目参数同步修改" :visible.sync="showAdminEdit" width="420px" center>
      <div v-if="editForm" style="padding: 0 10px;">
          <p style="font-size: 13px; color: #666; margin-bottom:20px;">
              正在同步修订科目：<span style="font-weight:800; color: #1e3a8a;">《{{editForm.chineseName}}》</span>
          </p>
          <el-form label-position="top">
              <el-form-item label="设定该科目认证基准分">
                  <el-input-number v-model="editForm.credits" :precision="1" :step="0.5" style="width:100%"></el-input-number>
              </el-form-item>
              <el-form-item label="难度权重调节 (AI智能评估核心值)">
                  <el-slider v-model="editForm.weight" :min="0.5" :max="2.0" :step="0.1" show-input></el-slider>
              </el-form-item>
          </el-form>
      </div>
      <span slot="footer">
        <el-button @click="showAdminEdit = false" size="small">弃用变更</el-button>
        <el-button type="primary" size="small" style="background:#1e3a8a !important" @click="confirmGlobalUpdate">生效并将数据存库</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "CourseContainer",
  data() {
    return {
      allGlobalCourses: [],
      alreadyEnrolledIds: [],
      userRole: localStorage.getItem("userRole"), 
      myUserId: localStorage.getItem("username"),
      search: "",
      // 控制修改对话框的状态
      showAdminEdit: false,
      editForm: null
    }
  },
  computed: {
    roleDisplayName() {
        const roles = { admin: '系统档案管理权限', teacher: '任课教师查看权限', student: '学生本人选课视图' };
        return roles[this.userRole] || '游客';
    },
    filteredData() {
        return this.allGlobalCourses.filter(c => 
            (c.chineseName && c.chineseName.includes(this.search)) || (c.id && c.id.includes(this.search))
        );
    }
  },
  mounted() {
    this.refreshAll();
    if(this.userRole === 'student') this.fetchStudentEnrollStatus();
  },
  methods: {
    // 拉取最新的数据
    refreshAll() {
      this.$axios.get("/courses").then(res => { this.allGlobalCourses = res.data.data; });
    },
    fetchStudentEnrollStatus() {
        this.$axios.get(`/courses/my/${this.myUserId}`).then(res => {
            this.alreadyEnrolledIds = res.data.data.map(c => c.id);
        });
    },
    checkIfEnrolled(id) { return this.alreadyEnrolledIds.includes(id); },

    // 【新增方法】：点击“调整基础参数”，只开启弹窗，不做任何 push
    handleQuickEdit(row) {
        this.editForm = { ...row }; // 克隆选中的这行数据
        this.showAdminEdit = true;
    },
    // 【执行真正的后端同步】
    confirmGlobalUpdate() {
        this.$axios.put('/admin/courses', this.editForm).then(res => {
            if(res.data.code === 200) {
                this.$message.success("全局科目资源库数据已校准生效");
                this.showAdminEdit = false;
                this.refreshAll(); // 刷新数据，保证管理员在这一页即时看到变动后的分值/权重
            }
        });
    },

    // 维持原有学生选课申请逻辑
    handleEnroll(row) {
        this.$confirm(`确认为您本人[${this.myUserId}]开启课程《${row.chineseName}》的认定吗？`, '注册提示')
        .then(() => {
            this.$axios.post(`/students/pickCourse/${this.myUserId}/${row.id}`).then(res => {
                if(res.data.code === 200) {
                    this.$message.success("修读记录已正式上报");
                    this.fetchStudentEnrollStatus();
                }
            });
        });
    }
  }
}
</script>

<style scoped>
/* 风格维持严肃专业的蓝色调 */
.academic-container { padding: 40px; background-color: transparent;}
.academic-card { border-radius: 8px; border: 1px solid #dcdfe6; }
.main-title { font-weight: 900; font-size: 19px; color: #1e3a8a; }
.perspective-hint { margin-left: 15px; font-weight: bold; color: #8c939d; font-size: 13px; }
.filter-toolbar { margin: 10px 0 30px; }

/* 加固按钮风格，防止尖锐感 */
.thick-btn { font-weight: 800; border-radius: 4px; border: none; transition: 0.2s;}
.weight-label { font-weight: 800 !important; width: 120px; text-align: center;}

/deep/ .el-table__header { line-height: 25px; }
/deep/ .el-dialog { border-radius: 12px; }
</style>
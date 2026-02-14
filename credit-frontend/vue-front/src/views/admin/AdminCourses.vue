<template>
  <div class="admin-data-view">
    <el-card class="admin-table-card" shadow="never">
      <div slot="header" class="admin-table-header">
        <span class="bold-title">🏛️ 全校教务排课与学分池资源中心</span>
        <el-button type="success" size="mini" icon="el-icon-circle-plus-outline" @click="openAdd">录入新科项</el-button>
      </div>

      <!-- 表格部分 -->
      <el-table :data="courseList" border stripe>
        <el-table-column prop="id" label="标准编号" width="120" align="center"></el-table-column>
        <el-table-column prop="chineseName" label="课程全称" align="center"></el-table-column>
        
        <!-- 【核心改进点】：展示当前谁在教这门课 -->
        <el-table-column label="承担教员" width="160" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.teacherId ? 'primary' : 'warning'" size="small">
               {{ findTeacherName(scope.row.teacherId) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="credits" label="标准学分" width="90" align="center"></el-table-column>

        <el-table-column label="资源状态及运维" width="220" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" class="academic-btn" @click="handleEdit(scope.row)">教员指派与微调</el-button>
            <el-button size="mini" type="danger" plain @click="handleDelete(scope.row)">注销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分配教员与参数编辑 弹窗 -->
    <el-dialog :title="isAdd ? '系统新课程初始化' : '教学任务分配与数据校准'" :visible.sync="showDialog" width="450px">
        <el-form label-position="top">
            <el-form-item label="核心编号" required>
                <el-input v-model="form.id" :disabled="!isAdd" placeholder="识别号一经生成不可更改"></el-input>
            </el-form-item>

            <!-- 【分配功能核心】：从全校老师中挑选 -->
            <el-form-item label="🔏 指定承担教员" required>
                <el-select v-model="form.teacherId" filterable placeholder="按工号或姓名模糊搜索教师" style="width:100%">
                    <el-option
                      v-for="t in teacherList"
                      :key="t.id"
                      :label="t.name + ' (工号:' + t.id + ')'"
                      :value="t.id">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="设定基准学分">
                  <el-input-number v-model="form.credits" :min="0" :step="0.5" style="width:100%"></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="难度因子/权重">
                  <el-input-number v-model="form.weight" :min="0.1" :max="2.0" :step="0.1" style="width:100%"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
        </el-form>
        <div slot="footer">
            <el-button @click="showDialog = false" size="small">取消</el-button>
            <el-button type="primary" size="small" style="background:#1e3a8a!important" @click="submitSave">生效存库并发布指令</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "AdminCourses",
  data() {
    return {
      courseList: [],
      teacherList: [], // 全校教师名录
      showDialog: false,
      isAdd: true,
      form: { id: '', chineseName: '', credits: 2.0, weight: 1.0, teacherId: null }
    }
  },
  mounted() {
    this.refreshList();
    this.fetchTeacherNames(); // 初始化加载老师名单
  },
  methods: {
    refreshList() {
        this.$axios.get("/admin/courses").then(res => this.courseList = res.data.data);
    },
    fetchTeacherNames() {
        this.$axios.get("/admin/teachers").then(res => {
            this.teacherList = res.data.data;
        });
    },
    // 工具函数：根据 ID 在前端匹配出老师名字显示
    findTeacherName(tid) {
        if(!tid) return '未指派教员';
        const teacher = this.teacherList.find(t => t.id === tid);
        return teacher ? teacher.name : '未知老师';
    },
    openAdd() { this.isAdd = true; this.form = { id: '', credits: 2.0, weight: 1.0, teacherId: null }; this.showDialog = true; },
    handleEdit(row) {
        this.isAdd = false;
        this.form = { ...row }; // 将当前行的值复制进表单
        this.showDialog = true;
    },
    submitSave() {
        const url = "/admin/courses"
        const method = this.isAdd ? 'post' : 'put';
        
        this.$axios[method](url, this.form).then(res => {
            if(res.data.code === 200) {
                this.$message.success("教务指令下达成功");
                this.showDialog = false;
                this.refreshList(); // 刷数据
            }
        });
    },
    handleDelete(row) {
        this.$confirm('确定要永久销号此科目资源吗？').then(() => {
            this.$axios.delete(`/admin/courses/${row.id}`).then(() => this.refreshList());
        });
    }
  }
};
</script>

<style scoped>
.admin-data-view { padding: 40px; }
.admin-table-card { border-radius: 4px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05); border-top: 6px solid #34495e; }
.bold-title { font-weight: 800; font-size: 17px; }
.academic-btn { font-weight: 800; border-radius: 4px; background: #34495e!important; }
</style>
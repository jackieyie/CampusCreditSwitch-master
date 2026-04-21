<template>
  <div class="academic-wrapper">
    <div class="academic-container">
      <!-- 教师档案卡 -->
      <el-card class="info-card" shadow="never">
        <div slot="header" class="info-header">
          <span><i class="el-icon-user"></i> 教师个人档案卡</span>
        </div>

        <el-form ref="form" :model="form" label-width="110px" label-suffix="：">
          <el-form-item label="教师姓名">
            <el-input v-model="form.name" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="教职编号">
            <el-input v-model="form.id" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="所属院系">
            <el-input v-model="form.major" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="办公邮箱">
            <el-input v-model="form.mail" class="edit-input" :disabled="!isEdit.mail"></el-input>
            <el-button 
              :type="isEdit.mail ? 'success' : 'primary'" 
              size="small"
              class="action-button"
              @click="handleAction('mail')">
              {{ isEdit.mail ? '确认并保存' : '修改邮箱' }}
            </el-button>
          </el-form-item>

          <el-form-item label="系统密码">
            <el-input 
              :type="isEdit.password ? 'text' : 'password'" 
              v-model="form.password" 
              class="edit-input" 
              :disabled="!isEdit.password">
            </el-input>
            <el-button 
              :type="isEdit.password ? 'danger' : 'warning'" 
              size="small"
              class="action-button"
              @click="handleAction('password')">
              {{ isEdit.password ? '确认修改' : '修改密码' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: "TeacherInfo",
  data() {
    return {
      form: { id: '', name: "", mail: "", major:'', password: "" },
      isEdit: { mail: false, password: false }
    };
  },
  mounted() {
    this.initTeacherData();
  },
  methods: {
    initTeacherData() {
      const userId = localStorage.getItem("username");
      if (!userId) return;
      this.$axios.get(`/teachers/info/${userId}`).then(res => {
        if (res.data.code === 200) { this.form = res.data.data; }
      });
    },
    handleAction(field) {
      if (this.isEdit[field]) { this.saveData(); }
      this.isEdit[field] = !this.isEdit[field];
    },
    saveData() {
      this.$axios({
        method: 'put', url: '/teachers', data: this.form
      }).then(res => {
        if (res.data.code === 200) {
          this.$message({ message: '同步成功：您的档案信息已由教务系统入库。', type: 'success', duration: 1500 });
          this.initTeacherData();
        }
      });
    }
  },
};
</script>

<style scoped>
.academic-wrapper { background-color: transparent; min-height: 100%; }
.academic-container { display: flex; justify-content: center; padding: 50px 0; }
.info-card { width: 630px; border-radius: 8px; border: 1px solid #dcdfe6; background-color: #ffffff; }
.info-header { font-size: 19px; font-weight: 700; color: #1f2d3d; }
/deep/ .el-input__inner { border-radius: 4px; font-weight: 600; border-color: #abb2bb; color: #2c3e50; }
/deep/ .el-input.is-disabled .el-input__inner { background-color: #f1f3f5; color: #5c636a; }
.static-input, .edit-input { width: 285px; }
.action-button { margin-left: 18px; width: 110px; height: 38px; font-weight: 700 !important; border: none; }
.el-button--primary { background-color:rgb(79, 121, 237) !important; }
.el-button--success { background-color: #15803d !important; }
.el-button--warning { background-color: #d97706 !important; }
.el-button--danger { background-color: #e11d48 !important; }
/deep/ .el-form-item__label { font-weight: 800; color: #475569; font-size: 14.5px; }
.el-form-item { margin-bottom: 30px; }
</style>
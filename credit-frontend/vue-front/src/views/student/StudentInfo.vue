<template>
  <div class="academic-wrapper">
    <div class="academic-container">
      <!-- 外层卡片：只保留用户资料功能 -->
      <el-card class="info-card" shadow="never">
        <div slot="header" class="info-header">
          <span><i class="el-icon-user-solid"></i> 用户资料</span>
        </div>

        <el-form ref="form" :model="form" label-width="110px" label-suffix="：">
          <el-form-item label="姓名">
            <el-input v-model="form.name" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="学生学号">
            <el-input v-model="form.id" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="所在院系">
            <el-input v-model="form.academy" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="专业班级">
            <el-input v-model="form.major" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="性别">
            <el-input v-model="form.gender" class="static-input" :disabled="true"></el-input>
          </el-form-item>

          <el-form-item label="联系邮箱">
            <el-input v-model="form.mail" class="edit-input" :disabled="!isEdit.mail"></el-input>
            <el-button 
              :type="isEdit.mail ? 'success' : 'primary'" 
              size="small"
              class="action-button"
              @click="handleAction('mail')">
              {{ isEdit.mail ? '确认并保存' : '修改' }}
            </el-button>
          </el-form-item>

          <el-form-item label="手机号码">
            <el-input v-model="form.tel" class="edit-input" :disabled="!isEdit.tel"></el-input>
            <el-button 
              :type="isEdit.tel ? 'success' : 'primary'" 
              size="small"
              class="action-button"
              @click="handleAction('tel')">
              {{ isEdit.tel ? '确认并保存' : '修改' }}
            </el-button>
          </el-form-item>

          <el-form-item label="密码">
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
  name: "StudentInfo",
  data() {
    return {
      form: { id: '', name: "", gender: "", mail: "", tel: "", academy:'', major:'', password: "" },
      isEdit: { mail: false, tel: false, password: false }
    };
  },
  mounted() {
    this.initStudentData();
  },
  methods: {
    initStudentData() {
      const userId = localStorage.getItem("username");
      if (!userId) return;
      this.$axios.get(`/students/info/${userId}`).then(res => {
        if (res.data.code === 200) { this.form = res.data.data; }
      });
    },
    handleAction(field) {
      if (this.isEdit[field]) { this.saveData(); }
      this.isEdit[field] = !this.isEdit[field];
    },
    saveData() {
      this.$axios({
        method: 'put', url: '/students', data: this.form
      }).then(res => {
        if (res.data.code === 200) {
          this.$message({ message: '数据保存成功：该修改已同步到教务后台。', type: 'success', duration: 1500 });
          this.initStudentData();
        }
      });
    }
  },
};
</script>

<style scoped>
.academic-wrapper { background-color: transparent; min-height: 100%; }
.academic-container { display: flex; justify-content: center; padding: 50px 0; font-family: "Helvetica Neue", Arial, sans-serif; }
.info-card { width: 630px; border-radius: 8px; border: 1px solid #dcdfe6; background-color: #ffffff; }
.info-header { font-size: 19px; font-weight: 700; color: #1f2d3d; letter-spacing: 0.5px; }
/deep/ .el-input__inner { border-radius: 4px; font-weight: 600; border-color: #abb2bb; color: #2c3e50; }
/deep/ .el-input.is-disabled .el-input__inner { background-color: #f1f3f5; color: #5c636a; }
.static-input, .edit-input { width: 285px; }
.action-button { margin-left: 18px; width: 110px; height: 38px; border-radius: 4px; font-weight: 700 !important; border: none; }
.el-button--primary { background-color:rgb(79, 121, 237) !important; }
.el-button--success { background-color: #15803d !important; }
.el-button--warning { background-color: #d97706 !important; }
.el-button--danger { background-color: #e11d48 !important; }
/deep/ .el-form-item__label { font-weight: 800; color: #475569; font-size: 14.5px; }
.el-form-item { margin-bottom: 30px; }
</style>
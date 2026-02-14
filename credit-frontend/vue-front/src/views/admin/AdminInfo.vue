<template>
  <div class="academic-container">
    <el-card class="info-card" shadow="never">
      <div slot="header" class="info-header"><span>核心账户权限卡</span></div>

      <el-form :model="form" label-width="120px" label-suffix="：">
        <el-form-item label="授权ID">
          <el-input v-model="form.id" class="static-input" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="系统备注">
          <el-input v-model="form.name" class="edit-input" :disabled="!isEdit" placeholder="教务管理员"></el-input>
        </el-form-item>

        <el-form-item label="登录密钥">
          <el-input :type="isEdit ? 'text' : 'password'" v-model="form.password" class="edit-input" :disabled="!isEdit"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button 
            :type="isEdit ? 'success' : 'primary'" 
            size="small"
            class="action-button-bold"
            @click="handleAction">
            {{ isEdit ? '确定更新密码' : '申请修改密码' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "AdminInfo",
  data() {
    return {
      isEdit: false,
      form: { id: localStorage.getItem("username"), name: '系统执行管理员', password: "" }
    };
  },
  mounted() { this.getPass(); },
  methods: {
    getPass() {
        this.$axios.get(`/account/info/${this.form.id}`).then(res => {
            this.form.password = res.data.data.password;
        });
    },
    handleAction() {
        if(this.isEdit) {
            this.$axios.put('/account/update', this.form).then(res => {
                this.$message.success("密钥更新成功");
                this.getPass();
            });
        }
        this.isEdit = !this.isEdit;
    }
  }
};
</script>

<style scoped>
.academic-container { display: flex; justify-content: center; padding: 40px; }
.info-card { width: 620px; border-radius: 8px; border: 1px solid #dcdfe6; }
.info-header { font-weight: 800; font-size: 18px; color: #1f2d3d; }
.static-input, .edit-input { width: 300px; }
/deep/ .el-input__inner { border-radius: 4px; font-weight: 700; }

.action-button-bold {
  margin-top: 10px; width: 150px; height: 38px; border-radius: 4px;
  font-weight: 900 !important; font-size: 14px; transition: background-color 0.2s;
}
.el-button--primary { background-color: #34495e !important; border: none; }
.el-button--success { background-color: #2e7d32 !important; border: none; }
</style>
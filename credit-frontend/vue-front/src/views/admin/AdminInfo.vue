<template>
  <div class="academic-container">
    <el-card class="info-card" shadow="never">
      <div slot="header" class="info-header"><span>核心账户权限卡</span></div>

      <el-form :model="form" label-width="120px" label-suffix="：">
        <!-- 只读展示项 -->
        <el-form-item label="授权ID">
          <el-input v-model="form.id" class="static-input" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="管理等级">
           <el-tag type="danger" effect="dark" style="font-weight: 900;">SUPER_ADMIN</el-tag>
        </el-form-item>

        <el-form-item label="系统备注">
          <!-- 这里假设管理员的名字写死或从 role 判断 -->
          <el-input v-model="form.note" class="edit-input" :disabled="!isEdit" placeholder="教务中心执行总管"></el-input>
        </el-form-item>

        <!-- 关键操作项：修改管理员自己的密码 -->
        <el-form-item label="登录密钥">
          <el-input 
            :type="isEdit ? 'text' : 'password'" 
            v-model="form.password" 
            class="edit-input" 
            :disabled="!isEdit"
            placeholder="密钥字符">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button 
            :type="isEdit ? 'success' : 'primary'" 
            size="small"
            class="action-button-bold"
            @click="handleAction">
            {{ isEdit ? '确定更新并保存' : '修改管理密钥' }}
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
      form: { 
        id: localStorage.getItem("username"), // 从本地缓存获取当前工号
        note: '高级系统管理员', 
        password: "" 
      }
    };
  },
  mounted() { 
      // 页面载入时从数据库加载真实的密码
      this.getAdminAccountData(); 
  },
  methods: {
    // 1. 获取管理员原始数据
    getAdminAccountData() {
        if(!this.form.id) return;
        // 🛑 修改路径：加上 /admin 前缀，解决 404 报错
        this.$axios.get(`/admin/accounts/info/${this.form.id}`).then(res => {
            if(res.data.code === 200) {
                this.form.password = res.data.data.password;
                console.log("管理员资料已同步");
            }
        });
    },
    
    // 2. 切换模式并执行提交
    handleAction() {
        if(this.isEdit) {
            // 当处于“保存”状态下被点击时
            this.$axios({
                method: 'put',
                url: '/admin/users', // 复用我们在 AdminController 里的更新用户接口
                data: {
                    id: this.form.id,
                    password: this.form.password
                }
            }).then(res => {
                if(res.data.code === 200) {
                    this.$message({
                        message: '系统根密匙已重塑生效',
                        type: 'success',
                        duration: 2000
                    });
                    this.getAdminAccountData(); // 重新加载一次确保无误
                }
            });
        }
        this.isEdit = !this.isEdit; // 切换编辑模式
    }
  }
};
</script>

<style scoped>
.academic-container { display: flex; justify-content: center; padding-top: 50px; }
.info-card { width: 620px; border-radius: 8px; border: 1px solid #dcdfe6; background-color: #ffffff;}
.info-header { font-weight: 800; font-size: 18px; color: #1e3a8a; }
.static-input, .edit-input { width: 280px; }
/deep/ .el-input__inner { border-radius: 4px; font-weight: bold; }
/* 禁止输入时的底色 */
/deep/ .el-input.is-disabled .el-input__inner { background-color: #f7f9fb; border-color: #e4e7ed; color: #64748b; }

.action-button-bold {
  margin-top: 10px; width: 150px; height: 38px; border-radius: 4px;
  font-weight: 900 !important; font-size: 13.5px; transition: 0.2s; border: none;
}
/* 这里对应管理员母版的深灰色系按钮 */
.el-button--primary { background-color: #34495e !important; }
.el-button--success { background-color: #2e7d32 !important; }
</style>
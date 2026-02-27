<template>
  <div class="user-admin">
    <el-card shadow="never" class="main-card">
      <div slot="header">
        <span class="header-title">🛡️ 全校系统账户授权监控中心</span>
      </div>

      <!-- 用户账户授权列表 -->
      <el-table 
        :data="userList" 
        border 
        stripe
        :header-cell-style="{ background: '#f8fafc', color: '#1a1a1a', fontWeight: '800', textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        
        <el-table-column prop="id" label="账户识别 ID" width="150"></el-table-column>
        
        <!-- 角色显示列：饱满色块风格 -->
        <el-table-column label="当前授权状态" width="160">
            <template slot-scope="scope">
                <el-tag :type="getRoleTag(scope.row.role)" effect="dark" class="bold-filled-tag">
                    {{ roleDisplay(scope.row.role) }}
                </el-tag>
            </template>
        </el-table-column>

        <!-- 管理操作列：已移除密码展示列，此处按钮宽度设为 110px 以对齐标签厚度 -->
        <el-table-column label="行政管控指令" min-width="260">
          <template slot-scope="scope">
            <template v-if="scope.row.id.toString() !== currentLoginId">
                <el-button 
                    size="mini" 
                    type="primary" 
                    class="action-btn-solid" 
                    icon="el-icon-key"
                    @click="openChangeRole(scope.row)">
                    身份重组
                </el-button>
                <el-button 
                    size="mini" 
                    type="warning" 
                    class="action-btn-solid" 
                    icon="el-icon-refresh"
                    @click="handleResetPassword(scope.row)">
                    覆写密令
                </el-button>
            </template>
            <span v-else class="self-tag">
                <i class="el-icon-user"></i> 您当前的管理会话
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 身份重组对话框：延续美化样式 -->
    <el-dialog title="权限等级重定义" :visible.sync="roleDialog" width="450px" center custom-class="fancy-dialog">
      <div v-if="editingUser" class="dialog-body">
        <div class="user-focus-header">
            当前介入账号：<span>{{ editingUser.id }}</span>
        </div>
        <div class="radio-stack">
            <el-radio-group v-model="editingUser.role" class="vertical-radios">
                <el-radio border label="student" class="role-item std-select">
                    <i class="el-icon-school"></i> 学籍：一般在校生
                </el-radio>
                <el-radio border label="teacher" class="role-item tea-select">
                    <i class="el-icon-user"></i> 教务：任课教职工
                </el-radio>
                <el-radio border label="admin" class="role-item adm-select">
                    <i class="el-icon-unlock"></i> 行政：高级管理者
                </el-radio>
            </el-radio-group>
        </div>
      </div>
      <span slot="footer">
        <el-button size="small" @click="roleDialog = false">放弃修改</el-button>
        <el-button type="primary" size="small" class="commit-btn" @click="confirmRoleUpdate">正式下发并同步</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "AdminUsers",
  data() {
    return {
      userList: [],
      roleDialog: false,
      editingUser: null,
      currentLoginId: localStorage.getItem("username")
    };
  },
  mounted() { this.getUsers(); },
  methods: {
    getUsers() { 
        this.$axios.get("/admin/users").then(res => this.userList = res.data.data); 
    },
    getRoleTag(role) { 
        return role === 'admin' ? 'danger' : (role === 'teacher' ? 'primary' : 'success'); 
    },
    roleDisplay(role) {
      if(role === 'admin') return '系统行政总监';
      return role === 'teacher' ? '讲师管理层' : '学籍录入学生';
    },
    openChangeRole(row) {
        this.editingUser = JSON.parse(JSON.stringify(row));
        this.roleDialog = true;
    },
    confirmRoleUpdate() {
        this.$axios.put("/admin/users/role", this.editingUser).then(res => {
            if(res.data.code === 200) {
                this.$message.success("账户身份属性迁移成功");
                this.roleDialog = false;
                this.getUsers();
            }
        });
    },
    handleResetPassword(row) {
        this.$prompt('录入新的强制覆盖密令', '系统内核指令介入', {
            confirmButtonText: '强制持久化',
            cancelButtonText: '作废',
            inputPattern: /^.{3,}$/,
            inputErrorMessage: '密码长度不能少于3位'
        }).then(({ value }) => {
            this.$axios.put("/admin/users/password", { id: row.id, password: value }).then(res => {
                this.$message.success("该账号访问密钥已由管理员手动刷新");
                this.getUsers();
            });
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.user-admin { padding: 40px; background-color: transparent;}
.main-card { border-radius: 8px; border: 1px solid #ced4da; }
.header-title { font-weight: 900; font-size: 20px; color:rgb(147, 181, 237); }

/* 💎 饱满厚实的标签效果 (120px) */
.bold-filled-tag {
  width: 120px;
  height: 30px;
  line-height: 30px;
  font-weight: 900 !important;
  font-size: 13px;
  letter-spacing: 0.5px;
  border-radius: 6px;
  border: none;
}

/* 🚀 饱满饱满的操作按钮效果 (110px) */
.action-btn-solid {
  font-weight: 900 !important;
  letter-spacing: 0.5px;
  width: 110px;      /* 修正宽度使其与标签看起来是对齐的 */
  height: 30px;
  padding: 0 !important;
  border: none;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.12);
  transition: opacity 0.2s;
}
.action-btn-solid:hover { opacity: 0.88; }

.self-tag { color:rgb(194, 210, 253); font-weight: 800; font-size: 13px; padding: 6px 15px; background: #e0f2fe; border-radius: 6px; display: inline-block;}

/* 弹窗内容排列美化 */
.user-focus-header { background: #f1f5f9; padding: 15px; border-radius: 6px; margin-bottom: 25px; font-weight: 800; border: 1px dashed #cbd5e1;}
.user-focus-header span { color: #be123c; margin-left: 10px; text-decoration: underline; font-family: monospace;}
.vertical-radios { display: flex; flex-direction: column; width: 100%; }
.role-item {
    margin: 8px 0 !important;
    width: 100% !important;
    padding: 16px !important;
    border-radius: 8px !important;
    font-weight: 900 !important;
    border: 2.5px solid #f1f5f9 !important; /* 增加边框厚度 */
    height: auto !important;
}
.std-select.is-checked { border-color: #67c23a !important; background: #f0f9eb !important;}
.tea-select.is-checked { border-color: #409eff !important; background: #ecf5ff !important;}
.adm-select.is-checked { border-color: #f56c6c !important; background: #fef0f0 !important;}

.commit-btn { background:rgb(172, 203, 253) !important; border: none; font-weight: 900; letter-spacing: 1px;}

/deep/ .fancy-dialog { border-radius: 12px; border-top: 8px solidrgb(185, 212, 255); overflow: hidden; }
</style>
<template>
  <div class="user-admin">
    <el-card shadow="never" style="border-radius: 8px;">
      <div slot="header">
        <span style="font-weight: 800; font-size: 18px; color:#1e3a8a">全校系统账户授权监控中心</span>
      </div>

      <el-table 
        :data="userList" 
        border 
        stripe
        :header-cell-style="{ background: '#f5f7fa', color: '#1e3a8a', fontWeight: 'bold', textAlign: 'center' }"
        :cell-style="{ textAlign: 'center' }"
      >
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="id" label="学号/工号 (ID)" width="160"></el-table-column>
        
        <el-table-column label="系统授权身份" width="160">
            <template slot-scope="scope">
                <el-tag :type="getRoleTag(scope.row.role)" effect="dark" style="font-weight: 900;">
                    {{ scope.row.role === 'teacher' ? '教职工' : (scope.row.role === 'admin' ? '总管' : '在校生') }}
                </el-tag>
            </template>
        </el-table-column>

        <el-table-column prop="password" label="认证密匙">
            <template slot-scope="scope">
                <span style="font-family: monospace; font-size: 13px;">{{ scope.row.password }}</span>
            </template>
        </el-table-column>

        <el-table-column label="权限管理操作" min-width="250">
          <template slot-scope="scope">
            <el-button 
                v-if="scope.row.role !== 'admin'" 
                size="mini" 
                type="primary" 
                plain
                style="font-weight: 800;"
                @click="openChangeRole(scope.row)">
                身份修改
            </el-button>
            <el-button 
                size="mini" 
                type="warning" 
                plain 
                style="font-weight: 800;"
                @click="handleResetPassword(scope.row)">
                重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 角色更变 Dialog -->
    <el-dialog title="权限重构：修正账户系统标识" :visible.sync="roleDialog" width="400px" center>
      <div v-if="editingUser" style="padding: 10px;">
        <p>正对用户 <span style="color:#f56c6c; font-weight:800">{{ editingUser.id }}</span> 进行权权重定义：</p>
        <div style="margin: 25px 0; text-align: center;">
            <el-radio-group v-model="editingUser.role">
                <el-radio border label="student">分配学生权限</el-radio>
                <el-radio border label="teacher">分配教师权限</el-radio>
            </el-radio-group>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="roleDialog = false">废弃</el-button>
        <el-button type="primary" @click="confirmRoleUpdate">正式下达授权</el-button>
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
      editingUser: null
    };
  },
  mounted() { this.getUsers(); },
  methods: {
    getUsers() {
      this.$axios.get("/admin/users").then(res => this.userList = res.data.data);
    },
    getRoleTag(role) {
      if(role === 'admin') return 'danger';
      return role === 'teacher' ? 'primary' : 'success';
    },
    openChangeRole(row) {
        this.editingUser = JSON.parse(JSON.stringify(row)); // 隔离数据
        this.roleDialog = true;
    },
    confirmRoleUpdate() {
        this.$axios.put("/admin/users/role", this.editingUser).then(res => {
            if(res.data.code === 200) {
                this.$message.success("该账户在系统内的社会身份已修正");
                this.roleDialog = false;
                this.getUsers();
            }
        });
    },
    handleResetPassword(row) {
        this.$prompt('请输入新密码', '最高管理员权限介入', {
            confirmButtonText: '立即生效',
            cancelButtonText: '取消'
        }).then(({ value }) => {
            this.$axios.put("/admin/users/password", { id: row.id, password: value }).then(res => {
                this.$message.success("密令已更迭，旧密码已废止");
                this.getUsers();
            });
        }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.user-admin { padding: 40px; }
/deep/ .el-dialog { border-radius: 12px; }
</style>
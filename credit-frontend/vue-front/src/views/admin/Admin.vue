<template>
  <div class="admin-wrapper">
    <el-container>
      <!-- 侧边导航菜单 -->
      <el-aside width="200px">
        <el-col :span="24">
          <el-menu
            default-active="1"
            class="el-menu-vertical-demo"
            active-text-color="#ffd04b"
            style="font-weight: bold; background:url(http://zsjyc.ncst.edu.cn/tpl/1589185379807/images/bg.png) repeat-x;"
          >
            <!-- 1. 个人档案管理 -->
            <router-link to="/admin/info">
              <el-menu-item index="1">
                <i class="el-icon-s-custom"></i>
                <span>账户档案</span>
              </el-menu-item>
            </router-link>

            <!-- 2. 全校资源总表 (共享视图) -->
            <router-link to="/admin/container">
              <el-menu-item index="2">
                <i class="el-icon-school"></i>
                <span slot="title">总科目</span>
              </el-menu-item>
            </router-link>

            <!-- 3. 管理端核心：课程及权重维护 -->
            <router-link to="/admin/courses">
              <el-menu-item index="3">
                <i class="el-icon-set-up"></i>
                <span slot="title">进行中的课程</span>
              </el-menu-item>
            </router-link>

            <!-- 4. 用户中心：角色及身份修改功能 -->
            <router-link to="/admin/users-mgr">
              <el-menu-item index="4">
                <i class="el-icon-key"></i>
                <span slot="title">用户管理</span>
              </el-menu-item>
            </router-link>

            <!-- ✅ 5. 新增：公告管理功能 -->
            <router-link to="/admin/notices">
              <el-menu-item index="5">
                <i class="el-icon-bell"></i>
                <span slot="title">公告发布</span>
              </el-menu-item>
            </router-link>
            
          </el-menu>
        </el-col>
      </el-aside>

      <el-container>
        <!-- 行政色系顶栏 -->
        <el-header class="admin-header">
          <img src="../../assets/logo1.png" style="width:230px; cursor: pointer; filter: brightness(0) invert(1);" @click="toIndex" /> 
          
          <div class="admin-meta">
            <el-dropdown>
              <span class="el-dropdown-link" style="color: white;">
                 💎 总控管理员 [ID: {{ adminId }}] <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="toIndex()">系统首页</el-dropdown-item>
                <el-dropdown-item @click.native="logout()">安全注销</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 管理员具体子页显示区 -->
        <el-main class="transparent-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "Admin",
  data() {
    return {
      adminId: localStorage.getItem("username") || 'Administrator'
    }
  },
  methods: {
    toIndex() { 
      this.$router.push('/'); 
    },
    logout() {
      this.$confirm('此操作将结束当前管理指令权限，是否退出?', '系统控制提示', { 
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning' 
      }).then(() => {
        localStorage.clear();
        this.$router.push('/login');
        this.$message.success("安全退出成功");
      });
    }
  }
};
</script>

<style scoped>
.admin-wrapper { 
  height: 100vh; 
  background: url('@/assets/嫩绿.jpg') no-repeat repeat-x;
  background-size: cover;
  z-index: 1000;
  overflow: hidden;
}
a { text-decoration: none; }
.el-container { height: 100vh; }
.admin-meta {
  width: auto;
  float: right;
  display: flex;
  align-items: center;
  padding-right: 30px;
}
.admin-header {
  background-color:rgba(86, 169, 252, 0.89); 
  color: white;
  line-height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}
.el-dropdown-link { cursor: pointer; font-size: 16px; font-weight: 800; letter-spacing: 0.5px; }
.el-aside { height:100vh; background-color: white; border-right: 1px solid #dcdfe6;}
.el-menu { height: 100vh; border: none; }
.transparent-main {
  background-color: transparent;
}
</style>
<template>
  <div class="teacher-index">
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
            <router-link to="/teacher/info">
              <el-menu-item index="1">
                <i class="el-icon-user-solid"></i>
                <span slot="title">个人档案</span>
              </el-menu-item>
            </router-link>

            <router-link to="/teacher/apply">
              <el-menu-item index="2">
                <i class="el-icon-s-check"></i>
                <span slot="title">学分申请审批</span>
              </el-menu-item>
            </router-link>

            <router-link to="/teacher/classes">
              <el-menu-item index="3">
                <i class="el-icon-s-management"></i>
                <span slot="title">课程管理</span>
              </el-menu-item>
            </router-link>

            <!-- ✅ 新增：教务公告入口 -->
            <router-link to="/teacher/notices">
              <el-menu-item index="4">
                <i class="el-icon-bell"></i>
                <span slot="title">教务公告</span>
              </el-menu-item>
            </router-link>
          </el-menu>
        </el-col>
      </el-aside>

      <el-container>
        <el-header class="blue-header">
          <img src="../../assets/logo1.png" style="width:230px; cursor: pointer; filter: brightness(0) invert(1);" @click="toIndex" /> 
          
          <div class="admin-info">
            <el-dropdown>
              <span class="el-dropdown-link" style="color: white;">
                 老师：{{ teacher.name }} [{{ teacher.id }}] <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="toIndex()">回到主页</el-dropdown-item>
                <el-dropdown-item @click.native="logout()">注销登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "Teacher",
  data() {
    return {
      teacher: { name: '获取中...', id: '' }
    }
  },
  created() {
    this.getTeacherInfo();
  },
  methods: {
    getTeacherInfo() {
      const tid = localStorage.getItem("username");
      if (!tid) {
        this.$router.push('/login');
        return;
      }
      this.$axios.get(`/teachers/info/${tid}`).then(res => {
        if (res.data.code === 200) {
          this.teacher = res.data.data;
        }
      }).catch(err => {
          console.error("加载顶栏信息失败", err);
      });
    },
    toIndex() { this.$router.push('/'); },
    logout() {
      this.$confirm('是否退出教师管理后台?', '安全提醒', { type: 'warning' }).then(() => {
        localStorage.clear();
        this.$router.push('/login');
      });
    }
  }
};
</script>

<style scoped>
.teacher-index { 
  height: 100vh; 
  background: url('@/assets/嫩绿.jpg') no-repeat repeat-x;
  background-size: cover;
  z-index: 1000;
}
a { text-decoration: none; }
.el-container { height: 100vh; }
.admin-info { width: auto; float: right; display: flex; align-items: center; padding-right: 30px; }
.blue-header {
  background-color: rgb(119, 186, 246);
  color: white;
  line-height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.el-dropdown-link { cursor: pointer; font-size: 15px; font-weight: bold; }
.el-aside { height:100vh; border-right: 1px solid #e0e6ed; }
.el-menu { height: 100vh; border: none; }
.el-main { background-color: transparent; }
</style>
<template>
  <div class="index">
    <el-container>
      <!-- 侧边导航菜单保持原有风格 -->
      <el-aside width="200px">
        <el-col :span="24">
          <el-menu
            default-active="1"
            class="el-menu-vertical-demo"
            active-text-color="#ffd04b"
            style="font-weight: bold; background:url(http://zsjyc.ncst.edu.cn/tpl/1589185379807/images/bg.png) repeat-x;"
          >
            <router-link to="/student/info">
              <el-menu-item index="1">
                <template slot="title">
                  <i class="el-icon-s-custom"></i>
                  <span>个人信息</span>
                </template>
              </el-menu-item>
            </router-link>
            
            <router-link to="/student/course">
              <el-menu-item index="2">
                <i class="el-icon-info"></i>
                <span slot="title">我的课程</span>
              </el-menu-item>
            </router-link>

            <router-link to="/student/container">
              <el-menu-item index="3">
                <i class="el-icon-s-unfold"></i>
                <span slot="title">课程库</span>
              </el-menu-item>
            </router-link>

            <router-link to="/student/credit">
              <el-menu-item index="4">
                <i class="el-icon-s-opportunity"></i>
                <span slot="title">我的学分</span>
              </el-menu-item>
            </router-link>

            <router-link to="/student/apply">
              <el-menu-item index="5">
                <i class="el-icon-success"></i>
                <span slot="title">提交申请</span>
              </el-menu-item>
            </router-link>
          </el-menu>
        </el-col>
      </el-aside>

      <el-container>
        <!-- 【核心修改】：上边栏 Header 改为蓝色 -->
        <el-header class="blue-header">
          <img src="../../assets/logo1.png" style="width:230px; cursor: pointer; filter: brightness(0) invert(1);" @click="toIndex" /> 
          
          <div class="admin">
            <el-dropdown>
              <span class="el-dropdown-link" style="color: white;">
                {{ student.name }} {{ student.id }} <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="toIndex()">返回首页</el-dropdown-item>
                <el-dropdown-item @click.native="logout()">注销登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容区 -->
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "Student",
  data() {
    return {
      student: { name: '加载中...', id: '' }
    }
  },
  mounted() { this.getUserInfo(); },
  methods: {
    getUserInfo() {
      const userId = localStorage.getItem("username"); 
      if (!userId) { this.$router.push('/login'); return; }
      this.$axios.get(`/students/info/${userId}`).then(res => {
        if (res.data.code === 200) { this.student = res.data.data; }
      });
    },
    toIndex() { this.$router.push('/'); },
    logout() {
      this.$confirm('确定要退出系统吗?', '提示', { type: 'warning' }).then(() => {
        localStorage.clear();
        this.$router.push('/login');
      });
    }
  }
};
</script>

<style scoped>
.index{
  background:url('@/assets/嫩绿.jpg') no-repeat repeat-x;
  background-size: cover;
  z-index:1000;
}
a { text-decoration: none; }
.index { height: 100vh; }
.el-container { height: 100vh; }

.admin {
  width: auto;
  float: right;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 20px;
}

/* 【样式修改】定义蓝色顶栏 */
.el-header.blue-header {
  background-color:rgb(119, 186, 246); /* 这里是专业的学术蓝 */
  color: white;
  text-align: center;
  line-height: 60px;
  border-bottom: 1px solidrgb(107, 167, 235); /* 深一点的边框，更有质感 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1); /* 添加轻微阴影，更具立体感 */
}

/* 强制让下拉链接变成白色 */
.el-dropdown-link {
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.el-aside { height:100vh; }
.el-menu { height: 100vh; }

/* 增强Logo对比度，使其在蓝色背景上更清晰 */
img {
    transition: transform 0.3s;
}
img:hover {
    transform: scale(1.05);
}
</style>
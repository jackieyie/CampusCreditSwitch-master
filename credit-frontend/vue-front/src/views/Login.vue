<template>
  <div class="login">
      <el-row type="flex" class="row-bg" justify="center">
        <!-- 左侧欢迎区域 -->
        <el-col :xl="6" :lg="7">
            <h2>欢迎登录学分认证系统</h2>
            <el-image :src="require('@/assets/Login.png')"></el-image>
            <p>校园学分认证管理系统</p>
        </el-col>

        <el-col :span="1">
            <el-divider direction="vertical"></el-divider>
        </el-col>

        <!-- 右侧登录区域 -->
        <el-col :xl="6" :lg="5">
            <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="100px" class="demo-loginForm">
                <el-form-item label="用户名" prop="username" style="width:380px;">
                    <el-input v-model="loginForm.username" placeholder="请输入学号/工号"></el-input>
                </el-form-item>
                 <el-form-item label="密  码" prop="password" style="width:380px;">
                    <el-input type='password' v-model="loginForm.password" placeholder="请输入密码" @keyup.enter.native="submitForm('loginForm')"></el-input>
                </el-form-item>
                
                <el-form-item>
                    <el-button type="primary" @click="submitForm('loginForm')" style="width: 100px;">登录</el-button>
                    <el-button type="text" @click="openRegisterDialog">没有账号？立即注册</el-button>
                </el-form-item>
            </el-form>
        </el-col>
      </el-row>

      <!-- 注册悬浮弹窗 -->
      <el-dialog title="新用户注册" :visible.sync="registerVisible" width="450px" center :close-on-click-modal="false">
        <el-form :model="regForm" :rules="regRules" ref="regForm" label-width="100px">
          <el-form-item label="学号/工号" prop="username">
            <el-input v-model="regForm.username" placeholder="建议使用真实学号"></el-input>
          </el-form-item>
          <el-form-item label="设置密码" prop="password">
            <el-input type="password" v-model="regForm.password" placeholder="至少6位密码"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="checkPass">
            <el-input type="password" v-model="regForm.checkPass" placeholder="请再次输入密码"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="registerVisible = false">取 消</el-button>
          <el-button type="success" @click="submitRegister('regForm')">提交注册</el-button>
        </span>
      </el-dialog>
  </div>
</template>

<script>
export default {
    name:'Login', 
    data() {
      return {
        registerVisible: false,
        loginForm: {
          username:'', 
          password: ''
        },
        regForm: {
          username: '',
          password: '',
          checkPass: ''
        },
        rules: {
          username: [
            { required: true, message: '请输入用户名', trigger: 'blur' },
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
          ]
        },
        regRules: {
          username: [
            { required: true, message: '学号不能为空', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '请设置密码', trigger: 'blur' },
            { min: 6, message: '密码至少6位', trigger: 'blur' }
          ],
          checkPass: [
            { required: true, message: '请再次输入密码确认', trigger: 'blur' }
          ]
        }
      };
    },
    methods: {
        openRegisterDialog() {
            this.registerVisible = true;
            this.$nextTick(() => {
                if (this.$refs.regForm) {
                    this.$refs.regForm.resetFields();
                }
            });
        },
        // 登录逻辑
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.$axios.post('/login', this.loginForm).then((res) => {
                        if (res.data.code === 200) {
                            this.$message.success("登录成功");
                            
                            // 1. 获取后端返回的角色 (role)
                            const role = res.data.role;
                            
                            // 2. 存储 Token、角色信息 和 用户名（ID）到本地
                            const jwt = res.headers['authorization'];
                            if(jwt) localStorage.setItem("token", jwt);
                            
                            localStorage.setItem("userRole", role); 
                            // 【关键新增】必须存入 username，供 Student.vue 等组件使用
                            localStorage.setItem("username", this.loginForm.username);

                            // 3. 根据角色跳转
                            if (role === 'admin') {
                                this.$router.push('/admin/info');
                            } else if (role === 'teacher') {
                                this.$router.push('/teacher/info');
                            } else {
                                // 默认跳转到学生系统页面
                                this.$router.push('/student/info');
                            }
                        } else {
                            this.$message.error(res.data.msg || "用户名或密码错误");
                        }
                    }).catch(err => {
                        this.$message.error("服务器连接异常");
                    })
                }
            });
        },
        // 注册逻辑
        submitRegister(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    if (this.regForm.password !== this.regForm.checkPass) {
                        this.$message.error("两次输入的密码不一致！");
                        return;
                    }
                    this.$axios.post('/register', this.regForm).then(res => {
                        if (res.data.code === 200) {
                            this.$message.success("注册成功！");
                            this.registerVisible = false;
                            this.loginForm.username = this.regForm.username; 
                        } else {
                            this.$message.error(res.data.msg || "注册失败");
                        }
                    })
                } else {
                    return false;
                }
            });
        }
    }
  }
</script>

<style scoped>
    .login{
        height: 100vh;
        background: url(http://zsjyc.ncst.edu.cn/tpl/1589185379807/images/bg.png) repeat-x;
        overflow: hidden;
    }
    .el-row {
        height:100%;
        display: flex;
        align-items: center;
        text-align: center;
    }
    .el-image {
        height: 180px;
        width: 180px;
    }
    .el-divider {
        height:240px;
    }
</style>
<template>
  <div class="login">
      <el-row type="flex" class="row-bg" justify="center">
        <el-col :xl="6" :lg="7">
            <h2>欢迎登录学分认证系统</h2>
            <el-image :src="require('@/assets/Login.png')"></el-image>
            <p>校园学分认证管理系统</p>
        </el-col>
        <el-col :span="1">
            <el-divider direction="vertical"></el-divider>
        </el-col>
        <el-col :xl="6" :lg="5">
            <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="100px" class="demo-loginForm">
                <el-form-item label="用户名" prop="username" style="width:380px;">
                    <el-input v-model="loginForm.username" placeholder="请输入学号/工号"></el-input>
                </el-form-item>
                 <el-form-item label="" prop="password" style="width:380px;">
                     <label slot="label">密&nbsp;&nbsp;&nbsp; 码</label>
                    <el-input type='password' v-model="loginForm.password" placeholder="请输入密码"></el-input>
                </el-form-item>
                
                <!-- 验证码部分：使用 Flex 布局确保左右排列 -->
                <el-form-item label="验证码" prop="code" style="width:380px;">
                    <div style="display: flex; align-items: center;">
                        <el-input v-model="loginForm.code" style="width:160px;" placeholder="验证码"></el-input>
                        <img 
                            v-if="codeImgPath"
                            :src="codeImgPath" 
                            @click="getCodeImgPath"
                            title="点击刷新验证码"
                            style="width:110px; height:40px; margin-left:12px; cursor:pointer; border:1px solid #ddd; border-radius: 4px; display: block;"
                        />
                    </div>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="submitForm('loginForm')" style="width: 100px;">登录</el-button>
                    <el-button @click="resetForm('loginForm')" style="width: 100px;">重置</el-button>
                </el-form-item>
            </el-form>
        </el-col>
      </el-row>
  </div>
</template>

<script>
export default {
    name:'Login', 
    mounted() {
        // 初始化获取验证码
        this.getCodeImgPath();
    },
    data() {
      return {
        loginForm: {
          username:'', 
          password: '',
          code:'',
          token:''       // 存储后端返回的验证码标识
        },
        codeImgPath: null,  // 验证码图片路径
        rules: {
          username: [
            { required: true, message: '请输入用户名', trigger: 'blur' },
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
          ],
          code: [
            { required: true, message: '请输入验证码', trigger: 'blur' },
            { min: 5, max: 5, message: '长度应为 5 个字符', trigger: 'blur' }
          ]
        }
      };
    },
    methods: {
        // 获取验证码方法
        getCodeImgPath(){
            console.log("正在请求验证码...");
            this.$axios({
                method:'get',
                url: '/codepath', 
            }).then(res=>{
                console.log("验证码获取结果:", res.data);
                if(res.data && res.data.data) {
                    this.loginForm.token = res.data.data.token;
                    this.codeImgPath = res.data.data.codeImgPath;
                }
            }).catch(err => {
                console.error("无法获取验证码，请检查后端服务:", err);
            })
        },
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
                // 真实登录逻辑
                this.$axios({
                    method: 'post',
                    url: '/login',
                    data: this.loginForm
                }).then((response) => {
                    console.log("登录响应:", response.data);
                    
                    if (response.data.code === 200) {
                        this.$message.success("登录成功");
                        
                        // 1. 存储 Token
                        const jwt = response.headers['authorization'];
                        if(jwt) {
                            localStorage.setItem("token", jwt);
                            this.$store.commit('SET_TOKEN', jwt);
                        }

                        // 2. 身份跳转逻辑
                        if(this.loginForm.username === 'admin') {
                            this.$router.push('/users');
                        } else {
                            // 默认跳转到课程页
                            this.$router.push('/student/course');
                        }
                    } else {
                        this.$message.error(response.data.msg || "登录失败");
                    }
                }).catch(err => {
                    this.$message.error("服务器连接失败，请检查后端是否开启");
                    console.error(err);
                })
          } else {
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
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
    /* 这里移除了 float，改用 flex 控制 */
    .demo-loginForm {
        margin-top: 20px;
    }
</style>
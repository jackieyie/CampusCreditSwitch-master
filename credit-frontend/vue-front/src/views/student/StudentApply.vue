<template>
  <div class="apply">
    <div class="header">
      <el-steps :active="step" align-center>
        <el-step title="选择目标专业"></el-step>
        <el-step title="核对个人信息"></el-step>
        <el-step title="确认联系方式"></el-step>
        <el-step title="申请已提交"></el-step>
      </el-steps>
    </div>

    <!-- 步骤 1：选择意向专业 -->
    <div v-if="step === 1" class="main">
      <div style="margin: 50px 0;">
        <h3>请在下方列表选择您想要申请调转的专业：</h3>
        <el-select v-model="targetMajor" placeholder="搜索或选择目标专业" style="width: 300px; margin-top: 20px">
          <el-option v-for="item in majorsList" :key="item.id" :label="item.name" :value="item.name"></el-option>
        </el-select>
      </div>
      <div>
        <el-button @click="nextToStep2" type="primary" style="width: 300px" icon="el-icon-right">下一步</el-button>
      </div>
    </div>

    <!-- 步骤 2：信息确认与补充 -->
    <div v-else-if="step === 2" class="main align">
      <el-form ref="form" :model="form" label-width="120px" label-suffix="：">
        <el-form-item label="申请人姓名">
          <el-input v-model="form.name" style="width: 300px" disabled></el-input>
        </el-form-item>

        <!-- 【核心改动处】：这里之前绑定的是 form.id，导致显示空白，现改为绑定 studentId -->
        <el-form-item label="本人学号">
          <el-input v-model="form.studentId" style="width: 300px" disabled></el-input>
        </el-form-item>

        <el-form-item label="目前所在院系">
          <el-input v-model="form.academy" style="width: 300px" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="目标专业">
           <el-tag type="success" effect="dark" style="font-size: 16px">{{ targetMajor }}</el-tag>
        </el-form-item>

        <el-form-item label="累计学分">
          <el-input-number v-model="form.credits" :precision="2" :step="0.1" style="width: 300px"></el-input-number>
        </el-form-item>

        <el-form-item label="学业课程情况">
          <el-select v-model="form.preStatus" style="width: 300px">
            <el-option label="暂无挂科记录" :value="1"></el-option>
            <el-option label="存在不及格课程" :value="0"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button @click="step = 1" style="width: 130px">回上一步</el-button>
          <el-button type="primary" @click="step = 3" style="width: 130px">核实无误</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 步骤 3：联系方式确认 -->
    <div v-else-if="step === 3" class="main">
      <div class="top3">
        <h3>确认您的邮箱：</h3>
        <p style="color: #666; font-size: 13px;">(系统将会把处理进度发送至该邮箱)</p>
        <el-input
          placeholder="请输入接收结果的邮箱"
          style="width: 350px; margin-top: 20px"
          v-model="form.mail"
          prefix-icon="el-icon-message"
        ></el-input>
      </div>
      <div class="bottom3">
        <el-button type="info" @click="step = 2" plain style="width: 120px">上一步</el-button>
        <el-button type="success" style="width: 150px" @click="handleFinalSubmit" :loading="isSubmitting">正式提交申请</el-button>
      </div>
    </div>

    <!-- 步骤 4：结束页 -->
    <div v-else-if="step === 4" class="main">
      <el-result icon="success" title="申请已正式受理" subTitle="您的申请单已进入教务审批流程，后续结果可通过‘我的学分’查询">
        <template slot="extra">
          <el-button type="primary" @click="backToPersonal">返回个人资料</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script>
export default {
  name: "StudentApply",
  data() {
    return {
      step: 1,
      targetMajor: "", 
      isSubmitting: false,
      majorsList: [
        { id: 1001, name: "计算机科学与技术" },
        { id: 1002, name: "数据科学与大数据" },
        { id: 1003, name: "人工智能" },
        { id: 1004, name: "电气工程" },
      ],
      form: {
        id: null,           // 数据库申请单号主键
        studentId: "",      // 学生真实的学号
        name: "", 
        major: "", 
        academy: "", 
        credits: 0.0, 
        mail: "", 
        preStatus: 1, 
      },
    };
  },
  mounted() {
    this.initCurrentUserInfo();
  },
  methods: {
    // 回显逻辑：从后端拉取登录账号的实名信息
    initCurrentUserInfo() {
      const currentUserId = localStorage.getItem("username");
      if (!currentUserId) {
        this.$router.push("/login");
        return;
      }
      this.$axios.get(`/students/info/${currentUserId}`).then((res) => {
        if (res.data && res.data.code === 200) {
          const s = res.data.data;
          // 设置回显
          this.form.id = null; // 确认为空
          this.form.studentId = s.id; // 学号赋予显示字段
          this.form.name = s.name;
          this.form.academy = s.academy;
          this.form.mail = s.mail || "";
        }
      });
    },

    nextToStep2() {
      if (!this.targetMajor) {
        this.$message.warning("请先指定您要申请的目标专业");
        return;
      }
      this.step = 2;
    },

    // 最后提交请求
    handleFinalSubmit() {
      if (!this.form.mail) {
        this.$message.error("联系邮箱是必填项");
        return;
      }

      this.isSubmitting = true;
      this.form.major = this.targetMajor; // 把选好的专业塞入表单

      this.$axios.post("/appliers/submit", this.form)
        .then((res) => {
          if (res.data.code === 200) {
            this.step = 4;
            this.$message.success("数据存入数据库成功");
          }
        })
        .catch(() => this.$message.error("连接异常"))
        .finally(() => this.isSubmitting = false);
    },

    backToPersonal() {
        this.$router.push("/student/info");
    }
  },
};
</script>

<style scoped>
.header { padding: 40px 0; border-bottom: 1px solid #f2f2f2; }
.main { text-align: center; padding: 40px 0;}
.align { display: flex; justify-content: center; }
.top3 { padding-bottom: 30px; }
.bottom3 { padding-top: 10px; }
</style>
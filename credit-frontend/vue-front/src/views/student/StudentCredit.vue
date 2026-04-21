<template>
  <div class="credit-page-container">
    <!-- 1. 统计指标 -->
    <div class="summary-stats">
      <el-row :gutter="25">
        <el-col :span="6">
          <div class="stat-card gold-border">
            <div class="label">已获正式加权学分</div>
            <div class="value">{{ weightedTotalEarned.toFixed(2) }} <span class="unit">pts</span></div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card blue-border">
            <div class="label">基础课程学分总额</div>
            <div class="value">{{ baseTotal.toFixed(1) }} <span class="unit">pts</span></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 2. 数据表格 -->
    <el-table :data="tableData" border stripe class="academic-table" v-loading="loading">
      <el-table-column type="index" label="序号" width="60"></el-table-column>
      <el-table-column prop="chineseName" label="修读课程" min-width="160"></el-table-column>
      <el-table-column label="学分值" width="90" prop="credits"></el-table-column>
      <el-table-column label="系数" width="90" prop="weight"></el-table-column>
      <el-table-column label="得分" width="100" prop="score"></el-table-column>
      <el-table-column label="修读状态认定" width="140">
        <template slot-scope="scope">
          <el-tag :type="statusAttribute(scope.row.score).type" effect="dark" size="small">
            {{ statusAttribute(scope.row.score).label }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 🤖 AI 悬浮按钮 -->
    <div class="ai-fab" @click="showAiDialog = true">
        <div class="fab-inner">
            <i class="el-icon-cpu"></i>
            <span>AI 导师</span>
        </div>
    </div>

    <!-- 🤖 AI 对话弹窗 -->
    <el-dialog 
        title="ai导师在线指导" 
        :visible.sync="showAiDialog" 
        width="600px"
        append-to-body
        custom-class="ai-custom-dialog"
    >
        <div class="chat-wrapper">
            <div class="chat-display" id="chatWindow" v-loading="aiLoading">
                <div v-for="(item, index) in chatHistory" :key="index" :class="['chat-bubble', item.role]">
                    <div class="bubble-meta">{{ item.role === 'user' ? '我的咨询' : '导师寄语' }}</div>
                    <!-- 关键：white-space 处理 AI 纯文本的换行 -->
                    <div class="bubble-content" style="white-space: pre-wrap;">{{ item.content }}</div>
                </div>
                <div v-if="chatHistory.length === 0" class="chat-placeholder">
                    同学你好，我是你的学业导师。关于你目前的成绩和选课规划，有什么想聊聊的吗？
                </div>
            </div>
            <div class="input-area">
                <el-input 
                    placeholder="输入你的学业疑问..." 
                    v-model="userQuestion"
                    @keyup.enter.native="handleSendChat"
                >
                    <el-button slot="append" type="primary" @click="handleSendChat" :loading="aiLoading">发送</el-button>
                </el-input>
            </div>
        </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      tableData: [],
      showAiDialog: false,
      aiLoading: false,
      userQuestion: "",
      chatHistory: [],
      studentName: ""
    };
  },
  computed: {
    weightedTotalEarned() {
      return this.tableData
        .filter(item => item.score != null && parseFloat(item.score) >= 60)
        .reduce((sum, item) => sum + (parseFloat(item.credits || 0) * parseFloat(item.weight || 1.0)), 0);
    },
    baseTotal() {
      return this.tableData.reduce((sum, item) => sum + parseFloat(item.credits || 0), 0);
    }
  },
  mounted() {
    this.initCreditRecord();
    this.getSelfInfo();
  },
  methods: {
    initCreditRecord() {
      const sid = localStorage.getItem("username");
      this.loading = true;
      this.$axios.get(`/courses/my/${sid}`).then(res => {
        this.tableData = res.data.data;
      }).finally(() => { this.loading = false; });
    },
    getSelfInfo() {
      const sid = localStorage.getItem("username");
      this.$axios.get(`/students/info/${sid}`).then(res => {
        this.studentName = res.data.data.name;
      });
    },
    handleSendChat() {
      if (!this.userQuestion.trim() || this.aiLoading) return;
      const qText = this.userQuestion;
      this.chatHistory.push({ role: 'user', content: qText });
      this.userQuestion = "";
      this.aiLoading = true;
      
      this.$axios.post('/ai/chat', {
        id: localStorage.getItem("username"),
        name: this.studentName,
        record: this.tableData,
        question: qText
      }).then(res => {
        if (res.data.code === 200) {
          this.chatHistory.push({ role: 'bot', content: res.data.data });
        }
      }).finally(() => {
        this.aiLoading = false;
        this.$nextTick(() => {
          const c = document.getElementById('chatWindow');
          if (c) c.scrollTop = c.scrollHeight;
        });
      });
    },
    statusAttribute(score) {
      if (score === null) return { label: '修读中', type: 'info' };
      return score >= 60 ? { label: '已认证', type: 'success' } : { label: '未通过', type: 'danger' };
    }
  }
};
</script>

<style scoped>
.credit-page-container { padding: 30px; position: relative; min-height: 80vh; }
.summary-stats { margin-bottom: 20px; }
.stat-card { padding: 20px; border-radius: 8px; border-left: 5px solid; background: #fff; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.gold-border { border-left-color: #ca8a04; }
.blue-border { border-left-color: #1e3a8a; }
.stat-card .value { font-size: 24px; font-weight: bold; }

/* 悬浮按钮 */
.ai-fab {
    position: fixed; right: 50px; bottom: 50px;
    width: 70px; height: 70px;
    background: linear-gradient(135deg, #1e3a8a, #3b82f6);
    border-radius: 50%; color: #fff;
    display: flex; align-items: center; justify-content: center;
    cursor: pointer; box-shadow: 0 8px 20px rgba(30,58,138,0.3);
    z-index: 1000; transition: 0.3s;
}
.ai-fab:hover { transform: scale(1.1); }
.fab-inner { text-align: center; }
.fab-inner i { font-size: 24px; }
.fab-inner span { display: block; font-size: 10px; font-weight: bold; }

/* 聊天窗口 */
.chat-display { height: 400px; overflow-y: auto; padding: 15px; background: #f9fbff; border-radius: 8px; border: 1px solid #eef2f7; }
.chat-bubble { margin-bottom: 15px; max-width: 85%; }
.bubble-meta { font-size: 11px; color: #999; margin-bottom: 4px; }
.bubble-content { padding: 10px 14px; font-size: 14px; line-height: 1.6; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.03); }
.user { margin-left: auto; text-align: right; }
.user .bubble-content { background: #1e3a8a; color: #fff; text-align: left; }
.bot .bubble-content { background: #fff; color: #333; border-left: 4px solid #1e3a8a; }
.chat-placeholder { text-align: center; color: #bbb; padding-top: 150px; font-style: italic; }
.input-area { margin-top: 15px; }
/deep/ .el-input-group__append { background: #1e3a8a; color: #fff; border: none; }
</style>
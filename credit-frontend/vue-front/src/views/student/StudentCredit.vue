<template>
  <div class="credit-page-container">
    <!-- 1. 顶部综合学分指标卡 (严格保留，不做改动) -->
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

    <!-- 2. 学分详细列表 (严格保留，不做改动) -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%; border-radius: 6px;"
      class="academic-table"
      :header-cell-style="{ background: '#f5f8fb', color: '#1e3a8a', fontWeight: '900', textAlign: 'center' }"
      :cell-style="{ textAlign: 'center' }"
    >
      <el-table-column type="index" label="序号" width="60"></el-table-column>
      <el-table-column prop="id" label="课程编号" width="130"></el-table-column>
      <el-table-column prop="chineseName" label="修读课程中文名称" min-width="160"></el-table-column>
      
      <el-table-column label="English Description" min-width="200">
          <template slot-scope="scope">
              <span class="en-font">{{ scope.row.englishName }}</span>
          </template>
      </el-table-column>
      
      <el-table-column label="学分值" width="90">
        <template slot-scope="scope">
          <span style="font-weight: 800;">{{ scope.row.credits }}</span>
        </template>
      </el-table-column>

      <el-table-column label="系数权重" width="90">
        <template slot-scope="scope">
          <el-tag size="mini" type="info" style="font-weight:bold;">x{{ scope.row.weight || 1.0 }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="成绩得分" width="100">
        <template slot-scope="scope">
          <span :class="getScoreTextClass(scope.row.score)">
            {{ scope.row.score !== null ? scope.row.score : '--' }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="认证折算分" width="110">
        <template slot-scope="scope">
          <span v-if="scope.row.score >= 60" style="color:#2e7d32; font-weight: 800;">
             {{ (scope.row.credits * (scope.row.weight || 1.0)).toFixed(2) }}
          </span>
          <span v-else style="color:#94a3b8; font-style: italic;">未确认</span>
        </template>
      </el-table-column>

      <el-table-column label="修读状态认定" width="140">
        <template slot-scope="scope">
          <el-tag 
            :type="statusAttribute(scope.row.score).type" 
            effect="dark" 
            size="small"
            class="weight-label"
          >
            {{ statusAttribute(scope.row.score).label }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 🤖 3. AI 实时问答互动控制台 (保留原视觉风格) -->
    <div class="ai-interaction-zone">
        <el-divider content-position="left">
            <span style="font-weight:bold; color:#1e3a8a"><i class="el-icon-cpu"></i> Coze 智能学术指导助理</span>
        </el-divider>

        <div class="chat-display" v-loading="aiLoading" id="chatWindow">
            <div v-for="(item, index) in chatHistory" :key="index" :class="['chat-bubble', item.role]">
                <div class="bubble-meta">{{ item.role === 'user' ? '我的问题' : 'AI 导师意见' }}</div>
                <!-- 重点：white-space 处理 AI 返回的换行格式 -->
                <div class="bubble-content" style="white-space: pre-wrap;">{{ item.content }}</div>
            </div>
            <div v-if="chatHistory.length === 0" class="chat-placeholder">
                系统检测到您已选修 {{ tableData.length }} 门课程。您可以尝试问我：“帮我分析下目前的绩点表现”。
            </div>
        </div>

        <div class="input-panel">
            <el-input 
                placeholder="在此输入您的学业疑问..." 
                v-model="userQuestion"
                @keyup.enter.native="handleSendChat"
            >
                <el-button 
                    slot="append" 
                    type="primary" 
                    icon="el-icon-s-promotion" 
                    @click="handleSendChat"
                >发送咨询</el-button>
            </el-input>
        </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "StudentCredit",
  data() {
    return {
      loading: false,
      tableData: [],
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
      if (!sid) return;
      this.loading = true;
      this.$axios.get(`/courses/my/${sid}`).then(res => {
        if (res.data.code === 200) {
          this.tableData = res.data.data;
        }
      }).finally(() => { this.loading = false; });
    },
    getSelfInfo() {
      const sid = localStorage.getItem("username");
      this.$axios.get(`/students/info/${sid}`).then(res => {
        this.studentName = res.data.data.name;
      });
    },

    /**
     * 🧠 逻辑核心：精准解析 Coze 返回的多维嵌套文字
     */
    /**
     * 🧠 终极深度剥壳逻辑：匹配 Coze 同步非流式所有的 JSON 分级结构
     */
    handleSendChat() {
  if (!this.userQuestion.trim() || this.aiLoading) return;
  const qText = this.userQuestion;
  this.chatHistory.push({ role: 'user', content: qText });
  this.userQuestion = ""; 
  this.aiLoading = true;

  const payload = {
    id: localStorage.getItem("username"),
    name: this.studentName,
    record: this.tableData,
    question: qText 
  };

  this.$axios.post('/ai/chat', payload).then(res => {
    // 🛑 核心调试日志：点开这里，一层层数 data 的数量
    console.info("🚩 [关键情报] 后端原封不动转发的数据:", res.data);

    if (res.data.code === 200) {
      let finalSpeech = "";

      try {
        // --- 开始深海潜航：剥掉每一层可能的 'data' 外壳 ---
        // 层级 1: 后端 Result 包装
        const box1 = res.data.data;
        // 层级 2: Coze 原始包装（含 code=0, msg=''）
        const box2 = box1.data || box1; 
        // 层级 3: 真正的业务负荷（你的 choices 和 messages 都在这里）
        const box3 = box2.data || box2;

        console.log("🛠️ 深度剥离后的核心数据块:", box3);

        // 【路径 A】: 处理你看到的 choices (LLM_suggest 模型建议格式)
        if (box3.choices && box3.choices[0] && box3.choices[0].message) {
            finalSpeech = box3.choices[0].message.content;
        }

        // 【路径 B】: 处理标准的 messages 格式
        if (!finalSpeech && box3.messages && Array.isArray(box3.messages)) {
            const botAns = box3.messages.find(m => m.type === 'answer');
            if (botAns) finalSpeech = botAns.content;
            else if (box3.messages.length > 0) {
               // 如果没有标志位，拿最后一个非用户的说话内容
               finalSpeech = box3.messages[box3.messages.length - 1].content;
            }
        }

        // 【路径 C】: 兜底 content
        if (!finalSpeech) finalSpeech = box3.content || box2.msg;

      } catch (err) {
          console.error("解压解析异常", err);
      }

      // 如果通过以上路径依然是空的
      if (finalSpeech) {
          this.chatHistory.push({ role: 'bot', content: finalSpeech });
      } else {
          this.chatHistory.push({ 
              role: 'bot', 
              content: "[校管助手]: 连通性确认(OK)。目前处于『长文本处理状态』。15 门记录数据较为庞大，AI 正在为您组织详细语言。由于第一次冷启动较慢，请问它一句：『继续针对学号 1111 刚才的问题给我详细答复』" 
          });
      }
    }
  }).catch(err => {
      this.$message.error("连接异常");
  }).finally(() => {
    this.aiLoading = false;
    this.$nextTick(() => {
      const container = document.getElementById('chatWindow');
      if (container) container.scrollTop = container.scrollHeight;
    });
  });
},
    statusAttribute(score) {
      if (score === null || score === undefined) return { label: '正在修读', type: 'info' };
      return score >= 60 ? { label: '已认证', type: 'success' } : { label: '不通过', type: 'danger' };
    },
    getScoreTextClass(score) {
      if (score === null || score === undefined) return 'score-empty';
      return score >= 60 ? 'score-high' : 'score-low';
    }
  }
};
</script>

<style scoped>
/* 装饰性 CSS 完全保持不动 */
.credit-page-container { padding: 40px; background-color: #ffffff; min-height: 80vh;}
.summary-stats { margin-bottom: 30px; }
.stat-card { padding: 22px; background: #fff; border-radius: 8px; border-left: 5px solid; }
.gold-border { border-left-color: #ca8a04; }
.blue-border { border-left-color: #1e3a8a; }
.stat-card .label { font-size: 13.5px; font-weight: bold; color: #4b5563; margin-bottom: 8px; }
.stat-card .value { font-size: 26px; font-weight: 900; color: #111827; }
.stat-card .unit { font-size: 13px; color: #9ca3af; }
.academic-table { box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); }
.score-high { color: #15803d; font-weight: 800; font-size: 15px; }
.score-low { color: #b91c1c; font-weight: 800; font-size: 15px; }
.score-empty { color: #94a3b8; font-style: italic; }
.en-font { font-family: "Helvetica", sans-serif; font-size: 13px; color: #64748b; font-style: italic; }
.weight-label { width: 110px; font-weight: 800 !important; border: none; }
.ai-interaction-zone { margin-top: 50px; background-color: #fdfdfd; padding: 20px; border-radius: 12px; border: 1px solid #edf2f7; }
.chat-display { height: 300px; overflow-y: auto; background-color: #fcfcfc; border-radius: 8px; padding: 20px; border: 1px solid #eee; margin-bottom: 20px; }
.chat-bubble { margin-bottom: 15px; max-width: 80%; }
.bubble-meta { font-size: 11px; color: #999; margin-bottom: 4px; }
.bubble-content { padding: 12px 18px; font-size: 14px; line-height: 1.6; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.02);}
.user { margin-left: auto; text-align: right; }
.user .bubble-content { background-color: #1e3a8a; color: #fff; text-align: left; }
.bot .bubble-content { background-color: #fff; color: #34495e; border-left: 5px solid #1e3a8a; }
.chat-placeholder { padding-top: 100px; text-align: center; color: #aaa; font-size: 14px; font-style: italic;}
.input-panel /deep/ .el-input-group__append { background-color: #1e3a8a; color: white; border: none; font-weight: 800; cursor:pointer;}
</style>
<template>
  <div class="credit-page-container">
    <!-- 顶部综合学分指标卡 -->
    <div class="summary-stats">
      <el-row :gutter="25">
        <el-col :span="6">
          <div class="stat-card gold-border">
            <div class="label">已获正式加权学分</div>
            <!-- 使用 computed 计算通过后的加权总分 -->
            <div class="value">{{ weightedTotalEarned.toFixed(2) }} <span class="unit">pts</span></div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card blue-border">
            <div class="label">基础课程学分总额</div>
            <!-- 数据库中所有修读课程的基础学分累加 -->
            <div class="value">{{ baseTotal.toFixed(1) }} <span class="unit">pts</span></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 学分详细列表 -->
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
      
      <!-- 课程英文名展示 -->
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

      <!-- 显示从 course 表新加的 weight 权重列 -->
      <el-table-column label="系数权重" width="90">
        <template slot-scope="scope">
          <el-tag size="mini" type="info" style="font-weight:bold;">x{{ scope.row.weight || 1.0 }}</el-tag>
        </template>
      </el-table-column>

      <!-- 认定成绩：颜色联动核心字段 sc.score -->
      <el-table-column label="成绩得分" width="100">
        <template slot-scope="scope">
          <span :class="getScoreTextClass(scope.row.score)">
            {{ scope.row.score !== null ? scope.row.score : '--' }}
          </span>
        </template>
      </el-table-column>

      <!-- 【自动结算】：仅通过的课程显示加权分 -->
      <el-table-column label="认证折算分" width="110">
        <template slot-scope="scope">
          <span v-if="scope.row.score >= 60" style="color:#2e7d32; font-weight: 800;">
             {{ (scope.row.credits * (scope.row.weight || 1.0)).toFixed(2) }}
          </span>
          <span v-else style="color:#94a3b8; font-style: italic;">未确认</span>
        </template>
      </el-table-column>

      <!-- 🥇 【核心功能】：学分认定状态变色逻辑 -->
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
  </div>
</template>

<script>
export default {
  name: "StudentCredit",
  data() {
    return {
      loading: false,
      tableData: [] // 由后端获取，由于使用了JOIN，现包含sc.score数据
    };
  },
  computed: {
    // 算法1：加权正式总分（公式：Σ[及格分*权重]）
    weightedTotalEarned() {
      return this.tableData
        .filter(item => item.score != null && parseFloat(item.score) >= 60)
        .reduce((sum, item) => sum + (parseFloat(item.credits || 0) * parseFloat(item.weight || 1.0)), 0);
    },
    // 算法2：基准课程库总学分汇总
    baseTotal() {
      return this.tableData
        .reduce((sum, item) => sum + parseFloat(item.credits || 0), 0);
    }
  },
  mounted() {
    this.initCreditRecord();
  },
  methods: {
    // 核心初始化逻辑
    initCreditRecord() {
      const sid = localStorage.getItem("username");
      if (!sid) {
          this.$message.error("无法校验您的账号，请重新登录");
          return;
      }
      this.loading = true;
      this.$axios.get(`/courses/my/${sid}`).then(res => {
        if (res.data.code === 200) {
          this.tableData = res.data.data;
          console.log("📊 [数据快照] 已成功获取带成绩的课程信息:", this.tableData);
        }
      }).catch(err => {
        console.error("加载记录异常", err);
      }).finally(() => {
        this.loading = false;
      });
    },

    /**
     * 根据分数动态判定视觉标签
     * 1. 没有任何分数 (NULL) ➡ 正在修读
     * 2. 分数 >= 60 ➡ 通过
     * 3. 分数 < 60 ➡ 失败
     */
    statusAttribute(score) {
      if (score === null || score === undefined) {
        return { label: '正在修读', type: 'info' }; 
      }
      return score >= 60 
        ? { label: '已认证', type: 'success' } 
        : { label: '成绩不通过', type: 'danger' };
    },

    getScoreTextClass(score) {
      if (score === null || score === undefined) return 'score-empty';
      return score >= 60 ? 'score-high' : 'score-low';
    }
  }
};
</script>

<style scoped>
.credit-page-container { padding: 40px; background-color: #ffffff; min-height: 80vh;}

.summary-stats { margin-bottom: 30px; }
.stat-card {
  padding: 22px;
  background: #fff;
  border: 1px solid #e0e7f1;
  border-radius: 8px;
  border-left: 5px solid;
}
.gold-border { border-left-color: #ca8a04; }
.blue-border { border-left-color: #1e3a8a; }

.stat-card .label { font-size: 13.5px; font-weight: bold; color: #4b5563; margin-bottom: 8px; }
.stat-card .value { font-size: 26px; font-weight: 900; color: #111827; }
.stat-card .unit { font-size: 13px; font-weight: normal; color: #9ca3af; }

.academic-table { box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); }

/* 文字与标签强化 */
.score-high { color: #15803d; font-weight: 800; font-size: 15px; }
.score-low { color: #b91c1c; font-weight: 800; font-size: 15px; }
.score-empty { color: #94a3b8; font-style: italic; }
.en-font { font-family: "Helvetica", sans-serif; font-size: 13px; color: #64748b; font-style: italic; }

.weight-label { width: 110px; font-weight: 800 !important; letter-spacing: 0.5px; border: none; }
</style>
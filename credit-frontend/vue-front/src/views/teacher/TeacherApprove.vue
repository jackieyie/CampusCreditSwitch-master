<template>
  <div class="approve-container">
    <el-card class="approve-card" shadow="never">
      <!-- 页头统计与刷新操作 -->
      <div slot="header" class="clearfix">
        <el-row :gutter="20">
          <el-col :span="18">
            <span class="title">📋 个人学分认定 - 老师审批中心</span>
          </el-col>
          <el-col :span="6" style="text-align: right;">
            <!-- Badge 角标实时显示未处理条数 -->
            <el-badge :value="pendingCount" class="item" :hidden="pendingCount === 0">
              <el-button 
                size="mini" 
                type="primary" 
                icon="el-icon-refresh" 
                @click="getApplyList"
                :disabled="loading"
              >
                刷新申请列表
              </el-button>
            </el-badge>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格：增加了 v-loading 实现转圈圈动画 -->
      <el-table 
        v-loading="loading"
        element-loading-text="正在同步教务处最新申请..."
        :data="applyList" 
        border 
        stripe 
        style="width: 100%;"
        :header-cell-style="{ background: '#f5f7fa', color: '#1e3a8a', fontWeight: 'bold' }"
      >
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="date" label="提交日期" width="160" align="center">
            <template slot-scope="scope">{{ formatDate(scope.row.date) }}</template>
        </el-table-column>
        <el-table-column prop="studentId" label="申请人学号" width="120" align="center"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100" align="center"></el-table-column>
        <el-table-column prop="major" label="意向转入专业" min-width="150" align="center"></el-table-column>
        <el-table-column prop="credits" label="当前学分" width="90" align="center"></el-table-column>
        
        <el-table-column label="审核进度" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="statusConfig(scope.row.processStatus).type" effect="dark" size="small">
                {{ statusConfig(scope.row.processStatus).label }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="教务操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button 
                v-if="scope.row.processStatus === 0" 
                type="primary" 
                size="mini" 
                icon="el-icon-edit"
                @click="openAudit(scope.row)">
                进入审核
            </el-button>
            <span v-else style="color: #909399; font-size: 12px; font-style: italic;">处理已存档</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审批操作弹窗 -->
    <el-dialog title="学分申请详细内容核对与审批" :visible.sync="auditVisible" width="480px" center>
      <div class="audit-info" v-if="currentRow">
          <p><strong>学生身份：</strong> {{currentRow.name}} (ID: {{currentRow.studentId}})</p>
          <p><strong>申请专业：</strong> {{currentRow.major}}</p>
          <el-divider></el-divider>
          <el-form label-position="top">
              <el-form-item label="设置审核结论">
                  <el-radio-group v-model="auditForm.processStatus">
                      <el-radio :label="1">审核通过（确认认定）</el-radio>
                      <el-radio :label="2">驳回申请（资料不符）</el-radio>
                  </el-radio-group>
              </el-form-item>
              <el-form-item label="批复语/原因描述">
                  <el-input 
                    type="textarea" 
                    :rows="4" 
                    v-model="auditForm.result" 
                    placeholder="请输入对该学生的审核批语或拒绝原因">
                  </el-input>
              </el-form-item>
          </el-form>
      </div>
      <span slot="footer">
        <el-button @click="auditVisible = false" size="small">关闭窗口</el-button>
        <el-button type="primary" @click="submitAudit" size="small" style="background-color:#1e3a8a !important; border:none;">正式发布审核结论</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "TeacherApprove",
  data() {
    return {
      applyList: [],      // 表格数据
      loading: false,     // 控制表格刷新时的动画状态
      auditVisible: false, 
      currentRow: null,    
      auditForm: {
          id: null,
          processStatus: 1,
          result: ""
      }
    };
  },
  computed: {
      // 实时计算有多少条申请等待处理
      pendingCount() {
          return this.applyList.filter(item => item.processStatus === 0).length;
      }
  },
  mounted() {
    this.getApplyList(); // 进入页面自动加载一次
  },
  methods: {
    // 💡 获取/刷新数据的方法
    getApplyList() {
      this.loading = true; // 开始转圈

      this.$axios.get("/appliers/students").then(res => {
        if (res.data.code === 200) {
          this.applyList = res.data.data;
          
          // 如果是手动点刷新的话，弹个提示告诉老师已经最新了
          this.$message({
            message: '学分申请列表同步成功',
            type: 'success',
            duration: 1200
          });
        }
      }).catch(() => {
        this.$message.error("无法同步数据，请检查网络或后端");
      }).finally(() => {
        // 加载动画展示够半秒钟，让用户有“体感”，不然跳太快没感觉
        setTimeout(() => {
            this.loading = false; 
        }, 500);
      });
    },

    openAudit(row) {
        this.currentRow = row;
        this.auditForm.id = row.id; // 取主键
        this.auditForm.processStatus = 1;
        this.auditForm.result = ""; // 清空上次记录
        this.auditVisible = true;
    },

    submitAudit() {
        if(!this.auditForm.result.trim()) {
            this.$message.warning("必须填写审批评价内容");
            return;
        }

        // 调用刚才在 ApplierController 中准备好的处理接口
        this.$axios.put("/appliers/handle", this.auditForm).then(res => {
            if(res.data.code === 200) {
                this.$notify({
                    title: '成功',
                    message: '学生 ' + this.currentRow.name + ' 的结果已下达',
                    type: 'success'
                });
                this.auditVisible = false;
                this.getApplyList(); // 重要：提交完成后，自动刷新一次列表同步数据库状态
            }
        });
    },

    statusConfig(status) {
        const map = {
            0: { label: '未处理', type: 'info' },
            1: { label: '通过', type: 'success' },
            2: { label: '驳回', type: 'danger' }
        };
        return map[status] || { label: '未知', type: 'info' };
    },
    
    formatDate(dateStr) {
        if(!dateStr) return '--';
        return new Date(dateStr).toLocaleDateString();
    }
  }
};
</script>

<style scoped>
/* 样式调整：严肃端庄的卡片和布局 */
.approve-container { padding: 30px; }
.approve-card { border-radius: 8px; box-shadow: none !important; }
.title { font-weight: 800; font-size: 19px; color: #1e3a8a; }
.audit-info p { margin-bottom: 15px; font-size: 14px; color: #303133; }
.el-button--primary { background-color: #1e3a8a !important; border:none; }
.el-badge { vertical-align: middle; }
</style>
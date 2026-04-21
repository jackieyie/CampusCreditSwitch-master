<template>
  <div class="notice-container">
    <el-card shadow="never" class="notice-card">
      <div slot="header" class="notice-header">
        <span><i class="el-icon-news"></i> 校园教务公告中心</span>
      </div>
      
      <el-collapse v-loading="loading" accordion>
        <el-collapse-item v-for="item in noticeList" :key="item.id" :name="item.id">
          <template slot-scope="" slot="title">
            <span class="notice-item-title">{{ item.title }}</span>
            <span class="notice-item-date">{{ item.createTime | dateFilter }}</span>
          </template>
          <div class="notice-item-content">{{ item.content }}</div>
        </el-collapse-item>
      </el-collapse>

      <div v-if="noticeList.length === 0 && !loading" class="empty-state">
        <el-empty description="当前暂无发布的教务公告"></el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "ViewNotices",
  data() {
    return {
      noticeList: [],
      loading: false
    };
  },
  mounted() {
    this.fetchNotices();
  },
  methods: {
    fetchNotices() {
      this.loading = true;
      this.$axios.get('/notice/list').then(res => {
        if (res.data.code === 200) {
          this.noticeList = res.data.data;
        }
      }).finally(() => { this.loading = false; });
    }
  }
};
</script>

<style scoped>
.notice-container { padding: 30px; display: flex; justify-content: center; }
.notice-card { width: 900px; border-radius: 12px; border: 1px solid #e2e8f0; }
.notice-header { font-size: 20px; font-weight: 800; color: #1e3a8a; }
.notice-item-title { font-weight: 700; color: #2c3e50; font-size: 16px; }
.notice-item-date { margin-left: auto; margin-right: 20px; color: #94a3b8; font-size: 13px; }
.notice-item-content { 
  white-space: pre-wrap; 
  line-height: 1.8; 
  color: #475569; 
  padding: 15px; 
  background: #f8fafc; 
  border-radius: 8px;
  font-size: 15px;
}
.empty-state { padding: 50px 0; }
</style>
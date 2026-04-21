<template>
  <div class="admin-notice-container">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span style="font-weight: bold; font-size: 18px;">📢 校园公告管理</span>
        <el-button 
          style="float: right; padding: 10px 15px" 
          type="primary" 
          icon="el-icon-plus" 
          @click="addDialogVisible = true"
        >发布新公告</el-button>
      </div>

      <!-- 公告列表表格 -->
      <el-table :data="noticeList" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="title" label="公告标题" min-width="200"></el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button type="danger" size="mini" icon="el-icon-delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布公告弹窗 -->
    <el-dialog title="发布新公告" :visible.sync="addDialogVisible" width="40%">
      <el-form :model="noticeForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" :rows="6" v-model="noticeForm.content" placeholder="请输入公告内容..."></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitNotice" :loading="submitLoading">确认发布</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "AdminNotices",
  data() {
    return {
      noticeList: [],
      loading: false,
      addDialogVisible: false,
      submitLoading: false,
      noticeForm: {
        title: '',
        content: ''
      }
    };
  },
  mounted() {
    this.fetchNotices();
  },
  methods: {
    // 1. 获取所有公告
    fetchNotices() {
      this.loading = true;
      this.$axios.get('/notice/list').then(res => {
        if (res.data.code === 200) {
          this.noticeList = res.data.data;
        }
      }).finally(() => { this.loading = false; });
    },
    // 2. 提交新公告
    submitNotice() {
      if (!this.noticeForm.title || !this.noticeForm.content) {
        return this.$message.warning("标题和内容不能为空");
      }
      this.submitLoading = true;
      this.$axios.post('/notice/add', this.noticeForm).then(res => {
        if (res.data.code === 200) {
          this.$message.success("公告发布成功");
          this.addDialogVisible = false;
          this.noticeForm = { title: '', content: '' };
          this.fetchNotices(); // 刷新列表
        }
      }).finally(() => { this.submitLoading = false; });
    },
    // 3. 删除公告
    handleDelete(id) {
      this.$confirm('此操作将永久删除该公告, 是否继续?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$axios.post(`/notice/delete/${id}`).then(res => {
          this.$message.success("删除成功");
          this.fetchNotices();
        });
      }).catch(() => {});
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleString();
    }
  }
};
</script>

<style scoped>
.admin-notice-container { padding: 20px; }
</style>
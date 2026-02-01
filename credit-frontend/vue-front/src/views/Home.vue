<template>
<div class="home">
  <div class="homeSub">
      <div class="floatL left">
         <a href="https://www.hist.edu.cn/" target="_blank">
          <div class="l-top">
            <img @mousemove= "toIndex" @mouseleave="toIndexCancel" src="@/assets/logo1.png" style="margin-top:0;height:80px;cursor:pointer;" alt="校徽">
          </div>
          </a>
          <div class="l-middle">
              <div style="font-size:34px; margin-top:15px;">校园学分认证</div>
              <div style="font-size:11px; margin-top:10px; ">Authentication of credits in college</div>
              
              <!-- 登录功能区 -->
              <div style="width:100%;height:150px;margin-left:20px;">
                  <!-- 学生登录 -->
                  <div class="fun-left" @click="toLogin">
                      <div style="margin:0;padding:0;">
                          <img src="/学生登录.png" style="width:90px;margin:0;padding:0;">
                      </div>
                      <div style="margin-top:10px;color:#000;font-size:17px;">
                          学生登录
                      </div>
                  </div>
                  <!-- 教师登录 -->
                  <div class="fun-right" @click="toLogin">
                      <div style="margin:0;padding:0;">
                          <img src="/老师登录.png" style="width:90px;margin:0;padding:0;">
                      </div>
                      <div style="margin-top:10px;color:#000;font-size:17px;">
                          教师登录
                      </div>
                  </div>
              </div>

              <div style="width:100%;height:150px; margin-left:20px;">
                  <!-- 管理登录 -->
                  <div class="fun-left" @click="toLogin">
                      <div style="margin:0;padding:0;">
                          <img  src="/管理登录.png" style="width:90px;margin:0;padding:0;">
                      </div>
                      <div  style="margin-top:10px;color:#000;font-size:17px;">
                          管理登录
                      </div>
                  </div>
                  <!-- 公告信息 (静态展示) -->
                  <div class="fun-right">
                      <div style="margin:0;padding:0;">
                          <el-image src="http://zsjyc.ncst.edu.cn/atm/1589187409104/20200511170109575.png" style="width:90px;margin:0;padding:0;"></el-image>
                      </div>
                      <div style="margin-top:10px;color:#000;font-size:17px;">
                          公告信息
                      </div>
                  </div>
              </div>
          </div>
      </div>

      <div class="floatL right">
         <div class="r-top" style="height:80%;">
             <el-carousel :interval="4000" height="400px"  trigger="click">
                <el-carousel-item v-for="(item,idx) in UrlList" :key="idx">
                    <el-image :src="item" style="padding-left:25px; margin-top:10px; padding-right:15px;height:500px;width:800px;"></el-image>
                </el-carousel-item>
            </el-carousel>
         </div>
         
         <!-- 底部链接导航 -->
         <div style="text-align:center;background:#f1f1f1; height:80px; width:700px;  margin-left:150px; margin-top:30px;">
            <div class="link-item"><a href="mailto:shuji@ncst.edu.cn" target="_blank">学校首页</a></div>
            <div class="link-item"><a href="mailto:xiaozhang@ncst.edu.cn" target="_blank">教学信息</a></div>
            <div class="link-item"><a href="/xwgk/" target="_blank">教学机构</a></div>
            <div class="link-item"><a href="http://jwc.ncst.edu.cn/" target="_blank">文献检索</a></div>
            <div class="link-item"><a href="http://kyglxt.ncst.edu.cn/" target="_blank">科研系统</a></div>
            <div class="link-item"><a href="http://elearning.ncst.edu.cn" target="_blank">OA系统</a></div>
            <div class="link-item"><a href="http://xxfw.ncst.edu.cn/" target="_blank">教务管理</a></div>
            <div class="link-item"><a href="http://fxcszx.ncst.edu.cn/" target="_blank">学生管理</a></div>
            <div class="link-item"><a href="http://xkjs.ncst.edu.cn" target="_blank">科研管理</a></div>
            <div class="link-item"><a href="http://hqggfw.ncst.edu.cn/" target="_blank">后勤服务</a></div>
            <div class="link-item"><a href="http://www.qgxy.cn/" target="_blank">VPN入口</a></div>
            <div class="link-item"><a href="http://jtxy.ncst.edu.cn/" target="_blank">教务系统</a></div>
            <div class="link-item"><a href="http://qaxy.ncst.edu.cn/" target="_blank">红色资源</a></div>
            <div class="link-item"><a href="http://www.myfy.com.cn/" target="_blank">学习系统</a></div>
            <div class="clearfloat"></div>
         </div>
      </div>

      <div class="clearfloat"></div>

      <!-- 底部认证简介卡片 -->
      <div class="foot">
          <div @click="dialogVisible = true" class="foot-row" style="background: url('http://zsjyc.ncst.edu.cn/atm/1589187409104/20200602112815977.png') no-repeat;" >
            <div class="foot-icon"><img src="http://zsjyc.ncst.edu.cn/atm/1589187409104/20200520085817254.png"></div>
            <div style="float:left; font-size:18px;">学分认证简介</div>
            <div style="clear:both"></div>
          </div>
          <div class="foot-row" style="background: url('http://zsjyc.ncst.edu.cn/atm/1589187409104/20200602112815977.png') no-repeat;">
            <div class="foot-icon"><img src="http://zsjyc.ncst.edu.cn/atm/1589187409104/20200520085817376.png"></div>
            <div style="float:left; font-size:18px;">认证类型</div>
            <div style="clear:both"></div>
          </div>
          <div class="foot-row"  style="background: url('http://zsjyc.ncst.edu.cn/atm/1589187409104/20200602112815977.png') no-repeat;">
            <div class="foot-icon"><img src="http://zsjyc.ncst.edu.cn/atm/1589187409104/2020052008581729.png"></div>
            <div style="float:left; font-size:18px;">认证指南</div>
            <div style="clear:both"></div>
          </div>
          <div class="foot-row" style="background: url('http://zsjyc.ncst.edu.cn/atm/1589187409104/20200602112815977.png') no-repeat;" >
            <div class="foot-icon"><img src="http://zsjyc.ncst.edu.cn/atm/1589187409104/20200520085817450.png"></div>
            <div style="float:left; font-size:18px;">联系我们</div>
            <div style="clear:both"></div>
          </div>
      </div>
  </div>

  <!-- 简介弹窗 -->
  <el-dialog title="认证简介" :visible.sync="dialogVisible" width="40%">
    <div style="text-align:left; line-height: 1.6;">
        <p>&nbsp;&nbsp;&nbsp;&nbsp;基于我国高考政策、高招录取规则等因素，大学生入学后产生对当前专业不满意或不适应等情况，进而产生了学生转专业的需要。本系统旨在充分发挥高校教学资源，通过数字化的转专业信息管理，为学生提供便捷的学分认证与申请服务。</p>
        <p>&nbsp;&nbsp;&nbsp;&nbsp;校园学分认证系统的设计与实现，注重学生兴趣和专长。校师生可以方便有效地进行申请、查询、录取等一系列业务，进一步推进学校的信息化发展。</p>
    </div>
    <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      UrlList: ["/xiaoxun1.png", "/keep01.png", "/keep02.png"],
      dialogVisible : false,
    };
  },
  methods: {
    toLogin() {
      // 使用相对路径跳转，修复硬编码端口导致的跳转错误
      this.$router.push("/login");
    },
    toIndex() {
      var img = document.querySelector(".l-top>img");
      if(img) img.style.height = "82px";
    },
    toIndexCancel() {
      var img = document.querySelector(".l-top>img");
      if(img) img.style.height = "80px";
    },
  },
};
</script>

<style scoped>
.home {
  display: flex;
  align-items: center;
  justify-content: space-around;
  text-align: center;
}
.homeSub {
  margin-top: 30px;
  margin-left: 20px;
  margin-right: 20px;
  padding: 15px;
  border: 5px solid white;
  border-radius: 4px;
}
a {
  text-decoration: none;
}
.link-item {
    float:left; 
    width:auto; 
    text-align:center;  
    line-height:30px; 
    padding:5px 20px;
}
.link-item a {
    color:#333333; 
    font-size:14px;
    font-family:'Microsoft YaHei' !important;
}
.l-top {
  height: 70px;
  margin-left: 0;
  padding-left: 0;
}
.floatL {
  display: inline;
  float: left;
}
.left {
  width: 50vh;
  height: 70vh;
}
.right {
  width: 140vh;
  height: 70vh;
}
.foot {
  width: 100%;
  height: 18vh;
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.foot-row {
  margin: 40px 20px 0 20px;
  width: 260px;
  height: 52px;
  line-height: 52px;
  cursor: pointer;
  text-align: center;
}
.foot-icon {
    float:left; 
    margin-left:40px; 
    margin-right:20px; 
    padding-top:8px; 
    padding-left:8px;
}
.clearfloat {
  clear: both;
  height: 0;
}
.fun-left, .fun-right {
  float: left;
  width: 150px;
  height: 90%;
  margin-top: 30px;
  cursor: pointer;
  transition: transform 0.2s;
}
.fun-left:hover, .fun-right:hover {
    transform: scale(1.05);
}
</style>
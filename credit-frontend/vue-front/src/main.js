import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 引入 element
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// 引入 axios
import axios from 'axios'

// --- 【全局过滤器：日期格式化】 ---
// 让公告和申请列表的时间显示更专业
Vue.filter('dateFilter', function(value) {
  if (!value) return '';
  const date = new Date(value);
  const y = date.getFullYear();
  const m = (date.getMonth() + 1).toString().padStart(2, '0');
  const d = date.getDate().toString().padStart(2, '0');
  return `${y}/${m}/${d}`; // 格式：2026/02/27
})

// 【修改 2】设置后端接口的基础地址
// 因为你已经解决了跨域，这里指向 8081 是 OK 的
axios.defaults.baseURL = 'http://localhost:8081'

// 【修改 3】添加请求拦截器
// 核心逻辑：公告发布(POST)时需要校验 Admin 的 Token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem("token") 
  if (token) {
    config.headers['Authorization'] = token
  }
  return config
}, error => {
  return Promise.reject(error)
})

Vue.use(ElementUI)
Vue.prototype.$axios = axios
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
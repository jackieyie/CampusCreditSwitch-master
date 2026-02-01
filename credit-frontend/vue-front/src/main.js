import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 引入 element
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// 引入 axios
import axios from 'axios'

// 【修改 1】注释掉 Mock，关闭学生端的模拟数据
// require('./mock')

// 【修改 2】设置后端接口的基础地址（确保两个前端都指向同一个后端 8081）
axios.defaults.baseURL = 'http://localhost:8081'

// 【修改 3】添加请求拦截器
// 这样学生登录后，查看自己的学分、申请课程时，后端才能识别是谁在操作
axios.interceptors.request.use(config => {
  // 注意：这里尝试从 store 或 localStorage 获取 token
  const token = localStorage.getItem("token") 
  if (token) {
    config.headers['Authorization'] = token
  }
  return config
}, error => {
  return Promise.reject(error)
})

Vue.use(ElementUI)
// 将 axios 挂载到 Vue 原型上
Vue.prototype.$axios = axios

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app') // vue-front 通常使用这种挂载方式
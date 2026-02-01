import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// 引入element
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// 引入axios
import axios from 'axios'

// 【修改 1】注释掉 Mock，不再使用模拟数据
// require('./mock')

// 【修改 2】设置后端接口的基础地址（指向你的 Spring Boot 端口）
axios.defaults.baseURL = 'http://localhost:8081'

// 【修改 3】添加请求拦截器（可选但强烈建议）
// 作用：登录成功后，每次发请求都会自动在 Header 里带上 Token，后端才能通过验证
axios.interceptors.request.use(config => {
  const token = localStorage.getItem("token") // 或者从 store 拿
  if (token) {
    config.headers['Authorization'] = token
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 使用element
Vue.use(ElementUI)
// 使用axios
Vue.prototype.$axios = axios

Vue.config.productionTip = false

new Vue({
  el:"#app",
  router,
  store,
  render: h => h(App)
})
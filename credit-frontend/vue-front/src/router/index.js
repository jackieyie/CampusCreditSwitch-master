import Vue from 'vue'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'

Vue.use(VueRouter)

/**
 * 💡 提示：
 * Admin/AdminInfo/AdminCourses 等文件位于 views/admin/ 下
 * CourseContainer 现在位于 views/ 目录下 (公用)
 */
const routes = [
  // --- 开放入口模块 ---
  {
    path: '/',
    name: 'Home',
    component: () => import("../views/Home.vue")
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import("../views/Login.vue")
  },

  // --- 1. 管理员系统模块 (权限码: admin) ---
  {
    path: '/admin',
    name: 'AdminContainer',
    component: () => import("../views/admin/Admin.vue"),
    redirect: '/admin/info',
    meta: { role: 'admin' },
    children: [
      {
        path: 'info',
        name: 'AdminInfo',
        component: () => import("../views/admin/AdminInfo.vue"),
        meta: { role: 'admin' }
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import("../views/admin/AdminCourses.vue"),
        meta: { role: 'admin' }
      },
      {
        path: 'users-mgr',
        name: 'AdminUserList',
        component: () => import("../views/admin/AdminUsers.vue"),
        meta: { role: 'admin' }
      },
      // ✅ 共享组件入口：管理员查看全库视角
      {
        path: 'container', 
        name: 'AdminGlobalContainer',
        component: () => import("../views/CourseContainer.vue"), 
        meta: { role: 'admin' }
      }
    ]
  },

  // --- 2. 教师系统模块 (权限码: teacher) ---
  {
    path: '/teacher',
    name: 'TeacherContainer',
    component: () => import("../views/teacher/Teacher.vue"),
    redirect: '/teacher/info',
    meta: { role: 'teacher' }, 
    children: [
      {
        path: 'info',
        name: 'TeacherInfo',
        component: () => import("../views/teacher/TeacherInfo.vue"),
        meta: { role: 'teacher' }
      },
      {
        path: 'apply',
        name: 'TeacherApprove',
        component: () => import("../views/teacher/TeacherApprove.vue"),
        meta: { role: 'teacher' }
      },
      {
        path: 'classes',
        name: 'TeacherClasses',
        component: () => import("../views/teacher/TeacherClasses.vue"),
        meta: { role: 'teacher' }
      },
      // ✅ 共享组件入口：教师查看授课比对视角
      {
        path: 'container',
        name: 'TeacherGlobalContainer',
        component: () => import("../views/CourseContainer.vue"),
        meta: { role: 'teacher' }
      }
    ]
  },

  // --- 3. 学生系统模块 (权限码: student) ---
  {
    path: '/student',
    name: 'Student',
    component: () => import("../views/student/Student.vue"),
    redirect: '/student/info',
    meta: { role: 'student' },
    children: [
      {
        path: 'info',
        name: 'StudentInfo',
        component: () => import("../views/student/StudentInfo.vue"),
        meta: { role: 'student' }
      },
      {
        path: 'course',
        name: 'StudentCourse',
        component: () => import("../views/student/StudentCourse.vue"),
        meta: { role: 'student' }
      },
      {
        path: 'credit',
        name: 'StudentCredit',
        component: () => import("../views/student/StudentCredit.vue"),
        meta: { role: 'student' }
      },
      {
        path: 'apply',
        name: 'StudentApply',
        component: () => import("../views/student/StudentApply.vue"),
        meta: { role: 'student' }
      },
      // ✅ 共享组件入口：学生进行选课修读视角
      // 注意此处路径现在统一指向了 ../views/CourseContainer.vue
      {
        path: 'container',
        name: 'CourseContainer',
        component: () => import("../views/CourseContainer.vue"), 
        meta: { role: 'student' }
      },
      {
        path: 'container/courses',
        name: 'Coursessss',
        component: () => import("../views/student/Coursessss.vue"),
        meta: { role: 'student' }
      }
    ]
  },
  
  // 补丁：无效路径一律遣返回首页
  {
    path: '*',
    redirect: '/'
  }
]

const router = new VueRouter({
  routes,
  mode: 'history',
  base: process.env.BASE_URL,
})

/**
 * 🔐 加固版权限守卫：解决空白页、角色漂移及空格/大小写匹配问题
 */
router.beforeEach((to, from, next) => {
  // 1. 获取登录状态，强制校验合法性
  const userRole = localStorage.getItem("userRole");

  // 2. 动页修改标题，提升体验
  if (to.path.includes('/admin')) document.title = '控制台-管理端';
  else if (to.path.includes('/teacher')) document.title = '工作台-教务端';
  else if (to.path.includes('/student')) document.title = '档案中心-学生端';
  else document.title = '学分认证管理系统';

  // 3. 拦截检查
  const roleRequired = to.matched.find(record => record.meta && record.meta.role)?.meta.role;

  if (roleRequired) {
    // 处理未登录情况
    if (!userRole || userRole === 'null') {
      ElementUI.Message.warning("权限不足：请登录授权账号后继续");
      return next('/login');
    }

    // 执行强类型一致化匹配 (防空格，防大写)
    const normalizedUser = userRole.toString().trim().toLowerCase();
    const normalizedTarget = roleRequired.toString().trim().toLowerCase();

    // 如果拥有管理权限(admin)可进入所有页面；如果是目标角色放行
    if (normalizedUser === 'admin' || normalizedUser === normalizedTarget) {
      next();
    } else {
      ElementUI.Message.error(`您的[${userRole}]身份无法访问该授权页面`);
      next('/'); 
    }
  } else {
    // 基础公共页面不设卡
    next();
  }
})

export default router
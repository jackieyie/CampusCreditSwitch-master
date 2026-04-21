import Vue from 'vue'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'

Vue.use(VueRouter)

/**
 * 💡 路由架构说明：
 * 1. AdminNotices.vue: 管理员发布和删除公告的后台页面
 * 2. ViewNotices.vue: 学生和教师查看公告列表的展示页面
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
      // ✅ 管理员专供：公告发布与管理
      {
        path: 'notices',
        name: 'AdminNotices',
        component: () => import("../views/admin/AdminNotices.vue"),
        meta: { role: 'admin' }
      },
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
      // ✅ 教师端：教务公告查阅
      {
        path: 'notices',
        name: 'TeacherNotices',
        component: () => import("../views/ViewNotices.vue"),
        meta: { role: 'teacher' }
      },
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
      // ✅ 学生端：教务公告查阅
      {
        path: 'notices',
        name: 'StudentNotices',
        component: () => import("../views/ViewNotices.vue"),
        meta: { role: 'student' }
      },
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
 * 🔐 加固版权限守卫
 */
router.beforeEach((to, from, next) => {
  const userRole = localStorage.getItem("userRole");

  if (to.path.includes('/admin')) document.title = '控制台-管理端';
  else if (to.path.includes('/teacher')) document.title = '工作台-教务端';
  else if (to.path.includes('/student')) document.title = '档案中心-学生端';
  else document.title = '学分认证管理系统';

  const roleRequired = to.matched.find(record => record.meta && record.meta.role)?.meta.role;

  if (roleRequired) {
    if (!userRole || userRole === 'null') {
      ElementUI.Message.warning("权限不足：请登录授权账号后继续");
      return next('/login');
    }

    const normalizedUser = userRole.toString().trim().toLowerCase();
    const normalizedTarget = roleRequired.toString().trim().toLowerCase();

    if (normalizedUser === 'admin' || normalizedUser === normalizedTarget) {
      next();
    } else {
      ElementUI.Message.error(`您的[${userRole}]身份无法访问该授权页面`);
      next('/'); 
    }
  } else {
    next();
  }
})

export default router
import router from '@/router'

// import Index from '@/views/Index.vue'
// import Home from '@/views/index/Home.vue'

// components
const NotFound = () => import('@/components/NotFound.vue')

// blossom
const Index = () => import('../views/Index.vue')
const Home = () => import('../views/index/Home.vue')
const Login = () => import('@/views/index/Login.vue')
const Articles = () => import('@/views/article/Articles.vue')
const TodoIndex = () => import('@/views/todo/TodoIndex.vue')
const PlanIndex = () => import('@/views/plan/PlanIndex.vue')
const NoteIndex = () => import('@/views/note/NoteIndex.vue')

router.addRoute({ path: '/404', component: NotFound })
router.addRoute({ path: '/:pathMatch(.*)', redirect: '/404' })
router.addRoute({ path: '/', redirect: '/home' })
router.addRoute({
  path: '/',
  name: 'Index',
  component: Index,
  meta: { keepAlive: true },
  children: [
    { path: '/home', name: 'Home', component: Home, meta: { keepAlive: true } },
    { path: '/login', name: 'Login', component: Login, meta: { keepAlive: true } },
    { path: '/articles', name: 'Articles', component: Articles, meta: { keepAlive: true } },
    { path: '/todo', name: 'TodoIndex', component: TodoIndex, meta: { keepAlive: true } },
    { path: '/plan', name: 'PlanIndex', component: PlanIndex, meta: { keepAlive: false } },
    { path: '/note', name: 'NoteIndex', component: NoteIndex, meta: { keepAlive: false } }
  ]
})

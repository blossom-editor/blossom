import router from '@/router'

// components
const NotFound = () => import("../components/NotFound.vue")

// blossom
const IndexBlossom = () => import('../views/Index.vue')
const BlossomHome = () => import('../views/index/Home.vue')
const Articles = () => import('../views/article/Articles.vue')


router.addRoute({ path: '/404', component: NotFound, })
router.addRoute({ path: '/:pathMatch(.*)', redirect: '/404', })
router.addRoute({
  path: '/', redirect: '/home'
})

// blossom
router.addRoute({
  path: '/', name: 'IndexBlossom', component: IndexBlossom, meta: { keepAlive: false },
  children: [
    { path: '/home', name: 'BlossomHome', component: BlossomHome, meta: { keepAlive: false } },
    { path: '/articles', name: 'Articles', component: Articles, meta: { keepAlive: false } }
  ]
})
import { createApp } from 'vue'
import pinia from './stores/storeConfig'
import App from './App.vue'
import router from './router'
import './router/routeInit'

import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/styles/css/theme.css'
import '@/assets/styles/css/main.css'
import '@/assets/styles/iconfont/blossom/iconfont.css'
import '@/assets/styles/iconfont/weblogo/iconfont.js'

const app = createApp(App);

app.use(pinia)
app.use(router)
app.mount('#app');

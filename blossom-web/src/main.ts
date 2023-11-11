import '@/assets/styles/css/main.css'
import '@/assets/styles/iconfont/blossom/iconfont.css'
import '@/assets/styles/iconfont/weblogo/iconfont.js'

import { createApp } from 'vue'
import App from './App.vue'

import pinia from '@/stores/store-config'
import router from '@/router/index'
import '@/router/route-init'

import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/styles/css/theme.css'

import BLRow from '@/components/BLRow.vue'
import BLCol from '@/components/BLCol.vue'
import BLTag from '@/components/BLTag.vue'

const app = createApp(App)
app.use(pinia)
app.use(router)
app.component('bl-row', BLRow).component('bl-col', BLCol).component('bl-tag', BLTag)
app.mount('#app')

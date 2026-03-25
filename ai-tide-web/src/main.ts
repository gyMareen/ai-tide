import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import ElementPlusLocaleZhCn from 'element-plus/es/locale/lang/zh-cn'
import router from './router'
import App from './App.vue'

// 样式文件
import '@/styles/variables.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: ElementPlusLocaleZhCn })

// 全局样式
app.use(() => {
  document.body.className = 'app'
})

app.mount('#app')

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import '@/assets/base.css';
import ElementPlus from 'element-plus'; 
import 'element-plus/dist/index.css';
import * as ElIcon from '@element-plus/icons-vue';
import store from './store/storage';
const app = createApp(App);

Object.keys(ElIcon).forEach((key) => {
  app.component(key, ElIcon[key]);
});

app.use(ElementPlus);
app.use(router);
app.mount('#app');
app.use(store);

const debounce = (fn, delay) => {
    let timer = null;
    return function () {
        let context = this;
        let args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function () {
            fn.apply(context, args);
        }, delay);
    };
};

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
    constructor(callback) {
        callback = debounce(callback, 16);
        super(callback);
    }
};
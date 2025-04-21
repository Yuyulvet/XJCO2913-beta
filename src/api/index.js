import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建 axios 实例
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 10000
});

// 响应拦截器
api.interceptors.response.use(
    response => response.data,
    error => {
        const message = error.response?.data?.message || '请求失败';
        ElMessage.error(message);
        return Promise.reject(error);
    }
);

export default api; 
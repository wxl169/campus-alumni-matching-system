import axios from "axios";

const isDev = process.env.NODE_ENV === 'development';

const myAxios = axios.create({
    baseURL : isDev ? 'http://localhost:8080/api/' : 'https://www.cqiewxl.cn:8888/api/',
})


// const myAxios = axios.create({
//     baseURL :  'http://localhost:8080/api/',
// })


myAxios.defaults.withCredentials = true;//设置为ture

// 添加请求拦截器
myAxios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
myAxios.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    if (response?.data?.code === 40010) {
      const redirectUrl = window.location.href;
      window.location.href = `/home?redirect=${redirectUrl}`;
  }

    return response.data;
  }, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  });

export default myAxios;
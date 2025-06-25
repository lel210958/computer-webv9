import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    console.log('API请求:', config.url, config.data)
    return config
  },
  error => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log('API响应:', response.config.url, response.data)
    return response.data
  },
  error => {
    console.error('API响应错误:', error)
    return Promise.reject(error)
  }
)

// API方法
export const networkAPI = {
  // 获取网络位置列表
  getNetworkLocations() {
    return api.get('/smc/api/network-locations')
  },

  // 获取分类统计信息
  getCategoryCount(networkLocation) {
    return api.post('/smc/api/network-location/category-count', {
      networkLocation
    })
  },

  // 获取文件夹列表
  getFolderList(networkLocation, currentPath) {
    return api.post('/smc/api/network-location/folder-list', {
      networkLocation,
      currentPath
    })
  },

  // 获取分类文件列表
  getCategoryFiles(networkLocation, type) {
    return api.post('/smc/api/network-location/category-files', {
      networkLocation,
      type
    })
  },

  // 获取文件播放URL
  playFile(networkLocation, filePath) {
    return api.post('/smc/api/network-location/play', {
      networkLocation,
      filePath
    })
  }
}

export default api 
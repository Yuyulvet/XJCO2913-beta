<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1>欢迎使用滑板车租赁系统</h1>
        <p>便捷、环保、经济的出行方式</p>
        <div class="banner-actions">
          <el-button type="primary" size="large" @click="$router.push('/booking')">
            立即租用
          </el-button>
          <el-button size="large" @click="scrollToMap">
            查看地图
          </el-button>
        </div>
      </div>
    </div>

    <!-- 功能区域 -->
    <div class="features-section">
      <h2>我们的服务</h2>
      <div class="features-grid">
        <el-card class="feature-card">
          <template #header>
            <div class="feature-header">
              <el-icon><LocationFilled /></el-icon>
              <span>便捷定位</span>
            </div>
          </template>
          <p>实时查看附近可用的滑板车</p>
        </el-card>

        <el-card class="feature-card">
          <template #header>
            <div class="feature-header">
              <el-icon><Clock /></el-icon>
              <span>灵活计费</span>
            </div>
          </template>
          <p>按分钟计费，使用多少付多少</p>
        </el-card>

        <el-card class="feature-card">
          <template #header>
            <div class="feature-header">
              <el-icon><Service /></el-icon>
              <span>全天候服务</span>
            </div>
          </template>
          <p>24小时随时可用，贴心客服支持</p>
        </el-card>

        <el-card class="feature-card">
          <template #header>
            <div class="feature-header">
              <el-icon><Discount /></el-icon>
              <span>优惠活动</span>
            </div>
          </template>
          <p>常用用户享受专属优惠</p>
        </el-card>
      </div>
    </div>

    <!-- 地图区域 -->
    <div id="map-section" class="map-section">
      <h2>附近的滑板车</h2>
      <div id="map-container" class="map-container">
        <el-card v-if="!mapLoaded" class="loading-card">
          <el-skeleton :rows="3" animated />
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { LocationFilled, Clock, Service, Discount } from '@element-plus/icons-vue'

const map = ref(null)
const currentPosition = ref(null)
const mapLoaded = ref(false)

const scrollToMap = () => {
  document.getElementById('map-section').scrollIntoView({ behavior: 'smooth' })
}

const initMap = async () => {
  if (!window.AMap) {
    console.warn('高德地图API未加载，跳过初始化')
    return
  }

  try {
    map.value = new AMap.Map('map-container', {
      zoom: 13,
      center: currentPosition.value || [116.397428, 39.90923]
    })
    
    // 添加定位控件
    map.value.addControl(new AMap.Geolocation({
      position: 'RB',
      showButton: true,
      buttonOffset: new AMap.Pixel(10, 20)
    }))

    // 添加比例尺控件
    map.value.addControl(new AMap.Scale())

    // 添加工具条控件
    map.value.addControl(new AMap.ToolBar())

    mapLoaded.value = true
  } catch (error) {
    console.warn('地图初始化失败，但不影响其他功能:', error)
  }
}

const getCurrentPosition = () => {
  if (!navigator.geolocation) {
    console.warn('浏览器不支持地理位置功能')
    return
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      currentPosition.value = [position.coords.longitude, position.coords.latitude]
      if (map.value) {
        map.value.setCenter(currentPosition.value)
      }
    },
    (error) => {
      console.warn('获取位置失败，使用默认位置:', error)
    }
  )
}

onMounted(() => {
  // 延迟初始化地图，确保DOM已经渲染
  setTimeout(() => {
    getCurrentPosition()
    initMap()
  }, 500)
})
</script>

<style scoped>
.home-container {
  width: 100%;
}

.welcome-banner {
  height: 500px;
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.banner-content {
  max-width: 800px;
  padding: 0 20px;
}

.banner-content h1 {
  font-size: 2.5em;
  margin-bottom: 20px;
}

.banner-content p {
  font-size: 1.2em;
  margin-bottom: 30px;
}

.banner-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.features-section {
  padding: 60px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.features-section h2 {
  text-align: center;
  margin-bottom: 40px;
  color: #2c3e50;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.2em;
}

.feature-header .el-icon {
  font-size: 1.5em;
  color: #409EFF;
}

.map-section {
  padding: 60px 20px;
  background-color: #f5f7fa;
}

.map-section h2 {
  text-align: center;
  margin-bottom: 40px;
  color: #2c3e50;
}

.map-container {
  height: 500px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

.loading-card {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
}
</style> 
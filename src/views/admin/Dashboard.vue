<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
            </div>
          </template>
          <div class="stats-value">{{ stats.totalUsers }}</div>
          <div class="stats-trend" :class="{ 'up': stats.userGrowth > 0 }">
            {{ stats.userGrowth }}% 较上月
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <template #header>
            <div class="card-header">
              <span>活跃订单</span>
            </div>
          </template>
          <div class="stats-value">{{ stats.activeBookings }}</div>
          <div class="stats-trend" :class="{ 'up': stats.bookingGrowth > 0 }">
            {{ stats.bookingGrowth }}% 较上周
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <template #header>
            <div class="card-header">
              <span>本月收入</span>
            </div>
          </template>
          <div class="stats-value">¥{{ stats.monthlyIncome }}</div>
          <div class="stats-trend" :class="{ 'up': stats.incomeGrowth > 0 }">
            {{ stats.incomeGrowth }}% 较上月
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <template #header>
            <div class="card-header">
              <span>待处理问题</span>
            </div>
          </template>
          <div class="stats-value">{{ stats.pendingIssues }}</div>
          <div class="stats-link">
            <el-button type="primary" link @click="handleViewIssues">
              查看详情
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收入趋势</span>
              <el-radio-group v-model="incomeChartPeriod" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
                <el-radio-button label="year">年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <canvas ref="incomeChart"></canvas>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>租赁时长分布</span>
            </div>
          </template>
          <div class="chart-container">
            <canvas ref="durationChart"></canvas>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-row class="activity-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button type="primary" link>查看全部</el-button>
            </div>
          </template>
          
          <el-table :data="recentActivities" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getActivityTagType(row.type)">
                  {{ getActivityTypeText(row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleViewActivity(row)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import Chart from 'chart.js/auto'
import api from '@/api'

const router = useRouter()

// 状态变量
const stats = ref({
  totalUsers: 0,
  userGrowth: 0,
  activeBookings: 0,
  bookingGrowth: 0,
  monthlyIncome: 0,
  incomeGrowth: 0,
  pendingIssues: 0
})

const incomeChartPeriod = ref('month')
const incomeChart = ref(null)
const durationChart = ref(null)
const recentActivities = ref([])

// 图表实例
let incomeChartInstance = null
let durationChartInstance = null

// 方法
const initIncomeChart = (data) => {
  if (!incomeChart.value) return

  const ctx = incomeChart.value.getContext('2d')
  if (incomeChartInstance) {
    incomeChartInstance.destroy()
  }

  incomeChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [{
        label: '收入',
        data: data.values,
        borderColor: '#409EFF',
        tension: 0.1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false
    }
  })
}

const initDurationChart = (data) => {
  if (!durationChart.value) return

  const ctx = durationChart.value.getContext('2d')
  if (durationChartInstance) {
    durationChartInstance.destroy()
  }

  durationChartInstance = new Chart(ctx, {
    type: 'pie',
    data: {
      labels: ['1小时内', '1-4小时', '4-8小时', '8-24小时', '1天以上'],
      datasets: [{
        data: data,
        backgroundColor: [
          '#409EFF',
          '#67C23A',
          '#E6A23C',
          '#F56C6C',
          '#909399'
        ]
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false
    }
  })
}

const getActivityTagType = (type) => {
  const types = {
    booking: 'success',
    issue: 'warning',
    user: 'info',
    system: ''
  }
  return types[type] || ''
}

const getActivityTypeText = (type) => {
  const texts = {
    booking: '预订',
    issue: '问题',
    user: '用户',
    system: '系统'
  }
  return texts[type] || type
}

const getStatusTagType = (status) => {
  const types = {
    '成功': 'success',
    '失败': 'danger',
    '处理中': 'warning',
    '待处理': 'info'
  }
  return types[status] || ''
}

const handleViewIssues = () => {
  router.push('/admin/issues')
}

const handleViewActivity = (activity) => {
  // 根据活动类型跳转到相应页面
  switch (activity.type) {
    case 'booking':
      router.push(`/admin/bookings/${activity.id}`)
      break
    case 'issue':
      router.push(`/admin/issues/${activity.id}`)
      break
    case 'user':
      router.push(`/admin/users/${activity.id}`)
      break
    default:
      console.warn('未知的活动类型:', activity.type)
  }
}

// 获取数据
const fetchStats = async () => {
  try {
    const response = await api.get('/admin/stats')
    stats.value = response.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchIncomeData = async (period) => {
  try {
    const response = await api.get(`/admin/income/${period}`)
    initIncomeChart(response.data)
  } catch (error) {
    console.error('获取收入数据失败:', error)
  }
}

const fetchDurationData = async () => {
  try {
    const response = await api.get('/admin/duration-distribution')
    initDurationChart(response.data)
  } catch (error) {
    console.error('获取时长分布数据失败:', error)
  }
}

const fetchRecentActivities = async () => {
  try {
    const response = await api.get('/admin/activities')
    recentActivities.value = response.data
  } catch (error) {
    console.error('获取最近活动失败:', error)
  }
}

// 监听图表周期变化
watch(incomeChartPeriod, (newPeriod) => {
  fetchIncomeData(newPeriod)
})

// 初始化
onMounted(() => {
  fetchStats()
  fetchIncomeData(incomeChartPeriod.value)
  fetchDurationData()
  fetchRecentActivities()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 100%;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 10px 0;
}

.stats-trend {
  font-size: 14px;
  color: #F56C6C;
}

.stats-trend.up {
  color: #67C23A;
}

.stats-link {
  margin-top: 10px;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-row {
  margin-bottom: 20px;
}
</style> 
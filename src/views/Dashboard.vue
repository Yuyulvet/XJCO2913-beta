<template>
  <div class="dashboard-container">
    <!-- 用户统计卡片 -->
    <el-row :gutter="20" class="dashboard-stats">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总骑行时长</span>
            </div>
          </template>
          <div class="card-value">{{ stats.totalDuration || '0' }}分钟</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本周骑行时长</span>
            </div>
          </template>
          <div class="card-value">{{ stats.weeklyDuration || '0' }}分钟</div>
          <div class="discount-info" v-if="stats.weeklyDuration >= 360">
            <el-tag type="success">已获得优惠资格</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总消费金额</span>
            </div>
          </template>
          <div class="card-value">¥{{ stats.totalSpending || '0' }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>活跃订单</span>
            </div>
          </template>
          <div class="card-value">{{ stats.activeBookings || '0' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能区域 -->
    <el-row :gutter="20" class="dashboard-features">
      <el-col :span="16">
        <!-- 快捷功能卡片 -->
        <el-card class="quick-actions" v-if="!currentBooking">
          <template #header>
            <div class="card-header">
              <span>快捷功能</span>
            </div>
          </template>
          <div class="quick-actions-grid">
            <el-card 
              shadow="hover" 
              class="action-card"
              @click="$router.push('/booking')"
            >
              <el-icon class="action-icon"><ShoppingBag /></el-icon>
              <div class="action-text">预订滑板车</div>
            </el-card>
            <el-card 
              shadow="hover" 
              class="action-card"
              @click="$router.push('/map')"
            >
              <el-icon class="action-icon"><Location /></el-icon>
              <div class="action-text">查看地图</div>
            </el-card>
            <el-card 
              shadow="hover" 
              class="action-card"
              @click="$router.push('/bookings')"
            >
              <el-icon class="action-icon"><List /></el-icon>
              <div class="action-text">预订历史</div>
            </el-card>
            <el-card 
              shadow="hover" 
              class="action-card"
              @click="createIssue"
            >
              <el-icon class="action-icon"><Warning /></el-icon>
              <div class="action-text">问题反馈</div>
            </el-card>
          </div>
        </el-card>

        <!-- 当前订单 -->
        <el-card v-if="currentBooking" class="booking-card">
          <template #header>
            <div class="card-header">
              <span>当前订单</span>
              <div class="header-actions">
                <el-button type="primary" size="small" @click="extendBooking">
                  延长租用
                </el-button>
                <el-button type="danger" size="small" @click="endBooking">
                  结束租用
                </el-button>
              </div>
            </div>
          </template>
          <div class="booking-info">
            <p>滑板车编号：{{ currentBooking.scooterId }}</p>
            <p>开始时间：{{ formatTime(currentBooking.startTime) }}</p>
            <p>已用时长：{{ calculateDuration(currentBooking.startTime) }}分钟</p>
            <p>预计费用：¥{{ currentBooking.estimatedCost }}</p>
          </div>
        </el-card>

        <!-- 使用记录图表 -->
        <el-card class="usage-chart">
          <template #header>
            <div class="card-header">
              <span>使用记录</span>
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <canvas ref="usageChart"></canvas>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <!-- 问题反馈 -->
        <el-card class="issues-card">
          <template #header>
            <div class="card-header">
              <span>我的反馈</span>
              <el-button type="primary" size="small" @click="createIssue">
                新建反馈
              </el-button>
            </div>
          </template>
          <el-table :data="issues" style="width: 100%" :max-height="400">
            <el-table-column prop="scooterId" label="滑板车" width="80" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getIssueStatusType(row.status)">
                  {{ getIssueStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="feedback" label="处理结果">
              <template #default="{ row }">
                {{ row.feedback || '暂无反馈' }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 延长租用对话框 -->
    <el-dialog
      v-model="extendDialogVisible"
      title="延长租用时间"
      width="30%"
    >
      <el-form :model="extendForm">
        <el-form-item label="延长时间">
          <el-input-number
            v-model="extendForm.minutes"
            :min="15"
            :step="15"
            step-strictly
          />
          分钟
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="extendDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmExtend">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新建反馈对话框 -->
    <el-dialog
      v-model="issueDialogVisible"
      title="新建反馈"
      width="40%"
    >
      <el-form :model="issueForm" :rules="issueRules" ref="issueFormRef">
        <el-form-item label="滑板车编号" prop="scooterId">
          <el-input v-model="issueForm.scooterId" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input
            type="textarea"
            v-model="issueForm.description"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="issueDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitIssue">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useBookingStore } from '@/stores/booking'
import api from '@/api'
import Chart from 'chart.js/auto'
import { formatTime } from '@/utils/date'
import { ShoppingBag, Location, List, Warning } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const bookingStore = useBookingStore()

const stats = ref({
  totalDuration: 0,
  weeklyDuration: 0,
  totalSpending: 0,
  activeBookings: 0
})

const chartPeriod = ref('week')
const usageChart = ref(null)
const chartInstance = ref(null)

const issues = ref([])
const currentBooking = ref(null)
const extendDialogVisible = ref(false)
const issueDialogVisible = ref(false)

const extendForm = ref({
  minutes: 15
})

const issueForm = ref({
  scooterId: '',
  description: ''
})

const issueRules = {
  scooterId: [
    { required: true, message: '请输入滑板车编号', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入问题描述', trigger: 'blur' }
  ]
}

const issueFormRef = ref(null)

const getIssueStatusType = (status) => {
  const types = {
    'pending': 'warning',
    'processing': 'primary',
    'resolved': 'success',
    'rejected': 'danger'
  }
  return types[status] || 'info'
}

const getIssueStatusText = (status) => {
  const texts = {
    'pending': '待处理',
    'processing': '处理中',
    'resolved': '已解决',
    'rejected': '已驳回'
  }
  return texts[status] || status
}

const calculateDuration = (startTime) => {
  const start = new Date(startTime)
  const now = new Date()
  return Math.floor((now - start) / 1000 / 60)
}

const createIssue = () => {
  issueDialogVisible.value = true
}

const extendBooking = () => {
  extendDialogVisible.value = true
}

const endBooking = async () => {
  try {
    await bookingStore.endBooking(currentBooking.value.id)
    ElMessage.success('租用已结束')
    currentBooking.value = null
  } catch (error) {
    ElMessage.error('结束租用失败：' + error.message)
  }
}

const confirmExtend = async () => {
  try {
    await bookingStore.extendBooking(currentBooking.value.id, extendForm.value.minutes)
    extendDialogVisible.value = false
    ElMessage.success('租用时间已延长')
  } catch (error) {
    ElMessage.error('延长租用失败：' + error.message)
  }
}

const initChart = () => {
  if (!usageChart.value) {
    console.warn('图表容器未准备好')
    return
  }

  try {
    const ctx = usageChart.value.getContext('2d')
    if (chartInstance.value) {
      chartInstance.value.destroy()
    }
    
    // 模拟数据
    const labels = chartPeriod.value === 'week' 
      ? ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      : Array.from({length: 30}, (_, i) => i + 1 + '日')
    
    chartInstance.value = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: '使用时长（分钟）',
          data: Array.from({length: labels.length}, () => Math.floor(Math.random() * 100)),
          borderColor: '#409EFF',
          tension: 0.1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    })
  } catch (error) {
    console.warn('图表初始化失败:', error)
  }
}

// 模拟获取数据的函数
const fetchDashboardData = () => {
  // 模拟统计数据
  stats.value = {
    totalDuration: Math.floor(Math.random() * 1000),
    weeklyDuration: Math.floor(Math.random() * 400),
    totalSpending: Math.floor(Math.random() * 1000),
    activeBookings: Math.floor(Math.random() * 5)
  }

  // 模拟当前订单
  currentBooking.value = null

  // 模拟反馈列表
  issues.value = []
}

watch(chartPeriod, () => {
  initChart()
})

onMounted(() => {
  fetchDashboardData()
  // 延迟初始化图表，确保DOM已经渲染
  setTimeout(() => {
    initChart()
  }, 500)
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-stats {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0;
}

.discount-info {
  margin-top: 10px;
}

.booking-card {
  margin-bottom: 20px;
}

.booking-info {
  line-height: 1.8;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.usage-chart {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.issues-card {
  height: 100%;
}

.quick-actions {
  margin-bottom: 20px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.action-card {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}

.action-icon {
  font-size: 24px;
  margin-bottom: 10px;
}

.action-text {
  text-align: center;
}
</style> 
<!-- 滑板车预定页面 -->
<template>
  <div class="booking-container">
    <div class="header">
      <h2>滑板车租赁</h2>
      
      <!-- 租赁模式选择 -->
      <div class="booking-mode">
        <el-radio-group v-model="bookingMode" @change="handleModeChange">
          <el-radio-button label="random">随机租赁</el-radio-button>
          <el-radio-button label="store">到店租赁</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 搜索和筛选 -->
      <div class="filters" v-if="bookingMode === 'store'">
        <el-input
          v-model="search"
          placeholder="搜索滑板车型号"
          prefix-icon="Search"
          clearable
          class="search-input"
        />
        <el-select v-model="sortBy" placeholder="排序方式" class="sort-select">
          <el-option label="价格从低到高" value="priceAsc" />
          <el-option label="价格从高到低" value="priceDesc" />
          <el-option label="续航里程" value="range" />
          <el-option label="电池容量" value="battery" />
        </el-select>
        <el-select v-model="rentalPeriod" placeholder="租赁周期" class="period-select">
          <el-option label="按小时" value="hour" />
          <el-option label="按天" value="day" />
          <el-option label="按月" value="month" />
        </el-select>
      </div>
    </div>

    <!-- 随机租赁模式 -->
    <div v-if="bookingMode === 'random'" class="random-booking">
      <el-card class="random-info">
        <template #header>
          <div class="card-header">
            <h3>随机租赁说明</h3>
          </div>
        </template>
        <div class="info-content">
          <h4>计费规则</h4>
          <p>按分钟计费：¥0.5/分钟</p>
          <h4>使用规则</h4>
          <ul>
            <li>仅可在指定区域内使用</li>
            <li>电量低于20%需要及时充电或更换</li>
            <li>仅可在指定还车点归还</li>
          </ul>
          <div class="map-container">
            <h4>可用区域地图</h4>
            <div id="areaMap" class="area-map"></div>
          </div>
          <el-button type="primary" @click="handleRandomBooking" :loading="loading">
            立即租用
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 到店租赁模式 -->
    <div v-else class="store-booking">
      <div class="scooter-grid">
        <el-card
          v-for="scooter in filteredScooters"
          :key="scooter.id"
          class="scooter-card"
        >
          <template #header>
            <div class="scooter-header">
              <h3>{{ scooter.model }}</h3>
              <el-tag :type="scooter.status === 'available' ? 'success' : 'danger'">
                {{ scooter.status === 'available' ? '可租用' : '已租出' }}
              </el-tag>
            </div>
          </template>
          
          <div class="scooter-info">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="续航里程">{{ scooter.range }}公里</el-descriptions-item>
              <el-descriptions-item label="最高时速">{{ scooter.maxSpeed }}km/h</el-descriptions-item>
              <el-descriptions-item label="电池容量">{{ scooter.batteryCapacity }}mAh</el-descriptions-item>
              <el-descriptions-item label="车辆重量">{{ scooter.weight }}kg</el-descriptions-item>
            </el-descriptions>
            
            <div class="price-info">
              <div class="price-item">
                <span>时租：</span>
                <span class="price">¥{{ scooter.hourlyRate }}/小时</span>
              </div>
              <div class="price-item">
                <span>日租：</span>
                <span class="price">¥{{ scooter.dailyRate }}/天</span>
              </div>
              <div class="price-item">
                <span>月租：</span>
                <span class="price">¥{{ scooter.monthlyRate }}/月</span>
              </div>
            </div>

            <el-button 
              type="primary" 
              @click="handleStoreBooking(scooter)"
              :disabled="scooter.status !== 'available'"
            >
              预订
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 到店租赁预订对话框 -->
    <el-dialog
      v-model="storeBookingDialog.visible"
      title="确认预订"
      width="40%"
    >
      <div v-if="storeBookingDialog.scooter" class="booking-form">
        <el-form :model="storeBookingForm" label-width="100px">
          <el-form-item label="租赁周期">
            <el-radio-group v-model="storeBookingForm.periodType">
              <el-radio label="hour">按小时</el-radio>
              <el-radio label="day">按天</el-radio>
              <el-radio label="month">按月</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item :label="periodLabel">
            <el-input-number 
              v-model="storeBookingForm.duration" 
              :min="1" 
              :max="periodMaxDuration"
            />
          </el-form-item>

          <el-form-item label="是否购买保险">
            <el-switch v-model="storeBookingForm.insurance" />
          </el-form-item>

          <el-form-item label="预计费用">
            <div class="estimated-cost">
              <p>租赁费用：¥{{ calculateRentalFee }}</p>
              <p v-if="storeBookingForm.insurance">保险费用：¥{{ calculateInsuranceFee }}</p>
              <p class="total">总计：¥{{ calculateTotal }}</p>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="storeBookingDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="confirmStoreBooking" :loading="loading">
            确认预订
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import api from '@/api';

// 状态变量
const bookingMode = ref('random');
const search = ref('');
const sortBy = ref('priceAsc');
const rentalPeriod = ref('hour');
const loading = ref(false);
const scooters = ref([]);

// 到店租赁对话框
const storeBookingDialog = ref({
  visible: false,
  scooter: null
});

// 到店租赁表单
const storeBookingForm = ref({
  periodType: 'hour',
  duration: 1,
  insurance: false
});

// 计算属性
const filteredScooters = computed(() => {
  let result = scooters.value;
  
  if (search.value) {
    result = result.filter(s => 
      s.model.toLowerCase().includes(search.value.toLowerCase())
    );
  }
  
  return result.sort((a, b) => {
    switch (sortBy.value) {
      case 'priceAsc':
        return a.hourlyRate - b.hourlyRate;
      case 'priceDesc':
        return b.hourlyRate - a.hourlyRate;
      case 'range':
        return b.range - a.range;
      case 'battery':
        return b.batteryCapacity - a.batteryCapacity;
      default:
        return 0;
    }
  });
});

const periodLabel = computed(() => {
  switch (storeBookingForm.value.periodType) {
    case 'hour': return '租用小时数';
    case 'day': return '租用天数';
    case 'month': return '租用月数';
    default: return '租用时长';
  }
});

const periodMaxDuration = computed(() => {
  switch (storeBookingForm.value.periodType) {
    case 'hour': return 24;
    case 'day': return 30;
    case 'month': return 12;
    default: return 1;
  }
});

const calculateRentalFee = computed(() => {
  if (!storeBookingDialog.value.scooter) return 0;
  const scooter = storeBookingDialog.value.scooter;
  const duration = storeBookingForm.value.duration;
  
  switch (storeBookingForm.value.periodType) {
    case 'hour':
      return (scooter.hourlyRate * duration).toFixed(2);
    case 'day':
      return (scooter.dailyRate * duration).toFixed(2);
    case 'month':
      return (scooter.monthlyRate * duration).toFixed(2);
    default:
      return 0;
  }
});

const calculateInsuranceFee = computed(() => {
  if (!storeBookingForm.value.insurance) return 0;
  // 保险费用计算逻辑
  const baseFee = 20; // 基础保险费
  const duration = storeBookingForm.value.duration;
  
  switch (storeBookingForm.value.periodType) {
    case 'hour':
      return (baseFee * (duration / 24)).toFixed(2);
    case 'day':
      return (baseFee * duration).toFixed(2);
    case 'month':
      return (baseFee * duration * 30).toFixed(2);
    default:
      return 0;
  }
});

const calculateTotal = computed(() => {
  return (Number(calculateRentalFee.value) + Number(calculateInsuranceFee.value)).toFixed(2);
});

// 方法
const handleModeChange = (mode) => {
  if (mode === 'store') {
    fetchScooters();
  }
};

const fetchScooters = async () => {
  try {
    loading.value = true;
    const response = await api.get('/scooters/store');
    scooters.value = response.data;
  } catch (error) {
    ElMessage.error('获取滑板车列表失败');
  } finally {
    loading.value = false;
  }
};

const handleRandomBooking = async () => {
  try {
    loading.value = true;
    await api.post('/bookings/random');
    ElMessage.success('租用成功，请在地图上查看最近的可用车辆');
  } catch (error) {
    ElMessage.error('租用失败');
  } finally {
    loading.value = false;
  }
};

const handleStoreBooking = (scooter) => {
  storeBookingDialog.value.scooter = scooter;
  storeBookingDialog.value.visible = true;
  storeBookingForm.value = {
    periodType: 'hour',
    duration: 1,
    insurance: false
  };
};

const confirmStoreBooking = async () => {
  try {
    loading.value = true;
    await api.post('/bookings/store', {
      scooterId: storeBookingDialog.value.scooter.id,
      periodType: storeBookingForm.value.periodType,
      duration: storeBookingForm.value.duration,
      insurance: storeBookingForm.value.insurance
    });
    
    ElMessage.success('预订成功');
    storeBookingDialog.value.visible = false;
    fetchScooters();
  } catch (error) {
    ElMessage.error('预订失败');
  } finally {
    loading.value = false;
  }
};

// 生命周期钩子
onMounted(() => {
  // 初始化地图（这里需要集成高德地图或其他地图服务）
  if (bookingMode.value === 'store') {
    fetchScooters();
  }
});
</script>

<style scoped>
.booking-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  margin-bottom: 30px;
}

.booking-mode {
  margin: 20px 0;
}

.filters {
  display: flex;
  gap: 20px;
  margin-top: 15px;
}

.search-input {
  width: 300px;
}

.sort-select,
.period-select {
  width: 150px;
}

.random-booking {
  max-width: 800px;
  margin: 0 auto;
}

.random-info {
  margin-bottom: 20px;
}

.info-content {
  padding: 20px;
}

.info-content h4 {
  margin-top: 15px;
  margin-bottom: 10px;
}

.area-map {
  height: 300px;
  margin: 20px 0;
  background-color: #f5f7fa;
}

.scooter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.scooter-card {
  height: 100%;
}

.scooter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scooter-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.price-info {
  margin: 15px 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin: 5px 0;
}

.price {
  color: #409EFF;
  font-weight: bold;
}

.estimated-cost {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.estimated-cost .total {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #dcdfe6;
  font-weight: bold;
  color: #409EFF;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 
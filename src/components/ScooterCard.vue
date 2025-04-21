<!-- 滑板车卡片组件 -->
<template>
  <el-card class="scooter-card" :body-style="{ padding: '0px' }">
    <div class="scooter-image">
      <img :src="scooter.imageUrl || '/default-scooter.png'" class="image" />
    </div>
    <div class="scooter-info">
      <h3>{{ scooter.name }}</h3>
      <div class="details">
        <p class="model">型号：{{ scooter.model }}</p>
        <p class="price">价格：¥{{ scooter.pricePerHour }}/小时</p>
        <p class="status" :class="{ 'available': scooter.available }">
          状态：{{ scooter.available ? '可用' : '已预订' }}
        </p>
        <p class="quantity">剩余数量：{{ scooter.availableQuantity }}</p>
      </div>
      <div class="actions">
        <el-button 
          type="primary" 
          :disabled="!scooter.available || scooter.availableQuantity <= 0"
          @click="$emit('book', scooter)"
        >
          立即预订
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
defineProps({
  scooter: {
    type: Object,
    required: true
  }
});
</script>

<style scoped>
.scooter-card {
  width: 300px;
  margin: 10px;
  transition: transform 0.3s;
}

.scooter-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.scooter-image {
  height: 200px;
  overflow: hidden;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.scooter-info {
  padding: 14px;
}

.details {
  margin: 10px 0;
}

.details p {
  margin: 5px 0;
  color: #666;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.status {
  color: #f56c6c;
}

.status.available {
  color: #67c23a;
}

.actions {
  margin-top: 10px;
  text-align: right;
}
</style> 
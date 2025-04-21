<!-- 用户资料页面 -->
<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 基本信息 -->
      <el-col :span="12">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <h3>基本信息</h3>
              <el-button type="primary" @click="handleEditProfile">
                编辑资料
              </el-button>
            </div>
          </template>
          
          <el-form 
            ref="profileForm"
            :model="profileData"
            :disabled="!isEditing"
            label-width="100px"
          >
            <el-form-item label="用户名">
              <el-input v-model="profileData.username" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileData.email" />
            </el-form-item>
            <el-form-item label="手机号码">
              <el-input v-model="profileData.phone" />
            </el-form-item>
            <el-form-item label="学生认证">
              <el-switch
                v-model="profileData.isStudent"
                :disabled="true"
              />
              <el-button 
                v-if="!profileData.isStudent"
                type="primary" 
                link
                @click="handleStudentVerification"
              >
                申请认证
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 支付卡信息 -->
      <el-col :span="12">
        <el-card class="payment-card">
          <template #header>
            <div class="card-header">
              <h3>支付方式</h3>
              <el-button type="primary" @click="handleAddCard">
                添加卡片
              </el-button>
            </div>
          </template>

          <div v-if="paymentCards.length === 0" class="empty-cards">
            <el-empty description="暂无支付卡" />
          </div>

          <el-collapse v-else v-model="activeCard">
            <el-collapse-item
              v-for="card in paymentCards"
              :key="card.id"
              :name="card.id"
            >
              <template #title>
                <div class="card-title">
                  <span>**** **** **** {{ card.lastFourDigits }}</span>
                  <el-tag 
                    size="small"
                    :type="card.isDefault ? 'success' : ''"
                  >
                    {{ card.isDefault ? '默认' : '' }}
                  </el-tag>
                </div>
              </template>
              
              <div class="card-details">
                <p>持卡人：{{ card.cardHolder }}</p>
                <p>过期时间：{{ card.expiryMonth }}/{{ card.expiryYear }}</p>
                <div class="card-actions">
                  <el-button 
                    v-if="!card.isDefault"
                    type="primary" 
                    link
                    @click="setDefaultCard(card.id)"
                  >
                    设为默认
                  </el-button>
                  <el-button 
                    type="danger" 
                    link
                    @click="removeCard(card.id)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加卡片对话框 -->
    <el-dialog
      v-model="cardDialog.visible"
      title="添加支付卡"
      width="500px"
    >
      <el-form
        ref="cardForm"
        :model="cardDialog.form"
        :rules="cardRules"
        label-width="100px"
      >
        <el-form-item label="卡号" prop="cardNumber">
          <el-input 
            v-model="cardDialog.form.cardNumber"
            placeholder="请输入16位卡号"
            maxlength="19"
            :formatter="formatCardNumber"
          />
        </el-form-item>
        
        <el-form-item label="持卡人" prop="cardHolder">
          <el-input 
            v-model="cardDialog.form.cardHolder"
            placeholder="请输入持卡人姓名"
          />
        </el-form-item>

        <el-form-item label="有效期" required>
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item prop="expiryMonth">
                <el-select 
                  v-model="cardDialog.form.expiryMonth"
                  placeholder="月"
                >
                  <el-option
                    v-for="month in 12"
                    :key="month"
                    :label="String(month).padStart(2, '0')"
                    :value="String(month).padStart(2, '0')"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="expiryYear">
                <el-select 
                  v-model="cardDialog.form.expiryYear"
                  placeholder="年"
                >
                  <el-option
                    v-for="year in validYears"
                    :key="year"
                    :label="year"
                    :value="year"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="安全码" prop="cvv">
          <el-input
            v-model="cardDialog.form.cvv"
            placeholder="CVV"
            maxlength="3"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="cardDialog.form.setDefault">
            设为默认支付方式
          </el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cardDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitCard">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 学生认证对话框 -->
    <el-dialog
      v-model="studentDialog.visible"
      title="学生认证"
      width="500px"
    >
      <el-form
        ref="studentForm"
        :model="studentDialog.form"
        :rules="studentRules"
        label-width="100px"
      >
        <el-form-item label="学校" prop="school">
          <el-input v-model="studentDialog.form.school" />
        </el-form-item>
        
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="studentDialog.form.studentId" />
        </el-form-item>

        <el-form-item label="学生证" prop="studentCard">
          <el-upload
            class="student-card-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="studentDialog.form.studentCard" :src="studentDialog.form.studentCard" class="student-card" />
            <el-icon v-else class="student-card-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="studentDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitStudentVerification">
            提交认证
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import api from '@/api'

// 状态变量
const isEditing = ref(false)
const activeCard = ref([])
const paymentCards = ref([])

// 表单数据
const profileData = ref({
  username: '',
  email: '',
  phone: '',
  isStudent: false
})

// 卡片对话框
const cardDialog = ref({
  visible: false,
  form: {
    cardNumber: '',
    cardHolder: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
    setDefault: false
  }
})

// 学生认证对话框
const studentDialog = ref({
  visible: false,
  form: {
    school: '',
    studentId: '',
    studentCard: ''
  }
})

// 计算有效年份
const validYears = computed(() => {
  const currentYear = new Date().getFullYear()
  return Array.from({ length: 10 }, (_, i) => String(currentYear + i))
})

// 表单验证规则
const cardRules = {
  cardNumber: [
    { required: true, message: '请输入卡号', trigger: 'blur' },
    { pattern: /^[0-9\s]{16,19}$/, message: '请输入正确的卡号', trigger: 'blur' }
  ],
  cardHolder: [
    { required: true, message: '请输入持卡人姓名', trigger: 'blur' }
  ],
  expiryMonth: [
    { required: true, message: '请选择月份', trigger: 'change' }
  ],
  expiryYear: [
    { required: true, message: '请选择年份', trigger: 'change' }
  ],
  cvv: [
    { required: true, message: '请输入安全码', trigger: 'blur' },
    { pattern: /^[0-9]{3}$/, message: '请输入3位数字安全码', trigger: 'blur' }
  ]
}

const studentRules = {
  school: [
    { required: true, message: '请输入学校名称', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  studentCard: [
    { required: true, message: '请上传学生证照片', trigger: 'change' }
  ]
}

// 方法
const formatCardNumber = (value) => {
  if (!value) return ''
  const v = value.replace(/\s/g, '').replace(/[^\d]/g, '')
  const parts = []
  for (let i = 0; i < v.length; i += 4) {
    parts.push(v.slice(i, i + 4))
  }
  return parts.join(' ')
}

const handleEditProfile = () => {
  isEditing.value = !isEditing.value
  if (!isEditing.value) {
    // 保存修改
    updateProfile()
  }
}

const updateProfile = async () => {
  try {
    await api.put('/users/profile', profileData.value)
    ElMessage.success('资料更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
    console.error(error)
  }
}

const handleAddCard = () => {
  cardDialog.value.form = {
    cardNumber: '',
    cardHolder: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
    setDefault: false
  }
  cardDialog.value.visible = true
}

const submitCard = async () => {
  try {
    const cardData = { ...cardDialog.value.form }
    cardData.cardNumber = cardData.cardNumber.replace(/\s/g, '')
    await api.post('/users/cards', cardData)
    ElMessage.success('卡片添加成功')
    cardDialog.value.visible = false
    fetchPaymentCards()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const setDefaultCard = async (cardId) => {
  try {
    await api.put(`/users/cards/${cardId}/default`)
    ElMessage.success('设置成功')
    fetchPaymentCards()
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

const removeCard = async (cardId) => {
  try {
    await ElMessageBox.confirm('确定要删除这张卡片吗？')
    await api.delete(`/users/cards/${cardId}`)
    ElMessage.success('删除成功')
    fetchPaymentCards()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStudentVerification = () => {
  studentDialog.value.visible = true
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  studentDialog.value.form.studentCard = response.url
}

const submitStudentVerification = async () => {
  try {
    await api.post('/users/student-verification', studentDialog.value.form)
    ElMessage.success('认证申请已提交，请等待审核')
    studentDialog.value.visible = false
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

// 获取数据
const fetchUserProfile = async () => {
  try {
    const response = await api.get('/users/profile')
    profileData.value = response.data
  } catch (error) {
    ElMessage.error('获取用户资料失败')
  }
}

const fetchPaymentCards = async () => {
  try {
    const response = await api.get('/users/cards')
    paymentCards.value = response.data
  } catch (error) {
    ElMessage.error('获取支付卡信息失败')
  }
}

// 初始化
onMounted(() => {
  fetchUserProfile()
  fetchPaymentCards()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-card,
.payment-card {
  margin-bottom: 20px;
}

.empty-cards {
  padding: 40px 0;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-details {
  padding: 10px;
}

.card-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.student-card-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}

.student-card-uploader:hover {
  border-color: #409EFF;
}

.student-card-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.student-card {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 
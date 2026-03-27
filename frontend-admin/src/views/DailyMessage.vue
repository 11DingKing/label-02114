<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">
        <span style="font-size: 18px">💌</span>
        每日情话列表
      </span>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>新增情话
      </el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="content" label="情话内容" show-overflow-tooltip />
      <el-table-column prop="author" label="作者/来源" width="120">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.author || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="showDate" label="展示日期" width="130">
        <template #default="{ row }">
          <el-tag size="small" type="success">{{ row.showDate }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm 
            title="确定删除这条情话吗？" 
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleDelete(row.id)"
          >
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑情话' : '新增情话'" width="520px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="情话内容" prop="content">
          <el-input 
            v-model="form.content" 
            placeholder="请输入情话内容" 
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="作者/来源" prop="author">
          <el-input 
            v-model="form.author" 
            placeholder="请输入作者或来源（可选）" 
          />
        </el-form-item>
        <el-form-item label="展示日期" prop="showDate">
          <el-date-picker
            v-model="form.showDate"
            type="date"
            placeholder="选择展示日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDailyMessageList, saveDailyMessage, deleteDailyMessage } from '../api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  content: '',
  author: '',
  showDate: ''
})

const rules = {
  content: [{ required: true, message: '请输入情话内容', trigger: 'blur' }],
  showDate: [{ required: true, message: '请选择展示日期', trigger: 'blur' }]
}

const fetchList = async () => {
  loading.value = true
  try {
    list.value = await getDailyMessageList()
  } finally {
    loading.value = false
  }
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
    // 转换日期格式
    if (row.showDate) {
      form.showDate = row.showDate
    }
  } else {
    Object.assign(form, { id: null, content: '', author: '', showDate: '' })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveDailyMessage(form)
    ElMessage.success(form.id ? '更新成功' : '添加成功')
    dialogVisible.value = false
    fetchList()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id) => {
  await deleteDailyMessage(id)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped lang="scss">
// 表格操作列一行显示
:deep(.el-table) {
  .cell {
    white-space: nowrap;
  }
}
</style>

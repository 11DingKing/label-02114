<template>
  <div class="daily-message">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>每日情话管理</span>
          <el-button type="primary" @click="handleAdd">新增情话</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="情话内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="author" label="作者/来源" width="150" />
        <el-table-column prop="showDate" label="展示日期" width="150" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="情话内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入情话内容" />
        </el-form-item>
        <el-form-item label="作者/来源" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者或来源" />
        </el-form-item>
        <el-form-item label="展示日期" prop="showDate">
          <el-date-picker
            v-model="form.showDate"
            type="date"
            placeholder="选择展示日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDailyMessageList, getDailyMessageById, saveDailyMessage, deleteDailyMessage } from '@/api'

export default {
  name: 'DailyMessage',
  setup() {
    const loading = ref(false)
    const submitLoading = ref(false)
    const tableData = ref([])
    const total = ref(0)
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const formRef = ref(null)

    const queryParams = reactive({
      pageNum: 1,
      pageSize: 10
    })

    const form = reactive({
      id: null,
      content: '',
      author: '',
      showDate: ''
    })

    const rules = {
      content: [{ required: true, message: '请输入情话内容', trigger: 'blur' }],
      showDate: [{ required: true, message: '请选择展示日期', trigger: 'change' }]
    }

    const fetchData = async () => {
      loading.value = true
      try {
        const res = await getDailyMessageList(queryParams)
        if (res.code === 200) {
          tableData.value = res.data.records || res.data.list || res.data
          total.value = res.data.total || 0
        }
      } catch (error) {
        ElMessage.error('获取数据失败')
      } finally {
        loading.value = false
      }
    }

    const handleAdd = () => {
      dialogTitle.value = '新增情话'
      dialogVisible.value = true
    }

    const handleEdit = async (row) => {
      dialogTitle.value = '编辑情话'
      try {
        const res = await getDailyMessageById(row.id)
        if (res.code === 200) {
          Object.assign(form, res.data)
        }
      } catch (error) {
        ElMessage.error('获取详情失败')
      }
      dialogVisible.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm('确定要删除这条情话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteDailyMessage(row.id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            fetchData()
          }
        } catch (error) {
          ElMessage.error('删除失败')
        }
      }).catch(() => {})
    }

    const handleSubmit = async () => {
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (valid) {
          submitLoading.value = true
          try {
            const res = await saveDailyMessage(form)
            if (res.code === 200) {
              ElMessage.success('保存成功')
              dialogVisible.value = false
              fetchData()
            }
          } catch (error) {
            ElMessage.error('保存失败')
          } finally {
            submitLoading.value = false
          }
        }
      })
    }

    const resetForm = () => {
      form.id = null
      form.content = ''
      form.author = ''
      form.showDate = ''
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    onMounted(() => {
      fetchData()
    })

    return {
      loading,
      submitLoading,
      tableData,
      total,
      dialogVisible,
      dialogTitle,
      formRef,
      queryParams,
      form,
      rules,
      fetchData,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit,
      resetForm
    }
  }
}
</script>

<style scoped>
.daily-message {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

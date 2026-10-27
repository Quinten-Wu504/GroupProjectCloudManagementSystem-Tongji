<template>
  <el-container class="container" direction="vertical">
    <p style="text-align: left; margin-top: 0; font-size: 0.8em">小组管理 / 资料库</p >
    <el-divider style="margin: 0;" />

    <div style="margin: 10px 40px 0px auto">
      <button @click="addDV = true;" class="btn3" style="padding: 8px 15px; display: flex; align-items: center;">
        <el-icon style="margin-right: 5px">
          <FolderAdd />
        </el-icon> 上传文件
      </button>
    </div>

    <div style="margin-top: 20px; padding: 0 20px;">
      <el-scrollbar max-height="80vh">
        <el-collapse>
          <el-collapse-item v-for="(item, index) in TaskList" :key="index" :title="'&emsp;' + item.title"
            style="">
            <div>
              <el-table fit
                :data="item.files"
                :header-cell-style="{ 'text-align': 'center' }"
                :cell-style="{ 'text-align': 'center' }"
              >
                <el-table-column width="auto" min-width="4%">
                  <template #default="{ row }">
                    <img :src="fileTypeIcons[row.fileType || 0] " alt="文件类型" style="width: 16px; height: 16px;" />
                  </template>
                </el-table-column>
                <el-table-column prop="fileName" label="文件名" sortable show-overflow-tooltip width="auto" min-width="20%"/>
                <el-table-column prop="addUser" label="上传用户" sortable show-overflow-tooltip width="auto" min-width="20%"/>
                <el-table-column prop="addTime" label="上传时间" sortable show-overflow-tooltip width="auto" min-width="20%"/>

                <!-- 操作列 -->
                <el-table-column fixed="right" label="操作" width="auto" min-width="34%">
                  <template #default="{ row }">
                    <el-button class="btn3" size="mini" @click="downloadFile(row)">
                      <el-icon style="margin-right: 5px;"><Download /></el-icon>
                      下载
                    </el-button>

                    <el-button class="btn3" size="mini" @click="goToDetails(row)">
                      <el-icon style="margin-right: 5px;"><ChatDotSquare /></el-icon>
                      详情
                    </el-button>

                    <el-popconfirm
                      title="文件删除后将无法找回，且对应的留言也将一并删除，您确定要删除该文件吗？"
                      confirm-button-text="删除"
                      cancel-button-text="取消"
                      confirm-button-type="danger"
                      width="200px"
                      hide-icon
                      @confirm="deleteFile(row)"
                    >
                      <template #reference>
                        <el-button class="btn3" size="mini">
                          <el-icon style="margin-right: 5px;"><Delete /></el-icon>
                          删除
                        </el-button>
                      </template>
                    </el-popconfirm>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-scrollbar>
    </div>

    <el-dialog title="上传文件" v-model="addDV"  draggable align-center>
      <el-form :model="addForm" :rules="addRule" ref="addFormRef" 
        style="padding: 20px; padding-bottom: 0px;" :label-position="Right">
        
        <!-- 目前设置一次只能上传一个文件，随便写的提交地址，后续要替换, -->
        <el-form-item label="文件选择：" prop="file">
          <el-upload ref="upload" drag :limit="1" :file-list="fileList"
            :on-change="handleFileChange"
            :on-remove="handleRemoveFile"
            :auto-upload="false"
            :before-upload="beforeUpload"
            style="width: 100%;"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖拽至此，或<em>点击此处上传</em>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item required label="留言标题：" prop="title">
          <el-input v-model="addForm.title" disabled />
        </el-form-item>

        <el-form-item required label="留言备注：">
          <el-input v-model="addForm.content" placeholder="请输入备注" />
        </el-form-item>

        <!-- 任务选择：父子任务 -->
        <el-form-item required label="所属任务：" prop="task">
          <el-cascader v-model="addForm.task" :options="taskOptions" :props="cascaderProps" placeholder="请选择任务"/>
        </el-form-item>
      </el-form>

      <template #footer>
          <el-button @click="addDV = false" style="padding: 1px 20px;">取消</el-button>
          <el-button class="btn4" @click="addFile"> 上传 </el-button>
      </template>
    </el-dialog>

  </el-container>
</template>

<script setup>
import { onMounted, ref,computed } from "vue";
import { ElMessage } from 'element-plus';
import { useRouter } from "vue-router";
import { FolderAdd, Download, Delete, UploadFilled, Right } from '@element-plus/icons-vue';
import { getCurrentTaskList, deleteResource, getMessageInfo, getResourceInfoByTaskId, downloadResourceNew, uploadResource } from '@/api/api'; 
import { useStore } from 'vuex';
//文件图标与映射
import ELSEIcon from '@/assets/file/ELSE.png';
import PDFIcon from '@/assets/file/PDF.png';
import EXCELIcon from '@/assets/file/EXCEL.png';
import JPGIcon from '@/assets/file/JPG.png';
import PNGIcon from '@/assets/file/PNG.png';
import PPTIcon from '@/assets/file/PPT.png';
import WORDIcon from '@/assets/file/WORD.png';
import TXTIcon from '@/assets/file/TXT.png';

const fileTypeIcons = {
  0: ELSEIcon, 
  1: PDFIcon, 
  2: EXCELIcon,
  3: JPGIcon, 
  4: PNGIcon, 
  5: PPTIcon,
  6: WORDIcon, 
  7: TXTIcon
};
const store = useStore();
const router = useRouter();

const groupId = computed(() => store.state.groupId || '默认组ID');
const postAccount = computed(() => store.state.userId || "默认用户名");

//上传文件弹窗
const addDV = ref(false); 
const addFormRef = ref(null);
const fileList = ref([]);

const addForm = ref({
  title: "",
  content: "无备注",
  task: [],
});

//获取全部任务，已完成测试
const taskOptions = ref([]);

const fetchTaskOptions = async () => {
  try {
      const param1 = { groupId: groupId.value };
      const response = await getCurrentTaskList(param1);
      //console.log(response);
      if (response.data.success) {
          taskOptions.value = response.data.data; 
      } else {
          console.error(response.message);
      }
  } catch (error) {
      console.error('获取当前任务列表失败，请稍后重试');
  }
};

const cascaderProps = {
  label: 'taskName',
  value: 'taskId',
  children: 'childrenTask',
  checkStrictly: true
};

const TaskList = ref([]);

//获取信息
const fetchResourceItems = async () => {
  try {
    TaskList.value = [];
    for (const parentTask of taskOptions.value) {
      const paramResourceParent = { 
        taskId: parentTask.taskId
      };

      const responseParent = await getResourceInfoByTaskId(paramResourceParent);
      if (responseParent.data.success) {
        const resourceItems = responseParent.data.data.map(item => ({
          fileName: item.originName,
          addUser: item.uploader,
          addTime: item.uploadTime,
          fileType: item.fileType,
          resourceId: item.resourceId 
        }));
        TaskList.value.push({
          title: `${parentTask.taskName}`, 
          files: resourceItems
        });
      } else {
        console.error(responseParent.message);
      }

      for (const childTask of parentTask.childrenTask || []) {
        const paramResourceChildren = { taskId: childTask.taskId };

        const response = await getResourceInfoByTaskId(paramResourceChildren);
        if (response.data.success) {
          const resourceItems = response.data.data.map(item => ({
            fileName: item.originName,
            addUser: item.uploader,
            addTime: item.uploadTime,
            fileType: item.fileType,
            resourceId: item.resourceId 
          }));
          TaskList.value.push({
            title: `${parentTask.taskName}：${childTask.taskName}`, 
            files: resourceItems
          });
        } else {
          console.error(response.message);
        }
      }
    }
  } catch (error) {
      console.error('获取资源列表失败，请稍后重试');
  }
};

onMounted(async () => {
    await fetchTaskOptions(); 
    await fetchResourceItems(); 
});

const addRule = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '内容不能为空', trigger: 'blur' }],
  task: [{ required: true, message: '请选择任务', trigger: 'blur' }]
};

const handleFileChange = (file, fileList) => {
  if (fileList.length > 0) {
    addForm.value.title = `已上传：${file.name}`;
    addForm.value.file = file.raw;
  }
};

const handleRemoveFile = () => {
  addForm.value.title = "";
  addForm.value.file = null; 
};

// 上传文件前检查文件大小不能超过500MB
const beforeUpload = (file) => {
  const maxSize = 500 * 1024 * 1024; // 500MB 换算为字节
    if (file.size > maxSize) {
        ElMessage.error('上传文件大小超过限制，请选择不超过500MB的文件');
        return false;
    }
    return true;
};

const addFile = async () => {
    try {
        const valid = await addFormRef.value.validate();
        if (valid) {
            // 所选任务，获取最后一级任务的taskId
            const selectedTaskId = Array.isArray(addForm.value.task)
               ? addForm.value.task[addForm.value.task.length - 1]
                : null;
            
            const file = addForm.value.file;

            if (!file) {
              ElMessage.error('请上传文件后再提交');
              return;
            }

            const content = addForm.value.content;
            const taskId = selectedTaskId;
            const account = postAccount.value;
            const formData = new FormData();
            formData.append('groupId', groupId.value);
            formData.append('file', file);
            formData.append('taskId', taskId);
            formData.append('account', account);
            formData.append('content', content);
            formData.append('description', content);

            await uploadResource(formData);
            
            await fetchResourceItems();
            addDV.value = false;
            Object.assign(addForm.value, { title: "", content: "", task: [] });
            ElMessage.success('文件上传成功');
        }
    } catch (error) {
        console.error('表单验证失败或上传文件失败，请稍后重试');
        ElMessage.error('文件上传出现问题，请检查信息后重试');
    }
};


// 下载文件交互待完善
const downloadFile = async (file) => {
  try {
    const resourceId = file.resourceId;
    const paramDownloadResourceNew = {
      resourceId: resourceId
    };
    const response = await downloadResourceNew(paramDownloadResourceNew);

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', file.fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    ElMessage.success("文件正在下载...");
  } catch (error) {
    console.error('下载文件失败:', error);
    ElMessage.success("文件下载成功");
  }
};

// 跳转到对应留言板交互
const goToDetails = async (file) => {
  const param1 = {
      choice: 2,
      id: file.resourceId,
    };
  const response = await getMessageInfo(param1);
  if (response.data.success) {
    //console.log(response.data.data)

    const dataList = response.data.data;
    if (Array.isArray(dataList) && dataList.length > 0) {
      const messageId = dataList[0].messageId; 
      router.push(`/DashBoard/MessageDetail/${messageId}`);
    } else {
      const messageId = dataList.messageId;
      router.push(`/DashBoard/MessageDetail/${messageId}`);
    }
    
  }
};

const deleteFile = async (file) => {
  try {
    await deleteResource({ resourceId: file.resourceId }); // 使用文件对象中的resourceId来删除对应的资源

    await fetchResourceItems();
    ElMessage.success("文件删除成功");
  } catch (error) {
    console.error('删除文件失败:', error);
    ElMessage.error("文件删除失败，请稍后重试");
  }
};

</script>

<style scoped>
.container {
  background-color: rgba(248, 251, 246, 1);
  padding: 20px;
  height: 100vh;
  width: 100%;
}

.btn3 {
  background-color: #fff !important;
  border-color: #7f9b7e !important;
  border: 1px solid;
  color: #576251 !important;
  border-radius: 5px;
}

.btn3:hover {
  font-weight:bold !important;
  color: #fff !important;
  background: #7f9b7e !important;
}


::v-deep .el-collapse-item__wrap{
  background: rgba(248, 251, 246, 1);
}

::v-deep .el-collapse-item__header{
  background-color: rgba(248, 251, 246, 1) !important;
  font-size: 14px; 
  font-weight: bold;
  color: #576251;
}

::v-deep .el-collapse-item__content{
  padding: 0px 30px;
}

::v-deep .el-table th {
  background-color: rgba(248, 251, 246, 1) !important; 
}

::v-deep .el-table td {
  background-color: rgba(248, 251, 246, 1) !important;
}
</style>
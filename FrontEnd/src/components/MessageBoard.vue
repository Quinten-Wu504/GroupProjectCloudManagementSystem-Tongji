<template>
  <el-container class="container" direction="vertical">
    <p style="text-align: left; margin-top: 0; font-size: 0.8em">小组页面 / 留言板</p>
    <el-divider style="margin: 0px" />

    <div style="margin: 25px 0px 25px 0px;">
      <el-input style="max-width: 500px" v-model="searchQuery" clearable>
        <template #prepend>
          <el-select v-model="searchBy" placeholder="Select" style="width: 110px">
            <el-option label="发布者" value="nickname" />
            <el-option label="标题" value="title" />
          </el-select>
        </template>
      </el-input>

      <el-button @click="addDV = true;" class="btn3" style="margin-left: 50px;">
        <el-icon style="margin-right: 5px;"><CirclePlus /></el-icon>
        新增留言
      </el-button>

      <el-button @click="highLightFilter" class="btn3" style="margin-left: 20px;">
        <el-icon style="margin-right: 5px;"><Filter /></el-icon>
        筛选精华消息
      </el-button>
    </div>

    <el-row style="margin: 0px 10px 0px 10px;" :gutter="20">
      <el-col :span="17">
        <el-scrollbar max-height="80vh">
          <div v-if="!filteredMessages.length">
            <p style="margin-top:30px; font-size: 18px; font-weight: bold; text-align: center; color: rgba(116, 142, 97, 1);">
              搜索无结果
            </p>
          </div>

          <div v-else>
            <div v-for="(iteminfo, index) in filteredMessages" :key="index"
              style="cursor: pointer;" @click="goToDetails(index)">
              <el-row>
                <el-col :span="3">
                  <el-avatar :size="50" :src="'data:image/png;base64,'+iteminfo.headPortrait || 'https://via.placeholder.com/50'" />
                </el-col>
                <el-col :span="20" style="text-align: left;">
                  <p style="font-size:14px; color:darkgray; margin: 5px 0px 10px 0px;">
                    {{ iteminfo.nickname }}
                  </p>

                  <p style="font-size:16px; font-weight:bold; margin: 0px;">
                    {{ iteminfo.title }}
                  </p>

                  <p style="font-size:14px; color:#606266; margin: 10px 0px 10px 0px;">
                    {{ iteminfo.content }}
                  </p>

                  <div style="font-size:14px; color:#606266; margin: 10px 0px 10px 0px; display: flex; align-items: center;">     
                    <span>附件：</span>
                    <img :src="fileTypeIcons[iteminfo.fileType] " alt=" " 
                        style="width: 16px; height: 16px;  margin-right: 5px;" />

                    <span>{{ iteminfo.originName || "无"}}</span>
                  </div>

                  <div style="margin: 0px; font-size: 12px; display: flex; align-items: center; justify-content: space-between;">
                    <div style="display: flex; align-items: center;">
                        <el-tag type="success" effect="plain" round style="margin-right: 10px;">
                            {{ iteminfo.taskName }}
                        </el-tag>

                        <el-tag type="warning" effect="plain" round v-if="iteminfo.messageType === 1">
                            精华
                        </el-tag>
                    </div>

                    <span style="font-size: 12px; color: darkgray;">
                        发布时间：{{ iteminfo.time }} &emsp;&emsp; 回帖数：{{ iteminfo.answerNum }}
                    </span>
                  </div>
                </el-col>
              </el-row>
              <el-divider style="margin: 10px 0px 20px 0px;" />
            </div>
          </div>
        </el-scrollbar>
      </el-col>

      <el-col :span="7">
        <div style="background-color:#f2f7ef; text-align: left; padding: 20px 30px; height: 90%;">
          <p style="margin: 0px 0px 15px 0px">任务筛选：</p>

          <el-tree
            ref="treeRef"
            :props="commonProps"
            :data="taskOptions"
            node-key="taskId"
            show-checkbox   highlight-current    default-expand-all
            @check-change="handleTaskChange"
          >
          </el-tree>

        </div>
      </el-col>
    </el-row>

    <!-- 编辑小组信息对话框 -->
    <el-dialog title="新增留言" v-model="addDV" align-center draggable>
      <el-form :model="addForm" :rules="addRule" ref="addFormRef" style="padding: 20px; padding-bottom: 0px;">
        <el-form-item required label="留言标题：" prop="title" >
          <el-input v-model="addForm.title" placeholder="请输入留言标题"  clearable maxlength="50" show-word-limit/>
        </el-form-item>

        <el-form-item required label="留言内容：" prop="content" style="margin-top: 10px;">
          <el-input v-model="addForm.content" type="textarea" :autosize="{ minRows: 2, maxRows: 3 }" placeholder="请输入留言内容" maxlength="250" show-word-limit />
        </el-form-item>

        <el-form-item required label="所属任务：" prop="task" style="margin-top: 10px;">
          <el-cascader v-model="addForm.task" :options="taskOptions" :props="cascaderProps" placeholder="请选择任务"/>
        </el-form-item>

        <el-form-item required label="是否设为精华：" prop="isHighlight" style="margin-top: 10px;">
          <el-switch v-model="addForm.isHighlight" active-text="精华" inactive-text="普通" />
        </el-form-item>
      </el-form>

      <template #footer>
          <el-button @click="addDV = false" style="padding: 1px 20px;">取消</el-button>
          <el-button class="btn4" @click="addMessage"> 新增 </el-button>
      </template>
      
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed ,onMounted } from "vue";
import { ElTree,ElMessage } from 'element-plus'
import { useRouter } from "vue-router";
import { getCurrentTaskList, getMessageInfo, uploadMessage,getResourceByMessageId} from '@/api/api'; 
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
}
//const route = useRoute();
const store = useStore();
//后续要更新成动态读取
const groupId = computed(() => store.state.groupId || 6);
const postAccount = computed(() => store.state.userId|| "GPCadmin");//防止测试时出现错误，默认最好改一下

const treeRef = ref(null);

const router = useRouter();


//上传文件的表格以及弹窗
const addDV = ref(false); 
const addFormRef = ref(null);

const addForm = ref({
  title: "",
  content: "",
  task: "",
  isHighlight: false,
});

const addRule = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '内容不能为空', trigger: 'blur' }],
  task: [{ required: true, message: '请选择任务', trigger: 'blur' }]
};

//获取全部任务，已完成测试
const taskOptions = ref([]);

const fetchTaskOptions = async () => {
    try {
        const paramGetCurrentTaskList = { groupId: groupId.value };
        const response = await getCurrentTaskList(paramGetCurrentTaskList);

        if (response.data.success) {
            taskOptions.value = response.data.data;
        } else {
            console.error(response.data.message);
        }
    } catch (error) {
        console.error('获取当前任务列表失败，请稍后重试');
    }
};

onMounted(() => {
    fetchTaskOptions();
});


const commonProps = {
  label: 'taskName',
  value: 'taskId',
  children: 'childrenTask'
};

const cascaderProps = {
  label: 'taskName',
  value: 'taskId',
  children: 'childrenTask',
  checkStrictly: true
};

//获取全部消息，已测试
const messageItems = ref([]);

const fetchMessageItems = async () => {
  try {
    const paramGetMessageInfo = {
      choice: 5,
      id: groupId.value,
    };
    const response = await getMessageInfo(paramGetMessageInfo);

    if (response.data && response.data.success) {
      const messages = response.data.data;

      for (const msg of messages) {
        try {
          const paramGetResourceByMessageId = {
            messageId: msg.messageId,
          };
          const res = await getResourceByMessageId(paramGetResourceByMessageId);

          if (res.data && res.data.success) {
            msg.fileType = res.data.data.fileType;
            msg.originName = res.data.data.originName;
          } else {
            console.log('无对应资源');
            msg.fileType = 0;
            msg.originName = '无资源';
          }
        } catch (error) {
          console.error('Error fetching resource info:', error);
          msg.fileType = null;
          msg.originName = null;
        }
      }

      // 更新消息数据
      messageItems.value = messages;
    } else {
      console.error('Failed to fetch messages:', response.data?.message || 'Unknown error');
    }
  } catch (error) {
    console.error('Error fetching message info:', error);
  }
};


onMounted(() => {
    fetchMessageItems();
});


//搜索以及筛选，已测试通过
const searchQuery = ref("");
const searchBy = ref("title");
const showHighlightOnly = ref(false);

const selectedTasksId = ref([]);

const filteredMessages = computed(() => {
  return messageItems.value
    .filter((msg) => !showHighlightOnly.value || msg.messageType === 1)
    .filter((msg) => {
      if (!searchQuery.value) return true; 
      const fieldValue = msg[searchBy.value];
      return fieldValue ? fieldValue.includes(searchQuery.value) : false;
    })
    .filter((msg) => !selectedTasksId.value.length || selectedTasksId.value.includes(msg.taskId));
});

//详情跳转
const goToDetails = (index) => {
  const messageId = messageItems.value[index].messageId;
  router.push({
   path: `/DashBoard/MessageDetail/${messageId}`,
  });
};

//上传留言，已验证
const addMessage = async () => {
  addFormRef.value.validate(async (valid) => {
    if (valid) {
      //("Selected task:", addForm.value.task);
      
      const selectedTaskId = Array.isArray(addForm.value.task)
        ? addForm.value.task[addForm.value.task.length - 1] 
        : null;

      //console.log("Extracted taskId:", selectedTaskId);

      const newMessageParam = {
        postAccount: postAccount.value,
        title: addForm.value.title,
        messageContent: addForm.value.content,
        taskId: selectedTaskId,
        messageType: addForm.value.isHighlight ? 1 : 0
      };

      try {
        await uploadMessage(newMessageParam); 
        await fetchMessageItems();        
        addDV.value = false;
        Object.assign(addForm.value, { title: "", content: "", task: "", isHighlight: false });
      } catch (error) {
        console.log('新增留言失败，请稍后重试');
        ElMessage.error('新增留言失败，请稍后重试');
      }
    } else {
      console.log("表单验证失败");
      ElMessage.error('表单验证失败');
    }
  });
};

const highLightFilter = () => {
  showHighlightOnly.value = !showHighlightOnly.value;
};


const handleTaskChange = () => {
    selectedTasksId.value = treeRef.value.getCheckedKeys();
    console.error(treeRef.value.getCheckedKeys());
};

</script>

<style scoped>
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

.container {
  background-color: #fff;
  padding: 20px; 
  height: 100vh; 
  width: 100%;
}

::v-deep .el-tree{
  background-color: #f2f7ef !important; 
}
</style>

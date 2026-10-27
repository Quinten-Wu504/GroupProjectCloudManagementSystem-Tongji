<template>
  <el-container class="container" direction="vertical" >
    <p style="text-align: left; margin-top: 0; font-size: 0.8em">任务管理 / 小组管理</p>
      <el-divider style="margin: 0;"/>
    
    <!-- 第一行 -->
      <el-row :gutter="20" style="padding: 8vx; flex: 40%; margin-bottom: 3vh;margin-top: 3vh;" >
        <!-- 左侧任务备选列表 -->
        <el-col :span="9" height=100% >        
          <el-card class="box"  >
            <el-header style="text-align: left; font-size: 12px;height: 5vh">
              <div class="toolbar">
                <h3>备选任务列表</h3>
                <el-button type="text" size="mini" class="create-task-button" @click="createTaskInfo">新建任务</el-button>
              </div>
            </el-header>
            <el-main>
              <el-scrollbar>
                <el-table 
                :data="taskList" 
                style="width: 100% "
                max-height="20vh" 
                @row-click="editTask">
                  <el-table-column prop="taskName" label="任务名称" ></el-table-column>
                  <el-table-column prop="initiator" label="发起人" ></el-table-column>
                  <el-table-column label="操作"  >
                    <template #default="scope">
                      <el-popconfirm title="此操作无法撤回，您确定要删除该任务吗？" confirm-button-text="删除"
                        cancel-button-text="取消" confirm-button-type="danger" width="200px" hide-icon
                        @confirm="deleteTask(scope.row)">
                        <template #reference>
                          <el-button @click.stop type="success" circle>
                            <el-icon><Delete /></el-icon> 
                          </el-button>
                        </template>
                      </el-popconfirm>
                      
                      <!--el-button @click.stop="editTask(scope.row)" type="success" circle>
                        <el-icon><Edit /></el-icon> 
                      </el-button-->
                      <el-button @click.stop="submitTask(scope.row)" type="success" circle>
                        <el-icon><Right /></el-icon> 
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-scrollbar>
            </el-main>
          </el-card>
        
        </el-col>

        <!-- 右侧任务进程列表 -->
        <el-col :span="15" height=100%>
          <el-card class="box"  >
            <el-header style="text-align: left; font-size: 12px; height: 5vh;">
              <div class="toolbar">
                <h3>任务进程列表</h3>
                <el-input
                  v-model="searchQuery"
                  style="width: 240px; margin-right: 1vw; margin-top:1.5vh; margin-bottom: 1vw;"
                  prefix-icon="Search"
                  placeholder="搜索进程中的任务..."
                />
              </div>            
            </el-header>
            <el-main>
              <!--el-scrollbar style="padding: 0; margin: 0;"-->
                <el-table 
                  :data="filteredProgressList" 
                  class="my-group-table"
                  style="width: 100% " 
                  :header-cell-style="{ 'text-align': 'center' }" :cell-style="{ 'text-align': 'center' }"
                  max-height="20vh"                  
                  @row-click="TaskEdit">
                  <el-table-column prop="taskNo" label="No."  key="Math.random()"></el-table-column>
                  <el-table-column prop="taskName" label="任务名称" ></el-table-column>
                  <el-table-column prop="initiator" label="发起人" ></el-table-column>
                  <el-table-column prop="executor" label="执行人" ></el-table-column>
                  <el-table-column prop="deadline" label="截止时间" width="150" ></el-table-column>
                  <el-table-column prop="advanceTime" label="提前提醒天数" width="150"></el-table-column>
                  <el-table-column label="操作" fixed="right">
                  <template #default="scope">
                    <el-popconfirm title="此操作无法撤回，您确定要回退该任务吗？" confirm-button-text="回退"
                        cancel-button-text="取消" confirm-button-type="danger" width="200px" hide-icon
                        @confirm="TaskBack(scope.row)">
                        <template #reference>
                          <el-button  @click.stop type="success" circle>  
                            <el-icon> <Back /></el-icon> 
                          </el-button>
                        </template>
                      </el-popconfirm>
                    
                    <!--el-button @click="TaskEdit(scope.row)" type="success" circle >    
                      <el-icon> <Edit /></el-icon> 
                    </el-button-->
                  </template>
                  </el-table-column>
                </el-table>      
              <!--/el-scrollbar-->
            </el-main>
          </el-card>
        </el-col>
      </el-row>

      <!-- 第二行 -->
      <el-row style="padding: 8vx; flex: 60%;overflow: auto;">
          <el-card class="box">
          <div>
            <!-- Gantt 图容器 -->
            <div ref="ganttRef" ></div>

           </div>
          </el-card>    
      </el-row>
    
  <!-- 新建任务对话框 -->
    <el-dialog title="新建任务" v-model="dialogAddVisible" align-center draggable>
      <el-form :model="AddTaskform" :rules="rulesAddTask" ref="AddTaskRef" >
        <el-form-item required label="任务名称" prop="name">
          <el-input v-model="AddTaskform.name" maxlength="10" show-word-limit></el-input>
        </el-form-item>
        <el-form-item required label="任务描述" prop="desc">
          <el-input v-model="AddTaskform.desc" type="textarea" rows="4" maxlength="250" show-word-limit></el-input>
        </el-form-item> 
      </el-form>       
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogAddVisible= false" style="padding: 1px 20px;">取消</el-button>
          <el-button class="btn4"  @click="saveInfo">保存</el-button>
        </span>
      </template>
    </el-dialog>
     <!-- 编辑任务的对话框 -->
    <el-dialog title="编辑任务" v-model="dialogEditVisible" align-center draggable>
      <el-form :model="editForm" :rules="rulesAddTask" ref="EditFormRef" >
        <el-form-item   label="任务名称" :label-width="80" prop="name">
          <el-input v-model="editForm.name"  maxlength="10" show-word-limit/>
        </el-form-item>
        <el-form-item  label="任务描述" :label-width="80" prop="desc">
          <el-input v-model="editForm.desc" type="textarea" rows="4" placeholder="请输入"  maxlength="250" show-word-limit></el-input>
        </el-form-item> 
        <el-form-item label=" 发起人" :label-width="80" :text-align="left" >
          <el-input v-model="editForm.initiator" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogEditVisible = false" style="padding: 1px 20px;">取消</el-button>
          <el-button class="btn4"  @click="confirmEdit">确认</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 补充信息的对话框 -->
    <el-dialog title="补充任务信息" v-model="dialogSubmitVisible" align-center draggable>
      <el-form :model="SubmitForm" :rules="rulesSubmitForm" ref="SubmitFormRef" >
        <el-form-item required label="任务名称">
          <el-input v-model="SubmitForm.taskName" disabled />
        </el-form-item>
        <el-form-item required label="发起人">
          <el-input v-model="SubmitForm.initiator" disabled />
        </el-form-item>
        <el-form-item required label="任务描述" :label-width="formLabelWidth">
          <el-input v-model="SubmitForm.desc" type="textarea" rows="2" placeholder="请输入"  maxlength="250" show-word-limit></el-input>
        </el-form-item> 
        <el-form-item required label="执行人" prop="executor">
          <el-select v-model="SubmitForm.executor" placeholder="执行人">
            <el-option v-for="member in members" :key="member.account" :label="member.name" :value="member.account" />
          </el-select>
        </el-form-item>
        <el-form-item required label="开始时间" prop="startline">
          <el-date-picker
            v-model="SubmitForm.startline"
            type="datetime"
            placeholder="选择日期"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item required label="截止时间" prop="deadline">
          <el-date-picker
            v-model="SubmitForm.deadline"
            type="datetime"
            placeholder="选择日期"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item required label="提前提醒天数" prop="advanceTime">
          <el-input-number v-model="SubmitForm.advanceTime" :min="1" :max="10" />
        </el-form-item>
        <el-form-item  label="父任务" prop="parentID">
          <el-select 
            v-model="SubmitForm.parentID" 
            :placeholder="state.CanEditParent ? '选择父任务' : '父任务不可修改'" 
            :disabled="!state.CanEditParent">
            <el-option v-for="parentTask in ParentTaskList" :key="parentTask.ID" 
            :label="parentTask.ID?`${parentTask.taskNo}:${parentTask.taskName}`:`${parentTask.taskName}`" :value="parentTask.ID" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogSubmitVisible = false" style="padding: 1px 20px;">取消</el-button>
          <el-button class="btn4" @click="confirmSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>
    
  </el-container>
</template>

<script>
import { reactive, ref ,computed,onMounted,watch,nextTick  } from "vue";
//import { useRoute } from 'vue-router'
import { ElMessage } from "element-plus";
//import GanttTu from '@/components/GanttTu.vue';
import {  Delete, Right, Back } from "@element-plus/icons-vue";
import { getAlternativeTasks,getGroupMembers, getActiveTask,getActiveParentTask,
  createTask,deleteTaskapi,changeAlternativeTask,addTask,changeActiveTask,backTask } from "@/api/api_TaskManage.js";
import {getUserInfo, getGroupInfo} from '@/api/api_allneed.js'
import { useStore } from 'vuex';
import gantt from 'dhtmlx-gantt'; // 引入 gantt 库
import 'dhtmlx-gantt/codebase/dhtmlxgantt.css'; // 引入样式文件
export default {
  components: {//使用的组件
    //GanttTu,
    //Edit,
    Delete,
    Right,
    Back,
  },
  
  setup() {//进入页面时执行
    // 创建一个 ref 来引用 gantt DOM 元素
    const ganttRef = ref();
    // 数据定义
    //当前用户
    const store = useStore();
    const user = reactive({
      name: "默认用户名",
      id: computed(() => store.state.userId || '默认用户ID'),
      //avatar: ""
    })
    //当前小组
    //const route = useRoute()
    const group = reactive({
      name:  "默认组名",
      id: computed(() => store.state.groupId || '默认组ID'),
      role: "默认角色",
    })
   //用到的规则
   const rulesAddTask = {
      name: [{ required: true, message: "任务名称不能为空", trigger: "blur" }],
      desc: [{ required: true, message: "任务描述不能为空", trigger: "blur" }],
    };
    const AddTaskRef = ref(null);
    const EditFormRef = ref(null);
    const SubmitFormRef = ref(null);
    const rulesSubmitForm = {
      taskName: [{required: true, message: "任务名称不能为空", trigger: "blur" }],
      initiator: [{ required: true, message: "发起人不能为空", trigger: "blur" }],
      desc: [{ required: true, message: "任务描述不能为空", trigger: "blur" }],
      executor: [{required: true, message: "请选择执行人", trigger: "blur" }],
      startline: [{ required: true, message: "请选择开始时间", trigger: "blur" }],
      deadline: [{required: true, message: "请选择截止时间", trigger: "blur" }],
      advanceTime: [{ type: "number", required: true, message: "请输入提醒天数", trigger: "blur" }],
      parentID: [{ required: true, message: "请选择父任务", trigger: "blur" }],
    };
const fetchGroup = async ()=> {
  try {
    const response = await getGroupInfo({ groupId: group.id });
    if (response.data.success) {

      group.name = response.data.data.groupName;
      //group.GroupId = response.data.data.GroupId;
      let Groupleader = response.data.data.leaderNickname;//注：目前后端给的是名字不是account，存在潜在bug，一个组内有重复的名字
      group.role = Groupleader == user.name ? "组长" : "组员";
      //console.log(group.name);
    } else {
      throw new Error(response.data.message);
    }
  } catch (error) {
    console.error("获取组信息失败:", error);
  }
};

const fetchUser = async () => {
  try {
    const response = await getUserInfo({  "account": user.id });
    if (response.data.success) {
      user.name = response.data.data.nickname;
      //user.id = response.data.data.account;
      //user.avatar = response.data.data.avatar;
    } else {
      throw new Error(response.data.message);
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    }
};
    
    //成员，用于显示选择的执行人
   // 成员列表（原始数组格式）
const members = reactive([]);

// 获取成员数据
const fetchMembers = async () => {
  try {
    const response = await getGroupMembers({ groupId: group.id });
    //console.log(response.data);
    if (response.data.success) {
      // 将后端数据直接存入 members 数组
      const transformedData = response.data.data.map((item) => ({
        account: item.account, // 用于匹配的字段
        name: item.nickname, // 成员姓名
      }));
      members.splice(0,members.length,...transformedData); // 更新响应式数组
    } else {
      console.error("未获取到有效成员数据");
    }
  } catch (error) {
    console.error("获取成员数据失败:", error);
  }
};
    // 任务备选列表
    const taskList = reactive([]);
    const fetchTasks = async () => {
      try {
        const response = await getAlternativeTasks(
          {"groupId": group.id}
        );
        if (response.data.success) {
          // 映射后端数据到前端所需格式
          const transformedData = response.data.data.map((item) => {
            const initiatorMember = members.find((member) => member.account === item.creatorAccount);
            const initiatorName = initiatorMember ? initiatorMember.name : "账号" + item.creatorAccount+"不在此组";
            return {            
            ID: item.taskId,
            taskName: item.taskName,
            initiator: initiatorName,
            desc: item.description, }
          });

          // 更新响应式数组
          taskList.splice(0,taskList.length,...transformedData);
        } else {
          console.error("未获取到有效任务数据");
        }
      } catch (error) {
        console.error("获取任务数据失败:", error);
      }
    };
    // 父任务进程列表 用于选择父任务时显示
    const ParentTaskList = reactive([])//接口getActiveParentTask
    const fetchParentTasks = async () => {
      try {
        const response = await getActiveParentTask(
          {"groupId": group.id}
        );
        if (response.data.success) {
          // 映射后端数据到前端所需格式
          const transformedData = response.data.data.map((item) => {
            const taskInProgress=progressList.find((progress) => progress.ID === item.taskId);
            const number = taskInProgress ? taskInProgress.taskNo : "";
            return{
            ID: item.taskId,
            taskNo: number,
            taskName: item.taskName,
            }
          });

          // 更新响应式数组
          ParentTaskList.splice(0,ParentTaskList.length,...transformedData);
          const temp=reactive({
            ID: 0,
            taskName: "作为父任务",
          })
          ParentTaskList.push(temp);
        } else {
          console.error("未获取到有效任务数据");
        }
      } catch (error) {
        console.error("获取任务数据失败:", error);
      }
    };
    // 任务进程列表
// 任务进程列表
const progressList = reactive([]);
const searchQuery = ref("");
const filteredProgressList =  reactive([]);
// 任务数据甘特图使用
const gantttasks = {
  data: [ ]// 任务数组
};  
    
// 获取任务数据并映射到前端格式
const fetchProgressTasks = async () => {
  try {
    const response = await getActiveTask({"groupId": group.id});
    let count=0;
    if (response.data.success) {
      // 遍历任务数据并处理      
      //console.log(members);
      const transformedData = response.data.data.map((item) => {
        count=count+1;
        // 从 members 数组中查找匹配的 initiator（创建者）
        const initiatorMember = members.find((member) => member.account === item.creatorAccount);
        const initiatorName = initiatorMember ? initiatorMember.name : "账号"+item.creatorAccount+"未在此组";
        const executorMember = item.responsible?members.find((member) => member.account === item.responsible):"未分配";
        const executorName = executorMember ? executorMember.name : "账号"+item.responsible+"未在此组";
        // console.log("执行人发的啥");
        //console.log(item.responsible);
        return {
          taskNo:count,
          ID: item.taskId,
          taskName: item.taskName,
          initiator: initiatorName, // 使用找到的创建者姓名
          executor: executorName, // 任务执行人
          executorID: item.responsible,
          deadline: item.deadline,
          startline: item.startTime ,
          desc: item.description , // 描述信息
          advanceTime: item.advanceTime || 0, // 提前时间
          parentID: item.parentId , // 父任务 ID
        };
      });

      // 更新响应式数组,替换
      progressList.splice(0, progressList.length, ...transformedData);
    } else {
      console.error("未获取到有效任务数据");
    }
  } catch (error) {
    console.error("获取任务数据失败:", error);
  }
};
// 监听 progressList 和 searchQuery，动态更新 filteredProgressList
watch(
  () => progressList,
  (progresstasks) => {
    // progressList 改变时直接同步给 filteredProgressList
    //console.log("progressList updated", progresstasks);
    filteredProgressList.splice(0, filteredProgressList.length, ...progresstasks);
    searchQuery.value=""; // 清空搜索框
    // 转换后端数据为 Gantt 图所需格式
    //console.log("filteredProgressList updated", progresstasks);
    gantttasks.data = progresstasks.map((item) => {
      //const executorMember = item.responsible?members.find((member) => member.account === item.responsible):"未分配";
      //const executorName = executorMember ? executorMember.name : "账号"+item.responsible+"未在此组";
      return {
        id: item.ID,
        text: item.taskName || '无任务名称',
        start_date: item.startline ? item.startline.split(' ')[0] : null,//去掉时分秒
        end_date: item.deadline ? item.deadline.split(' ')[0] : null,//去掉时分秒
        person: item.executor ,
        parent: item.parentID || null, 
        progress: item.advanceTime ? item.advanceTime / 10 : 0, // 示例进度计算
        open: true, // 默认展开
        //status: item.status === 1 ? '进行中' : '未开始', // 示例状态转换
      }        
    });
    if (gantt.$container) gantt.parse(gantttasks); // 加载新的任务数据
    //else initGantt(); // 初始化 Gantt 图
    //console.log("tasks updated", gantttasks.data);
  },
  { immediate: true, deep: true } // 立即执行，深度监听
);

// 监听 searchQuery 的变化，调用过滤逻辑
watch(
  () => searchQuery.value,
  (query) => {
    if (progressList.length > 0) {
      // 根据搜索关键词过滤
      //console.log("searchQuery updated", query);
      filteredProgressList.splice(
        0,
        filteredProgressList.length,
        ...progressList.filter((task) => task?.taskName?.includes(query))
      );
    } else {
      // progressList 为空时清空 filteredProgressList
      filteredProgressList.splice(0, filteredProgressList.length);
    }
  }
);

// const fetchfilteredProgressList=()=>{
//   filteredProgressList.splice(
//         0,
//         filteredProgressList.length,
//         ...progressList,
//       );
// }
// const fetchganttTasks=()=>{
//   gantttasks.data = progressList.map((item) => {
//       //const executorMember = item.responsible?members.find((member) => member.account === item.responsible):"未分配";
//       //const executorName = executorMember ? executorMember.name : "账号"+item.responsible+"未在此组";
//       return {
//         id: item.ID,
//         text: item.taskName || '无任务名称',
//         start_date: item.startline ? item.startline.split(' ')[0] : null,//去掉时分秒
//         end_date: item.deadline ? item.deadline.split(' ')[0] : null,//去掉时分秒
//         person: item.executor ,
//         parent: item.parentID || null, 
//         progress: item.advanceTime ? item.advanceTime / 10 : 0, // 示例进度计算
//         open: true, // 默认展开
//         //status: item.status === 1 ? '进行中' : '未开始', // 示例状态转换
//       }        
//     });
//     console.log("tasks updated2", gantttasks.data);
// }
  // 示例：先获取成员信息，再获取任务信息
  const initialize = async () => {
    await fetchMembers(); // 先加载成员
    await fetchProgressTasks(); // 再加载任务
    fetchTasks(); // 获取备选任务列表
    await fetchParentTasks(); // 获取父任务列表
    
  };
  // 初始化 Gantt 图表
const initGantt = () => {
  if (!ganttRef.value) return; // 确保 ganttRef 不为空
  //console.log("initGantt");
  // 设置时间格式
  gantt.config.xml_date = "%Y-%m-%d";
  gantt.config.fit_tasks = true; //自适应
  gantt.config.autosize = true;//自适应甘特图的尺寸大小, 使得在不出现滚动条的情况下, 显示全部任务
  // 配置表格列
  gantt.config.columns = [
				{ name: 'text', label: '项目名称', 
        tree: true, width: '100', align: 'left', },
        { name: 'person', label: '负责人', width: '100', align: 'center' },    
			];
  
  gantt.config.scales = [{ unit: 'month', step: 1, date: ' %Y 年  %F' }, { unit: 'day', step: 1, date: '%d' }];
  // 清空之前的数据
  gantt.clearAll();
  gantt.i18n.setLocale('cn'); // 设置中文
  gantt.config.readonly = true; // 设置为只读，否则是可以移动甘特图和连线的

  // 初始化 Gantt 图表
  gantt.init(ganttRef.value);
  // 解析任务数据
  gantt.parse(gantttasks);
  //console.log("tasks 3", gantttasks.data);
  // 手动添加时间线
  const markerDate = new Date(); // 当前时间
  const markerPosition = gantt.posFromDate(markerDate); // 获取坐标位置

  const markerElement = document.createElement("div");
  markerElement.className = "today-marker";
  markerElement.style.left = markerPosition + "px";
  markerElement.style.top = "20px";
  markerElement.style.height = gantt.$task.offsetHeight + "px"; // 覆盖整个任务区域高度
  markerElement.style.position = "absolute";
  markerElement.style.backgroundColor = "green";
  markerElement.style.width = "2px";

  // 将时间线添加到任务区域的 DOM 中
  const tasksArea = document.querySelector(".gantt_task");
  if (tasksArea) {
    tasksArea.appendChild(markerElement);
  }
};
    onMounted(async () => {
      //console.log("Before fetching data, ganttRef:", ganttRef.value);
      
      fetchUser();
      fetchGroup();
      await initialize();

      //console.log("After initialize, ganttRef:", ganttRef.value);

      await nextTick();
      //console.log("After nextTick, ganttRef:", ganttRef.value);

    
        //console.log("After setTimeout, ganttRef:", ganttRef.value);
        if (ganttRef.value) {
          initGantt();
        } else {
          console.error("ganttRef is still not set");
        }

    });
    
    // 任务备选列表相关操作
    const dialogAddVisible = ref(false);//增加任务
    const dialogEditVisible = ref(false);//编辑任务
    const dialogSubmitVisible = ref(false);//提交任务
    //const dialogEdit2Visible = ref(false);//编辑进程中任务
    const AddTaskform = reactive({
      name: '',
      desc: '',
    });
    // 添加任务
    const createTaskInfo = () => {
      dialogAddVisible.value = true;
     // console.log("添加任务dialogAddVisible");
    };

    const saveInfo = async ()  => {
      AddTaskRef.value.validate(async (valid) => {
        if (valid) {
      //console.log("保存新建任务");
      /*createTask(
        {
          "taskName": "1",
          "creatorAccount": "1",
          "groupId": "1",
          "description": "nice"
        }
      )*/
      try {//await必须就近原则加aync
        const response = await createTask(
          {
            "taskName": AddTaskform.name,
            "creatorAccount": user.id,
            "groupId": group.id,
            "description": AddTaskform.desc,

          }
        );
        if (response.data.success) {
          ElMessage.success(`新建任务 ${AddTaskform.name} 成功`);
          //更新taskList
          taskList.push({
          ID: response.data.data,
          taskName: AddTaskform.name,
          desc: AddTaskform.desc,
          initiator: user.name,//改成当前登录用户

          });
          //console.log(response.data.data);
        }
        else {
          ElMessage.error(`新建任务 ${AddTaskform.name} 失败,${response.data.message}`);
        } 
      }catch (error) {
        console.error(error);
      }
      
      dialogAddVisible.value = false;
      }
      else {
        ElMessage.error("请输入完整信息");
      }
    });
    };

    // 删除任务
    const deleteTask = async (task) => {
      try {
        const response = await deleteTaskapi(
          {
            "taskId": task.ID
          }
        )
        if (response.data.success) {
          //ElMessage.success(`删除任务 ${task.taskName} 成功`);
          //前端数据更新taskList
          // const index = taskList.findIndex(t => t.ID === task.ID);
          // if (index >= 0) {
          //   taskList.splice(index, 1);
          //   ElMessage.success(`删除任务 ${task.taskName} 成功`);
          // } else {
          //   console.error("任务未找到:", task);
          // }
          initialize();
        } else {
          ElMessage.error(`删除任务 ${task.taskName} 失败，,${response.data.message}`);
        } 
      }catch (error) {
        console.error(error);
      }
    
      
    };

    // 编辑任务
    const editForm = reactive({
      name: "",
      desc: "",
      initiator: "",
    });
    let editingTaskId = null;

    const editTask = (task) => {
      editingTaskId = task.ID;
      editForm.name = task.taskName;
      editForm.desc = task.desc;
      editForm.initiator = task.initiator;
      //Object.assign(editForm, task);//将task的值赋给editForm,深拷贝
      //console.log("打印信息",editForm);
      dialogEditVisible.value = true;
      initialize();
    };

    const confirmEdit = async() => {
      EditFormRef.value.validate(async (valid) => {
        if (valid){
          try {//await必须就近原则加aync
            const response = await changeAlternativeTask(
              {
                "taskName": editForm.name,
                "description": editForm.desc,
              "taskId":  editingTaskId
              }
            );
            if (response.data.success) {
              ElMessage.success(`修改任务 ${editForm.name} 成功`);
              //更新taskList
              // if (editingTaskId !== null) {
              //   const task = taskList.find(t => t.ID === editingTaskId);
              //   if (task) {
              //     task.taskName = editForm.taskName;
              //     task.desc = editForm.desc;
              //   }
              // }  
              initialize();
              dialogEditVisible.value = false;
              editingTaskId = null;       
            }
            else {
              ElMessage.error(`修改任务 ${editForm.name} 失败，,${response.data.message}`);
            } 
          }catch (error) {
            console.error(error);
          }
        }
        else{
          ElMessage.error("请输入完整信息");
        }
      });      
    };

    // 提交任务
    const SubmitForm = reactive({
      taskId: "",
      taskName: "",
      initiator: "",
      executor: "",
      deadline: "",
      startline: "",
      desc:"",
      advanceTime: 1,//提前提醒天数
      parentID: null,
    });
    let pendingTaskId = null;
    
    const submitTask = (task) => {
      state.CanEditParent=true;
      pendingTaskId = task.ID;
      SubmitForm.taskName = task.taskName;
      SubmitForm.desc = task.desc;
      SubmitForm.initiator = task.initiator;
      SubmitForm.taskId = task.ID;
      SubmitForm.deadline = '';
      SubmitForm.startline = '';
      SubmitForm.advanceTime = '';
      SubmitForm.executor = '';
      SubmitForm.parentID = null;
      dialogSubmitVisible.value = true;
      initialize();
    };

    const confirmSubmit = async() => {
      SubmitFormRef.value.validate(async (valid) => {
        if (valid) {
          
          // 校验开始时间和截止时间
          if (new Date(SubmitForm.startline) > new Date(SubmitForm.deadline)) {
            ElMessage.error('开始时间必须小于截止时间');
            return;
          }
          //校验子任务的开始时间截止时间在父任务之间
          if (SubmitForm.parentID) {
            
            const parentTask = progressList.find(task => task.ID == SubmitForm.parentID);
            if (new Date(SubmitForm.startline) < new Date(parentTask.startline) || new Date(SubmitForm.deadline) > new Date(parentTask.deadline)) {
              ElMessage.error('子任务的开始与截止须在父任务之间');
              return;
            }
          }
          //校验父任务的开始时间截止时间大于子任务
          else{
            const childTasks = progressList.filter(task => task.parentID ==SubmitForm.taskId);
            //ElMessage.error(`父任务的时间范围必须包含子任务“${SubmitForm.taskId}”`);
            if (childTasks.length > 0) {
              const parentStart = new Date(SubmitForm.startline);
              const parentEnd = new Date(SubmitForm.deadline);

              for (const child of childTasks) {
                const childStart = new Date(child.startline);
                const childEnd = new Date(child.deadline);

                if (parentStart > childStart || parentEnd < childEnd) {
                  ElMessage.error(`父任务的时间范围必须包含子任务“${child.taskName}”的时间范围！`);
                  return;
                }
              }
            }        
          }
          if (pendingTaskId!==null) {
            try {//await必须就近原则加aync         
                const response = SubmitForm.parentID?await addTask(
                  {
                    "taskId": pendingTaskId,    
                    "deadline": SubmitForm.deadline,
                     "description":SubmitForm.desc, 
                    "startTime": SubmitForm.startline,
                    "advanceTime": SubmitForm.advanceTime,
                    "responsible": SubmitForm.executor,
                    "parentId": SubmitForm.parentID,
                  }
                ):await addTask(
                  {
                    "taskId": pendingTaskId, 
                     "description":SubmitForm.desc,    
                    "deadline": SubmitForm.deadline,
                    "startTime": SubmitForm.startline,
                    "advanceTime": SubmitForm.advanceTime,
                    "responsible": SubmitForm.executor,
                    //"parentId": SubmitForm.parentID,
                  }
                );
              if (response.data.success) {
                ElMessage.success(`提交任务 ${SubmitForm.taskName} 到进程列表`);
                //更新taskList          
                // const index = taskList.findIndex(t => t.ID === pendingTaskId);
                // if (index >= 0) {
                //   taskList.splice(index, 1);
                //   progressList.push({ ...SubmitForm });
                //   //ElMessage.success(`${SubmitForm.taskName} 任务已提交到进程列表`);
                //  if(!SubmitForm.parentID){
                //   ParentTaskList.push({ 
                //     ID: SubmitForm.taskId,
                //     taskName: SubmitForm.taskName, });
                //  }
                // }
                initialize();
                dialogSubmitVisible.value = false;
                pendingTaskId = null;      
                //清空submitForm
                SubmitForm.taskName = '';               
                SubmitForm.desc = ''; 
                SubmitForm.deadline = '';
                SubmitForm.startline = '';
                SubmitForm.advanceTime = '';
                SubmitForm.executor = '';
                SubmitForm.parentID = '';
              }
              else {
                ElMessage.error(`提交任务 ${SubmitForm.taskName} 进程列表失败,${response.data.message}`);
                console.error(response);
              } 
            }catch (error) {
              console.error(error);
              ElMessage.error(`提交任务 ${SubmitForm.taskName} 进程列表失败,前后端连接问题`);
            }        
          }
          if (TaskEdit2Id !== null) {
            try {//await必须就近原则加aync
              const response = SubmitForm.parentID?await changeActiveTask(
                {
                  "taskId": TaskEdit2Id, 
                  "taskName": SubmitForm.taskName, 
                  "description":SubmitForm.desc, 
                  "deadline": SubmitForm.deadline,
                  "startTime": SubmitForm.startline,
                  "advanceTime": SubmitForm.advanceTime,
                  "responsible": SubmitForm.executor===''?null:SubmitForm.executor,
                  "parentID": SubmitForm.parentID,
                }):await changeActiveTask({
                  "taskId": TaskEdit2Id, 
                  "taskName": SubmitForm.taskName, 
                  "description":SubmitForm.desc, 
                  "deadline": SubmitForm.deadline,
                  "startTime": SubmitForm.startline,
                  "advanceTime": SubmitForm.advanceTime,
                  "responsible": SubmitForm.executor,
                });
              if (response.data.success) {
                ElMessage.success(`修改任务 ${SubmitForm.taskName} 在进程列表`);
                //更新taskList          
                // const task = progressList.find(t => t.ID === TaskEdit2Id);
                // if (task) {
                //   Object.assign(task,SubmitForm);
                //   if(!SubmitForm.parentID&&!ParentTaskList.find(t => t.ID === TaskEdit2Id)){
                //     ParentTaskList.push({ 
                //     ID: SubmitForm.taskId,
                //     taskName: SubmitForm.taskName, });
                //  }
                // }
                initialize();
                dialogSubmitVisible.value = false;
                TaskEdit2Id= null;   
                //清空submitForm
                SubmitForm.taskName = '';               
                SubmitForm.desc = ''; 
                SubmitForm.deadline = '';
                SubmitForm.startline = '';
                SubmitForm.advanceTime = '';
                SubmitForm.executor = '';
                SubmitForm.parentID = ''; 
              }
              else {
                ElMessage.error(`修改任务 ${SubmitForm.taskName} 到进程列表失败，,${response.data.message}`);
              } 
            }catch (error) {
              console.error(error);
              ElMessage.error(`修改任务 ${SubmitForm.taskName} 到进程列表失败2,${TaskEdit2Id}`);
            }        
        
          }
        }
        else{
          ElMessage.error("请输入完整信息");
        }
      });
    };
    
    
    
    
    //任务进程中的回退
    // 回退任务
    const TaskBack = async(task) => {
      try {
        const response = await backTask(
          {
            "taskId": task.ID
          }
        )
        if (response.data.success) {
          //ElMessage.success(`回退任务 ${task.taskName} 成功`);
          //前端数据更新taskList
          // 从任务进程列表中移除任务
          // const index = progressList.findIndex((t) => t.ID === task.ID);
          // if (index >= 0) {
          //   progressList.splice(index, 1); // 删除任务
          //   // 添加到任务备选列表
          //   taskList.push({
          //     ID: task.ID,
          //     taskName: task.taskName,
          //     initiator: task.initiator,
          //     desc: task.desc, // 确保 desc 字段存在
          //   });
          //   ElMessage.success(`任务 "${task.taskName}" 已回退到备选任务列表`);
          //   if(task.parentID == null){
          //     ParentTaskList.splice(index, 1);} // 删除父任务
          //     //找子任务并删除子任务的父任务
          //     // 遍历 progressList，将该父任务的子任务的 parentID 设置为 null
          //     progressList.forEach((test_task) => {
          //     if (test_task.parentID === task.ID) {
          //       test_task.parentID = null;
          //       ParentTaskList.push(test_task); // 将子任务添加到父任务列表
          //       }
          //     });
          initialize();
          gantt.deleteTask(task.ID);
            } 
        else {
          console.error("回退任务失败:", task);   
        }          
      }catch (error) {
          console.error(error);
        }      
    };
    let TaskEdit2Id = null;
    const state = reactive({
      CanEditParent: false
    });
    const TaskEdit = (task) => {
        TaskEdit2Id= task.ID;
        Object.assign(SubmitForm, task);
        SubmitForm.taskId= task.ID;
        SubmitForm.executor = task.executorID;
        SubmitForm.parentID = task.parentID?task.parentID:0;
        //SubmitForm.initiator = task.initiator;
         if(task.parentID == null|| task.parentID == 0)
            state.CanEditParent= false;
          else
            state.CanEditParent= true;
        dialogSubmitVisible.value = true;
        initialize();
      };
    

    return {
      ganttRef,//啊啊，不要忘return啊啊
      members,
      taskList,
      rulesSubmitForm,
      rulesAddTask,
      AddTaskRef,
      SubmitFormRef,
      EditFormRef,
      ParentTaskList, // 父任务列表
      searchQuery,
      filteredProgressList,
      dialogAddVisible,
      dialogEditVisible,
      dialogSubmitVisible,
      AddTaskform,
      progressList,
      state,
      //函数
      createTaskInfo,
      saveInfo,
      deleteTask,
      editForm,
      editTask,
      confirmEdit,
      SubmitForm,
      submitTask,
      confirmSubmit,
      TaskBack,
      TaskEdit,
      //confirmEdit2,
    };
  },
};
</script>


<style scoped>
/* 设置所有任务条的背景颜色为绿色 */
/deep/.gantt_task_line {
  background-color: rgba(116, 142, 97, 1) !important;
  border-color: rgba(204, 221, 191, 1) !important;
}
.my-group-table{
  overflow-y: auto; 
  overflow-x: hidden; 
  margin-bottom: 2vh;
}
.container {
  background-color: rgba(248, 251, 246, 1);
  padding: 20px; 
  height: 100vh; 
  width: 100%;
}
.box {
  border-radius: 4px;
  padding: 0px;
  width: 100%; /* 固定宽度 */
  height: 100%; /* 固定高度 */
  overflow-y: auto;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 1);
  border: 3px solid rgba(128, 140, 104, 1);
}
.box .toolbar {
  display: flex;
  justify-content: space-between; /* 左右对齐 */
  align-items: center; /* 垂直居中 */
  height: 100%;
  right: 20px;
}

.create-task-button {
  color: rgba(0, 0, 0, 1);
  text-align: left;
  vertical-align: top;
  padding: 10px;
  border-radius: 5px 5px, 5px, 5px;
  border: 1px solid rgba(116, 142, 97, 1);
}
</style>

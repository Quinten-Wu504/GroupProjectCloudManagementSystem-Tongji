<template>
  <el-container class="personal-page" direction="vertical" style="height: 100vh; "
  >

    <!-- Part 1：个人信息区 -->
    <el-row class="header" >    
      <el-row  class="user-info-section" style="backgroud-color: #d5e8d4; width: 98vw; justify-content: center;" >
        <el-col :span="3" class="user-info">
          <!-- <img :src="require('@/assets/profile-pic.png')" alt="User Avatar" /> -->
          <el-avatar :size="100" :src="'data:image/png;base64,'+ userInfo.head_portrait" />
        </el-col>
        <el-col :span="13" class="user-details">
          <h2>{{ userInfo.nickname }}</h2>
          <div>ID: {{ userInfo.id }} | 学校信息: {{ userInfo.university }}</div>
        </el-col>
        <el-col :span="4" class="user-actions user-actions-g1">
          <button @click="openDialog('editInfo')">
            <el-icon><edit /></el-icon> 信息修改
          </button>
          <button @click="openDialog('passwordChange')">
            <el-icon><edit-pen /></el-icon> 密码修改
          </button>
        </el-col>
        <el-col :span="4" class="user-actions user-actions-g2">
          <button @click="openDialog('schoolAuth')">
            <el-icon><checked /></el-icon> 校园认证
          </button>
          <button @click="confirmLogout">
            <el-icon><remove /></el-icon> 退出登录
          </button>
        </el-col>
      </el-row>
    </el-row>

    <!-- Part 2：主体部分 -->
    <el-row :gutter="0"  class="todo-group " >

      <!-- Part 2.1：TO DO LIST -->
      <el-col :span="6" 
        style=" height:70vh;">
        <el-card class="todo-list" >
          <h2>TO DO LIST</h2>
          <div class="todo-list-timeline">
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in tasks"
                :key="index"
                :timestamp="item.timestamp"
                :color="item.color"
              >
                <div>{{ item.content }}</div>
                <div>由 {{ item.groupName }} 发布</div>
              </el-timeline-item>
            </el-timeline>
          </div>
          <div class="todo-summary">待完成任务数: {{ tasks.length }}</div>
        </el-card>
      </el-col>
      <al-col :span="1"></al-col>
      <!-- Part 2.2：MY GROUP -->
      <el-col :span="17" style="height: 70vh" >
        <el-card class="my-group" >
          <el-row class="my-group-head" style="width: 70vw; justify-content:left;">
            <el-col span="9">
              <h2>MY GROUP</h2>
            </el-col>
            <el-col span="9">
              <el-input
                v-model="searchQuery"
                style="width: 240px; margin-right: 1vw; margin-top:1.5vh; margin-bottom: 1vw;"
                prefix-icon="Search"
                placeholder="搜索已加入小组..."
              />
            </el-col>
            <el-col span="3">
              <button @click="openDialog('createGroup')">
                <el-icon><circlePlus /></el-icon> 创建小组
              </button>
            </el-col>
            <el-col span="3">
              <button @click="openDialog('joinGroup')">
                <el-icon><user /></el-icon> 加入小组
              </button>
            </el-col>
          </el-row>
          <el-card >
          <el-table
            :data="filteredGroups"
            class="my-group-table"
            style="width: 100%"
            :header-cell-style="{ 'text-align': 'center' }" 
            :cell-style="{ 'text-align': 'center' ,'padding': '5px',height: '60px' }"
            :header-row-style="{ 'backgroundColor': '#e9f2e7' }"
            @row-click="confirmGroupInto"
            >
            <!-- 头像列 -->
            <el-table-column label="ID" prop="avatar" min-width="7%">
              <template #default="scope">
                <!-- <img :src="require('@/assets/' + scope.row.avatar)" alt="Group Avatar" class="group-avatar" /> -->
                <!-- <el-avatar :size="30" :src="'data:image/png;base64,'+ scope.row.avatar" /> -->
                <div class="group-id">{{ scope.row.id }}</div>
              </template>
            </el-table-column>
          
            <!-- 小组信息 -->
            <el-table-column label="小组名称" prop="name" min-width="13%">
              <template #default="scope">
                <div>{{ scope.row.name }}</div>
                
              </template>
            </el-table-column>
          
            <!-- 组长 -->
            <el-table-column label="小组组长" prop="leader" min-width="15%">
              <template #default="scope">
                {{ scope.row.leader }}
              </template>
            </el-table-column>
          
            <!-- 小组人数 -->
            <el-table-column label="人数" prop="number-of-members" min-width="7%">
              <template #default="scope">
                {{ scope.row.members }}
              </template>
            </el-table-column>
          
            <!-- 状态 -->
            <el-table-column label="当前状态" prop="status" :filters="statusFilters" :filter-method="filterStatus" min-width="10%">
              <template #default="scope">
                {{ scope.row.status }}
              </template>
            </el-table-column>
          
            <!-- 角色 -->
            <el-table-column label="角色" prop="role" min-width="7%">
              <template #default="scope">
                {{ scope.row.role }}
              </template>
            </el-table-column>
          
            <!-- 创建时间 -->
            <el-table-column label="创建时间" prop="createdAt" sortable min-width="15%">
              <template #default="scope">
                {{ new Date(scope.row.createdAt).toLocaleString() }}
              </template>
            </el-table-column>
          
            <!-- 操作列 -->
            <el-table-column fixed="right" min-width="29%">
              <template #default="scope">
                <!-- 退出小组按钮 -->
                <button @click.stop="confirmGroupExit(scope.row)" style="margin-right: 1vw;">
                  <el-icon><close /></el-icon> 退出小组
                </button>
                <!-- 状态切换按钮 -->
                <button @click.stop="toggleGroupStatus(scope.row)">
                  <el-icon><Refresh /></el-icon> 状态切换
                </button>
              </template>
            </el-table-column>
          </el-table>
          </el-card>
        </el-card>
      </el-col>
    
    </el-row>
 

    <!-- 弹窗 -->

    <!-- 弹窗 1：信息修改 -->
    <el-dialog v-model="dialogs.editInfo" title="个人信息修改">
    <el-form>
      <!-- 显示并修改昵称 -->
      <el-form-item label="昵称" :rules="[ { required: true, message: '昵称不能为空', trigger: 'blur' } ]">
        <el-input v-model="form.nickname"  />
      </el-form-item>

      <!-- 显示并修改头像 -->
      <el-form-item label="头像">
        <div class="avatar-upload">
          <!-- <img :src="form.value.avatarUrl || defaultAvatar" alt="头像" class="avatar-preview" /> -->
           <el-avatar :size="80" :src="'data:image/png;base64,'+userInfo.head_portrait" />
          <el-upload
            class="avatar-upload-btn"
            :file-list="fileList"
            list-type="picture-card"
            :limit=1
            :on-change="changeCarPicture"
            :on-remove="removeCarPicture"
            :on-exceed="handleExceed"
            accept="image/*"
            :auto-upload="false"
            :show-file-list="true"
          >
            <el-button>上传头像</el-button>
          </el-upload>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
    <el-button @click="closeDialog('editInfo')" style="padding: 1px 20px;">取消</el-button>
    <el-button class="btn4" @click="confirmEditInfo">确认</el-button>
    </template>
  <!-- 弹窗 ：缩略图 -->
  </el-dialog>
   <el-dialog v-model="dialogPreview">
    <img w-full :src="dialogImageUrl" alt="Preview Image" />
  </el-dialog>
    <!-- 弹窗 2：校园认证 -->
    <el-dialog v-model="dialogs.schoolAuth" title="校园认证">
      <el-form>
        <el-form-item label="学校">
          <el-select v-model="form.school" filterable clearable placeholder="选择学校">
            <el-option v-for="school in schools" :key="school.id" :label="school.name" :value="school.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="example@school.com" />
        </el-form-item>
        <el-form-item label="验证码">
          <el-input v-model="form.verificationCode" clearable style="width: 60%; margin-right: 5%;"/>
          <el-button type="button" class="btn4" 
          :disabled="countdown > 0" 
          style="width: 15%; margin-left: 60%; margin-top: 2%; height: 90%; font-size: 100%;" 
          @click="sendVerificationCode">
          {{ countdown > 0 ? `重新发送(${countdown})` : '发送验证码' }}
          </el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        
          <el-button @click="closeDialog('schoolAuth')" style="padding: 1px 20px;">取消</el-button>
          <el-button @click="confirmSchoolAuth" class="btn4">确认</el-button>
        
      </template>
    </el-dialog>

    <!-- 弹窗 3：密码修改 -->
    <el-dialog v-model="dialogs.passwordChange" title="密码修改">
    <el-form :model="form" :rules="rules0" ref="registerFormRef">
      <!-- 旧密码 -->
      <el-form-item label="旧密码&emsp;&emsp;">
        <el-input v-model="form.password" type="password" show-password placeholder="请输入旧密码" />
      </el-form-item>

      <!-- 新密码 -->
      <el-form-item label="新密码&emsp;&emsp;">
        <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码" />
      </el-form-item>
       <!-- 确认新密码 -->
      <el-form-item label="确认新密码">
        <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeDialog('passwordChange')" style="padding: 1px 20px;">取消</el-button>
      <el-button class="btn4" @click="confirmPasswordChange">确认</el-button>
    </template>
  </el-dialog>

    <!-- 弹窗 4：创建小组 -->
    <el-dialog v-model="dialogs.createGroup" title="创建小组">
  <el-form :model="newGroup" :rules="rules1" ref="createGroupRef" label-width="80px">
    <el-form-item label="小组名称" prop="groupName">
      <el-input
        v-model="newGroup.groupName"
        placeholder="请输入小组名称"
        maxlength="50"
        show-word-limit
      ></el-input>
    </el-form-item>
    <el-form-item label="小组描述" prop="description">
      <el-input
        type="textarea"
        v-model="newGroup.description"
        placeholder="请输入小组描述"
        maxlength="200"
        show-word-limit
        rows="4"
      ></el-input>
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="closeDialog('createGroup')" style="padding: 1px 20px;">取消</el-button>
    <el-button class="btn4" @click="confirmCreateGroup">确认添加</el-button>
  </template>
</el-dialog>

    <!-- 弹窗 5：加入小组 -->
    <el-dialog v-model="dialogs.joinGroup" title="加入小组" width="400px">
  <el-form :model="joinForm" :rules="rules2" ref="joinGroupRef" label-width="100px">
    <!-- 小组ID -->
    <el-form-item label="小组ID" prop="groupID">
      <el-input v-model="joinForm.groupID" placeholder="请输入小组ID" />
    </el-form-item>

    <!-- 申请描述 -->
    <el-form-item label="申请描述" prop="description">
      <el-input
        v-model="joinForm.description"
        type="textarea"
        placeholder="请输入申请描述"
        maxlength="100"
        show-word-limit
      />
    </el-form-item>
  </el-form>

  <template #footer>
    <el-button @click="closeDialog('joinGroup')" style="padding: 1px 20px;">取消</el-button>
    <el-button class="btn4" @click="confirmJoinGroup">确认加入</el-button>
  </template>
</el-dialog>
</el-container>

</template>

  
<script>
import { ref, computed, reactive, onMounted, watch } from "vue";
import { ElMessage,ElMessageBox } from "element-plus";
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { 
  getUserInfo, 
  changeName, 
  uploadUserHeadPortrait, 
  changePassword, 
  getGroupInfo, 
  changeGroupState, 
  getDutyByAccount, 
  createGroup,
  applyToGroup,
  getEmailCode,
  checkEmail,
} from '@/api/api_personal.js';
import {quitGroup} from '@/api/api.js';
// 目前头像接口还没做好

export default {
  setup() {
    const store = useStore();
    const userInfo = reactive({
      id:computed(() =>  store.state.userId || '默认用户ID'), 
      password: 'testPassword123',
      is_superuser: false,
      email: "test@example.com",
      phone_number: "12345678901",
      nickname: "默认昵称",
      head_portrait: '',
      university:"学校未验证",
    });

    const router = useRouter();
  
    const dialogs = ref({ 
      editInfo: false, 
      schoolAuth: false, 
      passwordChange: false,
      createGroup: false, 
      joinGroup: false,
    });

    const form = ref({ 
      //修改昵称
      nickname: "", 
      avatarUrl: "",
      imageFile: null,
      //校园认证
      school: "", 
      email: "", 
      //修改密码
      password: "",
      newPassword: "", 
      confirmPassword: "", 
      phone: "", 
      verificationCode: "",
    });

    const joinForm = ref({
      groupID: "",
      description: "申请加入小组~",
    });
    //规则：修改密码
    const rules0 = {
        password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
        newPassword: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '检验密码不能为空', trigger: 'blur' }],
        };
    // 规则：创建小组，申请加入小组
    const rules1 = ref({
      groupName: [
        { required: true, message: '请输入小组名称', trigger: 'blur' },
        { min: 3, message: '小组名称至少需要 3 个字符', trigger: 'blur' },
      ],
      description: [
        { required: true, message: '请输入小组描述', trigger: 'blur' },
        { min: 10, message: '小组描述至少需要 10 个字符', trigger: 'blur' },
      ],
    });
    const rules2 = ref({
      groupID: [
        { required: true, message: '请输入小组ID', trigger: 'blur' },
        { pattern: /^[0-9]+$/, message: '小组ID必须为数字', trigger: 'blur' },
      ],
      description: [
        { required: true, message: '请输入申请描述', trigger: 'blur' },
        { min: 10, message: '申请描述至少需要 10 个字符', trigger: 'blur' },
      ],
    });

    // 表单引用
    const createGroupRef = ref(null);
    const joinGroupRef = ref(null);

    
     // 获取用户信息
     const fetchUserInfo = async () => {
      try {
        const response = await getUserInfo( {account: userInfo.id} );
        if (response.data.success) { // 更新 userInfo 数据
          userInfo.password = response.data.data.password;
          userInfo.email = response.data.data.email;
          userInfo.phone_number = response.data.data.phoneNumber;
          userInfo.nickname = response.data.data.nickname;
          userInfo.head_portrait = response.data.data.headPortrait;
          userInfo.university = response.data.data.school||'学校未验证';
        } else {
          console.error('获取用户信息失败:', response.data.message);
        }
      } catch (error) {
        console.error('获取用户信息时出现错误:', error);
      }
    };

    const groups = reactive([]);
    const filteredGroups = reactive([]);
    const searchQuery = ref("");

    const fetchGroupInfo = async () => {
      try {
        const response = await getGroupInfo({
          account: userInfo.id 
        });
        if (response.data.success) {
          if( response.data.data === null ) {
            return;
          }
          const formattedGroups = response.data.data.map ((group) => ({
            id: group.groupId, 
            name: group.groupName, 
            status:group.state?"活跃":"不活跃",
            createdAt: group.createTime, 
            avatar: group.headPortrait, 
            leaderAccount: group.leaderAccount, 
            leader:group.leaderNickname,
            members: group.headcount, 
            role: group.leaderAccount==userInfo.id?"组长":"组员",
          }));
          groups.splice(0, groups.length, ...formattedGroups);
          initGroupColors();  // 初始化小组颜色
        } else {
          ElMessage.error(`获取小组信息失败: ${response.data.message}`);
          console.error(`获取小组信息失败: ${response.data.message}`);
        }
      } catch (error) {
        ElMessage.error("获取小组信息失败，请稍后再试！");
        console.error("获取小组信息失败，请稍后再试！");
      }
    };

    // 监听 groups 和 searchQuery（调用过滤逻辑），动态更新 filteredGroups
    watch(
      () => groups,
      (group) => {
        filteredGroups.splice(0, filteredGroups.length, ...group);
        searchQuery.value=""; // 清空搜索框
      },
      { immediate: true, deep: true } // 立即执行，深度监听
    );

    watch(
      () => searchQuery.value,
      (query) => {
        if (groups.length > 0) {
          // 根据搜索关键词过滤
          //console.log("searchQuery updated", query);
          filteredGroups.splice(
            0,
            filteredGroups.length,
            ...groups.filter((group) => group?.name?.includes(query))
          );
        } else {
          filteredGroups.splice(0, filteredGroups.length);
        }
      }
    );

    const tasks = reactive([]);
    const groupColors = reactive({});  // 存储小组的颜色

    // 用于生成莫代尔色系的颜色（色调范围内的随机颜色）
    function generateGroupColor(groupId) {
      const colorPool = [
        '#6c757d', '#17a2b8', '#28a745', '#fd7e14', '#ffc107', '#dc3545', // 自定义颜色列表
        '#6610f2', '#e83e8c', '#20c997', '#f8f9fa', '#343a40'
      ];
      // 使用 groupId 来确保颜色的稳定性
      const index = groupId % colorPool.length;
      return colorPool[index];
    }
    // 初始化每个小组的颜色
    const initGroupColors = () => {
      groups.forEach(group => {
        if (!groupColors[group.id]) {
          groupColors[group.id] = generateGroupColor(group.id);
        }
      });
    };

    const fetchDutyTask = async () => {
      try {
        const response = await getDutyByAccount({
          "account": userInfo.id 
        });
        
        if (response.data.success) {
          //console.log("打印这句话",groups); 
          const formattedTasks = response.data.data.map ((task) => {
            // 查找对应小组的名称
            //console.log(task.groupId);
            const group = groups.find((group) => group.id === task.groupId);
                        //console.log(group.name||"用户目前不属于任务发布小组");
            return {
              content: task.taskName, 
              groupId: task.groupId,
              groupName: group ? group.name : '未找到小组',  
              timestamp: task.deadline || '未知时间', 
              color: group ? groupColors[group.id] : '#6c757d'  // 使用小组对应的颜色
            };
          });
          
          // 按时间排序（越早的任务越靠前）
          formattedTasks.sort((a, b) => {
            const dateA = new Date(a.timestamp);
            const dateB = new Date(b.timestamp);
            return dateA - dateB; // 升序：时间越小越靠前
          });
          tasks.splice(0, tasks.length, ...formattedTasks);
          //console.log('Updated tasks:', tasks);
        } else {
          ElMessage.error(`获取 用户负责的任务 信息失败: ${response.data.message}`);
          console.error(`获取 用户负责的任务 信息失败: ${response.data.message}`);
        }
      } catch (error) {
        ElMessage.error("获取 用户负责的任务 信息失败，请稍后再试！");
        console.error("获取 用户负责的任务 信息失败，请稍后再试！",error);
      }
    };


    // 创建小组表单数据
    const newGroup = ref({
      groupName: '',
      description: '',
    });


    // 创建小组函数
    const confirmCreateGroup = async () => {
      try {
        const response = await createGroup({
          groupName: newGroup.value.groupName,
          description: newGroup.value.description,
          leaderAccount: userInfo.id
        });

        if (response.data.success) {
          ElMessage.success('小组创建成功！');
          closeDialog('createGroup');
          fetchGroupInfo();
        } else {
          ElMessage.error(`创建失败：${response.data.message}`);
        }
      } catch (error) {
        if (error.name === 'ValidationError') {
          ElMessage.warning('请完善表单信息！');
        } else {
          ElMessage.error('创建失败，请稍后再试！');
        }
        console.error(error);
      }
    };

    onMounted(async () => {
      fetchUserInfo();
      await fetchGroupInfo();
      await fetchDutyTask();
      // initialize();
    });

    // 提交加入小组申请
    const confirmJoinGroup = async () => {
      try {
        await joinGroupRef.value.validate();
        const payload = {
          groupId: joinForm.value.groupID,
          account: userInfo.id, // 假设 userInfo 是用户信息对象
          description: joinForm.value.description,
          applicantAccount: userInfo.id,
        };

        const response = await applyToGroup(payload);

        if (response.data.success) {
          ElMessage.success('申请已提交成功！请等待审批。');
          closeDialog('joinGroup');
        } else {
          ElMessage.error(`提交失败：${response.data.message}`);
        }
      } catch (error) {
        if (error.name === 'ValidationError') {
          ElMessage.warning('请完善表单信息！');
        } else {
          ElMessage.error('提交失败，请稍后再试！');
        }
        console.error(error);
      }
    };


    // 所有弹窗共用函数————打开弹窗
    const openDialog = (dialogName) => {
      if (dialogName === 'editInfo') {
        //console.log('打开编辑信息弹窗');
        form.value.nickname = userInfo.nickname || ''; // 将当前昵称赋值到表单中
        //form.value.avatarUrl = userInfo.head_portrait || ''; // 将当前头像赋值到表单中
        //console.log(form.value.nickname,form.value.avatarUrl);
        dialogs.value.editInfo = true;
      }
      else {
        dialogs.value[dialogName] = true;
      }
    };
    // 所有弹窗共用函数————关闭弹窗
    const closeDialog = (dialogName) => {
      dialogs.value[dialogName] = false;
      if (dialogName === 'createGroup') { // 清空表单
        // 延迟执行表单重置，避免在表单验证中被触发报错
        setTimeout(() => {
          newGroup.value.groupName = '';
          newGroup.value.description = '';
          createGroupRef.value?.resetFields();
        }, 0);
      }
      else if (dialogName === 'joinGroup') { // 清空表单
        joinForm.value.groupID = '';
        joinForm.value.description = '';
        joinGroupRef.value?.resetFields();
      }
    };
//修改信息
    // 修改昵称接口函数
    const changeUserNickname = async () => {
      const params = {
        "account": userInfo.id,
        "nickname": form.value.nickname,
      };
      return await changeName(params);
    };
    // 修改头像
    const fileList = ref([]);
   // const carPicture='' //定义一个值来接收base64的字符串 
    const changeCarPicture = (file) =>{
      
        // 图片转成base64,显示在页面上
      let reader = new FileReader();
      reader.readAsDataURL(file.raw);//FileReader异步读取文件的内容。
      reader.onload = () => {//FileReader读取完成，FileReader的onload事件会被触发，执行括号内代码（回调函数）
          let _base64 = reader.result;
          let BASE64 = _base64.split(",");
          form.value.avatarUrl= BASE64[1];  //赋值
          form.value.imageFile = file.raw;
        };
      
    };
    // const changeUserAvatar = async () => {
      
    //   // const params = {
    //   //   account: userInfo.id,
    //   //   image: form.value.avatarUrl, // 假设传递的是头像 URL 或文件
    //   // };
    //   return await uploadUserHeadPortrait(formData);
    // };
   const  handleExceed = () =>{
      //这个是文件超出个数限制时，给出提示
      ElMessage.error(`当前限制选择 1 个文件`);
    };
  const removeCarPicture = () => {
      form.value.avatarUrl = '';
      form.value.imageFile = null;
  };
  //预览没法实现
    // 上传头像前的检查
    const beforeAvatarUpload = (file,fileList) => {
      //console.log("检查头像",file);
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isImage) {
        ElMessage.error('上传头像图片只能是图片格式');
      }
      if (!isLt2M) {
        ElMessage.error('上传头像图片大小不能超过 2MB');
      }
       if(fileList.length>1){
        ElMessage.error('只能上传一张头像');
        return false;
      }
      return isImage && isLt2M ;
    };
    // // 头像上传成功的处理（？
    // const handleAvatarSuccess = (response) => {//, file
    //   if (response.success) {
    //     form.value.avatarUrl = response.data; // 假设返回的头像 URL 存储在 response.data
    //      ElMessage.success('头像上传成功');
    //   } else {
    //      ElMessage.error(`头像上传失败: ${response.message}`);
    //   }
    // };
    // 提交信息修改（昵称 + 头像）
    const confirmEditInfo = async () => {
      if (form.value.nickname !== userInfo.nickname) {
        try {
          const nicknameResponse = await changeUserNickname();
          if (nicknameResponse.data.success) {
            ElMessage.success('昵称修改成功');
            userInfo.nickname= form.value.nickname;
          } else {
            ElMessage.error(`'昵称修改失败: ${nicknameResponse.data.message}`);
          }
        } catch (error) {
         ElMessage.error('昵称修改失败',error);
        }
      }      
        //console.log("修改头像",userInfo.head_portrait);
        if (form.value.avatarUrl !== '') {
          const validiant=await beforeAvatarUpload(form.value.imageFile,fileList);
          if(!validiant){
            return;
          }
          // 查看 FormData 的所有内容
          const formData = new FormData();
          formData.append('account', userInfo.id);
          formData.append('image', form.value.imageFile); // 传递的是头像 URL 或文件
          // 查看 FormData 的所有内容（可选）
            // for (const [key, value] of formData.entries()) {
            //   console.log(`${key}:`, value);
            // }
          try{
            const avatarResponse = await uploadUserHeadPortrait(formData );
            if (avatarResponse.data.success) {
              ElMessage.success('头像修改成功');
              userInfo.head_portrait=form.value.avatarUrl;
            } else {
              ElMessage.error(`头像修改失败: ${avatarResponse.data.message}`);
            }
          }catch (error) {
            ElMessage.error('头像修改失败,请稍后再试');
            console.error(error);
          }
        }
        closeDialog('editInfo');      
    };

    //校园验证数据
    const schools= reactive([
      {id: 1,name: "同济大学"},
     // {"上海交通大学"},
      {id:2,name: "复旦大学"},
      {id:3,name: "华东师范大学"},
    ]    
    )
    const countdown = ref(0);
    const countdownTimer = ref(null); //倒计时定时器
    const startCountdown = () => {
      countdown.value = 60;
      countdownTimer.value = setInterval(() => {
        if (countdown.value > 0) {
          countdown.value--;
        } else {
          clearInterval(countdownTimer.value);
        }
      }, 1000);
    };
    // 发送验证码
    const sendVerificationCode = async () => {
      if (!form.value.email) {
        ElMessage.warning('请输入邮箱');
        return;
      }
      else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.value.email)) {
        ElMessage.warning('请输入正确格式的邮箱');
        return;
      }
      try {
        // 验证码接口待确认
        //console.log(form.value.email);
         const formData = new FormData();
        formData.append('email', form.value.email);
        const response = await getEmailCode(formData);
        if (response.data==="not school email") {
          ElMessage.error(`邮箱并非校园邮箱`);          
        } else {
          //console.log(response.data);
          ElMessage.success('验证码发送成功');
          startCountdown();
        }
      } catch (error) {
        console.error('发送验证码失败:', error);
        ElMessage.error('验证码发送失败');
      }
    };
    const confirmSchoolAuth = async() =>{
      try {
        const response = await checkEmail({
          "email": form.value.email,
          "account": userInfo.id,
          "code": form.value.verificationCode,
        });
        if (response.data.success) {
          if(response.data.data!=form.value.school){
            ElMessage.error('学校不匹配');
            return;
          }
           ElMessage.success("校园认证成功！");
           userInfo.university = form.value.school;
           closeDialog('schoolAuth');
           
        } else {
          ElMessage.error(response.data.message || '验证失败');
          }
      } catch (error) {
        ElMessage.error('请填写完整信息');
      }      
    };

    
    const confirmPasswordChange = async () => {
      // 检查旧密码是否为空
      if (!form.value.password?.trim()) {
        ElMessage.error('旧密码不能为空！');
        return;
      }
      //检查旧密码是否正确
      if(userInfo.password!=form.value.password){
        ElMessage.error('旧密码不正确！');
        return;
      }
      // 校验新密码是否为空
      if (!form.value.newPassword?.trim()) {
        ElMessage.error('新密码不能为空！');
        return;
      }
      // 校验密码格式
      const passwordPattern = /^(?!.*\s)(?=.*[A-Z])(?=.*[a-z])(?=.*\d|.*[._~!@#$^&*])[A-Za-z\d._~!@#$^&*]{8,}$/;
      if (!passwordPattern.test(form.value.newPassword)) {
        ElMessage.error(
          '新密码必须至少8位，不能包含空格，并且必须包含：大写字母、小写字母、数字、特殊字符（._~!@#$^&*）！'
        );
        return;
      }
      // 确认两次密码是否一致
      if (form.value.newPassword !== form.value.confirmPassword) {
        ElMessage.error('两次输入的密码不一致！');
        return;
      }
      // 调用修改密码的接口
      try {
        const response = await changePassword({
          account: userInfo.id, // 账号
          password: form.value.newPassword, // 新密码
        });
        //console.log(form.value.newPassword,response);
        if (response.data.success) {
          ElMessage.success('密码修改成功！');
          // 如果密码修改成功，可以关闭弹窗并重置表单
          dialogs.value.passwordChange = false;
          userInfo.password = form.value.newPassword;
        } else {
          ElMessage.error(`修改失败: ${response.data.message}`);
        }
      } catch (error) {
        ElMessage.error('修改密码失败，请稍后再试！');
      }
    };

    const confirmGroupInto = (group) => {
      ElMessage.info(`进入小组 ${group.name}`);
      router.push({
      path:'/DashBoard/Tasks',
      // query: {
      //       //GroupName: group.name,
      //       GroupId: group.id,
      //       UserId: userInfo.id,
      //       //GroupRole: group.role
      // }
       
      });
     store.commit('setGroupId', group.id);
    };


    // 切换小组状态
    const toggleGroupStatus = async (group) => {
      const newStatus = group.status === '活跃' ? '不活跃' : '活跃';  // 切换前端状态
      // const statusValue = newStatus === '活跃' ? 1 : 0;  // 对应后端的 0/1
      try {
        const response = await changeGroupState({ groupId: group.id });
        
        if (response.data.success) {
          // 后端状态切换成功，更新前端状态
          group.status = newStatus;
          ElMessage.success(`小组 "${group.name}" 的状态已切换为 "${newStatus}"`);
        } else {
          ElMessage.error(`小组状态切换失败: ${response.data.message}`);
        }
      } catch (error) {
        ElMessage.error("小组状态切换失败，请稍后再试！");
        console.error("小组状态切换失败，请稍后再试！");
      }
    };
    // 退出小组方法
    const confirmGroupExit = async (group) => {
  try {
    await ElMessageBox.confirm(
      `确定要退出小组 "${group.name}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    // 请求参数
    const paramQuitGroup = { 
      account: userInfo.id, 
      groupId: group.id 
    };
    
    // 调用退出小组的 API
    await quitGroup(paramQuitGroup);
    ElMessage.success('已成功退出小组');
    
    // 刷新小组信息
    fetchGroupInfo();

  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出小组失败:', error);
      ElMessage.error('退出小组出现问题，请稍后重试');
    } else {
      ElMessage.info('已取消操作');
    }
  }
};
    

    return {
      userInfo,
      dialogs, 
      form, 
      tasks,
      groups, 
      searchQuery, 
      filteredGroups, 
      newGroup,
      joinForm,
      rules0,
      rules1,
      rules2,
      createGroupRef,
      joinGroupRef,
      openDialog, 
      closeDialog, 
      confirmEditInfo, 
      //校园验证
      countdown,
      countdownTimer,
      startCountdown,
      sendVerificationCode,
      confirmSchoolAuth, 
      schools,
      confirmPasswordChange, 
      // 头像上传
      changeCarPicture,
      fileList,
      removeCarPicture,
      handleExceed,
     // handleAvatarSuccess,
      confirmGroupInto, 
      confirmCreateGroup, 
      confirmJoinGroup,
      toggleGroupStatus,
      confirmGroupExit 
    };
  },


  methods: {

    // 退出登录的方法
    confirmLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        // 用户点击确定后跳转到登录页面
        this.$store.commit('clearUserId'); 
        this.$router.push('/');

        this.$message({
          type: 'success',
          message: '已退出登录',
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消退出',
        });
      });
    },
    
  },



}; 
</script>
  
<style scoped>
/* 整个界面 */
.personal-page {
  height: 100vh; 
  width: 100%;
  background-color: #f5f5f5;
  padding: 10px;
}
/* 上部整体 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #d5e8d4;
  padding: 10px;
  border-radius: 8px 8px 8px 8px;
}
/* 下部整体 */
.todo-group {
  display: flex;
  margin-top: 10px;
  width: 97%;
  justify-content: space-between;
  align-items: center;
}
/* 上部内容：用户信息与功能按钮 */
.user-info-section {
  display: flex;
  justify-content: space-between;
  background-color: #d5e8d4;
  border-radius: 8px;
}
.user-info {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  border-radius: 8px;
}
.user-info img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}
.user-details {
  font-size: 14px;
  align-items: center;
  text-align:left;
}
.user-actions-g1{
  display: flex;
  align-items: center;
  flex-direction: column;
}
.user-actions-g2{
  display: flex;
  align-items: center;
  flex-direction: column;
}
.user-actions button {
  display: flex;
  margin-top:2vh;
  margin-bottom:0vh;
  padding: 8px 12px;
  width: 120px; 
  height: 31px; 
  font-size: 16px; 
  font-weight: 400; 
  letter-spacing: 0px;
  line-height: 23.17px; 
  color: rgba(0, 0, 0, 1);
  align-items: center;
  text-align: left;
  vertical-align: top; 
  opacity: 1; 
  border-radius: 25px; 
  background: rgba(248, 251, 246, 1); 
  border: 1px solid rgba(116, 142, 97, 1); 
  cursor: pointer; 
}
/* 下部内容：TODOLIST，我的小组 */
.todo-list, .my-group {
  margin: 0 10px;
  margin-top: 15px;
  padding: 10px;
  width: 100%; /* 固定宽度 */
  height: 100%; /* 固定高度 */
  background-color: #e9f2e7;
  border-radius: 8px;
  text-align:left;
}
.todo-list h2, .my-group h2 {
  font-size: 22px;
  margin-top: 1vh;
  margin-bottom: 3vh;
  text-align:left;
}
/* TODOLIST */
.todo-list-timeline {
  max-height: 53vh; 
  overflow-y: auto; 
  margin-bottom: 1vh;
}
/* 我的小组 */
.my-group-head{
  text-align:left;
}
.my-group-head h2{
  text-align:left;
  margin-right: 21vw;
}
.my-group-head button{
  display: flex;
  margin: 10px 10px 10px 10px;
  padding: 8px 12px;
  width: 120px; 
  height: 31px; 
  font-size: 16px; 
  font-weight: 400; 
  letter-spacing: 0px;
  line-height: 23.17px; 
  color: rgba(0, 0, 0, 1);
  align-items: center;
  vertical-align: top; 
  opacity: 1; 
  border-radius: 5px; 
  background: rgba(248, 251, 246, 1); 
  border: 1px solid rgba(116, 142, 97, 1); 
  cursor: pointer; 
}
.search-bar {
  margin-right: 3vw;
}
.group-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}
.my-group-table{
  max-height: 55vh; 
  overflow-y: auto; 
  margin-bottom: 2vh;
}
.create-group-dialog {
  width: 300px;
  padding: 20px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0.1);
}
.join-group-dialog {
  width: 300px;
  padding: 20px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0.1);
}


.avatar-upload {
  display: flex;
  align-items: center;
}
.avatar-preview {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 10px;
}
.avatar-upload-btn {
  display: inline-block;
}
</style>
  
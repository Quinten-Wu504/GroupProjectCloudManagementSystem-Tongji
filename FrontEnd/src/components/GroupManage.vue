<template>
  <el-container class="container" direction="vertical" >
    <p style="text-align: left; margin-top: 0; font-size: 0.8em">小组页面 / 小组管理</p>
    <el-divider style="margin: 0;"/>

    <!-- 小组信息和操作按钮 -->
    <div class="group-header">
      <span style="color:#576251; font-size: 1.2em;  font-weight: bold;">
        {{ groupInfo.groupName }}
      </span>

      <!-- 按钮组 -->
      <div class="button-group">
        <el-button @click="editDV = true;" class="btn3" :disabled="isButtonDisabled">
          <el-icon style="margin-right: 5px;"><Edit /></el-icon>
          编辑信息
        </el-button>

        <el-button @click="inviteUser" class="btn3" :disabled="isButtonDisabled">
          <el-icon style="margin-right: 5px;"><User /></el-icon>
          邀请用户
        </el-button>

        <el-popconfirm title="此操作无法撤回，您确定要解散该小组吗？" confirm-button-text="解散"
          cancel-button-text="取消" confirm-button-type="danger" width="200px" hide-icon
          @confirm="dissolveGroup">
          <template #reference>
            <el-button class="btn3" :disabled="isButtonDisabled">
              <el-icon style="margin-right: 5px;"><CircleClose /></el-icon>
              解散小组
            </el-button>
          </template>
        </el-popconfirm>

        <el-popconfirm title="此操作无法撤回，您确定要退出该小组吗？" confirm-button-text="退出"
          cancel-button-text="取消" confirm-button-type="danger" width="200px" hide-icon
          @confirm="quitTheGroup">
          <template #reference>
            <el-button class="btn3" :disabled="!isButtonDisabled">
              <el-icon style="margin-right: 5px;"><Close /></el-icon>
              退出小组
            </el-button>
          </template>
        </el-popconfirm>
      </div>
    </div>

    <!-- 小组简介 -->
    <p style="color: darkgray; margin: 10px 40px 20px 40px; text-align: left; font-size:0.9em">
      {{ groupInfo.description }}
    </p>
   
    <el-card style="height: 80vh;">
      <!-- 小组成员表格 -->
      <el-table :data="teamMembers" max-height="70vh"
        style="width: 100%; background-color: transparent !important;"
        :header-cell-style="{ 'text-align': 'center' }" :cell-style="{ 'text-align': 'center' }">

        <el-table-column prop="head_portrait" label="头像" width="90">
          <template #default="{ row }">
            <el-avatar :size="40" :src="'data:image/png;base64,'+ row.headPortrait" />
          </template>
        </el-table-column>
        
        <el-table-column prop="nickname" label="成员" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" />
        <el-table-column prop="enterTime" label="进组时间" sortable />

        <!-- 表格操作区 -->
        <el-table-column fixed="right" label="操作" min-width="150">
          <template #default="{ row }">
            <el-popconfirm title="此操作无法撤回，您确定要移除该组员吗？"
              confirm-button-text="移除" cancel-button-text="取消" @confirm="removeTheMember(row)"
              confirm-button-type="danger" width="200px" hide-icon>
              <template #reference>
                <el-button class="btn3" :disabled="isButtonDisabledColumn(row)">
                  <el-icon style="margin-right: 5px;"><Delete /></el-icon>
                  移除成员
                </el-button>
              </template>
            </el-popconfirm>

            <el-popconfirm title="此操作无法撤回，您确定要转让该小组吗？"
              confirm-button-text="转让" cancel-button-text="取消" confirm-button-type="danger"
              width="200px" hide-icon @confirm="transferMember(row)">
              <template #reference>
                <el-button class="btn3" :disabled="isButtonDisabledColumn(row)">
                  <el-icon style="margin-right: 5px;"><Switch /></el-icon>
                  转让小组
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

  
    <!-- 编辑小组信息对话框 -->
    <el-dialog title="编辑小组信息" v-model="editDV" center align-center draggable>
      <el-input v-model="editGroupName" placeholder="小组名" style="margin-bottom: 10px;" />
      <el-input v-model="editGroupDescription" type="textarea" rows="4" placeholder="请输入小组简介" />
      <template #footer>
        <el-button @click="editDV = false" style="padding: 1px 20px;">取消</el-button>
        <el-button class="btn4" @click="changeGroupInfo">保存</el-button>
      </template>
    </el-dialog>


    <!-- 邀请成员对话框 -->
    <el-dialog v-model="inviteUserDV" title="邀请用户" style="height: 500px;" center align-center draggable>
      <el-input v-model="searchQuery" placeholder="搜索用户ID或名字" style="margin-bottom: 1vh;" />
      <el-table :data="filteredUsers" :border="false" style="width: 100%;" max-height=300px highlight-current-row
        @current-change="handleSelectionChange"
        :header-cell-style="{ 'text-align': 'center' }" :cell-style="{ 'text-align': 'center' }">
        
        <el-table-column prop="head_portrait" label="头像" width="90">
          <template #default="{ row }">
            <el-avatar :size="40" :src="'data:image/png;base64,'+ row.headPortrait" />
          </template>
        </el-table-column>

        <el-table-column label="用户id" prop="account" />
        <el-table-column label="用户昵称" prop="nickname" />
      </el-table>

      <template #footer>
          <el-button @click="inviteUserDV = false" style="padding: 1px 20px;">取消</el-button>
          <el-button class="btn4" @click="getSelectedData"> 邀请 </el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Edit, User, ChatLineSquare } from "@element-plus/icons-vue";
import router from "@/router";
import { useStore } from 'vuex';
import { removeMember, applyToGroup, deleteGroup, transferLeader, changeGroup, quitGroup, getAllUsers, getAllGroupMembers, getGroupDescriptionById } from "@/api/api";
  
export default {
  setup() {

    //后续要更新成动态读取        
    const store = useStore();

    const groupId = computed(() => store.state.groupId || '默认组ID');
    const postAccount = computed(() => store.state.userId || "默认用户ID");
    const currentUserAccount = postAccount.value;

    const editDV = ref(false);
    const inviteUserDV = ref(false);

    const selectedRowKeys = ref([]);
    const selectedRowData = ref(null);

    const editGroupName = ref("");
    const editGroupDescription = ref("");

    // 获取小组信息，测试成功
    const groupInfo = ref({});

    //获取所有用户数据，测试成功，头像待完善
    const userList = ref([]);

    // 获取小组成员数据，测试成功，头像待完善
    const teamMembers = ref([]);
    
    //用户搜索
    const searchQuery = ref("");
    
    
    const fetchGroupInfo = async () => {
      try {
        const paramGetGroupDescriptionById = { 
          groupId: groupId.value 
        };
        const response = await getGroupDescriptionById(paramGetGroupDescriptionById);
        if (response.data.success) {
          groupInfo.value = response.data.data;
          editGroupName.value = response.data.data.groupName;
          editGroupDescription.value = response.data.data.description;
        }
        else {
          console.log(response.data.message);
          ElMessage.error('获取小组信息失败');
        }
      } catch (error) {
        console.log('获取小组信息失败:', error);
        ElMessage.error('获取小组信息失败，请稍后重试');
      }
    };


    const fetchMembers = async () => {
      try {
        const paramGetAllGroupMembers = { 
          groupId: groupId.value 
        };
        const response = await getAllGroupMembers(paramGetAllGroupMembers);
        if (response.data.success) {
          teamMembers.value = response.data.data;
          teamMembers.value.forEach(item => {
            item.role = (item.role === '1' ? "组员" : "组长");
          });
        }
        else {
          console.error('获取小组用户信息失败:', response.data.message);
        }
      } catch (error) {
        console.error('获取小组用户信息失败:', error);
      }
    };

    onMounted(async () => {
      await fetchGroupInfo();
      await fetchMembers();
    });


    //修改小组信息，测试成功
    const changeGroupInfo = async () => {
      if(!editGroupName.value || !editGroupDescription.value) {
        ElMessage.error('请填写完整信息');
        return;
      }
      
      try {
        await changeGroup({
          groupId: groupId.value,
          groupName: editGroupName.value,
          description: editGroupDescription.value
        });
        await fetchGroupInfo();
        ElMessage.success('小组信息修改成功');
        editDV.value = false;
      } catch (error) {
        console.log('修改小组信息失败:', error);
        ElMessage.error('修改小组信息出现问题，请稍后重试');
      }
    };


    const fetchUserInfo = async () => {
      try {
        const paramGetAllUsers = { 
          groupId: groupId.value
        };
        const response = await getAllUsers(paramGetAllUsers);
        if (response.data.success) {
          userList.value = response.data.data;
        }
        else {
          console.log(response.data.message);
          ElMessage.error('获取全部信息失败', response.data.message);
        }
      } catch (error) {
        console.log('获取全部用户信息失败:', error);
        ElMessage.error('获取全部用户信息失败，请稍后重试');
      }
    };


    const filteredUsers = computed(() => {
      return userList.value.filter((user) =>
        (user.nickname?.toString().includes(searchQuery.value) || user.account?.toString().includes(searchQuery.value))
      );
    });


    // 邀请用户弹窗启动按钮
    const inviteUser = async () => {
      inviteUserDV.value = true;
      await fetchUserInfo();
    };


    // 表格内单选
    const handleSelectionChange = (val) => {
      selectedRowKeys.value = val;
      if (val) {
        selectedRowData.value = val;
      } else {
        selectedRowData.value = null;
      }
    };


    // 表格内发送邀请，测试成功
    const getSelectedData = async () => {
      if (!selectedRowData.value) {
        ElMessage.error('请先选择用户');
        return;
      }
      try {
        await applyToGroup({
          groupId: groupId.value,
          account: selectedRowData.value.account,
          description: null,
          applicantAccount: postAccount.value,
        });

        ElMessage.success('已向该用户发送邀请');
      } catch (error) {
        console.log('邀请发送失败:', error);
        ElMessage.error('邀请发送失败，请稍后重试');
      }
      inviteUserDV.value = false;
    };

    
    // 解散小组，已完成，待测试（不敢测）
    const dissolveGroup = async () => {
      try {
        const paramDeleteGroup = {
          account: postAccount.value,
          groupId: groupId.value
        };
        await deleteGroup(paramDeleteGroup);
        ElMessage.success('小组已解散');
        router.push('/PersonalPage');
      } catch (error) {
        console.error('解散小组失败:', error);
        ElMessage.error('解散小组出现问题，请稍后重试');
      }
    };


    // 移除成员，测试成功
    const removeTheMember = async (member) => {
      try {
        const paramRemoveMember = { 
          account: member.account, 
          groupId: groupId.value
        };
        await removeMember(paramRemoveMember);
        await fetchMembers();
        ElMessage.success('该成员已移除');
      } catch (error) {
        console.error('移除成员失败:', error);
        ElMessage.error('移除成员出现问题，请稍后重试');
      }
    };


    // 转让小组，测试成功（本身已经测试成功了，但是后期要考虑导航栏是否要一起变动）
    const transferMember = async (member) => {
      try {
        await transferLeader({
          presentLeaderAccount: postAccount.value,
          nextLeaderAccount: member.account,
          groupId: groupId.value
        });
        await fetchGroupInfo();
        await fetchMembers();
        const tempRole = postAccount.value == member.account ? "组长" : "组员";
        store.commit('setUserRole', tempRole);
        ElMessage.success('小组已转让');
      } catch (error) {
        console.error('转让小组失败:', error);
        ElMessage.error('转让小组出现问题，请稍后重试');
      }
    };

    // 退出小组，已完成，待测试（不敢测）
    const quitTheGroup = async () => {
      try {
        const paramQuitGroup = { 
          account: postAccount.value, 
          groupId: groupId.value 
        };
        await quitGroup(paramQuitGroup);
        ElMessage.success('已成功退出小组');
        router.push('/PersonalPage');
      } catch (error) {
        console.error('退出小组失败:', error);
        ElMessage.error('退出小组出现问题，请稍后重试');
      }
    };

    return {
      currentUserAccount,

      userList,

      groupInfo,
      teamMembers,

      editDV,
      editGroupName,
      editGroupDescription,
      changeGroupInfo,

      inviteUser,
      inviteUserDV,

      searchQuery,
      filteredUsers,
      handleSelectionChange,
      getSelectedData,

      dissolveGroup,
      removeTheMember,
      transferMember,
      quitTheGroup,
      
      Edit,
      User,
      ChatLineSquare,
    };
  },

  computed: {
    isButtonDisabled() {
      return (this.groupInfo.leaderAccount && this.groupInfo.leaderAccount !== this.currentUserAccount);
    },
    isButtonDisabledColumn() {
      return (item) => {
        return (item.role === "组长" || (this.groupInfo.leaderAccount && this.groupInfo.leaderAccount !== this.currentUserAccount));
      };
    },
  },
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
  background-color: rgba(248, 251, 246, 1);
  padding: 20px;
   height: 100vh; 
  width: 100%;
}

.group-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 40px 0px 40px;
}

.button-group {
  display: flex;
  gap: 10px; 
}

.dialog-footer {
  text-align: right;
}
</style>



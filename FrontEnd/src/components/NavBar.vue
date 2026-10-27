<template>
	<el-container direction="vertical" style="background-color: rgb(213, 223, 205); width: 17%;" type="flex">
        <el-row style="padding: 30px 0px 20px 0px; flex:20%">
            <el-col :span="12"  @click="openDraw()"
                style="display: flex; align-items: center; justify-content: center; cursor: pointer;">
                <el-badge :value="totalValue" :max="99" :show-zero="false"  > 
                    <el-avatar :size="60" :src="'data:image/png;base64,'+ user.avatar" />
                </el-badge>
            </el-col>

            <el-col :span="12" style="text-align: left; margin: auto 0px">
                <p style="font-size:18px ; margin: 0px;"> 
                    {{user.name}} 
                </p>
                <p style="color:darkgray ;font-size:14px;margin: 0px; margin-top: 10px;"> 
                    @{{user.id}}
                </p >
            </el-col>
        </el-row>

        <div style="margin-bottom: 20%;flex:5%">
            <p style="font-size:14px" >
                {{ group.name }}&emsp;|&emsp;{{ group.role }}
            </p >
        </div>

        <div style="flex:65%">
            <el-menu :default-active="activeIndex" @select="handleSelect" router>
                <el-menu-item
                  v-for="(item, index) in NavList" :key="index" :index="item.url"
                  style="background-color:rgb(213, 223, 205); font-size: 16px; padding: 0px 10px;"
                >
                    <div style="padding: 0px 25px;">
                        <el-icon >
                            <component :is="item.icon" />
                        </el-icon>
                        <span>{{ item.title }}</span>
                    </div>
                </el-menu-item>
            </el-menu>
        </div>

        <div class="el-menu-item" style="padding: 30px; margin-top: auto;flex:10%" @click="backPerson">
            <el-icon style="margin-right: 5px;"><House /></el-icon>
            <span>返回个人页面</span>
        </div>

        <el-drawer v-model="drawer" title="消息管理"  size="25%"
            style="background-color: rgba(248, 251, 246, 1); ">

            <div style="background-color: #fff; border-radius: 5px; border: 1px #9BB588 solid;
                height: 30%; overflow: hidden;" >
                <p style="text-align: left; font-weight: bold; background-color: #9BB588; color:#fff; 
                    margin: 0px; padding: 5px 20px;" >
                    任务DDL提醒：
                </p>

                <el-scrollbar style="height: 90%;">
                    <div v-for="(iteminfo, index) in ddlWarnInfo" :key="index">
                        <div style="text-align: left; font-size:14px; padding: 15px 10px 5px 20px;">
                            <p style="font-weight:bold; color:#576251; margin: 0px;">
                                    {{ iteminfo.groupName }}的任务：{{ iteminfo.taskName }} 
                                </p>
                                <div style="margin-top: 5px;">
                                    <span>将于</span>
                                    <span style="font-size:16px; font-weight:bold; color:#f56c6c;">
                                        {{ iteminfo.deadline }}
                                    </span>
                                    <span>截止，请尽快完成！</span>
                                </div>
                        </div>
                        <el-divider style="margin: 0px; " />
                    </div>
                </el-scrollbar>
            </div>

            <div style="background-color: #fff; border-radius: 5px; border: 1px #9BB588 solid; 
                margin-top: 8%; overflow: hidden; height: 30%;" >
                <p style="text-align: left; font-weight: bold; background-color: #9BB588; color:#fff; 
                    margin: 0px; padding: 5px 20px;" >
                    审批进组申请：
                </p>

                <el-scrollbar style="height: 90%;">
                    <div v-for="(iteminfo, index) in applyInfo" :key="index" style="padding: 10px 10px 0px 10px; ">
                        <el-row>
                            <el-col :span="14" style="text-align: left;">
                                <p style="font-size:14px; margin: 0px;">
                                    用户
                                    <b>{{ iteminfo.applicantName }}</b><br>
                                    申请加入小组：
                                    <b>{{ iteminfo.groupName }}</b>
                                </p>

                                <p style="margin: 10px 0px 10px 0px; font-size: 12px; color: darkgray;">
                                    {{ iteminfo.applicationTime }}
                                </p>
                            </el-col>

                            <el-col :span="10">
                                <div v-if="iteminfo.applicationStatus === 0">
                                    <el-button class="btn4"  style="margin-left: 0px !important; padding: 5px 10px !important;" @click="updateApplication(iteminfo.account, iteminfo.groupId, 1)">
                                        同意
                                    </el-button>
                                    <el-button class="btn3"  style="margin-left: 0px !important; padding: 5px 10px !important;" @click="updateApplication(iteminfo.account, iteminfo.groupId, 2)">
                                        拒绝
                                    </el-button>
                                </div>
                                
                                <div v-else-if="iteminfo.applicationStatus === 1">
                                    <p style="text-align: center; font-size: 14px; color: darkgray;">
                                        已同意
                                    </p>
                                </div>

                                <div v-else>
                                    <p style="align-items: center; text-align: center;  font-size: 14px; color: darkgray;">
                                        已拒绝
                                    </p>
                                </div>
                            </el-col>
                        </el-row>
                    <el-divider style="margin: 0px; margin-top: 5px;" />
                    </div>
                </el-scrollbar>
            </div>

            <div style="background-color: #fff; border-radius: 5px; border: 1px #9BB588 solid; 
                margin-top: 8%; overflow: hidden; height: 30%;" >
                <p style="text-align: left; font-weight: bold; background-color: #9BB588; color:#fff; 
                    margin: 0px; padding: 5px 20px;" >
                    邀请加入小组：
                </p>

                <el-scrollbar style="height: 90%;">
                    <div v-for="(iteminfo, index) in inviteInfo" :key="index" style="padding: 10px 10px 0px 10px; ">
                        <el-row>
                            <el-col :span="18" style="text-align: left;">
                                <p style="font-size:14px; margin: 0px;">
                                    用户
                                    <b>{{ iteminfo.applicantName }}</b><br>
                                    邀请您加入小组：
                                    <b>{{ iteminfo.groupName }}</b>
                                </p>

                                <p style="margin: 10px 0px 10px 0px; font-size: 12px; color: darkgray;">
                                    {{ iteminfo.applicationTime }}
                                </p>
                            </el-col>

                            <el-col :span="6">
                                <div v-if="iteminfo.applicationStatus === 0">
                                    <el-button class="btn4" style="margin-left: 0px !important; padding: 5px 10px !important;" @click="updateApplication(iteminfo.account, iteminfo.groupId, 1)">
                                        同意
                                    </el-button>
                                    <el-button class="btn3" style="margin-left: 0px !important; padding: 5px 10px !important" @click="updateApplication(iteminfo.account, iteminfo.groupId, 2)">
                                        拒绝
                                    </el-button>
                                </div>
                                
                                <div v-else-if="iteminfo.applicationStatus === 1">
                                    <p style="text-align: center; font-size: 14px; color: darkgray;">
                                        已同意
                                    </p>
                                </div>

                                <div v-else>
                                    <p style="align-items: center; text-align: center;  font-size: 14px; color: darkgray;">
                                        已拒绝
                                    </p>
                                </div>
                            </el-col>
                        </el-row>
                    <el-divider style="margin: 0px; margin-top: 5px;" />
                    </div>
                </el-scrollbar>
            </div>
            
        </el-drawer>
    </el-container>
</template>

<script setup>
import { reactive, ref, watch ,onMounted ,computed} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Calendar, UserFilled, Files, ChatLineSquare } from '@element-plus/icons-vue'
import { getGroupApplication ,getUserInvitedInfo, updateApplicationStatus, sendDeadlineInfo } from '@/api/api'
import { getUserInfo, getGroupInfo } from '@/api/api_allneed.js'
import { useStore } from 'vuex';
const drawer = ref(false)
const store = useStore();
const router = useRouter();
const route = useRoute();

const user = reactive({
  name: "默认用户名",
  id: computed(() => store.state.userId || "默认用户名"),
  avatar: "",
});

//后续要更新成动态读取
const group = reactive({
  name:  "默认组名",
  id: computed(() => store.state.groupId || '默认组ID'),
  role: computed(() => store.state.userRole || '默认角色'),
});

// 定义三种类型的消息数量
const messageCount = reactive({
  apply: 0,
  invite: 0,
  ddl: 0,
});

// 计算总和
const totalValue = computed(() => messageCount.apply + messageCount.invite + messageCount.ddl);

//获取别人的申请
const applyInfo = ref([]);

//获取别人的邀请
const inviteInfo = ref([]);

//获取消息提示
const ddlWarnInfo = ref([]);


const fetchGroup = async ()=> {
  try {
    const response = await getGroupInfo({ "groupId": group.id });
    if (response.data.success) {
      group.name = response.data.data.groupName;
      let Groupleader = response.data.data.leaderAccount;
      const tempRole = Groupleader == user.id ? "组长" : "组员";
      store.commit('setUserRole', tempRole);
    } else {
      throw new Error(response.data.message);
    }
  } catch (error) {
    console.error("获取组信息失败:", error);
  }
};

const fetchUser = async () => {
  try {
    const response = await getUserInfo({ "account": user.id });
    if (response.data.success) {
      user.name = response.data.data.nickname;
      user.avatar = response.data.data.headPortrait;
    } else {
      throw new Error(response.data.message);
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    }
};

const fetchGroupApplication = async() => {
  try {
    const paramGetGroupApplication = {
        account: user.id,
    };
    const response = await getGroupApplication(paramGetGroupApplication);

    if (response.data.success) {
        applyInfo.value = response.data.data;
        messageCount.apply = applyInfo.value.filter(item => item.applicationStatus === 0).length;
    } else {
      console.error(response.data.message);
    }
  } catch (error) {
    console.error('获取当前申请消息列表失败，请稍后重试', error);
  }
}


const fetchGroupInvite = async() => {
  try {
    const paramGetGroupinvite = {
        account: user.id,
    };
    const response = await getUserInvitedInfo(paramGetGroupinvite);

    if (response.data.success) {
        inviteInfo.value = response.data.data;
        messageCount.invite = inviteInfo.value.filter(item => item.applicationStatus === 0).length;
    } else {
      console.error(response.data.message);
    }
  } catch (error) {
    console.error('获取当前邀请消息列表失败，请稍后重试', error);
  }
}

const fetchDDL = async() => {
  try {
    const paramDDL = {
        account: user.id,
    };

    const response = await sendDeadlineInfo(paramDDL);

    if (response.data.success) {
        ddlWarnInfo.value = response.data.data;
        messageCount.ddl = response.data.data.length;
    } else {
      console.error(response.data.message);
    }
  } catch (error) {
    console.error('获取当前ddl消息列表失败，请稍后重试', error);
  }
}


onMounted(() => {
    fetchUser();
    fetchGroup();   
    fetchGroupApplication();
    fetchGroupInvite();
    fetchDDL();
});


const openDraw = async() => {
    drawer.value = true;
    fetchGroupApplication();
    fetchGroupInvite();
    fetchDDL();
}

//审批邀请或申请
const updateApplication = async(account, applicantGroupId, status) => {
  try {
    const paramApplication = {
        account: account,
        groupId: applicantGroupId,
        status: status,
        reviewer: user.id,
    };
    const response = await updateApplicationStatus(paramApplication);

    if (response.data.success) {
        fetchGroupApplication();
        fetchGroupInvite();
    } else {
      console.error(response.data.message);
    }
  } catch (error) {
    console.error('获取当前邀请消息列表失败，请稍后重试', error);
  }
}

const NavList = reactive([
	{
		title: "任务管理",
		url: "/DashBoard/Tasks",
		icon: Calendar
    },
	{
		title: "小组管理",
		url: "/DashBoard/Group",
		icon: UserFilled
	},
	{
		title: "资料库",
		url: "/DashBoard/DataBank",
		icon: Files
	},
	{
		title: "留言板",
		url: "/DashBoard/MessageBoard",
		icon: ChatLineSquare
	},
])

const activeIndex = ref(route.path) 

watch(() => route.path, (newPath) => {
  activeIndex.value = newPath
})

function handleSelect(url) {
  activeIndex.value = url
  router.push({
    path: url,
  })
}

const backPerson = () => {
  store.commit('clearGroupId');
    router.push({
    path: '/PersonalPage',
  })
};
</script>

<style scoped>
.el-menu-item.is-active {
    background-color: #6C8A55 !important;
    color: #ffffff !important; 
    font-weight: bold; 
    
}

.el-menu-item.is-active  div{
    border-left:5px #fff solid !important;
}

.el-menu-item:hover {
  background-color: #9BB588 !important; 
  color: #ffffff !important; 
}

::v-deep .el-drawer__header{
  margin: 0px !important;
}
</style>

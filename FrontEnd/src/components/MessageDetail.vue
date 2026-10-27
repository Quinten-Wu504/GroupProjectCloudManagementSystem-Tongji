<template>
    <el-container id="test" class="container" direction="vertical">
        <p style="text-align: left; margin-top: 0; font-size: 0.8em">小组页面 / 留言板 / 留言信息</p>
        <el-divider style="margin: 0px" />

        <div style="text-align: left; padding: 10px 20px 15px 20px;">
            <el-row>
                <el-col :span="15">
                    <p style="color:#576251; font-size: 1.1em; letter-spacing: 0.1em; font-weight: bold; 
                        margin:0px;display: flex; align-items: center;">
                        <el-icon style="margin-right: 5px">
                            <Management />
                        </el-icon>
                        {{ message.title }}
                    </p>
                </el-col>

                 <el-col :span="9">
                    <el-button class="btn3" @click="downloadFile" v-if=" fileInfo.url">
                        <el-icon style="margin-right: 5px;"> <Download /> </el-icon>
                            下载附件
                    </el-button>

                    <el-popconfirm title="此操作无法撤回，您确定要删除该留言吗？" confirm-button-text="删除" cancel-button-text="取消"
                        confirm-button-type="danger" hide-icon @confirm="deleteM" width="200px">
                        <template #reference>
                            <el-button class="btn3">
                                <el-icon style="margin-right: 5px;">
                                    <Delete />
                                </el-icon>
                                删除帖子
                            </el-button>
                        </template>
                    </el-popconfirm>

                    <el-button class="btn3" @click="toggleHighlight" >
                        <el-icon style="margin-right: 5px;"> <Star /> </el-icon>
                        {{ message.messageType === 1 ? "取消精华" : "设为精华" }}
                    </el-button>
                </el-col>
            </el-row>

            <div style="margin-left: 20px;">
                <el-tag type="success" effect="plain" round style="margin-right:10px;">
                    {{ message.taskName }}
                </el-tag>
                <el-tag type="warning" effect="plain" round v-if="message.messageType === 1">
                    精华
                </el-tag>

                <img :src="fileTypeIcons[fileInfo.fileType] " alt=" " 
                    style="width: 16px; height: 16px; margin-left: 30px; margin-right: 5px;" />

                <span>{{ fileInfo.originName }}</span>
            </div>
        </div>

        <el-scrollbar max-height="77vh" style="padding: 0px 40px ;">
            <div style="border: 2px rgba(204, 221, 191, 1) solid;">
                <el-row>
                    <el-col :span="4" style="background-color: rgba(204, 221, 191, 1); padding: 15px 0px">
                        <div>
                            <el-avatar :size="64" style="border: 2px #fff solid;" :src="message.headPortrait ? 'data:image/png;base64,' + message.headPortrait : 'https://via.placeholder.com/50'"/>
                            <p style="margin:10px 0px 0px 0px; font-size:0.8em;">
                                {{ message.nickname }}（楼主）
                            </p>
                        </div>
                    </el-col>

                    <el-col :span="20" style="background-color: #fff; display: flex; 
                        padding: 10px 20px ;flex-direction: column; justify-content: space-between;">
                        <p style="font-size:0.9em; text-align: left; margin: 5px 0px 10px 0px;">
                            {{ message.content }}
                        </p>
                        <p style="font-size:0.8em; text-align: right; color:darkgray;  margin: 0; margin-top: auto;">
                            发布时间：{{ message.time }}
                        </p>
                    </el-col>
                </el-row>
            </div>


            <div v-for="(reply, index) in message.replyList" :key="index" 
                style="border-left: 1px #bdc8a2 solid ;border-right: 1px #bdc8a2 solid ;border-bottom: 1px #bdc8a2 solid ;">
                <el-row>
                    <el-col :span="4" style="background-color: rgba(234, 242, 229, 0.6); padding: 15px 0px">
                        <div>
                            <el-avatar :size="60" :src="'data:image/png;base64,'+  reply.headPortrait || 'https://via.placeholder.com/50'" />
                            <p style="margin:10px 0px 0px 0px; font-size:0.8em;">{{ reply.nickname }}</p>
                        </div>
                    </el-col>

                    <el-col :span="20" style="background-color: #fff; display: flex; 
                        padding: 10px 20px ;flex-direction: column; justify-content: space-between;">
                        <p style="font-size:0.9em; text-align: left; margin: 5px 0px 10px 0px;">
                            {{ reply.replyContent }}
                        </p>
                        <p style="font-size:0.8em; text-align: right; color:darkgray;  margin: 0; margin-top: auto;">
                            发布时间：{{ reply.replyTime }}
                        </p>
                    </el-col>
                </el-row>
            </div>
        </el-scrollbar>


        <div style="padding-top: 10px; padding-bottom: 0px; padding-right: 20px;">
            <el-row>
                <el-col :span="3" style="display: flex; align-items: center; justify-content: center; margin:0px">
                    <p style="color:#7f9b7e; font-size:0.9em; font-weight: bold;">新增回复:</p>
                </el-col>

                <el-col :span="18">
                    <el-input v-model="newReply" placeholder="输入回复内容" type="textarea" :autosize="{ minRows: 2, maxRows: 3 }" maxlength="250" show-word-limit/>
                </el-col>

                <el-col :span="3" style="display: flex; align-items: center; justify-content: center;">
                    <el-button class="btn3" @click="addReply" >回复</el-button>
                </el-col>
            </el-row>
        </div>
    </el-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import router from '@/router';
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { deleteMessage, setEssence, getReplyInfoByMessageId, uploadReply, getResourceByMessageId, downloadResourceNew } from '@/api/api';  
import { useStore } from 'vuex';

// 文件图标与映射
import ELSEIcon from '@/assets/file/ELSE.png';
import PDFIcon from '@/assets/file/PDF.png';
import EXCELIcon from '@/assets/file/EXCEL.png';
import JPGIcon from '@/assets/file/JPG.png';
import PNGIcon from '@/assets/file/PNG.png';
import PPTIcon from '@/assets/file/PPT.png';
import WORDIcon from '@/assets/file/WORD.png';
import TXTIcon from '@/assets/file/TXT.png';

const store = useStore();
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

const route = useRoute();

const messageId = parseInt(route.params.id.replace(':', ''), 10);

// 获取留言信息，已写好，需要确认生效时间，待测试
const message = ref({}); 
const fileInfo = ref({});

const fetchMessageInfo = async () => {
    try {
        const paramGetReplyInfoByMessageId = {
            messageId: messageId
        };
        const response = await getReplyInfoByMessageId(paramGetReplyInfoByMessageId);

        if (response.data.success) {
            message.value = response.data.data;
            const paramGetResourceByMessageId = {
                messageId: messageId
            };
            const fileResponse = await getResourceByMessageId(paramGetResourceByMessageId);

            if (fileResponse.data.success) {
                fileInfo.value = fileResponse.data.data;
            } else {
                fileInfo.value.fileType = 0;
            }
        } else {
            ElMessage.error(response.data.message);
        }
    } catch (error) {
        console.error('获取留言的详细异常信息：', error);
        ElMessage.error('获取留言信息失败，请稍后重试');
    }
};


onMounted(() => {
  fetchMessageInfo();
});


// 新增留言信息，已写好，需要更新用户账号，待测试
const newReply = ref("");

const addReply = async () => {
    if (newReply.value.trim()) {
        const paramUploadReply = {
            messageId: messageId,
            replierAccount: store.state.userId || "GPC45602017",
            replyContent: newReply.value.trim()
        };
        //console.log(paramUploadReply);
        try {
            await uploadReply(paramUploadReply);  // 异步操作
            // 重新获取留言信息
            newReply.value="";
            await fetchMessageInfo();
            ElMessage.success("回复提交成功！");
        } catch (error) {
            ElMessage.error('回复提交失败，请稍后重试');
        }
    } else {
        ElMessage.warning("回复内容不能为空！");
    }
};

// 删除留言信息，已写好，待测试
const deleteM = async () => {
    try {
        await deleteMessage({messageId}); 
        ElMessage.success("留言已删除");
        router.push({ path:"/DashBoard/MessageBoard" });
    } catch (error) {
        ElMessage.error('删除留言失败，请稍后重试');
    }
};

// 更改精华状态
const toggleHighlight = async () => {
    const paramEssence = {
        messageId: messageId,
        messageType: message.value.messageType === 1 ? 0 : 1
    };
    try {
        await setEssence(paramEssence);
        await fetchMessageInfo();
        ElMessage.success("状态已更新");
    } catch (error) {
        ElMessage.error('修改精华状态失败，请稍后重试');
    }
};

//下载文件，待测试（重点）
const downloadFile = async () => {
    if (!fileInfo.value.resourceId) {
        ElMessage.warning("没有可下载的附件！");
        return;
    }
    try {
        const resourceId = fileInfo.value.resourceId;
        const paramDownloadResourceNew = {
            resourceId: resourceId
        };
        const response = await downloadResourceNew(paramDownloadResourceNew);

        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute('download', fileInfo.value.originName);
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
    font-weight: bold !important;
    color: #fff !important;
    background: #7f9b7e !important;
}

.container {
    background-color: #fff;
    padding: 20px;
    height: 100vh;
    width: 100%;
}
</style>
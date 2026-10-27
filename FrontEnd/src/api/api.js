import {postAction, getAction, downloadAction } from "@/api/manage";

// 登录注册
// 账号/手机+密码登录
export const passwordLogin = (param) => postAction("/login",param)

// 手机验证码登录
export const verificationLogin = (param) => postAction("/loginV",param)

// 注册
export const register = (param) => postAction("/register", param)

// 发送验证码
export const sendVerificationCodeApi = (param) => postAction("/sms/getCode", param)



// 获取用户信息
// export const getUserInfo = (param) => postAction("/getUserInfo", param)



// 12.7更新 小组内部管理
// 修改小组信息
export const changeGroup = (param) => postAction("/changeGroup",param)

// 邀请用户
export const inviteMember = (param) => postAction("/inviteMember",param)

// 解散小组
export const deleteGroup = (param) => postAction("/deleteGroup",param)

// 移除组员
export const removeMember = (param) => postAction("/removeMember",param)

// 转让小组
export const transferLeader = (param) => postAction("/transferLeader",param)

//退出小组
export const quitGroup = (param) => postAction("/quitGroup",param)

//获取全部用户
export const getAllUsers = (param) => getAction("/getAllUsers",param)

//获取小组信息
export const getGroupDescriptionById = (param) => getAction("/getGroupDescriptionById",param)

//获取小组的全部成员信息
export const getAllGroupMembers = (param) => getAction("/getAllGroupMembers",param)


//12.7 留言板&detail更新
//删除留言
export const deleteMessage = (param) => postAction("/deleteMessage",param)

//获取当前任务列表（任务的同学应该也写了一份，看看后面要不要统一）
export const getCurrentTaskList = (param) => getAction("/getCurrentTaskList",param)

//获取回复
export const getReplyInfoByMessageId = (param) => postAction("/getReplyInfoByMessageId",param)

//获取全部留言
export const getMessageInfo = (param) => postAction("/getMessageInfo",param)

//发布回帖
export const uploadReply = (param) => postAction("/uploadReply",param)

//上传留言（资料库的同学应该也有，后续同步）
export const uploadMessage = (param) => postAction("/uploadMessage",param)

//修改消息精华状态
export const setEssence = (param) => postAction("/setEssence",param)

//获得留言关联的文件数据
export const getResourceByMessageId = (param) => postAction("/getResourceByMessageId",param)

//下载文件
export const downloadResource = (param) => postAction("/downloadResource", param)


//12.8 资料库更新
//新下载文件
export const downloadResourceNew = (param) => downloadAction("/downloadResourceNew", param)

//上传文件
export const uploadResource = (param) => postAction("/uploadResource", param)

//删除资源
export const deleteResource = (param) => postAction("/deleteResource", param)

//获得关联任务的资源列表
export const getResourceInfoByTaskId = (param) => postAction("/getResourceInfoByTaskId", param)


//12.8 消息管理更新
// 组长审批别人的入组申请
export const getGroupApplication = (param) => postAction("/getGroupApplication",param)

//同意或拒绝操作
export const updateApplicationStatus = (param) => postAction("/updateApplicationStatus",param)

//个人审批被其他组邀请
export const getUserInvitedInfo = (param) => postAction("/getUserInvitedInfo",param)

//消息提示
export const sendDeadlineInfo = (param) => postAction("/sendDeadlineInfo",param)


// 12.10更新 小组内部管理
// 邀请用户
export const applyToGroup = (param) => postAction("/applyToGroup",param)

// import {deleteAction, getAction, postAction, putAction} from "@/api/manage";
import {postAction, getAction} from "@/api/manage";


/************************/
/*** PersonalPage部分 ***/
/************************/

// 获取用户信息
export const getUserInfo = (params) => postAction("/getUserInfo", params);

// 修改用户昵称
export const changeName = (params) => postAction('/changeName', params);

// 上传用户头像接口
export const uploadUserHeadPortrait = (params) => postAction('/uploadUserHeadPortrait', params);

// 用户修改密码接口
export const changePassword = (params) => postAction('/changePassword', params);

// 修改密码处发送验证码接口
export const getCode = (params) => postAction('/getCode', params);

// 创建小组
export const createGroup = (params) => postAction('/createGroup', params);
//校园验证
export const getEmailCode = (params) => postAction('/email/getCode', params);
export const checkEmail = (params) => postAction('/email/check', params);





// 用于 TO DO LIST：

// 获取用户负责的任务接口
export const getDutyByAccount = (params) => postAction("/getDutyByAccount", params);




// 用于 MY GROUP：

// 获取用户加入的小组信息
export const getGroupInfo = (params) => postAction("/getGroupInfo", params);

// 更改小组状态 state
export const changeGroupState = (params) => postAction("/changeGroupState", params);

// 获取全数据库用户信息（暂时用不到）
export const getAllUsers = (params) => getAction("/getAllUsers", params);

// 通过搜索小组ID申请进入小组
export const applyToGroup = (params) => postAction('/applyToGroup',params);

// 成员退出已有小组
export const quitGroup = (params) => postAction('/quitGroup',params);
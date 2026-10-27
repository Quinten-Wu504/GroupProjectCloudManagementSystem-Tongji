import { getAction, postAction} from "@/api/manage";
//任务管理所需接口
// 由小组ID获取所有小组成员
export const getGroupMembers = (param) => getAction("/getAllGroupMembers",param)
// 由小组ID获取备选任务
//返回List<Task> Task包含所有的信息11列 
//问题1：status指？，在甘特图中还可以增加完成度(0-1)
export const getAlternativeTasks = (param) => postAction("/getAlternativeTask",param)
// 由小组ID获取父任务 返回List<Task>
export const getActiveParentTask = (param) => postAction("/getActiveParentTask",param)
// 由小组ID获取所有在进程中的任务（包括父任务和子任务）
export const getActiveTask = (param) => postAction("/getActiveTask",param)
//从父任务ID获取所有子任务的接口
export const getTaskChildren = (param) => postAction("/getTaskDistribute",param)
// 增加备选任务：任务名字，任务描述，任务类型，创建人accout，小组ID
export const createTask = (param) => postAction("/createTask",param)
// 删除任务 需要信息：任务ID
export const deleteTaskapi = (param) => postAction("/deleteTask",param)
// 编辑任务·任务名字，任务描述，任务类型，创建人accout，小组ID
export const changeAlternativeTask = (param) => postAction("/changeAlternativeTask",param)
// 提交任务：
//问题1：接口没有父任务ID
//问题2：看代码发现只能启用父任务，这和前端逻辑不一样
export const addTask = (param) => postAction("/addTask",param)
//回退任务：信息：任务ID
export const backTask = (param) => postAction("/taskSetNotActive",param)
// 修改进程中的任务 
//问题1：接口没有父任务ID
//问题2：接口没有任务名称和描述，意思是不能修改任务名称和描述？和前端设计不同(名称不可修改，描述可以修改)
export const changeActiveTask = (param) => postAction("/changeActiveTask",param)
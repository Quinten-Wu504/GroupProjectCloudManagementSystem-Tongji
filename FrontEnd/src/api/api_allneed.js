// 根据小组ID获取小组信息
import { getAction, postAction} from "@/api/manage";
export const getGroupInfo = (param) => getAction(
    "/getGroupDescriptionById",param);
//获取用户信息
export const getUserInfo = (param) => postAction(
    "/getUserInfo",param);
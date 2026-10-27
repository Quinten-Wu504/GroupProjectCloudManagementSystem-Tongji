package com.fzfnotfound.group.system.service.serviceImpl;


import com.fzfnotfound.group.system.entity.ApiResponse;
import com.fzfnotfound.group.system.entity.DeadlineInfo;
import com.fzfnotfound.group.system.entity.SendInformation;
import com.fzfnotfound.group.system.mapper.SendInfoMapper;
import com.fzfnotfound.group.system.service.SendInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Service
public class SendInfoImpl implements SendInfo {

    @Autowired
    private SendInfoMapper sendInfoMapper;

    @Override
    public ApiResponse<List<DeadlineInfo>> sendDeadlineInfo(String account) {
        List<DeadlineInfo> deadlineInfos = sendInfoMapper.selectGroupAndTaskInfoByAccount(account);
        LocalDateTime now = LocalDateTime.now();
        Iterator<DeadlineInfo> iterator=deadlineInfos.iterator();
        while (iterator.hasNext()){
            var deadlineInfo=iterator.next();

            LocalDateTime deadline = deadlineInfo.getDeadline();
            if(deadline==null){
                continue;
            }

            Duration duration=Duration.between(deadlineInfo.getDeadline(),now);

            if(deadlineInfo.getAdvanceTime()==null)
                continue;
            int advanceTime=deadlineInfo.getAdvanceTime();
            if(duration.toDays()>=advanceTime){
                iterator.remove();
            }
            System.out.println(duration.toDays());
        }
            //去除超过一天的信息
        //按截止时间排序，越早越靠前
        Collections.sort(deadlineInfos, new Comparator<DeadlineInfo>() {
            @Override
            public int compare(DeadlineInfo dl1, DeadlineInfo dl2) {
                return (dl1.getDeadline().isBefore(dl2.getDeadline()))?1:0;
            }
        });
        System.out.println(deadlineInfos);
        return ApiResponse.success(deadlineInfos);
    }
}

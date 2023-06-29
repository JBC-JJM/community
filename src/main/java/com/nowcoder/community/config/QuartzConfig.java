package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    // 配置JobDetail
//     @Bean//关掉了
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);//声明任务
        factoryBean.setName("alphaJob");//名称
        factoryBean.setGroup("alphaJobGroup");//属于哪个组（一个组多个任务）
        factoryBean.setDurability(true);//任务长久保存，就算不用了也不会删除
        factoryBean.setRequestsRecovery(true);//应用出错可以恢复
        return factoryBean;
    }

    //JobDetail由JobDetailFactoryBean实例化

    // 配置Trigger(简单类型：SimpleTriggerFactoryBean, 复杂类型：CronTriggerFactoryBean)
//     @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);//每3秒一次
        factoryBean.setJobDataMap(new JobDataMap());//jod状态存放
        return factoryBean;
    }


    // 刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);//声明任务
        factoryBean.setName("postScoreRefreshJob");//名称
        factoryBean.setGroup("communityJobGroup");//属于哪个组（一个组多个任务）
        factoryBean.setDurability(true);//任务长久保存，就算不用了也不会删除
        factoryBean.setRequestsRecovery(true);//应用出错可以恢复
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());//jod状态存放
        return factoryBean;
    }
}
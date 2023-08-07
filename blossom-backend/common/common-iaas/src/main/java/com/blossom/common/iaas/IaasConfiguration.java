package com.blossom.common.iaas;

import com.blossom.common.iaas.oss.OSSManager;
import com.blossom.common.iaas.blos.BLOSManager;
import com.blossom.common.iaas.cos.COSManager;
import com.blossom.common.iaas.kodo.KODOManager;
import com.blossom.common.iaas.obs.OBSManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IAAS 配置
 *
 * @author xzzz
 */
@Configuration
public class IaasConfiguration {

    @Bean
    @ConditionalOnProperty(name = "project.iaas.ostype", havingValue = "alibaba")
    public OSManager ossManager(IaasProperties iaasProperties) {
        return new OSSManager(iaasProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "project.iaas.ostype", havingValue = "huawei")
    public OSManager obsManager(IaasProperties iaasProperties) {
        return new OBSManager(iaasProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "project.iaas.ostype", havingValue = "tencent")
    public OSManager cosManager(IaasProperties iaasProperties) {
        return new COSManager(iaasProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "project.iaas.ostype", havingValue = "qiniu")
    public OSManager kodoManager(IaasProperties iaasProperties) {
        return new KODOManager(iaasProperties);
    }

    /**
     * 默认
     */
    @Bean
    @ConditionalOnProperty(name = "project.iaas.ostype", havingValue = "blossom", matchIfMissing = true)
    public OSManager blosManager(IaasProperties iaasProperties) {
        return new BLOSManager(iaasProperties);
    }
}

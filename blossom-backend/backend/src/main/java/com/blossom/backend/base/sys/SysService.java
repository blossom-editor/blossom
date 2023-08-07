package com.blossom.backend.base.sys;

import com.blossom.backend.base.sys.os.OSRes;
import com.blossom.common.iaas.IaasProperties;
import com.blossom.common.iaas.OSManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统
 *
 * @author xzzz
 */
@Service
public class SysService {

    @Autowired
    private OSManager osManager;

    /**
     * 对象存储的配置信息
     */
    public OSRes getOsConfig() {
        IaasProperties props = osManager.getProp();
        OSRes res = new OSRes();
        res.setOsType(props.getOsType());
        // TODO 目前只有 BLOS 一种
        res.setBucketName("");
        res.setDefaultPath(props.getBlos().getDefaultPath());
        res.setDomain(props.getBlos().getDomain());
        return res;
    }
}

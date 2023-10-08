package com.blossom.backend.base.sys;

import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.sys.os.OSRes;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统功能 [Sys]
 */
@Slf4j
@RestController
@RequestMapping("/sys")
public class SysController {

    @Autowired
    private SysService sysService;
    @Autowired
    private ParamService paramService;

    /**
     * 服务在线检查
     */
    @AuthIgnore
    @GetMapping("/alive")
    public R<String> checkAlive() {
        return R.ok(String.format("这里是 [%s] 服务器 [%s] 环境", SpringUtil.getAppName(), SpringUtil.getProfileAction()));
    }

    /**
     * 对象存储配置
     */
    @GetMapping("/osconfig")
    public R<OSRes> getOsConfig() {
        return R.ok(sysService.getOsConfig());
    }

    /**
     * 刷新系统配置
     */
    @PostMapping("/param/refresh")
    public R<?> paramRefresh() {
        paramService.refresh();
        return R.ok();
    }

}

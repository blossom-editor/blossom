package com.blossom.backend.base.sys;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.auth.annotation.AuthUserType;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamUpdReq;
import com.blossom.backend.base.sys.os.OSRes;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
     * 服务在线检查 [OP]
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
     * 系统参数列表
     *
     * @apiNote 敏感参数会进行脱敏
     */
    @GetMapping("/param/list")
    public R<Map<String, String>> list() {
        Map<String, String> param = paramService.selectMap(true, ParamEnum.values());
        param.put("serverVersion", SpringUtil.get("project.base.version"));
        return R.ok(param);
    }

    /**
     * 修改系统参数
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("/param/upd")
    public R<Map<String, String>> upd(@Validated @RequestBody ParamUpdReq req) {
        if (!UserTypeEnum.ADMIN.getType().equals(AuthContext.getType())) {
            throw new XzException400("非管理员用户无法配置服务器参数");
        }
        paramService.update(req);
        paramService.refresh();
        return R.ok(paramService.selectMap(true, ParamEnum.values()));
    }

    /**
     * 刷新系统配置
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("/param/refresh")
    public R<?> paramRefresh() {
        paramService.refresh();
        return R.ok();
    }

}

package com.blossom.backend.server.picture;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.auth.annotation.AuthUserType;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.backend.server.picture.pojo.*;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.pojo.PageRes;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 图片 [Picture]
 *
 * @author xzzz
 * @order 20
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/picture")
public class PictureController {

    private final PictureService baseService;

    /**
     * 分页列表
     *
     * @return 分页结果
     */
    @GetMapping("/page")
    public R<PageRes<PictureRes>> page(@ModelAttribute PicturePageReq req) {
        return R.ok(baseService.page(req), PictureRes.class);
    }

    /**
     * 查询图片信息
     *
     * @param url 图片URL
     */
    @GetMapping("/info")
    public R<PictureRes> info(@RequestParam("url") String url) {
        return R.ok(baseService.selectByUrl(url), PictureRes.class);
    }

    /**
     * 删除图片
     *
     * @param req 图片对象
     * @return 删除结果
     */
    @PostMapping("/del")
    public R<?> deleteById(@Validated @RequestBody PictureDelReq req) {
        if (req.getIgnoreCheck() == null) {
            req.setIgnoreCheck(false);
        }
        baseService.delete(req.getId(), req.getIgnoreCheck());
        return R.ok();
    }

    /**
     * 批量删除文件
     *
     * @since 1.10.0
     */
    @PostMapping("/del/batch")
    public R<PictureDelBatchRes> deleteBatch(@Validated @RequestBody PictureDelBatchReq req) {
        if (req.getIgnoreCheck() == null) {
            req.setIgnoreCheck(false);
        }
        PictureDelBatchRes res = PictureDelBatchRes.build();
        for (Long id : req.getIds()) {
            try {
                baseService.delete(id, req.getIgnoreCheck());
                res.getSuccessIds().add(id);
                res.incSuccess();
            } catch (XzException400 e) {
                res.incInuse();
            } catch (Exception e) {
                res.incFault();
            }
        }
        return R.ok(res);
    }

    /**
     * 文件转移
     *
     * @since 1.10.0
     */
    @PostMapping("/transfer")
    public R<?> transfer(@Validated @RequestBody PictureTransferReq req) {
        baseService.transfer(req.getIds(), req.getPid(), AuthContext.getUserId());
        return R.ok();
    }

    /**
     * 星标图片
     *
     * @param req 文章对象
     */
    @PostMapping("/star")
    public R<Long> star(@Validated @RequestBody PictureStarReq req) {
        return R.ok(baseService.update(req.to(PictureEntity.class)));
    }

    /**
     * 统计图片 [OP]
     *
     * @param pid 文件夹ID
     */
    @AuthIgnore
    @GetMapping("/stat")
    public R<PictureStatRes> stat(@RequestParam(value = "pid", required = false) Long pid) {
        return R.ok(baseService.stat(AuthContext.getUserId(), pid));
    }

    /**
     * 查询用户的图片统计
     *
     * @param userId 用户ID
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @GetMapping("/stat/user")
    public R<PictureStatRes> statUser(@RequestParam(value = "id") Long userId) {
        return R.ok(baseService.stat(userId, null));
    }
}

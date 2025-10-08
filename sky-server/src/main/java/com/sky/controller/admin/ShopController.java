package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理端商铺接口")
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 修改店铺营业状态
     *
     * @param status
     * @return
     */
    @ApiOperation("修改店铺营业状态")
    @PutMapping("/{status}")
    public Result updateShopStatus(@PathVariable("status") Integer status) {
        shopService.updateShopStatus(status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     *
     * @return
     */
    @ApiOperation("获取店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getShopStatus() {
        Integer status = shopService.getShopStatus();
        return Result.success(status);
    }
}

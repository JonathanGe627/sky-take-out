package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户端商铺接口")
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

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

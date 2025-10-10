package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "地址簿管理接口")
@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址簿
     * @param addressBook
     * @return
     */
    @ApiOperation("新增地址簿")
    @PostMapping
    public Result insertAddressBook(@RequestBody AddressBook addressBook){
        addressBookService.insertAddressBook(addressBook);
        return Result.success();
    }

    /**
     * 查询当前登录用户的所有地址
     * @return
     */
    @ApiOperation("查询当前登录用户的所有地址")
    @GetMapping("/list")
    public Result<List<AddressBook>> getAddressBookList(){
        List<AddressBook> addressBookList = addressBookService.getAddressBookList();
        return Result.success(addressBookList);
    }

    /**
     * 查询当前登录用户的默认地址
     * @return
     */
    @ApiOperation("查询当前登录用户的默认地址")
    @GetMapping("/default")
    public Result<AddressBook> getDefaultAddressBook(){
        AddressBook addressBook = addressBookService.getDefaultAddressBook();
        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @ApiOperation("根据id查询地址")
    @GetMapping("/{id}")
    public Result<AddressBook> getAddressBookById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getAddressBookById(id);
        return Result.success(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @ApiOperation("修改地址")
    @PutMapping
    public Result updateAddressBook(@RequestBody AddressBook addressBook){
        addressBookService.updateAddressBook(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @ApiOperation("根据id删除地址")
    @DeleteMapping
    public Result deleteAddressBookById(@RequestParam("id") Long id){
        addressBookService.deleteAddressBookById(id);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @return
     */
    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public Result updateDefaultAddressBook(@RequestBody AddressBook addressBook){
        addressBookService.updateDefaultAddressBook(addressBook);
        return Result.success();
    }
}

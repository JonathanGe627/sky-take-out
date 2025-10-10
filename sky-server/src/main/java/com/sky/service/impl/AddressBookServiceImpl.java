package com.sky.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址簿
     *
     * @param addressBook
     */
    @Override
    public void insertAddressBook(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        // 1.查看当前用户的地址簿内有几个地址
        List<AddressBook> addressBookList = this.getAddressBookList();
        if (CollUtil.isEmpty(addressBookList)) {
            // 2.若当前添加的是第一个地址，则设为默认
            addressBook.setIsDefault(1);
        } else {
            // 3.否则不设为默认
            addressBook.setIsDefault(0);
        }
        // 4.添加地址
        addressBookMapper.insertAddressBook(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址
     *
     * @return
     */
    @Override
    public List<AddressBook> getAddressBookList() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> addressBookList = addressBookMapper.getAddressBookList(userId);
        return addressBookList;
    }

    /**
     * 查询当前登录用户的默认地址
     *
     * @return
     */
    @Override
    public AddressBook getDefaultAddressBook() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getDefaultAddressBook(userId, 1);
        return addressBook;
    }

    /**
     * 修改地址
     *
     * @param addressBook
     */
    @Override
    public void updateAddressBook(AddressBook addressBook) {
        addressBookMapper.updateAddressBook(addressBook);
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @Override
    public AddressBook getAddressBookById(Long id) {
        AddressBook addressBook = addressBookMapper.getAddressBookById(id);
        return addressBook;
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */
    @Transactional
    @Override
    public void deleteAddressBookById(Long id) {
        // 1.判断要删除的地址是否为默认地址
        AddressBook defaultAddressBook = this.getDefaultAddressBook();
        if (id.equals(defaultAddressBook.getId())){
            // 2.是默认地址，则先删除该地址
            addressBookMapper.deleteAddressBookById(id);
            // 3.再查询该用户是否还有其他地址
            List<AddressBook> addressBookList = this.getAddressBookList();
            if (CollUtil.isNotEmpty(addressBookList)) {
                // 4.如果有其他地址，则将第一个地址设为默认
                AddressBook addressBookFirst = addressBookList.get(0);
                addressBookFirst.setIsDefault(1);
                this.updateAddressBook(addressBookFirst);
            }
        }else {
            // 5.不是默认地址，直接删除地址
            addressBookMapper.deleteAddressBookById(id);
        }
    }

    /**
     * 设置默认地址
     * @param addressBook
     */
    @Transactional
    @Override
    public void updateDefaultAddressBook(AddressBook addressBook) {
        // 1.先拿到当前默认地址
        AddressBook defaultAddressBook = this.getDefaultAddressBook();
        // 2.将当前默认地址改为非默认并更新
        defaultAddressBook.setIsDefault(0);
        this.updateAddressBook(defaultAddressBook);
        // 3.将新地址设为默认地址并更新
        addressBook = this.getAddressBookById(addressBook.getId());
        addressBook.setIsDefault(1);
        this.updateAddressBook(addressBook);
    }
}

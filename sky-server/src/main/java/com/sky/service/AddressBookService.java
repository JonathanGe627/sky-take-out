package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 新增地址簿
     * @param addressBook
     */
    void insertAddressBook(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址
     * @return
     */
    List<AddressBook> getAddressBookList();

    /**
     * 查询当前登录用户的默认地址
     * @return
     */
    AddressBook getDefaultAddressBook();

    /**
     * 修改地址
     * @param addressBook
     */
    void updateAddressBook(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getAddressBookById(Long id);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteAddressBookById(Long id);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void updateDefaultAddressBook(AddressBook addressBook);
}

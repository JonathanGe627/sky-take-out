package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址簿
     * @param addressBook
     */
    @Insert("insert into address_book " +
            "(user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "values " +
            "(#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName},#{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insertAddressBook(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址
     * @param userId
     * @return
     */
    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> getAddressBookList(Long userId);

    /**
     * 查询当前登录用户的默认地址
     * @param userId
     * @return
     */
    @Select("select * from address_book where user_id = #{userId} and is_default = #{isDefault}")
    AddressBook getDefaultAddressBook(Long userId, Integer isDefault);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getAddressBookById(Long id);

    /**
     * 修改地址
     * @param addressBook
     */
    void updateAddressBook(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteAddressBookById(Long id);
}

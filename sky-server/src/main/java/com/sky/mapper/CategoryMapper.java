package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 添加分类
     * @param category
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) values (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insertCategory(Category category);

    /**
     * 分页查询分类列表，支持根据分类名称模糊查询
     * @param name
     * @param type
     * @return
     */
    Page<Category> getCategoryList(String name, Integer type);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteCategory(Long id);

    /**
     * 修改分类
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateCategory(Category category);

    /**
     * 获取所有分类
     * @param type
     * @return
     */
    @Select("select * from category")
    List<Category> list(Integer type);
}

package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 插入地址数据
     * @param addressBook
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "VALUES (#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 查询默认地址数据
     * @param userId
     * @return
     */
    @Select("select * from address_book where user_id = #{userId} and is_default = 1")
    AddressBook checkDefault(Long userId);

    /**
     * 根据用户id查询所有地址
     * @param userId
     * @return List<AddressBook>
     */
    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> getAddressBookById(Long userId);

    /**
     * 修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据地址id查询地址
     * @param id
     * @return AddressBook
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 取消该用户的默认地址
     * @param userId
     */
    @Update("update address_book set is_default = 0 where user_id = #{userId} and is_default = 1")
    void cancelDefault(Long userId);
}

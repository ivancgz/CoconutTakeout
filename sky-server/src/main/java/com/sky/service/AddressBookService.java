package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 新增地址
     * @param addressBook
     */
    void add(AddressBook addressBook);

    /**
     * 查询登陆用户所有地址
     */
    List<AddressBook> getAddressBooks();

    /**
     * 查询默认地址
     * @return AddressBook
     */
    AddressBook getDefaultAddress();

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
    AddressBook getById(Long id);

    /**
     * 设置默认地址
     * @Param id
     */
    void setDefaultAddress(Long id);
}

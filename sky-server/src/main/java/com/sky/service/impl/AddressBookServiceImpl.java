package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Resource
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void add(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        // 新增时查看该用户是否有默认地址存在，没有则设置该地址为默认地址
        AddressBook defaultAddress = addressBookMapper.checkDefault(userId);
        if (defaultAddress == null) {
            addressBook.setIsDefault(1);
        } else {
            addressBook.setIsDefault(0);
        }
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询登陆用户所有地址
     */
    @Override
    public List<AddressBook> getAddressBooks() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> list = addressBookMapper.getAddressBookById(userId);
        return list;
    }

    /**
     * 查询默认地址
     * @return AddressBook
     */
    @Override
    public AddressBook getDefaultAddress() {
        Long userId = BaseContext.getCurrentId();
        AddressBook defaultAddress = addressBookMapper.checkDefault(userId);
        return defaultAddress;
    }

    /**
     * 修改地址
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据地址id查询地址
     * @param id
     * @return AddressBook
     */
    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    /**
     * 设置默认地址
     * @param id
     */
    @Override
    @Transactional
    public void setDefaultAddress(Long id) {
        // 先取消该用户原先的默认地址
        Long userId = BaseContext.getCurrentId();
        addressBookMapper.cancelDefault(userId);

        // 设置该地址为默认地址
        AddressBook defaultAddress = AddressBook.builder()
                                                .id(id)
                                                .isDefault(1)
                                                .build();
        addressBookMapper.update(defaultAddress);
    }
}

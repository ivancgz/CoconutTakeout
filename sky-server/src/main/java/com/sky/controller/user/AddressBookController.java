package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "地址簿相关接口")
@Slf4j
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return Result
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result<?> add(@RequestBody AddressBook addressBook) {
        log.info("新增地址：{}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    /**
     * 查询登陆用户所有地址
     * @return Result<List<AddressBook>>
     */
    @GetMapping("/list")
    @ApiOperation("查询登陆用户所有地址")
    public Result<List<AddressBook>> list() {
        List<AddressBook> addressBookList = addressBookService.getAddressBooks();
        return Result.success(addressBookList);
    }

    /**
     * 查询默认地址
     * @return Result<AddressBook>
     */
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> defaultAddress() {
        AddressBook defaultAddress = addressBookService.getDefaultAddress();
        return Result.success(defaultAddress);
    }

    /**
     * 修改地址
     * @param addressBook
     * @return Result
     */
    @PutMapping
    @ApiOperation("修改地址")
    public Result<?> update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据地址id查询地址
     * @return Result<AddressBook>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据地址id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     * @Param id
     * @return Result
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result<?> setDefaultAddress(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址");
        Long id = addressBook.getId();
        addressBookService.setDefaultAddress(id);
        return Result.success();
    }
}

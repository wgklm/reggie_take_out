package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * 对应service接口实现类
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
/**@Service 注解是 Spring Framework 中的一种注解，
 * 它标识了这个类是一个业务逻辑层的服务 Bean。
 * 这意味着当 Spring 应用启动时，该 Bean 会被自动
 * 创建并加入到 Spring 应用上下文中。
 * 简而言之，@Service 注解是一种用于标记服务层 Bean 的注解，
 * 是在 Spring Boot 应用中实现业务逻辑复用的重要方法之一。
 */

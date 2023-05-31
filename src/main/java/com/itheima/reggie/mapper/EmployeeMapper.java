package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;


/**@Mapper: 这个注解一般使用在Dao层接口上，相当于一个mapper.xml文件，它的作用就是将接口生成一个动态代理类。
 * 加入了@Mapper注解，目的就是为了不再写mapper映射文件。这个注解就是用来映射mapper.xml文件的。
使用@mapper后，不需要在spring配置中设置扫描地址，通过mapper.xml里面的namespace属性
对应相关的mapper类，spring将动态的生成Bean后注入到ServiceImpl中
*/

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}

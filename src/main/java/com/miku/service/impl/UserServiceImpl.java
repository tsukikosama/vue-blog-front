package com.miku.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miku.common.Result;
import com.miku.entity.Code;
import com.miku.entity.User;
import com.miku.mapper.UserMapper;
import com.miku.pojo.Userpo;
import com.miku.service.CoderService;
import com.miku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.miku.utils.SystemConstants.MAX_PAGE_SIZE;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {



    @Autowired
    private CoderService coderService;
    /**
     * 登录查询
     * @param user
     * @return
     */
    @Override
    public Userpo login(User user) {
        Userpo po = new Userpo();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //user不为空去数据库中查询
        if(user !=null){
            wrapper.eq(User::getUsername,user.getUsername()).eq(User::getPassword,user.getPassword());
            user = getOne(wrapper);
            System.out.println(user);
        }
        BeanUtil.copyProperties(user,po);

        return po;
    }
    //查询user信息
    @Override
    public Page<User> getUser(String username,Integer current) {

        Page<User> page = new Page<>(current,MAX_PAGE_SIZE);
        //
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //为空就查全部
        if (username != null ){
            wrapper.like(User::getUsername,username);
        }

        page = page(page, wrapper);
//        Page<Userpo> pagepo = new Page<>();
//        BeanUtil.copyProperties(page,pagepo);
//        System.out.println(page1);
//        //查询到所有的用户信息
//        List<User> list = list(wrapper);
//        //用一个集合来存储po对象
//        Userpo po = new Userpo();
//        List<Userpo> poList = new ArrayList<>();
//        //去封装成po对象返回
//        for(User k : list){
//            BeanUtil.copyProperties(k,po);
//            poList.add(po);
//        }
//        page.setRecords(poList);
        return page;
    }

    /**
     * 保持用户的方法
     * @param user
     * @return
     */
    @Override
    public Boolean addUser(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(User::getUsername,user.getUsername());
        User one = getOne(wrapper);
        //判断是否存在当前用户
        if (one != null){
            return false;
        }
        user.setUserType(1);
        user.setIsValid("1");
        DateTime now = DateTime.now();
        user.setCreateTime(now.toDateStr());
        save(user);
        return true;
    }

    /**
     * 这个方法数据库限制了邮箱和账户的唯一性 所以这里不去判断账号和邮箱的唯一性
     * @param user
     * @param code
     * @return
     */
    @Override
    public String register(User user, String code) {
        if(user == null){
            return "数据不合法";
        }

        //构建wrapper
        LambdaQueryWrapper<Code> wrapper = new LambdaQueryWrapper<Code>();
        Code code1 = new Code();
        code1.setEmail(user.getEmail());
        //判断验证码是否正确
        Code one = coderService.getOne(wrapper);
        if(one == null){
            return "验证码错误";
        }
//        System.out.println(code);
//        System.out.println(one.getCode());
        //判断验证码是否相同
//        System.out.println(!code.equals(one.getCode()));
        if(!code.equals(one.getCode())){

            //不相同
            return "验证码错误";
        }
//        System.out.println("11111111111");
        addUser(user);
        return "添加成功";
    }
}

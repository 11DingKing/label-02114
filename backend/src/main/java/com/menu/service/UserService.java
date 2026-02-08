package com.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.menu.common.Result;
import com.menu.config.JwtUtil;
import com.menu.dto.LoginDTO;
import com.menu.entity.User;
import com.menu.mapper.UserMapper;
import com.menu.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    
    public Result<LoginVO> login(LoginDTO dto) {
        log.info("用户尝试登录: {}", dto.getUsername());
        
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            log.warn("登录失败 - 用户不存在: {}", dto.getUsername());
            return Result.error("用户不存在");
        }
        
        if (!verifyPassword(dto.getPassword(), user.getPassword())) {
            log.warn("登录失败 - 密码错误: {}", dto.getUsername());
            return Result.error("密码错误");
        }
        
        if (user.getStatus() != 1) {
            log.warn("登录失败 - 账号已禁用: {}", dto.getUsername());
            return Result.error("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        
        log.info("用户登录成功: {} (角色: {})", user.getUsername(), user.getRole() == 1 ? "管理员" : "用户");
        return Result.success(vo);
    }
    
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 验证密码
     * 简化实现：直接比较明文密码
     * 生产环境应使用 BCrypt 等加密方式
     */
    private boolean verifyPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        // 直接比较（数据库存储的是明文密码用于演示）
        return rawPassword.equals(storedPassword);
    }
}

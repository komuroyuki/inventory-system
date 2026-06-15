package com.inventory.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inventory.DTO.LoginRequest;
import com.inventory.DTO.LoginResponse;
import com.inventory.Entity.User; 
import com.inventory.Repository.UserRepository;
import com.inventory.Security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(LoginRequest request) {
        System.out.println("① 受信したEmail: " + request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            System.out.println("❌ 原因: ユーザーが見つかりません。");
            throw new RuntimeException("メールアドレスが間違っています。");
        }

        // 【修正】パスワードが一致しない場合は、即座にエラーを投げてアクセスを拒否する！
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ 原因: パスワードが一致しません。");
            throw new RuntimeException("パスワードが間違っています。");
        }

        System.out.println("✅ 認証成功！");

        LoginResponse response = new LoginResponse();
        LoginResponse.UserInfoDto userInfo = new LoginResponse.UserInfoDto();

        userInfo.setId(String.valueOf(user.getId()));
        userInfo.setName(user.getName());
        userInfo.setEmail(user.getEmail());

        // 修正
        String role = Boolean.TRUE.equals(user.getIsAdmin()) ? "admin" : "user";
        userInfo.setRole(role);
        
        
        response.setUser(userInfo);

        String realToken = jwtUtil.generateToken(user.getEmail(), role);
        response.setAccess_token(realToken);

        return response;
    }
}
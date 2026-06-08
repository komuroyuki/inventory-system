package com.inventory.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inventory.DTO.LoginRequest;
import com.inventory.DTO.LoginResponse;
import com.inventory.Entity.User; 
import com.inventory.Repository.UserRepository;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse authenticate(LoginRequest request) {
    System.out.println("① 受信したEmail: " + request.getEmail());
    
    User user = userRepository.findByEmail(request.getEmail());

    if (user == null) {
        System.out.println("❌ 原因: ユーザーが見つかりません。");
        throw new RuntimeException("メールアドレスまたはパスワードが間違っています。");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        System.out.println("⚠️ ハッシュ不一致を検知。今の設定でパスワードを再保存します。");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
         throw new RuntimeException("メールアドレスまたはパスワードが間違っています。");
    }

    System.out.println("✅ 認証成功！");

    LoginResponse response = new LoginResponse();
    LoginResponse.UserInfoDto userInfo = new LoginResponse.UserInfoDto();

    userInfo.setId(String.valueOf(user.getId()));
    userInfo.setName(user.getName());
    userInfo.setEmail(user.getEmail());

    if (Boolean.TRUE.equals(user.getIsAdmin())) {
        userInfo.setRole("admin");
    } else {
        userInfo.setRole("user");
    }
    
    response.setUser(userInfo);

    String dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + UUID.randomUUID().toString();
    response.setAccess_token(dummyToken);

    return response;
}
}
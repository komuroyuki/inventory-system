package com.inventory.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.DTO.LoginRequest;
import com.inventory.DTO.LoginResponse;
import com.inventory.Entity.User; 
import com.inventory.Repository.UserRepository;

import java.util.UUID;

@Service // 「このクラスはビジネスロジックを担当します」という宣言
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // ★修正: エディタによって自動生成された空のメソッドを上書きし、本来のログイン処理に戻します
    public LoginResponse authenticate(LoginRequest request) {
        // 1. メールアドレスでユーザーをDBから検索
        User user = userRepository.findByEmail(request.getEmail());

        // 2. ユーザーが存在しない、またはパスワードが一致しない場合はエラー
        // （※現在は単純な文字列比較です。本番環境ではBCrypt等でのハッシュ比較にします）
        if (user == null || !request.getPassword().equals(user.getPassword())) {
            // 例外を投げて、Controllerのcatchブロックにキャッチさせます
            throw new RuntimeException("メールアドレスまたはパスワードが間違っています。");
        }

        // 3. レスポンス用のデータ（DTO）を組み立てる
        LoginResponse response = new LoginResponse();
        LoginResponse.UserInfoDto userInfo = new LoginResponse.UserInfoDto();

        userInfo.setId(String.valueOf(user.getId()));
        userInfo.setName(user.getName());
        userInfo.setEmail(user.getEmail());

        // 権限（role）の判定
        if (user.getIsAdmin() != null && user.getIsAdmin()) {
            userInfo.setRole("admin");
        } else {
            userInfo.setRole("user");
        }
        
        response.setUser(userInfo);

        // 4. 認証トークンの発行
        // （※今回は設計書に合わせて、仮のランダムな文字列（UUID）をトークンとして発行します）
        String dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + UUID.randomUUID().toString();
        response.setAccess_token(dummyToken);

        return response;
    }
}
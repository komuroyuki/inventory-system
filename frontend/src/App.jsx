import { BrowserRouter, Routes, Route, useNavigate, useLocation } from 'react-router-dom'
import { SWRConfig } from 'swr'
import { useEffect } from 'react'
import './App.css'

import Top from './Top.jsx'
import Product_details from './jsx/product_details.jsx'
import Login from './Login.jsx'
import AddProduct from './AddProduct.jsx'

function SWRGlobalConfig({ children }) {
  const navigate = useNavigate();
  const location = useLocation(); // 現在のページURLを取得

  // 全画面共通:ログインチェック＆壊れたトークンの最速弾き
  useEffect(() => {
    // ログイン画面（"/"）にいるときはチェックをスキップする
    if (location.pathname === "/") return;

    const token = localStorage.getItem("access_token");
    const isTokenInvalid = !token || token.split('.').length !== 3;

    // トークンが無い、または改ざん（全角など）されている場合
    if (isTokenInvalid) {
      localStorage.removeItem("access_token");
      localStorage.removeItem("user_role");
      navigate("/");
    }
  }, [location.pathname, navigate]);

  return (
    <SWRConfig
      value={{
        // 全画面共通で、401エラーの時はリトライを即座にストップさせる
        onErrorRetry: (err) => {
          if (err.status === 401) return;
        },

        // 15分タイマー切れなど、通常の操作中に401が出た場合はここで一括キャッチ
        onError: (error) => {
          if (error.status === 401) {
            alert("ログインセッションの有効期限が切れたか、認証に失敗しました。お手数ですが再度ログインしてください。");
            
            localStorage.removeItem("access_token");
            localStorage.removeItem("user_role");
            
            navigate("/");
            window.location.reload();
          }
        }
      }}
    >
      {children}
    </SWRConfig>
  );
}

function App() {
  return (
    <BrowserRouter>
      <SWRGlobalConfig>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/top" element={<Top />} />
          <Route path="/product/:productId" element={<Product_details />} />
          <Route path="/AddProduct" element={<AddProduct />} />
        </Routes>
      </SWRGlobalConfig>
    </BrowserRouter>
  )
}

export default App
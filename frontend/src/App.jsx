import { BrowserRouter, Routes, Route, useNavigate } from 'react-router-dom'
import { SWRConfig } from 'swr'
import './App.css'

import Top from './Top.jsx'
import Product_details from './jsx/product_details.jsx'
import Login from './Login.jsx'
import AddProduct from './AddProduct.jsx'

function SWRGlobalConfig({ children }) {
  const navigate = useNavigate();

  return (
    <SWRConfig
      value={{
        // 全画面共通で、401エラーの時はリトライを即座にストップさせる
        onErrorRetry: (err) => {
          if (err.status === 401) return; // 401の時はリトライせず即エラー確定
        },

        // 全画面のuseSWRでエラーが確定したとき、共通でここが走ります
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
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './App.css'

import Top from './Top.jsx'
import Product_details from './jsx/product_details.jsx'
import Login from './Login.jsx'
// 💡 1. 商品追加画面（AddProduct）をインポートする
// (※ファイルの置き場所に合わせて、パスは適宜調整してください。ここではTop.jsxと同じ階層にあると仮定しています)
import AddProduct from './AddProduct.jsx'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/top" element={<Top />} />
        <Route path="/product/:productId" element={<Product_details />} />
        <Route path="/AddProduct" element={<AddProduct />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App

import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './App.css'

import Top from './Top.jsx'
import Product_details from './jsx/product_details.jsx'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Top />} />
        <Route path="/product/:productId" element={<Product_details />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
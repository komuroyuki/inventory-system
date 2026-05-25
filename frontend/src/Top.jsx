import React from 'react';
import useSWR from 'swr';
import { useNavigate } from "react-router-dom";
import './Top.css';
import Header from './Header/Header.jsx';

const fetcher = async (url) => {
  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error('HTTP error! status: ${response.status}');
    }
    
    return await response.json();
  } catch (err) {
    console.error('API通信エラーの本当の原因:', err);
    throw new Error('データの取得に失敗しました');
  }
};

const Top = ({ searchQuery = '', selectedCategory = '' }) => {

  const navigate = useNavigate();

  const { data: product, error, isLoading } = useSWR('http://localhost:8080/product', fetcher);

  const filteredProduct = (product || []).filter((product) => {
    const productName = product.name || product.productName || product.product_name || '';
    const matchesSearch = 
      searchQuery === '' || 
      String(productName).toUpperCase().includes(searchQuery.toUpperCase());

    const matchesCategory = 
      selectedCategory === '' || 
      selectedCategory === 'all' || 
      product.category === selectedCategory ||
      String(product.categoryId) === String(selectedCategory);

    return matchesSearch && matchesCategory;
  });

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`); 
  };

  return (
    <div className="product_container">
      <Header />

      <div className="container">
        {isLoading ? (
          <div>読み込み中...</div>
        ) : error ? (
          <div>エラー: {error.message}</div>
        ) : (
          <>
            <h2>商品一覧</h2>
            <div className="grid">
              {filteredProduct.map((product) => (
                <div key={product.id} className="card">
                  <div className="product-header">
                    <div className="id">{product.id}</div>
                      <div 
                        className="name" 
                        onClick={() => handleProductClick(product.id)}
                        style={{ cursor: 'pointer' }} 
                      >
                        {product.name}
                      </div>
                    </div>
                    <div className="stockBox1">
                      <span>在庫数</span>
                    </div>
                    <div className="stockBox2">
                      <span>
                        {product.quantity}
                      </span>
                    </div>
                  </div>
              ))}
            </div>
            
            {filteredProduct.length === 0 && (
              <p style={{ textAlign: 'center', marginTop: '20px', color: '#666' }}>
                該当する商品は見つかりませんでした
              </p>
            )}
          </>
        )}

        <div className="footer">
          <p>© 2026 TeamB All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default Top;
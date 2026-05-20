import React from 'react';
import useSWR from 'swr';
import './Top.css';
// import './Header/Header.jsx';


const fetcher = async (url) => {
  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error('データの取得に失敗しました');
    }
    
    return await response.json();
  } catch (err) {
    throw new Error('データの取得に失敗しました');
  }
};

const Top = ({ searchQuery = '', selectedCategory = '' }) => {

  const { data: products, error, isLoading } = useSWR('http://localhost:8080/product', fetcher);

  const filteredProducts = (products || []).filter((product) => {
    const matchesSearch = searchQuery === '' || Object.values(product).some(value => 
      value !== undefined && 
      value !== null && 
      String(value).toUpperCase().includes(searchQuery.toUpperCase())
    );

    const matchesCategory = 
      selectedCategory === '' || 
      selectedCategory === 'all' || 
      product.category === selectedCategory;

    return matchesSearch && matchesCategory;
  });

  const handleProductClick = (productId) => {
    window.location.href = `/products/${productId}`; 
  };

  if (isLoading) return <div className="container">読み込み中...</div>;
  
  if (error) return <div className="container">エラー: {error.message}</div>;

  return (
    <div className="product_container">
      <header/>

    <div className="container">
      <h2>商品在庫一覧</h2>
      <div className="grid">
        {filteredProducts.map((product) => (
          <div key={product.product_id} className="card">
            <div className="product-header">
              <div className="id">ID: {product.product_id}</div>
              <div 
                className="name" 
                onClick={() => handleProductClick(product.product_id)}
                style={{ cursor: 'pointer' }} 
              >
                {product.product_name}
              </div>
            </div>
            <div className="stockBox">
              <span>在庫数</span>
              <span>
                {product.product_quantity}
              </span>
            </div>
          </div>
        ))}
      </div>
      
      {filteredProducts.length === 0 && (
        <p style={{ textAlign: 'center', marginTop: '20px', color: '#666' }}>
          該当する商品は見つかりませんでした
        </p>
      )}

      <div className="footer">
        <p>© 2026 TeamB All rights reserved.</p>
      </div>
    </div>
    </div>
  );
};

export default Top;
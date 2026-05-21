import React from 'react';
import useSWR from 'swr';
import './Top.css';
import Header from './Header/Header.jsx';

const fetcher = async (url) => {
  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error('データの取得に失敗しました');
    }
    
    return await response.json();
  } catch (err) {
    console.error('API通信エラーの本当の原因:', err);
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
      product.category === selectedCategory ||
      product.categoryId === selectedCategory;

    return matchesSearch && matchesCategory;
  });

  const handleProductClick = (productId) => {
    window.location.href = `/products/${productId}`; 
  };

  return (
    <div className="product_container">
      {/* 💡 ヘッダーを常に一番上に表示 */}
      <Header />

      <div className="container">
        {/* 💡 データの状態（ロード中・エラー・成功）によって中身だけを切り替える */}
        {isLoading ? (
          <div>読み込み中...</div>
        ) : error ? (
          <div>エラー: {error.message}</div>
        ) : (
          <>
            <h2>商品一覧</h2>
            <div className="grid">
              {filteredProducts.map((product) => (
                <div key={product.id || product.productId || product.product_id} className="card">
                  <div className="product-header">
                    <div className="id">{product.id || product.productId || product.product_id}</div>
                      <div 
                        className="name" 
                        onClick={() => handleProductClick(product.id || product.productId || product.product_id)}
                        style={{ cursor: 'pointer' }} 
                      >
                        {product.name || product.productName || product.product_name}
                      </div>
                    </div>
                    <div className="stockBox1">
                      <span>在庫数</span>
                    <div/>
                    <div className="stockBox2">
                      <span>
                        {product.quantity ?? product.stock ?? product.productQuantity ?? product.product_quantity}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            
            {filteredProducts.length === 0 && (
              <p style={{ textAlign: 'center', marginTop: '20px', color: '#666' }}>
                該当する商品は見つかりませんでした
              </p>
            )}
          </>
        )}

        {/* 💡 フッターも常に一番下に表示 */}
        <div className="footer">
          <p>© 2026 TeamB All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default Top;
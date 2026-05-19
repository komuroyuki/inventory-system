import React, { useState, useEffect } from 'react';
import './Top.css'

// ヘッダーの検索ボックス・プルダウンの値を受け取るようにpropsを設定
const Top = ({ searchQuery = '', selectedCategory = '' }) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 1. DB上の商品情報をAPI経由で取得（API完成までは仮データを使用）
  useEffect(() => {
    // APIやDBが未完成のため、6項目の仮データをセット
    const mockData = [
      { id: 1, name: '商品A', category: 'カテゴリ1', stock: 50 },
      { id: 2, name: '商品B', category: 'カテゴリ2', stock: 120 },
      { id: 3, name: '商品C', category: 'カテゴリ1', stock: 0 },
      { id: 4, name: '商品D', category: 'カテゴリ3', stock: 15 },
      { id: 5, name: '商品E', category: 'カテゴリ2', stock: 8 },
      { id: 6, name: '商品F', category: 'カテゴリ1', stock: 300 },
      { id: 7, name: '商品G', category: 'カテゴリ3', stock: 20 },
      { id: 1, name: '商品A', category: 'カテゴリ1', stock: 50 },
      { id: 2, name: '商品B', category: 'カテゴリ2', stock: 120 },
      { id: 3, name: '商品C', category: 'カテゴリ1', stock: 0 },
      { id: 4, name: '商品D', category: 'カテゴリ3', stock: 15 },
      { id: 5, name: '商品E', category: 'カテゴリ2', stock: 8 },
      { id: 6, name: '商品F', category: 'カテゴリ1', stock: 300 },
      { id: 7, name: '商品G', category: 'カテゴリ3', stock: 20 },
    ];
    
    setProducts(mockData);
    setLoading(false);
  }, []); // 初回レンダリング時のみ実行

  // 2. 検索ワードとカテゴリによる絞り込み処理
  const filteredProducts = products.filter((product) => {
    // product.name が存在しない場合のエラーを防ぐ安全処理
    const productName = product.name ? String(product.name) : '';
    const matchesSearch = productName.toLowerCase().includes(searchQuery.toLowerCase());
    
    // カテゴリが一致しているか（プルダウンが未選択の場合はすべてマッチ）
    const matchesCategory = selectedCategory === '' || product.category === selectedCategory;
    
    return matchesSearch && matchesCategory;
  });

  // 【書き換え箇所】外部CSS適用のため className="container" に修正
  if (loading) return <div className="container">読み込み中...</div>;
  if (error) return <div className="container">エラー: {error}</div>;

  return (
    // 【書き換え箇所】style={styles.xxx} から className="xxx" にすべて修正
    <div className="container">
      <h2>商品在庫一覧</h2>
      <div className="grid">
        {filteredProducts.map((product) => (
          <div key={product.id} className="card">
            <div className="product-header">
                <div className="id">{product.id}</div>
                <div className="name">{product.name}</div>
            </div>
            <div className="stockBox">
              <span>在庫数</span>
              {/* 詳細ページでの増減が反映された在庫数を枠に入れて表示 */}
              {/* <span className="stockBox"> */}
              <span>
                {product.stock}
              </span>
            {/* </div> */}
            </div>
          </div>
        ))}
      </div>
      <div className="footer">
        <p>© 2026 TeamB All rights reserved.</p>
      </div>
    </div>
  );
};

export default Top;
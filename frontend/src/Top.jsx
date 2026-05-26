import useSWR from 'swr';
import { useNavigate, useSearchParams } from "react-router-dom";
import './Top.css';
import Header from './Header/Header.jsx';
import React, { useMemo } from 'react';

const fetcher = async (url) => {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (err) {
    if (err instanceof TypeError) {
      throw new Error('ネットワーク接続に失敗しました', { cause: err });
    }
    console.error('API通信エラー:', err);
    throw err;
  }
};

const Top = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // URLパラメータを取得
  const keyword = searchParams.get('keyword') || '';
  const categoryId = searchParams.get('category_id') || '';

  // API用のURLを生成
  const apiUrl = useMemo(() => {
    // カテゴリIDがある場合はカテゴリ検索API、キーワードのみなら検索API、どちらもなければ全件
    if (categoryId && categoryId !== '0') {
      return `http://localhost:8080/products/category?category_id=${encodeURIComponent(categoryId)}`;
    } else if (keyword) {
      return `http://localhost:8080/product/search?keyword=${encodeURIComponent(keyword)}`;
    } else {
      return 'http://localhost:8080/products';
    }
  }, [keyword, categoryId]);

  // ★ここで isLoading を確実に受け取ります
  const { data: product, error, isLoading } = useSWR(apiUrl, fetcher);

  // Top.jsx 内
const displayProducts = product || [];
console.log("バックエンドから届いたデータ:", displayProducts); // この1行を追加

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`); 
  };

  return (
    <div className="product_container">
      <Header />

      <div className="container">
        {/* isLoading 変数がここで使われます */}
        {isLoading ? (
          <div>読み込み中...</div>
        ) : error ? (
          <div>エラー: {error.message}</div>
        ) : (
          <>
            <h2>商品一覧</h2>
            <div className="grid">
  {displayProducts.map((p) => {
    // 【重要】APIごとにキー名が違うことを考慮して、正しい値を探すロジック
    const displayId = p.id || p.productId || '---';
    const displayName = p.productName || p.name || '名前なし';
    const displayQuantity = p.productQuantity !== undefined ? p.productQuantity : (p.quantity !== undefined ? p.quantity : 0);

    return (
      <div key={displayId} className="card">
        <div className="product-header">
          <div className="id">{displayId}</div>
          <div 
            className="name" 
            onClick={() => handleProductClick(displayId)}
            style={{ cursor: 'pointer' }} 
          >
            {displayName}
          </div>
        </div>
        <div className="stockBox1">
          <span>在庫数</span>
        </div>
        <div className="stockBox2">
          <span>{displayQuantity}</span>
        </div>
      </div>
    );
  })}
</div>
            
            {displayProducts.length === 0 && (
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
import useSWR from "swr";
import { useNavigate, useSearchParams } from "react-router-dom";
import "./Top.css";
import Header from "./Header/Header.jsx";
import React, { useMemo } from "react";

export const getApiUrl = (categoryId, keyword) => {
  const cat = categoryId ?? "";
  const key = keyword ?? "";

  if (cat && cat !== "0" && key && key.trim() !== "") {
    return `http://localhost:8080/products/search-filter?category_id=${encodeURIComponent(cat)}&keyword=${encodeURIComponent(key)}`;
  } else if (cat && cat !== "0") {
    return `http://localhost:8080/products/search-filter?category_id=${encodeURIComponent(cat)}`;
  } else if (key && key.trim() !== "") {
    return `http://localhost:8080/products/search-filter?keyword=${encodeURIComponent(key)}`;
  } else {
    return "http://localhost:8080/products/search-filter";
  }
};

export const fetcher = async (url) => {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (err) {
    if (err instanceof TypeError) {
      throw new Error("ネットワーク接続に失敗しました", { cause: err });
    }
    console.error("API通信エラー:", err);
    throw err;
  }
};

const Top = () => {
  const isDirty = false;
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const keyword = searchParams.get("keyword") ?? "";
  const categoryId = searchParams.get("category_id") ?? "";

  const apiUrl = useMemo(
    () => getApiUrl(categoryId, keyword),
    [categoryId, keyword],
  );

  const { data: product, error, isLoading } = useSWR(apiUrl, fetcher);

  const displayProducts = product ?? [];
  console.log("バックエンドから届いたデータ:", displayProducts);

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`);
  };

  return (
    <div className="product_container">
      <Header
        showSearch={true}
        showCategory={true}
        confirmLeave={() => {
          return true;
        }}
      />

      <div className="container">
        {isLoading ? (
          <div>読み込み中...</div>
        ) : error ? (
          <div>エラー: {error.message}</div>
        ) : (
          <>
            <h2>商品一覧</h2>
            <div className="grid">
              {displayProducts.map((p) => {
                const displayId = p.id ?? p.productId ?? "---";
                const displayName = p.productName ?? p.name ?? "名前なし";
                const displayQuantity =
                  p.productQuantity !== undefined
                    ? p.productQuantity
                    : p.quantity !== undefined
                      ? p.quantity
                      : 0;

                return (
                  <div key={displayId} className="card">
                    <div className="product-header">
                      <div className="id">{displayId}</div>
                      <button
                        className="name"
                        onClick={() => handleProductClick(displayId)}
                      >
                        {displayName}
                      </button>
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
              <p
                style={{
                  textAlign: "center",
                  marginTop: "20px",
                  color: "#666",
                }}
              >
                該当する商品は見つかりませんでした
              </p>
            )}
          </>
        )}

        <div className="footer" data-testid="footer-area">
          <p>© 2026 TeamB All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default Top;

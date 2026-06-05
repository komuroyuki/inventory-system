import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AddProduct.css";
import Header from "./Header/Header.jsx";

// カテゴリーのマスターデータ定義
const CATEGORIES = [
  { id: "0", name: "すべて" },
  { id: "1", name: "水" },
  { id: "2", name: "お茶飲料" },
  { id: "3", name: "コーヒー飲料" },
  { id: "4", name: "炭酸飲料" },
  { id: "5", name: "果実・野菜飲料" },
  { id: "6", name: "スポーツドリンク" },
  { id: "7", name: "健康飲料" },
  { id: "8", name: "エナジードリンク" },
  { id: "9", name: "乳性・乳酸菌飲料" },
  { id: "10", name: "その他" },
];

const AddProduct = () => {
  const navigate = useNavigate();

  // フォームの状態管理
  const [productName, setProductName] = useState("");
  const [quantity, setQuantity] = useState(0);
  // 💡 調整ポイント1: 初期値を「すべて（"0"）」ではなく、何かしらの具体的なカテゴリー（例: 水 "1"）にするか、空文字にしてバリデーションをかけるのが安全です
  const [categoryId, setCategoryId] = useState("1"); 
  const [image, setImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState("");

  // 画像が選択された時の処理（プレビュー生成）
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImage(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  // トップへ戻るボタンの処理
  const handleBackToTop = () => {
    navigate("/");
  };

// 登録ボタンの処理
  const handleSubmit = async (e) => {
    e.preventDefault();

    // バリデーション
    if (!productName.trim()) {
      alert("商品名を入力してください");
      return;
    }

    if (categoryId === "0" || categoryId === "") {
      alert("具体的なカテゴリーを選択してください");
      return;
    }

    //  画像パスの文字列を組み立てる
    // 画像が選択されていれば「frontend/src/images/others/ファイル名.png」とし、なければ null にする
    const imagePath = image 
      ? `frontend/src/images/others/${image.name}` 
      : null;

    // 1. バックエンドの ProductRequest に合わせたオブジェクトを作る
    const requestBody = {
      name: productName,
      quantity: quantity,
      categoryId: Number(categoryId),
      image: imagePath //  組み立てたパス文字列をセット！
    };

    try {
      // 2. 指定のパスに JSON 形式でPOSTリクエストを送る
      const response = await fetch("http://localhost:8080/products", {
        method: "POST",
        headers: {
          "Content-Type": "application/json", 
        },
        body: JSON.stringify(requestBody), 
      });

      if (!response.ok) {
        throw new Error(`HTTPエラー! ステータス: ${response.status}`);
      }

      const result = await response.json();
      console.log("登録成功レスポンス:", result);

      alert("商品を登録しました！");
      navigate("/"); // トップへ戻る
    } catch (err) {
      console.error("登録エラー:", err);
      alert("登録に失敗しました。");
    }
  };

  return (
    <div className="product_container">
      <Header
        showSearch={false}
        showCategory={false}
        confirmLeave={() => true}
      />

      <div className="container">
        <h2>新規商品の登録</h2>

        <div className="form-card">
          <form onSubmit={handleSubmit}>
            
            {/* 商品名入力 */}
            <div className="form-group">
              <label className="form-label" htmlFor="product-name">商品名</label>
              <input
                type="text"
                id="product-name"
                className="form-input"
                placeholder="例: 爽快ミネラルウォーター"
                value={productName}
                onChange={(e) => setProductName(e.target.value)}
              />
            </div>

            {/* 在庫数 と カテゴリー の2カラム配置 */}
            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="stock-quantity">初期在庫数</label>
                <input
                  type="number"
                  id="stock-quantity"
                  className="form-input"
                  min="0"
                  value={quantity}
                  onChange={(e) => setQuantity(Number(e.target.value))}
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="category-select">カテゴリー</label>
                <select
                  id="category-select"
                  className="form-select"
                  value={categoryId}
                  onChange={(e) => setCategoryId(e.target.value)}
                >
                  {/* マスターデータから選択肢を動的に生成 */}
                  {CATEGORIES.map((category) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* 画像登録フォーム */}
            <div className="form-group">
              <span className="form-label">商品画像</span>
              <label className="image-dropzone">
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden-file-input"
                />
                {previewUrl ? (
                  <div className="preview-container">
                    <img src={previewUrl} alt="プレビュー" className="image-preview" />
                    <p className="upload-text-small">画像を切り替えるにはクリック</p>
                  </div>
                ) : (
                  <div className="dropzone-placeholder">
                    <span className="upload-icon">📁</span>
                    <p className="upload-text">クリックして画像をアップロード</p>
                  </div>
                )}
              </label>
            </div>

            {/* 下部ボタンエリア */}
            <div className="btn-container">
              <button
                type="button"
                className="btn btn-top"
                onClick={handleBackToTop}
              >
                トップへ戻る
              </button>
              <button type="submit" className="btn btn-register">
                商品を登録する
              </button>
            </div>

          </form>
        </div>

        <div className="footer" data-testid="footer-area">
          <p>© 2026 TeamB All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default AddProduct;
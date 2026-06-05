import "./Header.css";
import { useState } from "react";
import { useSearchParams, useNavigate, useLocation } from "react-router-dom"; // 💡 useLocation を追加
 
const Header = ({ showSearch = true, showCategory = true, confirmLeave, className = "" }) => {
  const [keyword, setKeyword] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("すべて");
  const fixedHeader = className.includes("product-detail-page-header");
  const headerStyle = fixedHeader
    ? {
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        width: "100%",
        zIndex: 9999,
      }
    : undefined;
  const categoryLabels = [
    "すべて",
    "水",
    "お茶飲料",
    "コーヒー飲料",
    "炭酸飲料",
    "果実・野菜飲料",
    "スポーツドリンク",
    "健康飲料",
    "エナジードリンク",
    "乳性・乳酸菌飲料",
    "その他",
  ];
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation(); // 💡 現在のURLの場所を取得

  const [, setSearchParams] = useSearchParams();
 
  const categoryMapping = {
    すべて: "0",
    水: "1",
    お茶飲料: "2",
    コーヒー飲料: "3",
    炭酸飲料: "4",
    "果実・野菜飲料": "5",
    スポーツドリンク: "6",
    健康飲料: "7",
    エナジードリンク: "8",
    "乳性・乳酸菌飲料": "9",
    その他: "10",
  };
 
  const handleSearch = () => {
    if (keyword.length > 50) {
      alert("50文字以内で入力してください");
      return;
    }
 
    const params = new URLSearchParams();
    params.append("keyword", keyword);
    params.append("category_id", categoryMapping[selectedCategory] || "0");
 
    setSearchParams(params);
  };
 
  const handleKeyDown = (event) => {
    if (event.nativeEvent.isComposing || event.keyCode === 229) {
      return;
    }
 
    if (event.key === "Enter") {
      handleSearch();
    }
  };
 
  const handleCategorySelect = (label) => {
    setSelectedCategory(label);
    setIsOpen(false);
 
    const params = new URLSearchParams();
    params.append("keyword", keyword);
    params.append("category_id", categoryMapping[label] || "0");
 
    setSearchParams(params);
  };
 
  const handleLogoClick = (e) => {
    e.preventDefault();
    if (confirmLeave && !confirmLeave()) {
      return;
    }
    navigate("/");
  };
 
  const handleAddProduct = () => {
    if (confirmLeave && !confirmLeave()) {
      return;
    }
    navigate("/AddProduct");
  };
 
  const handleLogout = () => {
    alert("ログアウトしました");
  };
 
  // 現在のURLパスに基づいて、ロゴ画像を出すべき画面かどうかを判定する
  // パスが 「/AddProduct」 または 「/product/〜 (商品詳細)」 の場合に true になります
  const isTargetPage = 
    location.pathname === "/AddProduct" || 
    location.pathname.startsWith("/product/");

  return (
    <header className={`header ${className}`.trim()} style={headerStyle}>
      <div className="header-logo">
        <h1>
          <a href="/" onClick={handleLogoClick} className="site-title">
            {/*条件分岐：対象の画面ならロゴ画像、それ以外ならテキストを表示 */}
            {isTargetPage ? (
              <img src="/logo.png" alt="システムロゴ" className="header-logo-image" />
            ) : (
              "在庫管理システム"
            )}
          </a>
        </h1>
      </div>
 
      {(showSearch || showCategory) && (
        <div className="header-area">
          {showSearch && (
            <div className="header-keyword">
              <input
                type="text"
                placeholder="商品名を入力"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                className="header-keyword-input"
                onKeyDown={handleKeyDown}
              />
              <button
                type="button"
                className="header-keyword-button"
                onClick={handleSearch}
              >
                <img
                  src="/keyword-button.png"
                  alt="検索"
                  className="header-keyword-button-icon"
                />
              </button>
            </div>
          )}
 
          {showCategory && (
            <div className="category">
              <div className="category-list" onClick={() => setIsOpen(!isOpen)}>
                <span>{selectedCategory}</span>
              </div>
              {isOpen && (
                <ul className="category-option">
                  {categoryLabels.map((item) => (
                    <li
                      key={item}
                      className="category-item"
                      onClick={() => handleCategorySelect(item)}
                    >
                      {item}
                    </li>
                  ))}
                </ul>
              )}
            </div>
          )}
        </div>
      )}
 
      <div className="header-logout">
        <button className="add-product-btn" onClick={handleAddProduct}>
          ＋ 商品追加
        </button>
        <button className="logout-btn" onClick={handleLogout}>
          ログアウト
        </button>
      </div>
    </header>
  );
};
 
export default Header;
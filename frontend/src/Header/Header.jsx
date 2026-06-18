import "./Header.css";
import { useState, useEffect } from "react";
import { useSearchParams, useNavigate, useLocation } from "react-router-dom";

const Header = ({ showSearch = true, showCategory = true, confirmLeave, className = "" }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();

  const [keyword, setKeyword] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("すべて");
  const [isOpen, setIsOpen] = useState(false);

  const isAdmin = localStorage.getItem("user_role") === "admin";

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
    "すべて", "水", "お茶飲料", "コーヒー飲料", "炭酸飲料",
    "果実・野菜飲料", "スポーツドリンク", "健康飲料", "エナジードリンク",
    "乳性・乳酸菌飲料", "その他",
  ];

  const categoryMapping = {
    すべて: "0", 水: "1", お茶飲料: "2", コーヒー飲料: "3", 炭酸飲料: "4",
    "果実・野菜飲料": "5", スポーツドリンク: "6", 健康飲料: "7", エナジードリンク: "8",
    "乳性・乳酸菌飲料": "9", その他: "10",
  };

  const categoryIdToLabel = Object.fromEntries(
    Object.entries(categoryMapping).map(([label, id]) => [id, label])
  );

  useEffect(() => {
    const currentKeyword = searchParams.get("keyword") || "";
    const currentCategoryId = searchParams.get("category_id") || "0";
    
    setKeyword(currentKeyword);
    setSelectedCategory(categoryIdToLabel[currentCategoryId] || "すべて");
  }, [searchParams]);

  const updateSearchParams = (newKeyword, categoryLabel) => {
    const currentParams = new URLSearchParams(searchParams);
    
    if (newKeyword) {
      currentParams.set("keyword", newKeyword);
    } else {
      currentParams.delete("keyword");
    }

    const categoryId = categoryMapping[categoryLabel] || "0";
    if (categoryId !== "0") {
      currentParams.set("category_id", categoryId);
    } else {
      currentParams.delete("category_id");
    }

    setSearchParams(currentParams);
  };

  const handleSearch = () => {
    if (keyword.length > 50) {
      alert("50文字以内で入力してください");
      return;
    }
    updateSearchParams(keyword, selectedCategory);
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
    updateSearchParams(keyword, label);
  };

  const handleLogoClick = (e) => {
    e.preventDefault();
    if (confirmLeave && !confirmLeave()) {
      return;
    }
    navigate("/top");
  };

  const handleAddProduct = () => {
    if (confirmLeave && !confirmLeave()) {
      return;
    }
    navigate("/AddProduct");
  };

  const handleLogout = () => {
    localStorage.removeItem("access_token");
    localStorage.removeItem("user_role");
    alert("ログアウトしました");
    navigate("/");
  };

  const isTargetPage =
    location.pathname === "/AddProduct" ||
    location.pathname.startsWith("/product/");

  return (
    <header className={`header ${className}`.trim()} style={headerStyle}>
      <div className="header-logo">
        <h1>
          <a href="/" onClick={handleLogoClick} className="site-title">
            {isTargetPage ? (
              <img src="/logo.png" alt="システムロゴ" className="header-logo-image" />
            ) : (
              "Re:fill"
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
                <span className="category-arrow"></span> 
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
        {isAdmin && (
          <button className="add-product-btn" onClick={handleAddProduct}>
            ＋ 商品追加
          </button>
        )}
        <button className="logout-btn" onClick={handleLogout}>
          ログアウト
        </button>
      </div>
    </header>
  );
};

export default Header;
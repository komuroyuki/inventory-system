import "./Header.css";
import React, { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";

const Header = ({ showSearch = true, showCategory = true, confirmLeave }) => {
  const [keyword, setKeyword] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("すべて");
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

  const [, setSearchParams] = useSearchParams();

  const [isComposing, setIsComposing] = useState(false);

  // IDのマッピング表
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
    if (event.key === "Enter" && !isComposing) {
      handleSearch();
    }
  };

  const handleCategorySelect = (label) => {
    setSelectedCategory(label);
    setIsOpen(false);

    const params = new URLSearchParams();
    params.append("keyword", keyword);
    // ★ここでラベル(label)をIDに変換して送信
    params.append("category_id", categoryMapping[label] || "0");

    setSearchParams(params);
  };

  const handleLogoClick = (e) => {
    e.preventDefault();
    if (confirmLeave && !confirmLeave()) {
      return;
    }
    window.location.href = "/";
  };

  return (
    <header className="header">
      <div className="header-logo">
        <h1>
          <a href="/" onClick={handleLogoClick}>
            <img
              src="/logo.png"
              alt="マイサイトのロゴ"
              className="logo-image"
            />
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
                onCompositionStart={() => setIsComposing(true)}
                onCompositionEnd={() => setIsComposing(false)}
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
    </header>
  );
};

export default Header;

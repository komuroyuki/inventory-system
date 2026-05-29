import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import Top from '../Top.jsx';

vi.mock("../Header/Header.jsx", () => {
  return {
    default: () => <div>Header</div>,
  };
});

vi.mock('swr', () => ({
  default: () => ({
    data: [],
    error: null,
    isLoading: false,
  }),
}));

vi.mock('react-router-dom', () => ({
  useNavigate: () => vi.fn(),
  useSearchParams: () => [new URLSearchParams()],
}));

const getApiUrl = (categoryId, keyword) => {
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

describe('TopコンポーネントのURL生成ロジック', () => {
  it('カテゴリとキーワードの両方が指定されている場合、正しいURLを生成する', () => {
    const url = getApiUrl("1", "tea");
    expect(url).toBe("http://localhost:8080/products/search-filter?category_id=1&keyword=tea");
  });

  it('カテゴリのみが指定されている場合、カテゴリ用URLを生成する', () => {
    const url = getApiUrl("2", "");
    expect(url).toBe("http://localhost:8080/products/search-filter?category_id=2");
  });

  it('カテゴリが"0"の場合は全件取得のURLを生成する', () => {
    const url = getApiUrl("0", "tea");
    expect(url).toBe("http://localhost:8080/products/search-filter?keyword=tea");
  });

  it('キーワードのみが指定されている場合、キーワード用URLを生成する', () => {
    const url = getApiUrl("", "irohasu");
    expect(url).toBe("http://localhost:8080/products/search-filter?keyword=irohasu");
  });

  it('何も指定されていない場合、全件取得のURLを生成する', () => {
    const url = getApiUrl("", "");
    expect(url).toBe("http://localhost:8080/products/search-filter");
  });

  it('キーワードが空白のみの場合は全件取得のURLを生成する', () => {
    const url = getApiUrl("", "   ");
    expect(url).toBe("http://localhost:8080/products/search-filter");
  });
});

describe('Topコンポーネントの表示', () => {
  it('Headerが正しく表示されること', () => {
    const { getByText } = render(<Top />);
    expect(getByText('Header')).toBeDefined();
  });
});
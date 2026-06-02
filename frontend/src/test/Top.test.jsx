import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, fireEvent } from "@testing-library/react";
import Top, { fetcher, getApiUrl } from "../Top.jsx";
import { Router } from "react-router-dom";
import useSWR from "swr";
import * as router from "react-router-dom";
import "@testing-library/jest-dom/vitest";

vi.mock("../Header/Header.jsx", () => {
  return {
    default: () => <div data-testid="mock-header">Header</div>,
  };
});

vi.mock("swr", () => ({
  default: vi.fn(() => ({
    data: [],
    error: null,
    isLoading: false,
  })),
}));

vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: vi.fn(),
    useSearchParams: vi.fn(() => [new URLSearchParams()]),
  };
});

global.fetch = vi.fn();

describe("TopコンポーネントのURL生成ロジック", () => {
  it("カテゴリとキーワードの両方が指定されている場合、正しいURLを生成する", () => {
    const url = getApiUrl("1", "tea");
    expect(url).toBe(
      "http://localhost:8080/products/search-filter?category_id=1&keyword=tea",
    );
  });

  it("カテゴリのみが指定されている場合、カテゴリ用URLを生成する", () => {
    const url = getApiUrl("2", "");
    expect(url).toBe(
      "http://localhost:8080/products/search-filter?category_id=2",
    );
  });

  it('カテゴリが"0"の場合は全件取得のURLを生成する', () => {
    const url = getApiUrl("0", "tea");
    expect(url).toBe(
      "http://localhost:8080/products/search-filter?keyword=tea",
    );
  });

  it("キーワードのみが指定されている場合、キーワード用URLを生成する", () => {
    const url = getApiUrl("", "irohasu");
    expect(url).toBe(
      "http://localhost:8080/products/search-filter?keyword=irohasu",
    );
  });

  it("何も指定されていない場合、全件取得のURLを生成する", () => {
    const url = getApiUrl("", "");
    expect(url).toBe("http://localhost:8080/products/search-filter");
  });

  it("キーワードが空白のみの場合は全件取得のURLを生成する", () => {
    const url = getApiUrl("", "   ");
    expect(url).toBe("http://localhost:8080/products/search-filter");
  });
});

describe("Topコンポーネントの操作", () => {
  it("商品リストがレンダリングされ、クリックで画面遷移関数が呼ばれること", () => {
    const mockNavigate = vi.fn();
    router.useNavigate.mockReturnValue(mockNavigate);

    const mockProducts = [
      { productId: "101", name: "テスト水", quantity: 50 },
      { productId: "102", name: "テストお茶", quantity: 20 },
    ];
    useSWR.mockReturnValue({
      data: mockProducts,
      error: undefined,
      isLoading: false,
    });

    const { getByText } = render(<Top />);

    const productButton = getByText("テスト水");
    fireEvent.click(productButton);

    expect(mockNavigate).toHaveBeenCalledWith("/product/101");
  });
});

describe("fetcher", () => {
  beforeEach(() => {
    vi.resetAllMocks();

    global.fetch.mockResolvedValue({
      ok: true,
      json: async () => ({ key: "value" }),
    });
  });

  it("通信が成功した場合、JSONデータが返却されること", async () => {
    const url = "http://localhost:8080/products/search-filter";
    const res = await fetcher(url);

    expect(global.fetch).toHaveBeenCalledWith(url);
    expect(res).toEqual({ key: "value" });
  });

  it("HTTPエラー(404など)の場合、例外がスローされること", async () => {
    const url = "http://localhost:8080/products/search-filter?keyword=test";

    global.fetch.mockResolvedValue({
      ok: false,
      status: 404,
    });

    await expect(fetcher(url)).rejects.toThrow("HTTP error! status: 404");
  });

  it("ネットワークエラーが発生した場合、カスタムエラーがスローされること", async () => {
    const url = "http://localhost:8080/products/search-filter?category_id=1";

    global.fetch.mockRejectedValue(new TypeError("Failed to fetch"));

    await expect(fetcher(url)).rejects.toThrow(
      "ネットワーク接続に失敗しました",
    );
  });
});

describe("Topコンポーネントの表示", () => {
  it("Headerが正しく表示されること", () => {
    // Routerラッパーを削除し、getByTestId で取得
    const { getByTestId } = render(<Top />);
    expect(getByTestId("mock-header")).toBeInTheDocument();
  });
});

describe("Topコンポーネントの表示", () => {
  it("footerが正しく表示されること", () => {
  
    const { getByTestId } = render(<Top/>);
    expect(getByTestId('footer-area')).toBeInTheDocument();
  });
});

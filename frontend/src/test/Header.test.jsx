import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { render, fireEvent, cleanup } from "@testing-library/react";
import "@testing-library/jest-dom/vitest";
import { Router } from "react-router-dom";
import Header from "../Header/Header.jsx";

const mockSetSearchParams = vi.fn();
const mockNavigate = vi.fn();
const mockSearchParams = new URLSearchParams();

const mockNavigator = {
  createHref: vi.fn(),
  go: vi.fn(),
  push: vi.fn(),
  replace: vi.fn(),
};

vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useSearchParams: () => [mockSearchParams, mockSetSearchParams],
  };
});

describe("Headerコンポーネントのテスト", () => {
  let alertSpy;
  beforeEach(() => {
    vi.clearAllMocks();
    alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
  });

  afterEach(() => {
    if (alertSpy) alertSpy.mockRestore();
    cleanup();
    document.body.innerHTML = "";
  });

  describe("初期表示とpropsの制御", () => {
    it("デフォルトで検索バーとカテゴリが表示されること", () => {
      const { getByPlaceholderText, getByText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header />
        </Router>,
      );
      expect(getByPlaceholderText("商品名を入力")).toBeInTheDocument();
      expect(getByText("すべて")).toBeInTheDocument();
    });

    it("showSearchとshowCategoryがfalseの場合、非表示になること", () => {
      const { queryByPlaceholderText, queryByText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header showSearch={false} showCategory={false} />
        </Router>,
      );
      expect(queryByPlaceholderText("商品名を入力")).not.toBeInTheDocument();
      expect(queryByText("すべて")).not.toBeInTheDocument();
    });
  });

  describe("検索機能（handleSearch, handleKeyDown）のテスト", () => {
    it("入力したキーワードで検索が実行されること", () => {
      const { getByPlaceholderText, getByAltText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header />
        </Router>,
      );

      const input = getByPlaceholderText("商品名を入力");
      fireEvent.change(input, { target: { value: "お茶" } });

      const searchButton = getByAltText("検索");
      fireEvent.click(searchButton);

      const expectedParams = new URLSearchParams({
        keyword: "お茶",
      });
      expect(mockSetSearchParams.mock.calls[0][0].toString()).toBe(expectedParams.toString());
    });

    it("Enterキーで検索が実行されること", () => {
      const { getByPlaceholderText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header />
        </Router>,
      );

      const input = getByPlaceholderText("商品名を入力");
      fireEvent.change(input, { target: { value: "コーヒー" } });
      fireEvent.keyDown(input, { key: "Enter", code: "Enter" });

      expect(mockSetSearchParams).toHaveBeenCalled();
    });

    it("IME入力中（変換中）はEnterキーを押しても検索されないこと", () => {
      const { getByPlaceholderText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header />
        </Router>,
      );

      const input = getByPlaceholderText("商品名を入力");
      fireEvent.change(input, { target: { value: "みず" } });

      fireEvent.keyDown(input, { key: "Enter", code: "Enter", keyCode: 229 });
      expect(mockSetSearchParams).not.toHaveBeenCalled();

      fireEvent.keyDown(input, { key: "Enter", code: "Enter", keyCode: 13 });
      expect(mockSetSearchParams).toHaveBeenCalled();
    });

    it("50文字を超えるキーワードを入力した場合、アラートが出て検索が中断されること", () => {
      const { getByPlaceholderText, getByAltText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header />
        </Router>,
      );

      const input = getByPlaceholderText("商品名を入力");
      fireEvent.change(input, { target: { value: "a".repeat(51) } });

      const searchButton = getByAltText("検索");
      fireEvent.click(searchButton);

      expect(alertSpy).toHaveBeenCalledWith("50文字以内で入力してください");
      expect(mockSetSearchParams).not.toHaveBeenCalled();
    });
  });

  describe("カテゴリ選択機能（handleCategorySelect）のテスト", () => {
    it("カテゴリを選択すると、即座に正しいIDに変換されて検索が実行されること", () => {
      const { getByText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header />
        </Router>,
      );

      const currentCategory = getByText("すべて");
      fireEvent.click(currentCategory);

      const waterCategory = getByText("水");
      fireEvent.click(waterCategory);

      const expectedParams = new URLSearchParams({
        category_id: "1",
      });
      expect(mockSetSearchParams.mock.calls[0][0].toString()).toBe(expectedParams.toString());
    });
  });
  

  describe("ロゴクリックによる遷移（handleLogoClick）のテスト", () => {
    it("confirmLeaveが設定されていない場合、無条件でトップへ遷移すること", () => {
      // 💡 getByText を追加
      const { getByText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header />
        </Router>,
      );

      // 💡 getByAltText から getByText("Re:fill") に修正
      const logo = getByText("Re:fill");
      fireEvent.click(logo);

      expect(mockNavigate).toHaveBeenCalledWith("/top");
    });

    it("confirmLeaveがtrueを返した場合、トップへ遷移すること", () => {
      const mockConfirmLeave = vi.fn(() => true);
      // 💡 getByText を追加
      const { getByText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header confirmLeave={mockConfirmLeave} />
        </Router>,
      );

      // 💡 getByAltText から getByText("Re:fill") に修正
      const logo = getByText("Re:fill");
      fireEvent.click(logo);

      expect(mockConfirmLeave).toHaveBeenCalled();
      expect(mockNavigate).toHaveBeenCalledWith("/top");
    });

    it("confirmLeaveがfalseを返した場合、遷移がキャンセルされること", () => {
      const mockConfirmLeave = vi.fn(() => false);
      // 💡 getByText を追加
      const { getByText } = render(
        <Router location="/" navigator={mockNavigator}>
          <Header confirmLeave={mockConfirmLeave} />
        </Router>,
      );

      const logo = getByText("Re:fill");
      fireEvent.click(logo);

      expect(mockConfirmLeave).toHaveBeenCalled();
      expect(mockNavigate).not.toHaveBeenCalled();
    });
  });
});

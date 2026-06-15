import "@testing-library/jest-dom";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { BrowserRouter } from "react-router-dom";
import AddProduct from "../AddProduct.jsx";

// 1. 外部依存（HeaderとReact Router）のモック化
vi.mock("./Header/Header.jsx", () => {
  return {
    default: () => <div data-testid="mock-header">Header</div>,
  };
});

const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe("AddProduct コンポーネントのテスト", () => {
  let alertSpy;

  beforeEach(() => {
    vi.clearAllMocks();
    alertSpy = vi.spyOn(window, "alert").mockImplementation(() => {});
    vi.spyOn(console, "error").mockImplementation(() => {});
    vi.spyOn(console, "log").mockImplementation(() => {});

    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ id: 1, name: "テスト商品" }),
    });
  });

  afterEach(() => {
    alertSpy.mockRestore();
  });

  const setup = () => {
    render(
      <BrowserRouter>
        <AddProduct />
      </BrowserRouter>
    );
    const user = userEvent.setup();
    return { user };
  };

  describe("初期表示のテスト", () => {
    it("すべての入力フォームとボタンが正しく表示されていること", () => {
      setup();
      expect(screen.getByText("新規商品の登録")).toBeInTheDocument();
      expect(screen.getByLabelText("商品名")).toBeInTheDocument();
      expect(screen.getByLabelText("初期在庫数")).toBeInTheDocument();
      expect(screen.getByLabelText("カテゴリー")).toBeInTheDocument();
      expect(screen.getByText("トップへ戻る")).toBeInTheDocument();
      expect(screen.getByRole("button", { name: "商品を登録する" })).toBeInTheDocument();
    });

    it("カテゴリーの初期値が '水' (value='1') であること", () => {
      setup();
      const select = screen.getByLabelText("カテゴリー");
      expect(select.value).toBe("1");
    });
  });

  describe("バリデーションのテスト", () => {
    it("商品名が空の状態で登録するとアラートが表示され、処理が中断されること", async () => {
      const { user } = setup();
      const submitButton = screen.getByRole("button", { name: "商品を登録する" });

      await user.click(submitButton);

      expect(alertSpy).toHaveBeenCalledWith("商品名を入力してください");
      expect(global.fetch).not.toHaveBeenCalled();
    });

    it("カテゴリーが 'すべて' (value='0') の状態で登録するとアラートが表示されること", async () => {
      const { user } = setup();
      const nameInput = screen.getByLabelText("商品名");
      await user.type(nameInput, "テストお茶");

      const select = screen.getByLabelText("カテゴリー");
      await user.selectOptions(select, "0");

      const submitButton = screen.getByRole("button", { name: "商品を登録する" });
      await user.click(submitButton);

      expect(alertSpy).toHaveBeenCalledWith("具体的なカテゴリーを選択してください");
      expect(global.fetch).not.toHaveBeenCalled();
    });

    it("初期在庫数が1000を超えているとアラートが表示されること", async () => {
      setup(); 

      const nameInput = screen.getByLabelText("商品名");
      fireEvent.change(nameInput, { target: { value: "テストお茶" } });

      const quantityInput = screen.getByLabelText("初期在庫数");
      fireEvent.change(quantityInput, { target: { value: "1001" } });

      // 💡 ボタンクリックではなく、form 自体の submit イベントを直接発生させる
      const form = screen.getByRole("button", { name: "商品を登録する" }).closest("form");
      fireEvent.submit(form);

      expect(alertSpy).toHaveBeenCalledWith("初期在庫数は1000以下で入力してください");
      expect(global.fetch).not.toHaveBeenCalled();
    });
  });

  describe("フォーム入力とキーガードのテスト", () => {
    it("在庫数に入力制限キー（-, +, e, .など）を押しても入力が防がれること", () => {
      setup();
      const quantityInput = screen.getByLabelText("初期在庫数");

      const eventMinus = fireEvent.keyDown(quantityInput, { key: "-" });
      const eventE = fireEvent.keyDown(quantityInput, { key: "e" });

      expect(eventMinus).toBe(false);
      expect(eventE).toBe(false);
    });

    it("在庫数を空にすると自動的に 0 になること", async () => {
      const { user } = setup();
      const quantityInput = screen.getByLabelText("初期在庫数");

      await user.clear(quantityInput);
      expect(quantityInput.value).toBe("0");
    });
  });

  describe("画像アップロードのテスト", () => {
    it("画像を選択するとプレビューが表示されること", async () => {
      const { container } = render(
        <BrowserRouter>
          <AddProduct />
        </BrowserRouter>
      );
      
      const file = new File(["dummy content"], "test-image.png", { type: "image/png" });
      global.URL.createObjectURL = vi.fn(() => "mock-preview-url");

      const fileInput = container.querySelector('input[type="file"]');
      fireEvent.change(fileInput, { target: { files: [file] } });

      const previewImg = screen.getByAltText("プレビュー");
      expect(previewImg).toBeInTheDocument();
      expect(previewImg.getAttribute("src")).toBe("mock-preview-url");
    });
  });

  describe("API通信（リクエスト・レスポンス）のテスト", () => {
    it("正常に入力して登録すると、正しいJSONボディでPOST送信され、トップに遷移すること", async () => {
      const { user } = setup();

      await user.type(screen.getByLabelText("商品名"), " コーラ ");
      
      const quantityInput = screen.getByLabelText("初期在庫数");
      await user.clear(quantityInput);
      await user.type(quantityInput, "50");

      await user.selectOptions(screen.getByLabelText("カテゴリー"), "4");

      await user.click(screen.getByRole("button", { name: "商品を登録する" }));

      expect(global.fetch).toHaveBeenCalledWith(
        "http://localhost:8080/products",
        expect.objectContaining({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            name: "コーラ",
            quantity: 50,
            categoryId: 4,
            image: null,
          }),
        })
      );

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith("商品を登録しました！");
        expect(mockNavigate).toHaveBeenCalledWith("/");
      });
    });

    it("サーバーから409エラー(Conflict)が返ってきた場合、重複エラーメッセージが表示されること", async () => {
      global.fetch = vi.fn().mockResolvedValue({
        ok: false,
        status: 409,
      });

      const { user } = setup();
      await user.type(screen.getByLabelText("商品名"), "重複する商品");
      await user.click(screen.getByRole("button", { name: "商品を登録する" }));

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith("同一名の商品が既に登録されています。");
        expect(mockNavigate).not.toHaveBeenCalled();
      });
    });

    it("レスポンスメッセージ内に「既に登録」が含まれる場合、重複エラーメッセージが表示されること", async () => {
      global.fetch = vi.fn().mockResolvedValue({
        ok: false,
        status: 400,
        json: async () => ({ message: "この商品は既に登録されています" }),
      });

      const { user } = setup();
      await user.type(screen.getByLabelText("商品名"), "重複する商品2");
      await user.click(screen.getByRole("button", { name: "商品を登録する" }));

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith("同一名の商品が既に登録されています。");
      });
    });

    it("一般的な通信エラー(500等)の場合、登録失敗メッセージが表示されること", async () => {
      global.fetch = vi.fn().mockResolvedValue({
        ok: false,
        status: 500,
        json: async () => ({ message: "Internal Server Error" }),
      });

      const { user } = setup();
      await user.type(screen.getByLabelText("商品名"), "エラー商品");
      await user.click(screen.getByRole("button", { name: "商品を登録する" }));

      await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith(
          "登録に失敗しました。詳細なエラーはコンソールを確認してください。"
        );
      });
    });
  });

  describe("画面遷移のテスト", () => {
    it("「トップへ戻る」ボタンをクリックするとトップ画面（/）へ遷移すること", async () => {
      const { user } = setup();
      const backButton = screen.getByText("トップへ戻る");

      await user.click(backButton);
      expect(mockNavigate).toHaveBeenCalledWith("/");
    });
  });
});
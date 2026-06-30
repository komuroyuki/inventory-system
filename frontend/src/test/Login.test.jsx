import { beforeEach, describe, expect, it, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import "@testing-library/jest-dom";
import { MemoryRouter } from "react-router-dom";
import Login from "../Login.jsx";

const mockNavigate = vi.fn();

vi.mock("react-router-dom", async () => {
    const actual = await vi.importActual("react-router-dom");

    return {
        ...actual,
        useNavigate: () => mockNavigate,
    };
});

beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
    global.fetch = vi.fn();
});

describe("Login", () => {
    it("初期表示で入力欄とログインボタンが表示される", () => {
        render(
            <MemoryRouter>
                <Login />
            </MemoryRouter>
        );

        expect(screen.getByPlaceholderText("ユーザーID")).toBeInTheDocument();
        expect(screen.getByPlaceholderText("パスワード")).toBeInTheDocument();
        expect(
            screen.getByRole("button", { name: "投入してログイン" })
        ).toBeInTheDocument();
    });

    it("ユーザーID未入力の場合、エラーメッセージが表示される", async () => {
        const user = userEvent.setup();

        render(
            <MemoryRouter>
                <Login />
            </MemoryRouter>
        );

        await user.type(screen.getByPlaceholderText("パスワード"), "password");
        await user.click(screen.getByRole("button", { name: "投入してログイン" }));

        expect(screen.getByText("ユーザーIDを入力してください")).toBeInTheDocument();
    });

    it("パスワード未入力の場合、エラーメッセージが表示される", async () => {
        const user = userEvent.setup();

        render(
            <MemoryRouter>
                <Login />
            </MemoryRouter>
        );

        await user.type(screen.getByPlaceholderText("ユーザーID"), "admin@example.com");
        await user.click(screen.getByRole("button", { name: "投入してログイン" }));

        expect(screen.getByText("パスワードを入力してください")).toBeInTheDocument();
    });

    it("ユーザーIDとパスワードが未入力の場合、エラーメッセージが表示される", async () => {
        const user = userEvent.setup();

        render(
            <MemoryRouter>
                <Login />
            </MemoryRouter>
        );

        await user.click(screen.getByRole("button", { name: "投入してログイン" }));

        expect(
            screen.getByText("ユーザーID、パスワードを入力してください")
        ).toBeInTheDocument();
    });

    it("メールアドレス形式が不正な場合、エラーメッセージが表示される", async () => {
        const user = userEvent.setup();

        render(
            <MemoryRouter>
                <Login />
            </MemoryRouter>
        );

        await user.type(screen.getByPlaceholderText("ユーザーID"), "1234");
        await user.type(screen.getByPlaceholderText("パスワード"), "password");
        await user.click(screen.getByRole("button", { name: "投入してログイン" }));

        expect(screen.getByText("ユーザーIDの形式が不正です")).toBeInTheDocument();
    });

    it("ログイン失敗時、共通エラーメッセージが表示される", async () => {
        const user = userEvent.setup();

        global.fetch.mockResolvedValue({
            ok: false,
            status: 401,
        });

        render(
            <MemoryRouter>
                <Login />
            </MemoryRouter>
        );

        await user.type(screen.getByPlaceholderText("ユーザーID"), "admin@example.com");
        await user.type(screen.getByPlaceholderText("パスワード"), "wrong-password");
        await user.click(screen.getByRole("button", { name: "投入してログイン" }));

        expect(
            await screen.findByText("サーバーに接続できませんでした")
        ).toBeInTheDocument();
    });

    it("ログイン成功時、トークンと権限を保存する", async () => {
        const user = userEvent.setup();

        global.fetch.mockResolvedValue({
            ok: true,
            json: async () => ({
                access_token: "test-token",
                user: {
                    role: "admin",
                },
            }),
        });

        render(
            <MemoryRouter>
                <Login />
            </MemoryRouter>
        );

        await user.type(screen.getByPlaceholderText("ユーザーID"), "admin@example.com");
        await user.type(screen.getByPlaceholderText("パスワード"), "password");
        await user.click(screen.getByRole("button", { name: "投入してログイン" }));

        await waitFor(() => {
            expect(localStorage.getItem("access_token")).toBe("test-token");
            expect(localStorage.getItem("user_role")).toBe("admin");
        });

        expect(global.fetch).toHaveBeenCalledWith(
            "http://localhost:8080/products/login",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: "admin@example.com",
                    password: "password",
                }),
            }
        );
    });
});

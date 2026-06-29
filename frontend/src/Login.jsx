import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

const Login = () => {
    const navigate = useNavigate();

    const [userId, setUserId] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [loginStep, setLoginStep] = useState("idle");

    const handleLogin = async () => {
        setErrorMessage("");

        if (!userId && !password) {
            setErrorMessage("ユーザーID、パスワードを入力してください");
            return;
        }

        if (!userId) {
            setErrorMessage("ユーザーIDを入力してください");
            return;
        }

        if (!password) {
            setErrorMessage("パスワードを入力してください");
            return;
        }

        if (userId.length > 255) {
            setErrorMessage("ユーザーIDは255文字以内で入力してください");
            return;
        }

        if (password.length > 64) {
            setErrorMessage("パスワードは64文字以内で入力してください");
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(userId)) {
            setErrorMessage("ユーザーIDの形式が不正です");
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/products/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: userId,
                    password: password,
                }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                if (response.status === 423) {
                    setErrorMessage(errorData.message);
                    return;
                }
                setErrorMessage("ユーザーIDまたはパスワードが正しくありません");
                return;
            }

            const data = await response.json();
            localStorage.setItem("access_token", data.access_token);
            localStorage.setItem("user_role", data.user.role);


            setLoginStep("loading");

            setTimeout(() => {
                setLoginStep("dispense");
            }, 1000);

            setTimeout(() => {
                setLoginStep("success");
            }, 2500);

            setTimeout(() => {
                navigate("/top");
            }, 3800);

            return;
        } catch (error) {
            setErrorMessage("サーバーに接続できませんでした");
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">
                <div className="display-window">
                    <div className="logo-area">
                        <h1>Re:Fill</h1>
                        <p>飲料在庫管理システム</p>
                    </div>

                    <form
                        className="login-form"
                        onSubmit={(e) => {
                            e.preventDefault();
                            handleLogin();
                        }}
                    >
                        {loginStep === "idle" && (
                            <>
                                <label>ユーザーID</label>
                                <input
                                    type="text"
                                    placeholder="ユーザーID"
                                    value={userId}
                                    onChange={(e) => setUserId(e.target.value)}
                                />

                                <label>パスワード</label>
                                <input
                                    type="password"
                                    placeholder="パスワード"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />

                                <button type="submit" className="login-button">
                                    投入してログイン
                                </button>

                                {errorMessage && (
                                    <div className="error-message">{errorMessage}</div>
                                )}
                            </>
                        )}

                        {loginStep === "loading" && (
                            <div className="login-status">
                                <h2>認証中...</h2>
                                <p>しばらくお待ちください</p>
                            </div>
                        )}

                        {loginStep === "dispense" && (
                            <div className="login-status">
                                <h2>商品を排出しています...</h2>
                            </div>
                        )}

                        {loginStep === "success" && (
                            <div className="login-status">
                                <h2>ログイン成功！</h2>
                                <p>ようこそ、Re:fillへ</p>
                            </div>
                        )}
                    </form>


                </div>

                <div className="change-slot">
                    おつり
                </div>

                <div className="pickup-box">

                    {loginStep === "success" && (
                        <img
                            src="/refill-can.png"
                            alt="drink"
                            className="dispensed-drink"
                        />
                    )}

                </div>

            </div>
        </div >
    );
};

export default Login;

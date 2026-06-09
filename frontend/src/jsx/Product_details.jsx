import { useParams, useNavigate} from 'react-router-dom';
import Header from '../Header/Header';
import { useState, useMemo, useEffect } from 'react';
import useSWR from 'swr';
import './product_details.css';

const allImagesGlob = import.meta.glob(
    '/src/images/**/*.{png,jpeg,jpg,PNG,JPEG,JPG}',
    { eager: true }
);

// カテゴリーIDとフォルダ名の対応表
const CATEGORY_FOLDERS = {
  "1": "water",          // 水
  "2": "tea",            // お茶飲料
  "3": "coffe",          // コーヒー飲料
  "4": "carbonated",     // 炭酸飲料
  "5": "fruits",         // 果実・野菜飲料
  "6": "sports",         // スポーツドリンク
  "7": "health",         // 健康飲料
  "8": "energy",         // エナジードリンク
  "9": "milky",          // 乳性・乳酸菌飲料
  "10": "others"         // その他
};

const allImagesList = Object.keys(allImagesGlob).map((filePath) =>
    filePath.replace('/public', '')
);

const fetcher = async (...args) => {
    const res = await fetch(...args);

    if (!res.ok) {
        throw new Error('API error');
    }

    return res.json();
};

const Product_details = () => {
    const {productId} = useParams();
    const [productQuantity, setProductQuantity] = useState(0);
    const currentId = Number(productId) || 1;
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [inflow, setInflow] = useState('');
    const [outflow, setOutflow] = useState('');
    const [imageIndex, setImageIndex] = useState(0);

    const { data, error, isLoading, mutate } = useSWR(
        `http://localhost:8080/products/${currentId}`,
        fetcher
    );

    const isDirty = inflow !== '' || outflow !== '';
    
    const confirmBeforeLeave = () => {
    if (!isDirty) return true;

    return window.confirm(
        '入力中の内容がありますが、登録しなくてよろしいですか？'
    );};

     useEffect(() => {
    if (data) {
        const initialStock =
            Number(data?.productQuantity ?? data?.quantity ?? 0) || 0;

        // eslint-disable-next-line react-hooks/set-state-in-effect
        setProductQuantity(initialStock);
    }

    setImageIndex(0);
    setInflow('');
    setOutflow('');
}, [data, currentId]);

    useEffect(() => {
        const handleBeforeUnload = (e) => {
        if (!isDirty) return;

        e.preventDefault();
        e.returnValue = '';
    };
    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => {
        window.removeEventListener('beforeunload', handleBeforeUnload);
    };
    }, [isDirty]);

    const productName =
        data?.productName ??
        data?.product_name ??
        '';

    const productImages = useMemo(() => {
        if (!data) return [];

        // ーバーから画像名（"1.png" や "orange-juice.png"）を取得
        const rawUrl = data.product_image_url || data.productImageUrl || data.image || '';
        const fileName = rawUrl ? rawUrl.split('/').pop() : `${currentId}.jpeg`;

        // この商品のカテゴリーIDを取得（1 や 5 など）
        const catId = String(data?.categoryId?.id ?? data?.categoryId ?? '10');
        
        // 対応表からフォルダ名（"water" や "fruit_vegetable"）を取得
        const folderName = CATEGORY_FOLDERS[catId] || 'others';

        // フォルダ名を含めた正しいパスを組み立てる（例: /src/images/fruit_vegetable/orange-juice.png）
        const targetCleanId = fileName.split('.')[0];

        const matchedImages = allImagesList.filter((path) => {
            // パスの中に、正しいカテゴリーのフォルダ名とファイル名が含まれているかチェック
            return path.includes(`/${folderName}/`) && path.includes(targetCleanId);
        });

        if (matchedImages.length > 0) {
            return matchedImages;
        }

        // 見つからなかった場合のフォールバック（バックアップ）
        return [`/src/images/${folderName}/${fileName}`];
    }, [data, currentId]);

    const displayImage =
        productImages.length > 0
            ? productImages[imageIndex]
            : null;

    const handleNextImage = () => {
        if (productImages.length === 0) return;

        setImageIndex((prev) =>
            prev < productImages.length - 1
                ? prev + 1
                : 0
        );
    };

    const handlePrevImage = () => {
        if (productImages.length === 0) return;

        setImageIndex((prev) =>
            prev > 0
                ? prev - 1
                : productImages.length - 1
        );
    };

    // サーバーから返ってくる「前の商品ID」を取得
    const prevProductId =
        data?.prevProductId ??
        data?.prev_product_id;

    const handlePrev = () => {
        if (!confirmBeforeLeave()) return;

        // prevProductId が存在するときだけそのIDへ移動
        if (prevProductId) {
            navigate(`/product/${prevProductId}`);
        } else {
            alert("前の商品は存在しません（これが最初の機能です）");
        }
    };

    const nextProductId =
        data?.nextProductId ??
        data?.next_product_id;

    const handleNext = () => {
        
        if (!confirmBeforeLeave()) return;
        if (!nextProductId) return; 

        navigate(`/product/${nextProductId}`);
    };

    const handleRegister = async () => {
        if (isSubmitting) return;

        const Regex = /^\d+$/;

        if ((inflow !== '' && !Regex.test(inflow)) || 
        (outflow !== '' && !Regex.test(outflow))) {
            alert('半角数字・整数・0以上の値で入力してください');
            return;
        }

        if ((inflow !== '' && (!Regex.test(inflow) || inflow.includes('.'))) ||
        (outflow !== '' && (!Regex.test(outflow) || outflow.includes('.')))) {
            alert('半角数字・整数・0以上の値で入力してください');
            return;
        }

        if(Number(inflow) >= 9999 || Number(outflow) >= 9999){
            alert('最大桁数を超えています');
            return;
        }

        if(Number(inflow) < 0 || Number(outflow) < 0){
            alert('半角数字・整数・0以上の値で入力してください');
            return;
        }

        const inf = Number(inflow) || 0;
        const outf = Number(outflow) || 0;
        const currentQuantity = Number(productQuantity) || 0;
        const newQuantity = currentQuantity + inf - outf;

        if (newQuantity < 0) {
            alert('出庫数が在庫数を超えています');
            return;
        }

        if (newQuantity > 1000) {
            alert('在庫数が上限（1000）を超えています');
            return;
        }

        const confirmed = window.confirm(
            `以下の内容で登録しますか？
            商品名: ${productName}
            入庫数: ${inf}
            出庫数: ${outf}
            更新後在庫数: ${newQuantity}`
        );
        
        if (!confirmed) {
            return;
        }

        try {
            setIsSubmitting(true);

            // サーバーから取得した画像URL/パスから、安全に「ファイル名のみ」を抽出する
            const rawImageUrl = data?.productImageUrl ?? data?.product_image_url ?? data?.image ?? '';
            const imageName = rawImageUrl ? rawImageUrl.split('/').pop() : '';

            const updatedProductPayload = {
                categoryId: data?.categoryId?.id ?? data?.categoryId ?? 1,
                name: productName,
                quantity: newQuantity,
                image: imageName
            };

            const response = await fetch(`http://localhost:8080/products/${currentId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updatedProductPayload),
            });

            if (!response.ok) {
                console.error(`PUT失敗! ステータス: ${response.status}`);
                alert('データの更新に失敗しました。');
                return;
            }

            await mutate();
            setProductQuantity(newQuantity);

            alert('登録が完了しました');
            setInflow('');
            setOutflow('');
        } catch (error) {
            console.error('通信エラー:', error);
            alert('通信に失敗しました。');
        } finally {
            setIsSubmitting(false);
        }
    };

if (isLoading) {
        return (
            <div className="product-container">
                <Header
                    showSearch={false}
                    showCategory={false}
                    confirmLeave={confirmBeforeLeave}
                    className="product-detail-page-header"
                />
                <main className="product-main">
                    <div style={{ textAlign: 'center', padding: '40px', fontSize: '18px' }}>
                        商品データを読み込み中...
                    </div>
                </main>
            </div>
        );
    }

    if (error || !data) { //商品0件の時の表示
        return (
            <div className="product-container">
                <Header
                    showSearch={false}
                    showCategory={false}
                    className="product-detail-page-header"
                />
                <main className="product-main">
                    <div style={{ textAlign: 'center', padding: '60px 20px' }}>
                        <h2 style={{ color: '#ff4d4f', marginBottom: '16px' }}>対象の商品データがありません</h2>
                        <p style={{ color: '#666', marginBottom: '24px' }}>
                            指定されたID（ID: {currentId}）の商品データは見つかりませんでした。
                        </p>
                        <button 
                            onClick={() => navigate(-1)} // 前のページに戻る、または一覧へ
                            className="register-btn" 
                            style={{ width: 'auto', padding: '10px 24px', cursor: 'pointer' }}
                        >
                            前のページに戻る
                        </button>
                    </div>
                </main>
            </div>
        );
    }

    return (
        <div className="product-container">
            <Header
              showSearch={false}
              showCategory={false}
              confirmLeave={confirmBeforeLeave}
              className="product-detail-page-header"
            />

            <main className="product-main">
                <div className="main-content-wrapper">

                    <button
                        onClick={handlePrev}
                        disabled={currentId <= 1}
                        className="center-page-btn prev-center"
                    >
                        <img
                            src="/left.png"
                            alt="前の商品へ"
                            className="left-button-icon"
                        />
                    </button>

                    <div className="detail-container">

                        <div className="image-section">
                            <div className="image-stage">

                                <img
                                    src="/back.png"
                                    className="base-background-image"
                                    alt="背景"
                                />

                                {displayImage ? (
                                    <img
                                        src={displayImage}
                                        alt={productName}
                                        className="product-overlay-image"
                                        onError={(e) => {
                                            e.target.src = '/images/1.jpeg';
                                        }}
                                    />
                                ) : (
                                    <div>
                                        No Image
                                    </div>
                                )}

                                {productImages.length > 1 && (
                                    <>
                                        <button
                                            onClick={handlePrevImage}
                                            className="pager-btn prev-img-btn"
                                        >
                                            ‹
                                        </button>

                                        <button
                                            onClick={handleNextImage}
                                            className="pager-btn next-img-btn"
                                        >
                                            ›
                                        </button>

                                        <span className="image-counter">
                                            {imageIndex + 1} / {productImages.length}
                                        </span>
                                    </>
                                )}
                            </div>
                        </div>

                        <div className="form-section">

                            <div className="product-detail-header">
                                <span className="detail-id">
                                    ID: {currentId}
                                </span>

                                <span className="detail-name">
                                    {productName}
                                </span>
                            </div>

                            <div className="combined-stock-wrapper">
                                <div className="combined-stock-container">

                                    <div className="combined-field inflow-field">
                                        <span className="field-prefix-label">
                                            入庫数
                                        </span>

                                        <div className="combined-input-box">
                                            <input
                                                type="text"
                                                value={inflow}
                                                onChange={(e) => setInflow(e.target.value)}
                                                className="combined-input no-spin"
                                                placeholder="0"
                                                aria-label="入庫数"
                                            />
                                        </div>
                                    </div>

                                    <div className="combined-divider"></div>

                                    <div className="combined-field outflow-field">
                                        <span className="field-prefix-label">
                                            出庫数
                                        </span>

                                        <div className="combined-input-box">
                                            <input
                                                type="text"
                                                value={outflow}
                                                onChange={(e) => setOutflow(e.target.value)}
                                                className="combined-input no-spin"
                                                placeholder="0"
                                                aria-label="出庫数"
                                            />
                                        </div>
                                    </div>

                                </div>
                            </div>

                            <div className="bottom-actions-row">

                                <div className="readonly-stock-box">
                                    <span className="readonly-prefix-label">
                                        在庫数
                                    </span>

                                    <div className="readonly-input-box">
                                        <input
                                            type="number"
                                            value={productQuantity}
                                            readOnly
                                            className="combined-input no-spin short-input"
                                        />
                                    </div>
                                </div>

                                <button
                                    onClick={handleRegister}
                                    className="register-btn"
                                >
                                    登録
                                </button>

                            </div>

                        </div>
                    </div>

                    <button
                        onClick={handleNext}
                        disabled={!nextProductId}
                        className="center-page-btn next-center"
                    >
                        <img
                            src="/left.png"
                            alt="次の商品へ"
                            className="right-button-icon"
                        />
                    </button>

                </div>
            </main>
        </div>
    );
};

export default Product_details;

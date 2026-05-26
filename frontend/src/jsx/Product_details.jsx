import { useParams, useNavigate } from 'react-router-dom';
import Header from '../Header/Header';
import React, { useState, useMemo, useEffect } from 'react';
import useSWR from 'swr';
import './product_details.css';

const globalStockRegistry = {};

const allImagesGlob = import.meta.glob(
    '/public/images/**/*.{png,jpeg,jpg,PNG,JPEG,JPG}',
    { eager: true }
);

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

    const { data, error, isLoading } = useSWR(
        `http://localhost:8080/products/${currentId}`,
        fetcher
    );

     useEffect(() => {
    if (globalStockRegistry[currentId] !== undefined) {
        setProductQuantity(globalStockRegistry[currentId]);
    } else if (data) {
        const initialStock = data?.productQuantity ?? 0;
        setProductQuantity(initialStock);
    }
    setImageIndex(0);
}, [data, currentId]);

    const productName =
        data?.productName ??
        data?.product_name ??
        '';

    const productImages = useMemo(() => {
        if (!data) return [];

        const rawUrl =
            data.product_image_url ||
            data.productImageUrl ||
            '';

        let dbImageUrl = rawUrl
            ? rawUrl.replace('frontend/public', '')
            : '';

        if (dbImageUrl && !dbImageUrl.startsWith('/')) {
            dbImageUrl = '/' + dbImageUrl;
        }

        const fileName = dbImageUrl
            ? dbImageUrl.split('/').pop()
            : `${currentId}.jpeg`;

        const cleanId = fileName.split('.')[0];

        const matchedImages = allImagesList.filter((path) => {
            const fName = path.split('/').pop();
            const fNameNoExt = fName.split('.')[0];

            return (
                fNameNoExt === cleanId ||
                fNameNoExt.startsWith(`${cleanId}_`)
            );
        });

        if (matchedImages.length > 0) {
            return matchedImages;
        }

        return [`/images/${cleanId}.jpeg`];
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

    const handlePrev = () => {
        if (currentId > 1) {
            navigate(`/product/${currentId - 1}`);
        }
    };

    const rawNextProductId =
        data?.nextProductId ??
        data?.next_product_id;

    const nextProductId = (rawNextProductId && Number(rawNextProductId) <= 53) 
        ? rawNextProductId 
        : null;

    const handleNext = () => {
        if (!nextProductId) return;
    navigate(`/product/${nextProductId}`);
    };

    const handleRegister = async () => {

        if (isSubmitting) return;

        const Regex = /^\d+$/;

        if ((inflow !== '' && !Regex.test(inflow)) || 
        (outflow !== '' && !Regex.test(outflow))) {
            alert('半角数字・整数で入力してください');
            return;}

        if(inflow.includes('.') || outflow.includes('.')){
            alert('整数で入力してください');
            return;}

        if(inflow.length > 4 || outflow.length > 4){
            alert('最大桁数を超えています');
            return;}

        if(inflow < 0 || outflow < 0){
            alert('0以上の数値を入力してください');
            return;}

        if(outflow > productQuantity){
            alert('出庫数が在庫数を超えています');
            return;}

        const inf = Number(inflow) || 0;
        const outf = Number(outflow) || 0;
        const newQuantity = productQuantity + inf - outf;

    try{
        setIsSubmitting(true);

        setProductQuantity(newQuantity);
        globalStockRegistry[currentId] = newQuantity;
        setInflow('');
        setOutflow('');

    const updatedProductPayload = {
            categoryId: data?.categoryId?.id ?? data?.categoryId ?? 1,
            name: productName,
            quantity: newQuantity,
            image: data?.productImageUrl ?? ''
        };
       const response = await fetch(`http://localhost:8080/products/${currentId}`, {
    method: 'PUT',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify(updatedProductPayload),
    });

    if (!response.ok) {
        console.error('PUT失敗');
        return;
        }
        const result = await response.json();

        } catch (error) {
            console.error('通信エラー:', error);
        } finally {
            setIsSubmitting(false);
        }
    };

if (isLoading) {
        return (
            <div className="product-container">
                <Header showSearch={false} showCategory={false} />
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
                <Header showSearch={false} showCategory={false} />
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
            <Header showSearch={false} showCategory={false} />

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
                                                type="number"
                                                value={inflow}
                                                onChange={(e) =>
                                                    setInflow(e.target.value)
                                                }
                                                className="combined-input no-spin"
                                                placeholder="0"
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
                                                type="number"
                                                value={outflow}
                                                onChange={(e) =>
                                                    setOutflow(e.target.value)
                                                }
                                                className="combined-input no-spin"
                                                placeholder="0"
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
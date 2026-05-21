import { useParams, useNavigate } from 'react-router-dom';
import Header from '../Header/Header';
import React, { useState, useEffect } from 'react';
import useSWR from 'swr';
import './product_details.css';

const allImagesGlob = import.meta.glob('/public/images/**/*.{jpeg,jpg,JPEG,JPG}', { eager: true });
const allImagesList = Object.keys(allImagesGlob).map((filePath) => filePath.replace('/public', ''));

const fetcher = async (...args) => {
    const res = await fetch(...args);
    if (!res.ok) {
        throw new Error('API error');
    }
    return res.json();
};

const Product_details = () => {
    const { productId } = useParams();
    const currentId = Number(productId) || 1;
    const navigate = useNavigate();

    const [name, setProductName] = useState('');
    const [id, setProductId] = useState(0);
    const [quantity, setProductQuantity] = useState(0);
    const [text, setText] = useState('');
    const [amount, setAmount] = useState(0);

    const [inflow, setInflow] = useState('');
    const [outflow, setOutflow] = useState('');

    const [productImages, setProductImages] = useState([]);
    const [imageIndex, setImageIndex] = useState(0);  

    const { data, error, isLoading } = useSWR(`http://localhost:8080/products?product_id=${currentId}`, fetcher);

    useEffect(() => {
        if (data) {
            setProductId(data.productId || data.product_id || 0);
            setProductName(data.productName || data.product_name || "");
            setProductQuantity(data.productQuantity ?? data.product_quantity ?? 0);
            setText(data.productDescription || data.product_description || ""); 
            setAmount(data.productPrice || data.product_price || 0);
            
            setInflow('');
            setOutflow('');
            setImageIndex(0);

            const rawUrl = data.product_image_url || data.productImageUrl || "";
            let dbImageUrl = rawUrl ? rawUrl.replace('frontend/public', '') : '';

            if (dbImageUrl && !dbImageUrl.startsWith('/')) {
                dbImageUrl = '/' + dbImageUrl;
            }

            const fileName = dbImageUrl ? dbImageUrl.split('/').pop() : `${currentId}.jpeg`;
            const cleanId = fileName.split('.')[0]; 

            const matchedImages = allImagesList.filter((path) => {
                const fName = path.split('/').pop();
                const fNameNoExt = fName.split('.')[0];
                return fNameNoExt === cleanId || fNameNoExt.startsWith(`${cleanId}_`);
            });

            if (matchedImages.length > 0) {
                setProductImages(matchedImages);
            } else {
                setProductImages([`/images/${cleanId}.jpeg`]);
            }
        }
    }, [data, currentId]); 

    if (error) return <div className="error">データの読み込みに失敗しました。Javaサーバーが起動しているか、CORS許可があるか確認してください。</div>;
    if (isLoading) return <div className="loading">読み込み中...</div>;

    const displayImage = productImages.length > 0 ? productImages[imageIndex] : null;

    const handleNextImage = () => {
        if (productImages.length === 0) return;
        if (imageIndex < productImages.length - 1) {
            setImageIndex(imageIndex + 1);
        } else {
            setImageIndex(0); 
        }
    };

    const handlePrevImage = () => {
        if (productImages.length === 0) return;
        if (imageIndex > 0) {
            setImageIndex(imageIndex - 1);
        } else {
            setImageIndex(productImages.length - 1);
        }
    };

    const handlePrev = () => {
        if (currentId > 1) navigate(`/product/${currentId - 1}`);
    };

    const handleNext = () => {
       navigate(`/product/${currentId + 1}`);
    };

    const handleRegister = () => {
        const inf = Number(inflow) || 0;
        const outf = Number(outflow) || 0;
        alert(`【変更を保存しました】\n商品ID: ${currentId}\n商品名: ${name}\n入庫数: ${inf} / 出庫数: ${outf}`);
    };

    return (
        <div className="product-container">
            <Header showSearch={false} showCategory={false} />

            <main className="product-main">
                <div className='main-content-wrapper'>

                    {/* 左移動ボタン */}
                    <button 
                        onClick={handlePrev} 
                        disabled={currentId <= 1} 
                        className="center-page-btn prev-center" 
                        title="前の商品へ"
                    >
                        <img src='/left.png' alt='前の商品へ' className='left-button-icon' />
                    </button>

                    {/* メイン詳細コンテンツ */}
                    <div className="detail-container">
                        
                        {/* 左側：画像セクション */}
                        <div className="image-section">
                            <div className="image-stage">
                                <img 
                                    src="/back.png"
                                    className="base-background-image" 
                                    alt="ベース背景"
                                />

                                {displayImage ? (
                                    <img 
                                        src={displayImage} 
                                        alt={name} 
                                        className="product-overlay-image" 
                                        onError={(e) => {
                                            e.target.src = "/images/1.jpeg";
                                        }}
                                    />
                                ) : (
                                    <div style={{ position: 'absolute', zIndex: 3, color: '#999', fontSize: '0.9rem' }}>
                                        No Image (商品ID: {currentId})
                                    </div>
                                )}

                                {productImages.length > 1 && (
                                    <>
                                        <button onClick={handlePrevImage} className="pager-btn prev-img-btn">‹</button>
                                        <button onClick={handleNextImage} className="pager-btn next-img-btn">›</button>
                                        <span className="image-counter">
                                            {imageIndex + 1} / {productImages.length}
                                        </span>
                                    </>
                                )}
                            </div>

                            {productImages.length === 0 && (
                                <div style={{ marginTop: '12px', fontSize: '0.85rem', color: '#e53e3e', textAlign: 'center' }}>
                                    ※ 画像URLが正しく取得できませんでした
                                </div>
                            )}
                        </div>

                        {/* 右側：フォームセクション */}
                        <div className="form-section">
                            
                            {/* ヘッダー：「ID 商品名」と下点線 */}
                            <div className="product-detail-header">
                                <span className="detail-id">ID: {currentId}</span>
                                <span className="detail-name">{name}</span>
                            </div>
                            
                            {/* 入出庫合体エリア */}
                            <div className="combined-stock-wrapper">
                                <div className="combined-stock-container">
                                    
                                    {/* 入庫（左寄せ数字＋薄青ボックス） */}
                                    <div className="combined-field inflow-field">
                                        <span className="field-prefix-label">入庫数</span>
                                        <div className="combined-input-box">
                                            <input 
                                                type="number" 
                                                value={inflow} 
                                                onChange={(e) => setInflow(e.target.value)} 
                                                className="combined-input no-spin"
                                                placeholder="0"
                                            />
                                        </div>
                                    </div>
                                    
                                    <div className="combined-divider"></div>
                                    
                                    {/* 出庫（左寄せ数字＋薄青ボックス） */}
                                    <div className="combined-field outflow-field">
                                        <span className="field-prefix-label">出庫数</span>
                                        <div className="combined-input-box">
                                            <input 
                                                type="number" 
                                                value={outflow} 
                                                onChange={(e) => setOutflow(e.target.value)} 
                                                className="combined-input no-spin"
                                                placeholder="0"
                                            />
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* 下部アクションエリア */}
                            <div className="bottom-actions-row">
                                {/* 在庫数（右寄せ数字＋薄青ボックス） */}
                                <div className="readonly-stock-box">
                                    <span className="readonly-prefix-label">在庫数</span>
                                    <div className="readonly-input-box">
                                        <input 
                                            type="number" 
                                            value={quantity} 
                                            readOnly
                                            className="combined-input no-spin short-input"
                                        />
                                    </div>
                                </div>

                                <button onClick={handleRegister} className="register-btn">
                                    登録
                                </button>
                            </div>

                        </div>
                    </div> 

                    {/* 右移動ボタン */}
                    <button 
                        onClick={handleNext} 
                        className="center-page-btn next-center" 
                        title="次の商品へ"
                    >
                        <img src='/left.png' alt='次の商品へ' className='right-button-icon' />
                    </button>

                 </div>
            </main>
        </div>
    );
};

export default Product_details;
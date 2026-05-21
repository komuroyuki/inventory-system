import { useParams, useNavigate } from 'react-router-dom';
import Header from '../Header/Header';
import React,{useState , useEffect} from 'react';
import useSWR from 'swr';
import './product_details.css';

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

    const[name,setProduct_Name] = useState('');
    const[id,setProduct_Id] = useState(0);
    const[quantity,setProduct_Quantity] = useState(0);
    const[imagepreview,setImagePreview] = useState('');
    const[text,setText] = useState('');
    const[amount,setAmount] = useState(0);

    const { data, error, isLoading } = useSWR(`http://localhost:8080/products?product_id=${currentId}`, fetcher);

    useEffect(() => {
        if (data) {
            setProduct_Name(data.name || "");
            setProduct_Quantity(data.quantity || 0);
            setText(data.description || "");
            setAmount(data.price || 0);
            
            setImagePreview(`/images/${data.image}`);
        }
    }, [data, currentId]); 

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setImagePreview(imageUrl);
        }
    };

    const handleRegister = () => {
        alert(`【変更を保存しました】\n商品ID: ${currentId}\n商品名: ${name}`);
    };

    const handlePrev = () => {
        if (currentId > 1) navigate(`/product/${currentId - 1}`);
    };

    const handleNext = () => {
       navigate(`/product/${currentId + 1}`);
    };

    if (error) return <div className="error">データの読み込みに失敗しました。</div>;

    return (
        <div className="product-container">

            <Header />

            <main className="product-main">
                
                <div className="navigation-box">
                    <button onClick={handlePrev} disabled={currentId <= 1} className="arrow-btn">
                        ◀ 前の商品
                    </button>
                    <span className="id-display">商品番号: {currentId}</span>
                    <button onClick={handleNext} className="arrow-btn">
                        次の商品 ▶
                    </button>
                </div>

                <div className="detail-container">
                    
                    <div className="image-section">
                        
                        <div className="image-stage">
                            <img 
                                src="/back.png"
                                alt="背景台座" 
                                className="base-background-image" 
                            />

                            <img src={imagepreview} alt="商品プレビュー" className="product-overlay-image" />
                        </div>
                        
                        <div className="image-upload-group">
                            <label className="image-upload-label">
                                画像を変更する
                                <input 
                                    type="file" 
                                    accept="image/*" 
                                    onChange={handleImageChange} 
                                    className="file-input"
                                />
                            </label>
                        </div>
                    </div>

                    <div className="form-section">
                        <h2>商品詳細編集</h2>
                        
                        <div className="form-group">
                            <label>商品名</label>
                            <input 
                                type="text" 
                                value={name} 
                                onChange={(e) => setProduct_Name(e.target.value)} 
                                className="form-input"
                            />
                        </div>

                        <div className="form-group">
                            <label>在庫数量</label>
                            <input 
                                type="number" 
                                value={quantity} 
                                onChange={(e) => setProduct_Quantity(Number(e.target.value))} 
                                className="form-input"
                            />
                        </div>

                        <div className="form-group">
                            <label>商品説明</label>
                            <textarea 
                                value={text} 
                                onChange={(e) => setText(e.target.value)} 
                                className="form-textarea"
                                rows="4"
                            />
                        </div>

                        <button onClick={handleRegister} className="register-btn">
                            この内容で変更を登録する
                        </button>
                    </div>

                </div>
            </main>
        </div>
    );
};

export default Product_details;
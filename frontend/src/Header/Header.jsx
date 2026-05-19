import './Header.css';
import React, {useState} from 'react';

const Header = () =>{
    const [keyword, setKeyword] = useState('');
    const [selectedCategory, setSelectedCategory] = useState('すべて');
    const category = ['すべて','水','お茶飲料','コーヒー飲料','炭酸飲料','果実・野菜飲料','スポーツドリンク','健康飲料','エナジードリンク','乳性・乳酸菌飲料','その他'];
    const [isOpen, setIsOpen] = useState(false);
    return(
        <header className='header'>
            <div className='header-logo'>
                <h1>
                    <a href='/'>
                    <img src='/logo.png' alt='マイサイトのロゴ' className='logo-image' />
                    </a>
                </h1>
            </div>

            <div className='header-area'>

            <div className='header-keyword'>
                <input 
                 type="text"
                 placeholder='商品名を入力'
                 value={keyword} 
                 onChange={(e) => setKeyword(e.target.value)}
                 className='header-keyword-input'
                  />
                  <button type="button" className='header-keyword-button'>
                    <img src='/keyword-button.png' alt='検索' className='header-keyword-button-icon' />
                </button>
                </div>
                
                <div className='category'>
                    <div className='category-list' onClick={() => setIsOpen(!isOpen)} >
                        <span>{selectedCategory}</span>
                    </div>
                    
                    {isOpen && (
                        <ul className='category-option'>
                            {category.map((item) =>{ 
                                return(
                                    <li
                                         key={item}
                                         className='category-option'
                                         onClick={() =>{
                                            setSelectedCategory(item);
                                            setIsOpen(false);
                                         }}
                                         >
                                            {item}
                                         </li>
                                )
                            })}
                        </ul>
                    )}
                </div>
                </div>

        </header>

    );
};

export default Header;
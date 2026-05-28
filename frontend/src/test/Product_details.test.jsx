import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Product_details from '../jsx/Product_details.jsx';
import { describe, it, expect, vi } from 'vitest';

vi.mock ('../Header/Header.jsx',() => {
    return {
        default: () => <div>Header</div>
    };
});

vi.mock ('swr',() => {
    return {
        default: () => ({
            data:{ //モックデータを定義
                productName:'いろはす',
                productId:1,
                productImage:'https://www.cocacola.jp/images/contents/brand/irohasu.png',
                productQuantity:10,
                nextProductId:2,
            },
            error: null,
            isLoading: false,
            mutate: vi.fn(),
        })
    };
});

vi.mock('react-router-dom', () => ({
    useParams: () => ({ productId: '1' }), // 常にID: 1の商品として動かす
    useNavigate: () => vi.fn(), //ボタンを押した時にページ遷移するためのモック関数を返す
}));

describe('test product details',() => {
    it ('詳細画面が正常に表示されること', () => {
        render(<Product_details />);
        expect(screen.getByText('いろはす')).toBeInTheDocument();
    });
});


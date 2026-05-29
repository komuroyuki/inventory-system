import '@testing-library/jest-dom';
import { fireEvent, within, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Product_details from '../jsx/Product_details.jsx';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

vi.mock ('../Header/Header.jsx',() => {
    return {
        default: () => <div>Header</div>
    };
});

const mockData = {
    productName: 'いろはす',
    productId: 1,
    productImageUrl: 'https://example.com',
    productQuantity: 10,
    nextProductId: 2,
};

vi.mock('swr', () => ({
    default: () => ({
        data: mockData,
        error: null,
        isLoading: false,
        mutate: vi.fn(),
    }),
}));

vi.mock('react-router-dom', () => ({
    useParams: () => ({ productId: '1' }), // 常にID:1
    useNavigate: () => vi.fn(), //ボタンを押した時にページ遷移するためのモック
}));

describe('test product details',() => {
    it ('詳細画面が正常に表示されること', () => {
        render(<Product_details />);

        expect(screen.getByText('いろはす')).toBeInTheDocument();
        expect(screen.getByText('ID: 1')).toBeInTheDocument();
    });
});

describe('test product details エラー',() => {

    let alertSpy;

    beforeEach(() => { 
        global.fetch = vi.fn().mockResolvedValue({ //fetchのモック
            ok: true,
            json: async () => ({ success: true }),
        });
        alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {}); //alertのモック
    });

    afterEach(() => {
        alertSpy.mockRestore();
    }); //スパイリセット
    
    it('小数を入力した時のエラー', async () => {
    render(<Product_details />);

    const user = userEvent.setup();

    const inflowWrapper = screen.getByText('入庫数').closest('.inflow-field');
    const inputElement = within(inflowWrapper).getByPlaceholderText('0');
    const buttonElement = screen.getByRole('button', { name: '登録' });

    await user.type(inputElement, '1.2');
    await user.click(buttonElement);

    expect(alertSpy).toHaveBeenCalledWith('半角数字・整数・0以上の値で入力してください');
});

it('最大桁数を超えた時のエラー', async () => {
    render(<Product_details />);
    const user = userEvent.setup();

    const inflowWrapper = screen.getByText('入庫数').closest('.inflow-field');
    const inputElement = within(inflowWrapper).getByPlaceholderText('0');
    const buttonElement = screen.getByRole('button', { name: '登録' });

    await user.type(inputElement, '10000');
    await user.click(buttonElement);

    expect(alertSpy).toHaveBeenCalledWith('最大桁数を超えています');
});

it('マイナス値を入力した時のエラー', async () => {
    render(<Product_details />);
    const user = userEvent.setup();

    const inflowWrapper = screen.getByText('入庫数').closest('.inflow-field');
    const inputElement = within(inflowWrapper).getByPlaceholderText('0');
    const buttonElement = screen.getByRole('button', { name: '登録' });

    await user.type(inputElement, '-1');
    await user.click(buttonElement);

    expect(alertSpy).toHaveBeenCalledWith('半角数字・整数・0以上の値で入力してください');
});

it('出庫数が在庫数を超えた時のエラー', async () => {
    render(<Product_details />);
    const user = userEvent.setup();

    const outflowWrapper = screen.getByText('出庫数').closest('.outflow-field');
    const inputElement = within(outflowWrapper).getByPlaceholderText('0');
    const buttonElement = screen.getByRole('button', { name: '登録' });

    await user.type(inputElement, '20'); // 在庫10より多い
    await user.click(buttonElement);

    expect(alertSpy).toHaveBeenCalledWith('出庫数が在庫数を超えています');
});

it('在庫上限を超えた時のエラー', async () => {
    render(<Product_details />);
    const user = userEvent.setup();

    const inflowWrapper = screen.getByText('入庫数').closest('.inflow-field');
    const inputElement = within(inflowWrapper).getByPlaceholderText('0');
    const buttonElement = screen.getByRole('button', { name: '登録' });

    await user.type(inputElement, '995'); // 10 + 995 = 1005
    await user.click(buttonElement);

    expect(alertSpy).toHaveBeenCalledWith('在庫数が上限（1000）を超えています');
});

it('正常に登録できること', async () => {
    render(<Product_details />);
    const user = userEvent.setup();

    const inflowWrapper = screen.getByText('入庫数').closest('.inflow-field');
    const inputElement = within(inflowWrapper).getByPlaceholderText('0');
    const buttonElement = screen.getByRole('button', { name: '登録' });

    await user.type(inputElement, '5');
    await user.click(buttonElement);

    // alertが出ないこと確認
    expect(alertSpy).not.toHaveBeenCalled();
});

describe('test product details ボタン操作',() => {
    it('ID: 1のとき、非活性であること', async () => {
    render(<Product_details />);
    
    const prevButton = screen.getByRole('button', { name: '前の商品へ' });
    
    expect(prevButton).toBeDisabled(); 
});
    it('最終IDのとき、非活性であること', async () => {
    vi.spyOn(mockData, 'nextProductId', 'get').mockReturnValue(null);
    render(<Product_details />);

    const nextButton = screen.getByRole('button', { name: '次の商品へ' });
    
    expect(nextButton).toBeDisabled();
});
});

    it('データ取得中のローディング表示がされること', () => {
        // このテストだけ一時的にisLoadingをtrueにする
        vi.spyOn(useSWR, 'default').mockReturnValue({
            data: null,
            error: null,
            isLoading: true,
            mutate: vi.fn(),
        });
        render(<Product_details />);
        expect(screen.getByText('商品データを読み込み中...')).toBeInTheDocument();
    });

    it('商品データが存在しない（エラー）のときの表示がされること', () => {
        // このテストだけ一時的にerrorを発生させる
        vi.spyOn(useSWR, 'default').mockReturnValue({
            data: null,
            error: new Error('API error'),
            isLoading: false,
            mutate: vi.fn(),
        });
        render(<Product_details />);
        expect(screen.getByText('対象の商品データがありません')).toBeInTheDocument();
    });

    it('サーバーへの登録（PUT）が失敗したときにエラーログが出ること', async () => {
        render(<Product_details />);
        const user = userEvent.setup();

        // fetchが「失敗（ok: false）」を返すようにこのテストだけ上書き
        global.fetch = vi.fn().mockResolvedValue({
            ok: false,
            status: 500,
        });
        
        // console.errorを見張る
        const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

        const inflowWrapper = screen.getByText('入庫数').closest('.inflow-field');
        const inputElement = within(inflowWrapper).getByPlaceholderText('0');
        const buttonElement = screen.getByRole('button', { name: '登録' });

        await user.type(inputElement, '5');
        await user.click(buttonElement);

        // 本番コードの「console.error('PUT失敗')」が動いたか確認
        expect(consoleSpy).toHaveBeenCalledWith('PUT失敗');
        consoleSpy.mockRestore();
    });
});

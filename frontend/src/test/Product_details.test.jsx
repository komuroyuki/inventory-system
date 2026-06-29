import '@testing-library/jest-dom';
import { within, render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Product_details from '../jsx/Product_details.jsx';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import useSWR from 'swr';
import { useParams, useNavigate } from 'react-router-dom';

vi.mock('../Header/Header.jsx', () => {
    return {
        default: () => <div>Header</div>
    };
});

vi.mock(import("swr"), async (importOriginal) => {
    const actual = await importOriginal();
    return {
        ...actual,
        default: vi.fn(),
    };
});

vi.mock('react-router-dom', () => ({
    useParams: vi.fn(),
    useNavigate: vi.fn(),
}));

const mockData = {
    productName: 'いろはす',
    productId: 1,
    productImageUrl: 'https://example.com',
    productQuantity: 10,
    nextProductId: 2,
};

const mockNavigate = vi.fn();
const mockedUseSWR = vi.mocked(useSWR);
const mockedUseParams = vi.mocked(useParams);
const mockedUseNavigate = vi.mocked(useNavigate);

describe('test product details', () => {
    it('詳細画面が正常に表示されること', () => {
        mockedUseParams.mockReturnValue({ productId: '1' });
        mockedUseNavigate.mockReturnValue(mockNavigate);
        mockedUseSWR.mockReturnValue({
            data: mockData,
            error: null,
            isLoading: false,
            mutate: vi.fn(),
        });

        render(<Product_details />);
        expect(screen.getByText('いろはす')).toBeInTheDocument();
        expect(screen.getByText('ID: 1')).toBeInTheDocument();
    });
});

describe('test product details エラー', () => {
    let alertSpy;

    beforeEach(() => {
        mockedUseParams.mockReturnValue({ productId: '1' });
        mockedUseNavigate.mockReturnValue(mockNavigate);

        mockedUseSWR.mockReturnValue({
            data: mockData,
            error: null,
            isLoading: false,
            mutate: vi.fn(),
        });

        global.fetch = vi.fn().mockResolvedValue({
            ok: true,
            json: async () => ({ success: true }),
        });
        
        vi.spyOn(window, 'confirm').mockReturnValue(true);
        alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
        vi.spyOn(console, 'error').mockImplementation(() => {});

        localStorage.setItem("user_role", "admin");
    });

    afterEach(() => {
        vi.clearAllMocks();
        alertSpy.mockRestore();
    });

    describe('Product_details 表示', () => {
        it('詳細画面が正常に表示されること', () => {
            render(<Product_details />);
            expect(screen.getByText('いろはす')).toBeInTheDocument();
            expect(screen.getByText('ID: 1')).toBeInTheDocument();
        });
    });

    describe('入力バリデーション', () => {
        // 各テストでまっさらな状態から入力を行えるよう、画面描画と要素取得のみを行うよう修正
        const setup = async () => {
            render(<Product_details />);
            const user = userEvent.setup();
            const input = screen.getByLabelText('入庫数');
            const button = screen.getByRole('button', { name: '登録' });
            return { user, input, button };
        };

        it('不正な入力値エラー（小数・マイナス）', async () => {
            const { user, input, button } = await setup();
            await user.type(input, '-1.5');
            await user.click(button);
            expect(window.alert).toHaveBeenCalledWith('半角数字・整数・0以上の値で入力してください');
        });

        it('最大桁数エラー', async () => {
            const { user, input, button } = await setup();
            await user.type(input, '10000');
            await user.click(button);
            expect(window.alert).toHaveBeenCalledWith('上限を超えています');
        });

        it('在庫上限エラー', async () => {
            const { user, input, button } = await setup();
            await user.type(input, '995');
            await user.click(button);
            expect(window.alert).toHaveBeenCalledWith('在庫数が上限（1000）を超えています');
        });

        it('正常登録', async () => {
          const { user, input, button } = await setup();
          await user.type(input, '5'); 
          await user.click(button);

          expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/products/1',
            expect.objectContaining({
              method: 'PUT',
              body: expect.stringContaining('"quantity":15'), })
        );});
    });

    describe('出庫バリデーション', () => {
        it('在庫超過エラー', async () => {
            render(<Product_details />);
            const user = userEvent.setup();
            const input = screen.getByLabelText('出庫数');
            const button = screen.getByRole('button', { name: '登録' });
            
            await user.type(input, '20');
            await user.click(button);
            
            expect(window.alert).toHaveBeenCalledWith('出庫数が在庫数を超えています');
        });
    });

    describe('ボタン状態', () => {
        it('前の商品へが無効', () => {
            render(<Product_details />);
            expect(screen.getByRole('button', { name: '前の商品へ' })).toBeDisabled();
        });

        it('次の商品へが無効', () => {
            mockedUseSWR.mockReturnValue({
                data: { ...mockData, nextProductId: null },
                error: null,
                isLoading: false,
                mutate: vi.fn(),
            });
            render(<Product_details />);
            expect(screen.getByRole('button', { name: '次の商品へ' })).toBeDisabled();
        });
    });

    describe('ローディング・エラー画面', () => {
        it('ローディング表示', () => {
            mockedUseSWR.mockReturnValue({ data: null, error: null, isLoading: true, mutate: vi.fn() });
            render(<Product_details />);
            expect(screen.getByText('商品データを読み込み中...')).toBeInTheDocument();
        });

        it('エラー表示', () => {
            mockedUseSWR.mockReturnValue({ data: null, error: new Error('API error'), isLoading: false, mutate: vi.fn() });
            render(<Product_details />);
            expect(screen.getByText('対象の商品データがありません')).toBeInTheDocument();
        });
    });

    describe('非同期通信・例外処理', () => {
        it('登録キャンセル時は処理されない', async () => {
            vi.spyOn(window, 'confirm').mockReturnValue(false);
            render(<Product_details />);
            const user = userEvent.setup();
            const input = screen.getByLabelText('入庫数');
            const button = screen.getByRole('button', { name: '登録' });

            await user.type(input, '5');
            await user.click(button);
            expect(window.alert).not.toHaveBeenCalled();
        });

        it('API失敗時はエラー処理される', async () => {
            global.fetch = vi.fn().mockResolvedValue({ ok: false });
            render(<Product_details />);
            const user = userEvent.setup();
            const input = screen.getByLabelText('入庫数');
            const button = screen.getByRole('button', { name: '登録' });

            await user.type(input, '5');
            await user.click(button);
            
            // 完全一致から「含まれているか」のチェックに修正
            expect(console.error).toHaveBeenCalledWith(
                expect.stringContaining('PUT失敗')
            );
        });

        it('通信エラー時はcatchに入る', async () => {
            global.fetch = vi.fn().mockRejectedValue(new Error('network error'));
            render(<Product_details />);
            const user = userEvent.setup();
            const input = screen.getByLabelText('入庫数');
            const button = screen.getByRole('button', { name: '登録' });

            await user.type(input, '5');
            await user.click(button);
            expect(console.error).toHaveBeenCalled();
        });
    });

    it('正常登録後に完了メッセージが表示される', async () => {
      render(<Product_details />);

      const user = userEvent.setup();
      const input = screen.getByLabelText('入庫数');
      const button = screen.getByRole('button', { name: '登録' });
      
      await user.type(input, '5');
      await user.click(button);

      await waitFor(() => {
        expect(window.alert).toHaveBeenCalledWith('登録が完了しました');
      });
    });

    describe('画像処理', () => {
        it('画像URLなしでfallback画像になる', () => {
            mockedUseSWR.mockReturnValue({
                data: { productName: 'test', productQuantity: 1, productImageUrl: '' },
                error: null,
                isLoading: false,
                mutate: vi.fn(),
            });
            render(<Product_details />);
            expect(screen.getByAltText('test')).toBeInTheDocument();
        });

        it('画像読み込みエラー時にフォールバック画像に切り替わること', async () => {
            render(<Product_details />);
            const img = screen.getByAltText('いろはす');
            
            fireEvent.error(img);
            
            await waitFor(() => {
                expect(img.getAttribute('src')).toContain('/images/1.jpeg');
            });
        });
    });

    describe('画面遷移ハンドラー関数', () => {
        it('入力がある状態で商品移動をして、確認でキャンセルする', async () => {
            vi.spyOn(window, 'confirm').mockReturnValue(false);
            mockedUseParams.mockReturnValue({ productId: '5' });

            const user = userEvent.setup();
            render(<Product_details />);

            const input = screen.getByLabelText('入庫数');
            await user.type(input, '10');

            const prevBtn = screen.getByRole('button', { name: '前の商品へ' });
            await user.click(prevBtn);

            expect(alertSpy).not.toHaveBeenCalled();
            expect(window.confirm).toHaveBeenCalled();

            expect(mockNavigate).not.toHaveBeenCalled();
        });

        it('次へボタンで画面が遷移する', async () => {
            vi.spyOn(window, 'confirm').mockReturnValue(true);
            mockedUseParams.mockReturnValue({ productId: '1' });
            mockedUseSWR.mockReturnValue({
                data: { ...mockData, nextProductId: 2 }, 
                error: null,
                isLoading: false,
                mutate: vi.fn(),
            });

            const user = userEvent.setup();
            render(<Product_details />);

            const nextBtn = screen.getByRole('button', { name: '次の商品へ' });
            await user.click(nextBtn);
            expect(mockNavigate).toHaveBeenCalledWith('/product/2');
        });

        it('データ不在時の戻るボタン', async () => {
            mockedUseSWR.mockReturnValue({
                data: null,
                error: new Error('API error'),
                isLoading: false,
                mutate: vi.fn(),
            });

            const user = userEvent.setup();
            render(<Product_details />);

            const backBtn = screen.getByRole('button', { name: '前のページに戻る' });
            await user.click(backBtn);
            expect(mockNavigate).toHaveBeenCalledWith(-1);
        });
    });
});


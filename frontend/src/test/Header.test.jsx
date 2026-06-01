import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Header from '../Header/Header.jsx';

const mockSetSearchParams = vi.fn();

vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: vi.fn(),
    useSearchParams: () => [new URLSearchParams(), mockSetSearchParams],
  };
});

describe('Headerコンポーネントのテスト', () => {
  const originalLocation = window.location;

  beforeEach(() => {
    vi.clearAllMocks();
    
    global.alert = vi.fn();

    delete window.location;
    window.location = { href: '' };
  });

  afterEach(() => {
    window.location = originalLocation;
  });

  describe('初期表示とpropsの制御', () => {
    it('デフォルトで検索バーとカテゴリが表示されること', () => {
      const { getByPlaceholderText, getByText } = render(
        <BrowserRouter><Header /></BrowserRouter>
      );
      expect(getByPlaceholderText('商品名を入力')).toBeDefined();
      expect(getByText('すべて')).toBeDefined();
    });

    it('showSearchとshowCategoryがfalseの場合、非表示になること', () => {
      const { queryByPlaceholderText, queryByText } = render(
        <BrowserRouter><Header showSearch={false} showCategory={false} /></BrowserRouter>
      );
      expect(queryByPlaceholderText('商品名を入力')).toBeNull();
      expect(queryByText('すべて')).toBeNull();
    });
  });

  describe('検索機能（handleSearch, handleKeyDown）のテスト', () => {
    it('入力したキーワードで検索が実行されること', () => {
      const { getByPlaceholderText, getByAltText } = render(
        <BrowserRouter><Header /></BrowserRouter>
      );
      
      const input = getByPlaceholderText('商品名を入力');
      fireEvent.change(input, { target: { value: 'お茶' } });
      
      const searchButton = getByAltText('検索');
      fireEvent.click(searchButton);

      expect(mockSetSearchParams).toHaveBeenCalled();
      const params = mockSetSearchParams.mock.calls[0][0];
      expect(params.get('keyword')).toBe('お茶');
      expect(params.get('category_id')).toBe('0');
    });

    it('Enterキーで検索が実行されること', () => {
      const { getByPlaceholderText } = render(
        <BrowserRouter><Header /></BrowserRouter>
      );
      
      const input = getByPlaceholderText('商品名を入力');
      fireEvent.change(input, { target: { value: 'コーヒー' } });
      fireEvent.keyDown(input, { key: 'Enter', code: 'Enter' });

      expect(mockSetSearchParams).toHaveBeenCalled();
    });

    it('IME入力中（変換中）はEnterキーを押しても検索されないこと', () => {
      const { getByPlaceholderText } = render(
        <BrowserRouter><Header /></BrowserRouter>
      );
      
      const input = getByPlaceholderText('商品名を入力');
      fireEvent.change(input, { target: { value: 'みず' } });
      
      fireEvent.keyDown(input, { key: 'Enter', code: 'Enter', keyCode: 229 });
      expect(mockSetSearchParams).not.toHaveBeenCalled();

      fireEvent.keyDown(input, { key: 'Enter', code: 'Enter', keyCode: 13 });
      expect(mockSetSearchParams).toHaveBeenCalled();
    });

    it('50文字を超えるキーワードを入力した場合、アラートが出て検索が中断されること', () => {
      const { getByPlaceholderText, getByAltText } = render(
        <BrowserRouter><Header /></BrowserRouter>
      );
      
      const input = getByPlaceholderText('商品名を入力');
      fireEvent.change(input, { target: { value: 'a'.repeat(51) } });
      
      const searchButton = getByAltText('検索');
      fireEvent.click(searchButton);

      expect(global.alert).toHaveBeenCalledWith('50文字以内で入力してください');
      expect(mockSetSearchParams).not.toHaveBeenCalled();
    });
  });

  describe('カテゴリ選択機能（handleCategorySelect）のテスト', () => {
    it('カテゴリを選択すると、即座に正しいIDに変換されて検索が実行されること', () => {
      const { getByText } = render(
        <BrowserRouter><Header /></BrowserRouter>
      );

      const currentCategory = getByText('すべて');
      fireEvent.click(currentCategory);
      
      const waterCategory = getByText('水');
      fireEvent.click(waterCategory);

      expect(mockSetSearchParams).toHaveBeenCalled();
      const params = mockSetSearchParams.mock.calls[0][0];
      expect(params.get('category_id')).toBe('1');
    });
  });

  describe('ロゴクリックによる遷移（handleLogoClick）のテスト', () => {
    it('confirmLeaveが設定されていない場合、無条件でトップへ遷移すること', () => {
      const { getByAltText } = render(
        <BrowserRouter><Header /></BrowserRouter>
      );
      
      const logo = getByAltText('マイサイトのロゴ');
      fireEvent.click(logo);

      expect(window.location.href).toBe('/');
    });

    it('confirmLeaveがtrueを返した場合、トップへ遷移すること', () => {
      const mockConfirmLeave = vi.fn(() => true);
      const { getByAltText } = render(
        <BrowserRouter><Header confirmLeave={mockConfirmLeave} /></BrowserRouter>
      );
      
      const logo = getByAltText('マイサイトのロゴ');
      fireEvent.click(logo);

      expect(mockConfirmLeave).toHaveBeenCalled();
      expect(window.location.href).toBe('/');
    });

    it('confirmLeaveがfalseを返した場合、遷移がキャンセルされること', () => {
      const mockConfirmLeave = vi.fn(() => false);
      const { getByAltText } = render(
        <BrowserRouter><Header confirmLeave={mockConfirmLeave} /></BrowserRouter>
      );
      
      window.location.href = 'current-page';

      const logo = getByAltText('マイサイトのロゴ');
      fireEvent.click(logo);

      expect(mockConfirmLeave).toHaveBeenCalled();
      expect(window.location.href).toBe('current-page');
    });
  });
});
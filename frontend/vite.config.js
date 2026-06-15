import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  
  test: {
    globals: true,             // 毎回テストファイルで `describe` や `expect` をインポートしなくてよくする設定
    environment: 'jsdom',      // ブラウザ環境を擬似的に再現する jsdom を指定
    setupFiles: './src/test/setup.js', // テスト開始前に自動で読み込む設定ファイル
  },
});
import { describe, it, expect } from 'vitest';

// テスト対象の関数を想定
function add(a, b) { return a + b; }

describe('add関数のテスト', () => {
  it('1と2を足すと3になること', () => {
    // 1. 実行
    const result = add(1, 2);
    // 2. 検証 (Expect)
    expect(result).toBe(3);
  });
});
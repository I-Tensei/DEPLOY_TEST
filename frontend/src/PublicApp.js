import React, { useEffect, useState } from 'react';
import './public.css';

// 一般ユーザー向けの軽量在庫閲覧画面（黄色基調）
export default function PublicApp() {
  const [items, setItems] = useState([]);
  const [search, setSearch] = useState('');
  const [onlyInStock, setOnlyInStock] = useState(false);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const res = await fetch('/api/items');
        const data = await res.json();
        setItems(data);
      } catch (e) {
        console.error(e);
        alert('在庫の取得に失敗しました');
      }
    };
    fetchItems();
  }, []);

  const filterd = items.filter((it) => {
    const hit =
      it.itemName?.toLowerCase().includes(search.toLowerCase()) ||
      it.itemNumber?.toLowerCase().includes(search.toLowerCase()) ||
      it.modelNumber?.toLowerCase().includes(search.toLowerCase());
    const stock = !onlyInStock || it.inStock;
    return hit && stock;
  });

  return (
    <div className="pub-app">
      <header className="pub-header">
        <h1>在庫ビュー</h1>
        <div className="pub-actions">
          <input
            className="pub-search"
            placeholder="検索: 名称/番号/型番"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <label className="pub-check">
            <input
              type="checkbox"
              checked={onlyInStock}
              onChange={(e) => setOnlyInStock(e.target.checked)}
            />
            在庫ありのみ
          </label>
        </div>
      </header>

      <main className="pub-content">
        {filterd.length === 0 ? (
          <p className="pub-empty">対象の在庫がありません</p>
        ) : (
          <div className="pub-grid">
            {filterd.map((it) => (
              <div key={it.id} className={`pub-card ${it.inStock ? 'ok' : 'ng'}`}>
                <div className="pub-title">{it.itemName}</div>
                <div className="pub-sub">{it.itemNumber} / {it.modelNumber || '-'}
                </div>
                <div className={`pub-badge ${it.inStock ? 'green' : 'red'}`}>
                  {it.inStock ? '在庫あり' : '貸出中'}
                </div>
                {it.remarks && <div className="pub-remarks">{it.remarks}</div>}
              </div>
            ))}
          </div>
        )}
      </main>

      <footer className="pub-footer">
        <a href="/admin">管理者ページへ</a>
      </footer>
    </div>
  );
}

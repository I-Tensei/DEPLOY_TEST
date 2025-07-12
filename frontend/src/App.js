import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [items, setItems] = useState([]);
  const [newItem, setNewItem] = useState({
    itemNumber: '',
    itemName: '',
    modelNumber: '',
    inStock: true,
    remarks: ''
  });
  const [activeMenu, setActiveMenu] = useState('dashboard');
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStock, setFilterStock] = useState('all');

  // 在庫一覧を取得
  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      const res = await fetch('http://ec2-18-219-230-59.us-east-2.compute.amazonaws.com:8080/items');
      const data = await res.json();
      setItems(data);
    } catch (error) {
      console.error('取得エラー:', error);
    }
  };

  // 新規登録
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('http://ec2-18-219-230-59.us-east-2.compute.amazonaws.com:8080/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newItem),
      });
      const data = await res.json();
      setItems([...items, data]);
      setNewItem({ itemNumber: '', itemName: '', modelNumber: '', inStock: true, remarks: '' });
      setActiveMenu('dashboard'); // 登録後はダッシュボードに戻る
    } catch (error) {
      console.error('登録エラー:', error);
    }
  };

  // フィルタリングされた在庫リスト
  const filteredItems = items.filter(item => {
    const matchesSearch = item.itemName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.itemNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.modelNumber.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesFilter = filterStock === 'all' || 
                         (filterStock === 'inStock' && item.inStock) ||
                         (filterStock === 'outOfStock' && !item.inStock);
    
    return matchesSearch && matchesFilter;
  });

  // 統計情報
  const totalItems = items.length;
  const inStockItems = items.filter(item => item.inStock).length;
  const outOfStockItems = totalItems - inStockItems;

  return (
    <div className="app">
      {/* サイドバー */}
      <div className="sidebar">
        <div className="sidebar-header">
          <h2>在庫管理システム</h2>
        </div>
        <nav className="sidebar-nav">
          <ul>
            <li className={activeMenu === 'dashboard' ? 'active' : ''}>
              <button onClick={() => setActiveMenu('dashboard')}>
                📊 ダッシュボード
              </button>
            </li>
            <li className={activeMenu === 'inventory' ? 'active' : ''}>
              <button onClick={() => setActiveMenu('inventory')}>
                📦 在庫一覧
              </button>
            </li>
            <li className={activeMenu === 'register' ? 'active' : ''}>
              <button onClick={() => setActiveMenu('register')}>
                ➕ 新規登録
              </button>
            </li>
            <li className={activeMenu === 'reports' ? 'active' : ''}>
              <button onClick={() => setActiveMenu('reports')}>
                📈 レポート
              </button>
            </li>
          </ul>
        </nav>
      </div>

      {/* メインコンテンツ */}
      <div className="main-content">
        <div className="header">
          <h1>
            {activeMenu === 'dashboard' && 'ダッシュボード'}
            {activeMenu === 'inventory' && '在庫一覧'}
            {activeMenu === 'register' && '新規登録'}
            {activeMenu === 'reports' && 'レポート'}
          </h1>
        </div>

        <div className="content">
          {/* ダッシュボード */}
          {activeMenu === 'dashboard' && (
            <div className="dashboard">
              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-icon">📦</div>
                  <div className="stat-info">
                    <h3>総備品数</h3>
                    <p className="stat-number">{totalItems}</p>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">✅</div>
                  <div className="stat-info">
                    <h3>在庫有り</h3>
                    <p className="stat-number">{inStockItems}</p>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">❌</div>
                  <div className="stat-info">
                    <h3>在庫切れ</h3>
                    <p className="stat-number">{outOfStockItems}</p>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon">📊</div>
                  <div className="stat-info">
                    <h3>在庫率</h3>
                    <p className="stat-number">{totalItems > 0 ? Math.round((inStockItems / totalItems) * 100) : 0}%</p>
                  </div>
                </div>
              </div>

              <div className="recent-items">
                <h3>最近登録された備品</h3>
                <div className="recent-list">
                  {items.slice(-5).reverse().map(item => (
                    <div key={item.id} className="recent-item">
                      <div className="item-info">
                        <strong>{item.itemName}</strong>
                        <span className="item-number">{item.itemNumber}</span>
                      </div>
                      <div className={`stock-status ${item.inStock ? 'in-stock' : 'out-of-stock'}`}>
                        {item.inStock ? '有' : '無'}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* 在庫一覧 */}
          {activeMenu === 'inventory' && (
            <div className="inventory">
              <div className="search-filter-bar">
                <div className="search-box">
                  <input
                    type="text"
                    placeholder="備品名、備品番号、型番で検索..."
                    value={searchTerm}
                    onChange={e => setSearchTerm(e.target.value)}
                  />
                </div>
                <div className="filter-box">
                  <select value={filterStock} onChange={e => setFilterStock(e.target.value)}>
                    <option value="all">全て</option>
                    <option value="inStock">在庫有り</option>
                    <option value="outOfStock">在庫切れ</option>
                  </select>
                </div>
              </div>

              <div className="table-container">
                <table className="inventory-table">
                  <thead>
                    <tr>
                      <th>備品番号（更新）</th>
                      <th>備品名称</th>
                      <th>型番</th>
                      <th>在庫状況</th>
                      <th>備考</th>
                      <th>登録時間</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredItems.map(item => (
                      <tr key={item.id}>
                        <td>{item.itemNumber}</td>
                        <td>{item.itemName}</td>
                        <td>{item.modelNumber}</td>
                        <td>
                          <span className={`status-badge ${item.inStock ? 'in-stock' : 'out-of-stock'}`}>
                            {item.inStock ? '在庫有り' : '在庫切れ'}
                          </span>
                        </td>
                        <td>{item.remarks}</td>
                        <td>{new Date(item.registeredAt).toLocaleString('ja-JP')}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* 新規登録 */}
          {activeMenu === 'register' && (
            <div className="register">
              <div className="form-container">
                <form onSubmit={handleSubmit} className="register-form">
                  <div className="form-group">
                    <label htmlFor="itemNumber">備品番号 *</label>
                    <input
                      id="itemNumber"
                      type="text"
                      value={newItem.itemNumber}
                      onChange={e => setNewItem({...newItem, itemNumber: e.target.value})}
                      required
                      placeholder="例: EQ001"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="itemName">備品名称 *</label>
                    <input
                      id="itemName"
                      type="text"
                      value={newItem.itemName}
                      onChange={e => setNewItem({...newItem, itemName: e.target.value})}
                      required
                      placeholder="例: ノートパソコン"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="modelNumber">型番</label>
                    <input
                      id="modelNumber"
                      type="text"
                      value={newItem.modelNumber}
                      onChange={e => setNewItem({...newItem, modelNumber: e.target.value})}
                      placeholder="例: ThinkPad X1"
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="inStock">在庫状況</label>
                    <select
                      id="inStock"
                      value={newItem.inStock}
                      onChange={e => setNewItem({...newItem, inStock: e.target.value === 'true'})}
                    >
                      <option value="true">在庫有り</option>
                      <option value="false">在庫切れ</option>
                    </select>
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="remarks">備考</label>
                    <textarea
                      id="remarks"
                      value={newItem.remarks}
                      onChange={e => setNewItem({...newItem, remarks: e.target.value})}
                      placeholder="追加情報があれば入力してください"
                      rows="3"
                    />
                  </div>
                  
                  <button type="submit" className="submit-btn">
                    登録する
                  </button>
                </form>
              </div>
            </div>
          )}

          {/* レポート */}
          {activeMenu === 'reports' && (
            <div className="reports">
              <div className="report-section">
                <h3>在庫状況レポート</h3>
                <div className="report-chart">
                  <div className="chart-placeholder">
                    <p>在庫率: {totalItems > 0 ? Math.round((inStockItems / totalItems) * 100) : 0}%</p>
                    <div className="progress-bar">
                      <div 
                        className="progress-fill" 
                        style={{ width: `${totalItems > 0 ? (inStockItems / totalItems) * 100 : 0}%` }}
                      ></div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div className="report-section">
                <h3>在庫切れアイテム</h3>
                <div className="low-stock-list">
                  {items.filter(item => !item.inStock).map(item => (
                    <div key={item.id} className="low-stock-item">
                      <strong>{item.itemName}</strong> ({item.itemNumber})
                    </div>
                  ))}
                  {items.filter(item => !item.inStock).length === 0 && (
                    <p>在庫切れのアイテムはありません。</p>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
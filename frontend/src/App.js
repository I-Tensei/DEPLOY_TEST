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
  const [editingItem, setEditingItem] = useState(null);
  const [editRemarks, setEditRemarks] = useState('');

  // 在庫一覧を取得
  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      // ローカル環境用に変更
      const res = await fetch('http://localhost:8080/items');
      const data = await res.json();
      setItems(data);
    } catch (error) {
      console.error('取得エラー:', error);
      alert('データの取得に失敗しました: ' + error.message);
    }
  };

  // 新規登録
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      console.log('送信データ:', newItem);
      const res = await fetch('http://localhost:8080/api/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newItem),
      });
      
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      
      const data = await res.json();
      console.log('登録結果:', data);
      setItems([data, ...items]);
      setNewItem({ itemNumber: '', itemName: '', modelNumber: '', inStock: true, remarks: '' });
      setActiveMenu('dashboard');
      alert('登録が完了しました！');
    } catch (error) {
      console.error('登録エラー:', error);
      alert('登録に失敗しました: ' + error.message);
    }
  };

  // 在庫状況変更（貸出・返却）
  const handleStockChange = async (item) => {
    try {
      const updatedItem = {
        ...item,
        inStock: !item.inStock
      };
      
      const res = await fetch(`http://localhost:8080/api/items/${item.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedItem),
      });
      
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      
      const data = await res.json();
      setItems(items.map(i => i.id === item.id ? data : i));
      
      const action = data.inStock ? '返却' : '貸出';
      alert(`${item.itemName} を${action}しました！`);
    } catch (error) {
      console.error('在庫状況変更エラー:', error);
      alert('在庫状況の変更に失敗しました: ' + error.message);
    }
  };

  // 備考編集開始
  const startEditRemarks = (item) => {
    setEditingItem(item);
    setEditRemarks(item.remarks || '');
  };

  // 備考編集保存
  const saveRemarks = async () => {
    try {
      const updatedItem = {
        ...editingItem,
        remarks: editRemarks
      };
      
      const res = await fetch(`http://localhost:8080/api/items/${editingItem.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedItem),
      });
      
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      
      const data = await res.json();
      setItems(items.map(i => i.id === editingItem.id ? data : i));
      setEditingItem(null);
      setEditRemarks('');
      alert('備考を更新しました！');
    } catch (error) {
      console.error('備考更新エラー:', error);
      alert('備考の更新に失敗しました: ' + error.message);
    }
  };

  // 備考編集キャンセル
  const cancelEditRemarks = () => {
    setEditingItem(null);
    setEditRemarks('');
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
        <div className="content">
          {/* ダッシュボード */}
          {activeMenu === 'dashboard' && (
            <div className="dashboard">
              <h1>ダッシュボード</h1>
              
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
                    <h3>貸出中</h3>
                    <p className="stat-number">{outOfStockItems}</p>
                  </div>
                </div>
              </div>

              <div className="recent-activity">
                <h3>最近登録された備品</h3>
                <div className="recent-items">
                  {items.slice(-5).reverse().map(item => (
                    <div key={item.id} className="recent-item">
                      <div className="item-info">
                        <strong>{item.itemName}</strong>
                        <span className="item-number">{item.itemNumber}</span>
                      </div>
                      <div className={`stock-status ${item.inStock ? 'in-stock' : 'out-of-stock'}`}>
                        {item.inStock ? '在庫有り' : '貸出中'}
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
              <h1>在庫一覧</h1>
              
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
                    <option value="outOfStock">貸出中</option>
                  </select>
                </div>
              </div>

              <div className="table-container">
                <table className="inventory-table">
                  <thead>
                    <tr>
                      <th>備品番号</th>
                      <th>備品名称</th>
                      <th>型番</th>
                      <th>在庫状況</th>
                      <th>備考</th>
                      <th>登録時間</th>
                      <th>操作</th>
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
                            {item.inStock ? '在庫有り' : '貸出中'}
                          </span>
                        </td>
                        <td>
                          {editingItem && editingItem.id === item.id ? (
                            <div className="edit-remarks">
                              <textarea
                                value={editRemarks}
                                onChange={e => setEditRemarks(e.target.value)}
                                rows="2"
                                placeholder="備考を入力..."
                              />
                              <div className="edit-buttons">
                                <button onClick={saveRemarks} className="save-btn">保存</button>
                                <button onClick={cancelEditRemarks} className="cancel-btn">キャンセル</button>
                              </div>
                            </div>
                          ) : (
                            <div className="remarks-display">
                              <span>{item.remarks || '未設定'}</span>
                              <button 
                                onClick={() => startEditRemarks(item)} 
                                className="edit-remarks-btn"
                                title="備考を編集"
                              >
                                ✏️
                              </button>
                            </div>
                          )}
                        </td>
                        <td>{new Date(item.registeredAt).toLocaleString('ja-JP')}</td>
                        <td>
                          <button
                            onClick={() => handleStockChange(item)}
                            className={`action-btn ${item.inStock ? 'lend-btn' : 'return-btn'}`}
                            title={item.inStock ? '貸出' : '返却'}
                          >
                            {item.inStock ? '📤 貸出' : '📥 返却'}
                          </button>
                        </td>
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
              <h1>新規登録</h1>
              
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
                      <option value="false">貸出中</option>
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
              <h1>レポート</h1>
              
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
                <h3>貸出中アイテム</h3>
                <div className="low-stock-list">
                  {items.filter(item => !item.inStock).map(item => (
                    <div key={item.id} className="low-stock-item">
                      <strong>{item.itemName}</strong> ({item.itemNumber})
                      {item.remarks && <span className="item-remarks"> - {item.remarks}</span>}
                    </div>
                  ))}
                  {items.filter(item => !item.inStock).length === 0 && (
                    <p>貸出中のアイテムはありません。</p>
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
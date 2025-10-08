import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [items, setItems] = useState([]);
  const [newItem, setNewItem] = useState({
    itemNumber: '',
    itemName: '',
    modelNumber: '',
    inStock: true,
    remarks: '',
    categoryId: 1,
    quantity: 1,
    price: 0
  });
  const [activeMenu, setActiveMenu] = useState('dashboard');
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStock, setFilterStock] = useState('all');
  const [editingItem, setEditingItem] = useState(null);
  const [editRemarks, setEditRemarks] = useState('');
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteItem, setDeleteItem] = useState(null);
  const [deletePassword, setDeletePassword] = useState('');

  // 在庫一覧を取得
  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      // Nginx経由でAPIにアクセス
      const res = await fetch('/api/items');
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
      const res = await fetch('/api/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newItem),
      });
      
      if (!res.ok) {
        // エラーレスポンスを解析
        const errorData = await res.json().catch(() => null);
        
        if (res.status === 409) { // Conflict - 重複エラー
          throw new Error(errorData?.error || '備品番号が既に登録されています。');
        } else if (res.status === 400) { // Bad Request - バリデーションエラー
          throw new Error(errorData?.error || '入力内容に問題があります。');
        } else {
          throw new Error(`登録に失敗しました (${res.status})`);
        }
      }
      
      const data = await res.json();
      console.log('登録結果:', data);
      setItems([data, ...items]);
      setNewItem({ itemNumber: '', itemName: '', modelNumber: '', inStock: true, remarks: '', categoryId: 1, quantity: 1, price: 0 });
      setActiveMenu('dashboard');
      alert('登録が完了しました！');
    } catch (error) {
      console.error('登録エラー:', error);
      alert(error.message);
    }
  };

  // 在庫状況変更（貸出・返却）
  const handleStockChange = async (item) => {
    try {
      const updatedItem = {
        ...item,
        inStock: !item.inStock
      };
      
      const res = await fetch(`/api/items/${item.id}`, {
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
      
      const res = await fetch(`/api/items/${editingItem.id}`, {
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

  // 削除確認ダイアログを開く
  const openDeleteModal = (item) => {
    setDeleteItem(item);
    setShowDeleteModal(true);
    setDeletePassword('');
  };

  // 削除確認ダイアログを閉じる
  const closeDeleteModal = () => {
    setShowDeleteModal(false);
    setDeleteItem(null);
    setDeletePassword('');
  };

  // 削除実行
  const handleDelete = async () => {
    // パスワード認証（簡易版）
    const ADMIN_PASSWORD = 'admin123'; // 実際の運用では環境変数やサーバー側認証を使用
    
    if (deletePassword !== ADMIN_PASSWORD) {
      alert('パスワードが間違っています。');
      return;
    }

    try {
      const res = await fetch(`/api/items/${deleteItem.id}`, {
        method: 'DELETE',
      });
      
      if (!res.ok) {
        throw new Error(`削除に失敗しました (${res.status})`);
      }
      
      // 削除成功時、リストから削除
      setItems(items.filter(item => item.id !== deleteItem.id));
      closeDeleteModal();
      alert(`${deleteItem.itemName} を削除しました。`);
    } catch (error) {
      console.error('削除エラー:', error);
      alert('削除に失敗しました: ' + error.message);
    }
  };

  // API 異常時に items が配列でない可能性に備えたセーフガード
  const list = Array.isArray(items) ? items : [];

  // フィルタリングされた在庫リスト（nullセーフ）
  const filteredItems = list.filter(item => {
    const name  = (item.itemName    || '').toLowerCase();
    const num   = (item.itemNumber  || '').toLowerCase();
    const model = (item.modelNumber || '').toLowerCase();
    const q     = (searchTerm       || '').toLowerCase();

    const matchesSearch = name.includes(q) || num.includes(q) || model.includes(q);
    const matchesFilter = filterStock === 'all' || 
                          (filterStock === 'inStock' && item.inStock) ||
                          (filterStock === 'outOfStock' && !item.inStock);
    return matchesSearch && matchesFilter;
  });

  // 統計情報（セーフリスト使用）
  const totalItems = list.length;
  const inStockItems = list.filter(item => item.inStock).length;
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
                  {list.slice(-5).reverse().map(item => (
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
                          <button
                            onClick={() => openDeleteModal(item)}
                            className="action-btn delete-btn"
                            title="削除"
                            style={{ marginLeft: '5px' }}
                          >
                            🗑️ 削除
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
                    <label htmlFor="categoryId">カテゴリID *</label>
                    <select
                      id="categoryId"
                      value={newItem.categoryId}
                      onChange={e => setNewItem({...newItem, categoryId: parseInt(e.target.value)})}
                      required
                    >
                      <option value={1}>1 - 電子機器</option>
                      <option value={2}>2 - 事務用品</option>
                      <option value={3}>3 - その他</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label htmlFor="quantity">数量 *</label>
                    <input
                      id="quantity"
                      type="number"
                      min="1"
                      value={newItem.quantity}
                      onChange={e => setNewItem({...newItem, quantity: parseInt(e.target.value)})}
                      required
                      placeholder="例: 1"
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="price">価格 *</label>
                    <input
                      id="price"
                      type="number"
                      min="0"
                      value={newItem.price}
                      onChange={e => setNewItem({...newItem, price: parseInt(e.target.value)})}
                      required
                      placeholder="例: 50000"
                    />
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

          {/* レポート 　*/}
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
                  {list.filter(item => !item.inStock).map(item => (
                    <div key={item.id} className="low-stock-item">
                      <strong>{item.itemName}</strong> ({item.itemNumber})
                      {item.remarks && <span className="item-remarks"> - {item.remarks}</span>}
                    </div>
                  ))}
                  {list.filter(item => !item.inStock).length === 0 && (
                    <p>貸出中のアイテムはありません。</p>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* 削除確認モーダル */}
      {showDeleteModal && (
        <div className="modal-overlay" onClick={closeDeleteModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>削除確認</h3>
            <p>以下の備品を削除しますか？</p>
            <div className="delete-item-info">
              <strong>{deleteItem?.itemName}</strong> ({deleteItem?.itemNumber})
            </div>
            <div className="form-group">
              <label htmlFor="deletePassword">管理者パスワード *</label>
              <input
                id="deletePassword"
                type="password"
                value={deletePassword}
                onChange={(e) => setDeletePassword(e.target.value)}
                placeholder="パスワードを入力してください"
                required
              />
            </div>
            <div className="modal-buttons">
              <button onClick={handleDelete} className="confirm-delete-btn">
                削除する
              </button>
              <button onClick={closeDeleteModal} className="cancel-btn">
                キャンセル
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
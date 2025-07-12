import React, { useState, useEffect } from 'react';

function App() {
  const [items, setItems] = useState([]);
  const [newItem, setNewItem] = useState({
    itemNumber: '',
    itemName: '',
    modelNumber: '',
    inStock: true,
    remarks: ''
  });

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
    } catch (error) {
      console.error('登録エラー:', error);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>在庫管理システム</h1>

      {/* 新規登録フォーム */}
      <div style={{ marginBottom: '30px', border: '1px solid #ccc', padding: '20px' }}>
        <h2>新規登録</h2>
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '10px' }}>
            <label>備品番号: </label>
            <input 
              value={newItem.itemNumber} 
              onChange={e => setNewItem({...newItem, itemNumber: e.target.value})}
              required 
            />
          </div>
          <div style={{ marginBottom: '10px' }}>
            <label>備品名称: </label>
            <input 
              value={newItem.itemName} 
              onChange={e => setNewItem({...newItem, itemName: e.target.value})}
              required 
            />
          </div>
          <div style={{ marginBottom: '10px' }}>
            <label>型番: </label>
            <input 
              value={newItem.modelNumber} 
              onChange={e => setNewItem({...newItem, modelNumber: e.target.value})}
            />
          </div>
          <div style={{ marginBottom: '10px' }}>
            <label>在庫有無: </label>
            <select 
              value={newItem.inStock} 
              onChange={e => setNewItem({...newItem, inStock: e.target.value === 'true'})}
            >
              <option value="true">有</option>
              <option value="false">無</option>
            </select>
          </div>
          <div style={{ marginBottom: '10px' }}>
            <label>備考: </label>
            <input 
              value={newItem.remarks} 
              onChange={e => setNewItem({...newItem, remarks: e.target.value})}
            />
          </div>
          <button type="submit">登録</button>
        </form>
      </div>

      {/* 在庫一覧テーブル */}
      <h2>在庫一覧</h2>
      <table border="1" style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr>
            <th>備品番号</th>
            <th>備品名称</th>
            <th>型番</th>
            <th>在庫有無</th>
            <th>備考</th>
            <th>登録時間</th>
          </tr>
        </thead>
        <tbody>
          {items.map(item => (
            <tr key={item.id}>
              <td>{item.itemNumber}</td>
              <td>{item.itemName}</td>
              <td>{item.modelNumber}</td>
              <td>{item.inStock ? '有' : '無'}</td>
              <td>{item.remarks}</td>
              <td>{new Date(item.registeredAt).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
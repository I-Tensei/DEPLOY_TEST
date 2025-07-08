import React, { useEffect, useState } from 'react';

function App() {
  const [message, setMessage] = useState('Loading...');

  useEffect(() => {
    fetch('http://localhost:8080/hello')
      .then((res) => res.text())
      .then((data) => setMessage(data))
      .catch((err) => setMessage('エラーが発生しました'));
  }, []);

  return (
    <div>
      <h1>Spring Bootからのメッセージ：</h1>
      <p>{message}</p>
    </div>
  );
}

export default App;

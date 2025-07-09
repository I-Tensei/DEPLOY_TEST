import React, { useState } from 'react';

function App() {
  const [input, setInput] = useState('');
  const [result, setResult] = useState(null);

  const handleCalc = async () => {
    const res = await fetch('http://http://ec2-3-135-214-156.us-east-2.compute.amazonaws.com/:8080/calc', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ number: Number(input) }),
    });
    const data = await res.text();
    setResult(data);
  };

  return (
    <div>
      <input value={input} onChange={e => setInput(e.target.value)} type="number" />
      <button onClick={handleCalc}>計算</button>
      {result && <div>結果: {result}</div>}
    </div>
  );
}

export default App;
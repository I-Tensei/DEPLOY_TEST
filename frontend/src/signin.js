// src/Signin.js
import { useState } from "react";
import { Navigate } from "react-router-dom";

export default function Signin() {
  const [id, setId] = useState("");
  const [password, setPassword] = useState("");
  const [redirect, setRedirect] = useState(false);
  const [error, setError] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await fetch('/api/auth/signin', {        
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',                  
        body: JSON.stringify({ id, password })
      });
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || 'Invalid ID or password');
      }
      setRedirect(true);
    } catch (err) {
      setError(err.message);
    }
  };

  if (redirect) return <Navigate to="/" replace />;

  return (
    <div className="login-page">
      <h1>ログイン</h1>
      <form onSubmit={submit} className="login-form">
        <input type="text" placeholder="ID" value={id}
               onChange={(e)=>setId(e.target.value)} required />
        <input type="password" placeholder="Password" value={password}
               onChange={(e)=>setPassword(e.target.value)} required />
        <button type="submit">Sign in</button>
        {error && <p style={{color:'red', marginTop:8}}>{error}</p>}
      </form>
    </div>
  );
}

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { auth } from '../auth.js';

function LoginPage() {
  const [username, setUsername] = useState('');
  const [parola, setParola] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const user = await api.login(username, parola);
      auth.save(user);
      navigate('/movies');
    } catch (err) {
      setError(err.message || 'Autentificare esuata.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-6 col-lg-5">
        <div className="card card-body shadow-sm">
          <h4 className="mb-3">Autentificare</h4>
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Username</label>
              <input
                className="form-control"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Parola</label>
              <input
                type="password"
                className="form-control"
                value={parola}
                onChange={(e) => setParola(e.target.value)}
              />
            </div>
            <button className="btn btn-primary w-100" disabled={loading}>
              {loading ? 'Se autentifica...' : 'Login'}
            </button>
          </form>
          <div className="text-muted small mt-3">Test: client1 / parola1</div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;


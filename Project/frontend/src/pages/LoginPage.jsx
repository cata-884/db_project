import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { saveSession } from '../auth.js';

function LoginPage() {
  const [username, setUsername] = useState('');
  const [parola, setParola] = useState('');
  const [nume, setNume] = useState('');
  const [prenume, setPrenume] = useState('');
  const [email, setEmail] = useState('');
  const [isRegister, setIsRegister] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = isRegister
        ? await api.register({ username, parola, nume, prenume, email })
        : await api.login(username, parola);
      saveSession(response);
      navigate('/movies');
    } catch (err) {
      const message = String(err?.message || '').toLowerCase();
      if (err?.status === 404) {
        setError('Endpoint-ul de autentificare nu este disponibil. Verifica backend-ul.');
      } else if (!isRegister && err?.status === 400) {
        setError('Username sau parola incorecte.');
      } else if (isRegister && (err?.status === 409 || message.includes('deja folosit'))) {
        setError('Username deja folosit. Alege altul.');
      } else if (err?.status === 400 && message.includes('obligatoriu')) {
        setError('Completeaza toate campurile obligatorii.');
      } else {
        setError(err?.message || 'A aparut o eroare. Incearca din nou.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="row justify-content-center">
      <div className="col-md-6 col-lg-5">
        <div className="card card-body shadow-sm">
          <h4 className="mb-3">{isRegister ? 'Inregistrare' : 'Autentificare'}</h4>
          {error && <div className="alert alert-danger">{error}</div>}
          <form onSubmit={handleSubmit}>
            {isRegister && (
              <div className="mb-3">
                <label className="form-label">Nume</label>
                <input
                  className="form-control"
                  value={nume}
                  onChange={(e) => setNume(e.target.value)}
                />
              </div>
            )}
            {isRegister && (
              <div className="mb-3">
                <label className="form-label">Prenume</label>
                <input
                  className="form-control"
                  value={prenume}
                  onChange={(e) => setPrenume(e.target.value)}
                />
              </div>
            )}
            {isRegister && (
              <div className="mb-3">
                <label className="form-label">Email</label>
                <input
                  type="email"
                  className="form-control"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            )}
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
              {loading ? 'Se proceseaza...' : isRegister ? 'Creeaza cont' : 'Login'}
            </button>
          </form>
          <button
            type="button"
            className="btn btn-link w-100 mt-2"
            onClick={() => {
              setError('');
              setIsRegister(!isRegister);
            }}
          >
            {isRegister ? 'Ai deja cont? Autentifica-te' : 'Nu ai cont? Inregistreaza-te'}
          </button>
          {!isRegister && (
            <div className="text-muted small">Test: client1 / parola1</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default LoginPage;

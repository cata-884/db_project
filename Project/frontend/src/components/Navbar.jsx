import { Link, useNavigate } from 'react-router-dom';
import { getUser, clearSession, isAuthenticated } from '../auth.js';
import { api } from '../api.js';

function Navbar() {
  const navigate = useNavigate();
  const user = getUser();

  const handleLogout = async () => {
    try { await api.logout(); } catch { /* ignora erori de retea */ }
    clearSession();
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand" to="/movies">
          MovieApp
        </Link>
        <div className="collapse navbar-collapse show">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <Link className="nav-link" to="/movies">
                Filme
              </Link>
            </li>
            {isAuthenticated() && (
              <li className="nav-item">
                <Link className="nav-link" to="/profile">
                  Profil
                </Link>
              </li>
            )}
            {isAuthenticated() && (
              <li className="nav-item">
                <Link className="nav-link" to="/stats">
                  Statistici
                </Link>
              </li>
            )}
            {isAuthenticated() && (
              <li className="nav-item">
                <Link className="nav-link" to="/account">
                  Contul meu
                </Link>
              </li>
            )}
          </ul>
          <div className="d-flex align-items-center gap-2">
            {isAuthenticated() ? (
              <>
                <span className="text-light small">Salut, {user?.nume}!</span>
                <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
                  Logout
                </button>
              </>
            ) : (
              <Link className="btn btn-outline-light btn-sm" to="/login">
                Login
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;

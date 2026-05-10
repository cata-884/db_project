const TOKEN_KEY = 'token';
const USER_KEY = 'movieapp_user';

export function saveSession(loginResponse) {
  localStorage.setItem(TOKEN_KEY, loginResponse.token);
  localStorage.setItem(USER_KEY, JSON.stringify({
    id: loginResponse.id,
    username: loginResponse.username,
    nume: loginResponse.nume,
    prenume: loginResponse.prenume,
    email: loginResponse.email
  }));
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getUser() {
  const raw = localStorage.getItem(USER_KEY);
  return raw ? JSON.parse(raw) : null;
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

export function isAuthenticated() {
  return !!getToken();
}

// Backward-compat: unele componente mai vechi folosesc auth.get() etc.
export const auth = {
  save: saveSession,
  get: getUser,
  clear: clearSession,
  isLoggedIn: isAuthenticated
};

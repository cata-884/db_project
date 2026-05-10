import { getToken, clearSession } from './auth';

function authHeaders() {
  const token = getToken();
  return token ? { 'Authorization': `Bearer ${token}` } : {};
}

async function apiFetch(path, options = {}) {
  const res = await fetch(path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
      ...(options.headers || {})
    }
  });

  if (res.status === 401) {
    clearSession();
    if (!window.location.pathname.endsWith('/login')) {
      window.location.href = '/login';
    }
    throw new Error('Sesiune expirată');
  }

  if (!res.ok) {
    const text = await res.text();
    let message = `HTTP ${res.status}`;
    try {
      const parsed = text ? JSON.parse(text) : null;
      message = parsed?.error || parsed?.message || message;
    } catch {
      if (text) message = text;
    }
    const err = new Error(message);
    err.status = res.status;
    throw err;
  }

  if (res.status === 204) return null;
  const bodyText = await res.text();
  if (!bodyText) return null;
  return JSON.parse(bodyText);
}

export const api = {
  login: (username, parola) =>
    apiFetch('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, parola })
    }),

  register: (data) =>
    apiFetch('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(data)
    }),

  logout: () =>
    apiFetch('/api/auth/logout', { method: 'POST' }),

  getFilme: () => apiFetch('/api/filme'),
  getFilmeByCategorie: (idCategorie) => apiFetch(`/api/filme/categorie/${idCategorie}`),
  getFilm: (id) => apiFetch(`/api/filme/${id}`),
  getCategorii: () => apiFetch('/api/categorii'),
  getVersiuniFilm: (idFilm) => apiFetch(`/api/versiuni-film/film/${idFilm}`),
  getActoriFilm: (idFilm) => apiFetch(`/api/actori/film/${idFilm}`),
  getRecenziiFilm: (idFilm) => apiFetch(`/api/recenzii/film/${idFilm}`),

  postRecenzie: (data) =>
    apiFetch('/api/recenzii', { method: 'POST', body: JSON.stringify(data) }),

  updateRecenzie: (id, data) =>
    apiFetch(`/api/recenzii/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  deleteRecenzie: (id) =>
    apiFetch(`/api/recenzii/${id}`, { method: 'DELETE' }),

  getEtichete: () => apiFetch('/api/etichete'),

  addEticheta: (idRecenzie, idEticheta) =>
    apiFetch(`/api/recenzii-etichete/${idRecenzie}/${idEticheta}`, { method: 'POST' }),

  postVizualizare: (data) =>
    apiFetch('/api/vizualizari', { method: 'POST', body: JSON.stringify(data) }),

  updateVizualizare: (idVizualizare, data) =>
    apiFetch(`/api/vizualizari/${idVizualizare}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    }),

  getIstoric: () => apiFetch('/api/vizualizari/me'),

  getRecomandari: () => apiFetch('/api/stats/recomandari'),
  getProfil: () => apiFetch('/api/stats/profil'),
  getSimilari: (topN = 5) => apiFetch(`/api/stats/similari?topN=${topN}`),

  getStatsSezon: () => apiFetch('/api/stats/sezon'),
  getPredictii: (luna, topN = 10) => apiFetch(`/api/stats/predictii?luna=${luna}&topN=${topN}`),
  getGrupare: (threshold) => apiFetch(`/api/stats/grupare?threshold=${threshold}`),

  getMe: () => apiFetch('/api/clienti/me'),
  updateMe: (data) =>
    apiFetch('/api/clienti/me', { method: 'PUT', body: JSON.stringify(data) })
};

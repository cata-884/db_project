const API = '';

async function request(path, options = {}) {
  const res = await fetch(`${API}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });

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
    request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, parola })
    }),
  register: (data) =>
    request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(data)
    }),
  getFilme: () => request('/api/filme'),
  getFilmeByCategorie: (idCategorie) => request(`/api/filme/categorie/${idCategorie}`),
  getFilm: (id) => request(`/api/filme/${id}`),
  getCategorii: () => request('/api/categorii'),
  getVersiuniFilm: (idFilm) => request(`/api/versiuni-film/film/${idFilm}`),
  getRecenziiFilm: (idFilm) => request(`/api/recenzii/film/${idFilm}`),
  postRecenzie: (data) =>
    request('/api/recenzii', { method: 'POST', body: JSON.stringify(data) }),
  updateRecenzie: (id, data) =>
    request(`/api/recenzii/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  getEtichete: () => request('/api/etichete'),
  addEticheta: (idRecenzie, idEticheta) =>
    request(`/api/recenzii-etichete/${idRecenzie}/${idEticheta}`, { method: 'POST' }),
  postVizualizare: (data) =>
    request('/api/vizualizari', { method: 'POST', body: JSON.stringify(data) }),
  updateVizualizare: (idVizualizare, data) =>
    request(`/api/vizualizari/${idVizualizare}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    }),
  getIstoric: (idClient) => request(`/api/vizualizari/client/${idClient}`),
  getRecomandari: (idClient) => request(`/api/stats/recomandari/${idClient}`),
  getProfil: (idClient) => request(`/api/stats/profil/${idClient}`),
  getSimilari: (idClient, topN = 5) =>
    request(`/api/stats/similari/${idClient}?topN=${topN}`),
  getActoriFilm: (idFilm) => request(`/api/actori/film/${idFilm}`),
  getStatsSezon: () => request('/api/stats/sezon'),
  getPredictii: (luna, topN = 10) => request(`/api/stats/predictii?luna=${luna}&topN=${topN}`),
  getGrupare: (threshold) => request(`/api/stats/grupare?threshold=${threshold}`),
  getMe: (username) => request(`/api/clienti/me?username=${encodeURIComponent(username)}`),
  updateMe: (username, data) =>
    request(`/api/clienti/me?username=${encodeURIComponent(username)}`, {
      method: 'PUT',
      body: JSON.stringify(data)
    })
};

import { useEffect, useState } from 'react';
import { api } from '../api.js';
import { auth } from '../auth.js';

const LUNI = ['Ian', 'Feb', 'Mar', 'Apr', 'Mai', 'Iun', 'Iul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

const RANK_COLORS = { 1: '#198754', 2: '#0dcaf0', 3: '#ffc107', 4: '#adb5bd', 5: '#dee2e6' };

function SezonHeatmap({ data }) {
  if (!data || data.length === 0) return <div className="text-muted">Nu exista date sezoniere.</div>;

  const categorii = [...new Set(data.map((d) => d.categorie))].sort();
  const byKey = {};
  data.forEach((d) => {
    byKey[`${d.categorie}|${d.luna}`] = d;
  });

  return (
    <div className="table-responsive">
      <table className="table table-bordered table-sm text-center">
        <thead className="table-dark">
          <tr>
            <th>Categorie</th>
            {LUNI.map((l, i) => (
              <th key={i + 1}>{l}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {categorii.map((cat) => (
            <tr key={cat}>
              <td className="text-start fw-bold">{cat}</td>
              {LUNI.map((_, i) => {
                const luna = i + 1;
                const cell = byKey[`${cat}|${luna}`];
                const bg = cell ? RANK_COLORS[cell.rankCategorie] || '#f8f9fa' : '#f8f9fa';
                return (
                  <td
                    key={luna}
                    style={{ background: bg, color: cell && cell.rankCategorie <= 2 ? '#fff' : '#000' }}
                    title={cell ? `${cell.nrVizionari} vizionari (rank ${cell.rankCategorie})` : ''}
                  >
                    {cell ? cell.nrVizionari : ''}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function PredictiiSection() {
  const [luna, setLuna] = useState(1);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const calculeaza = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.getPredictii(luna);
      setData(res || []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="d-flex align-items-center gap-3 mb-3">
        <select
          className="form-select w-auto"
          value={luna}
          onChange={(e) => setLuna(Number(e.target.value))}
        >
          {LUNI.map((l, i) => (
            <option key={i + 1} value={i + 1}>
              {l}
            </option>
          ))}
        </select>
        <button className="btn btn-primary btn-sm" onClick={calculeaza} disabled={loading}>
          {loading ? 'Se calculeaza...' : 'Calculeaza'}
        </button>
      </div>
      {error && <div className="alert alert-danger">{error}</div>}
      {data && data.length === 0 && <div className="text-muted">Nu exista predictii pentru luna selectata.</div>}
      {data && data.length > 0 && (
        <div className="table-responsive">
          <table className="table table-hover table-sm">
            <thead className="table-dark">
              <tr>
                <th>#</th>
                <th>Film</th>
                <th>Categorie</th>
                <th>Rating</th>
                <th>Factor sezonier</th>
                <th>Scor predictie</th>
              </tr>
            </thead>
            <tbody>
              {data.map((row, idx) => (
                <tr key={row.id}>
                  <td>{idx + 1}</td>
                  <td>{row.titlu}</td>
                  <td>{row.categorie}</td>
                  <td>{row.rating?.toFixed?.(1) ?? '-'}</td>
                  <td>{row.factorSezonier?.toFixed?.(3) ?? '-'}</td>
                  <td>
                    <strong>{row.scorPredictie?.toFixed?.(3) ?? '-'}</strong>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function GrupareSection() {
  const [threshold, setThreshold] = useState(0.3);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const calculeaza = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.getGrupare(threshold);
      setData(res || []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const grupe = data
    ? data.reduce((acc, row) => {
        const g = acc[row.idGrupa] || [];
        g.push(row.numeComplet);
        acc[row.idGrupa] = g;
        return acc;
      }, {})
    : null;

  return (
    <div>
      <div className="d-flex align-items-center gap-3 mb-3">
        <div>
          <label className="form-label mb-0 me-2">Threshold: {threshold.toFixed(2)}</label>
          <input
            type="range"
            className="form-range"
            min="0"
            max="1"
            step="0.05"
            value={threshold}
            onChange={(e) => setThreshold(Number(e.target.value))}
            style={{ width: '200px', display: 'inline-block' }}
          />
        </div>
        <button className="btn btn-primary btn-sm" onClick={calculeaza} disabled={loading}>
          {loading ? 'Se calculeaza...' : 'Calculeaza'}
        </button>
      </div>
      {error && <div className="alert alert-danger">{error}</div>}
      {grupe && Object.keys(grupe).length === 0 && (
        <div className="text-muted">Nu s-au format grupuri cu threshold-ul selectat.</div>
      )}
      {grupe &&
        Object.entries(grupe).map(([idGrupa, membri]) => (
          <div key={idGrupa} className="card mb-2">
            <div className="card-header py-1">
              <strong>Grupa {idGrupa}</strong>
              <span className="badge bg-secondary ms-2">{membri.length} clienti</span>
            </div>
            <div className="card-body py-2">
              <div className="d-flex flex-wrap gap-2">
                {membri.map((m, i) => (
                  <span key={i} className="badge bg-light text-dark border">
                    {m}
                  </span>
                ))}
              </div>
            </div>
          </div>
        ))}
    </div>
  );
}

function SimilariSection({ idClient }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;
    api
      .getSimilari(idClient, 5)
      .then((res) => {
        if (!ignore) setData(res || []);
      })
      .catch((err) => {
        if (!ignore) setError(err.message);
      })
      .finally(() => {
        if (!ignore) setLoading(false);
      });
    return () => {
      ignore = true;
    };
  }, [idClient]);

  if (loading) return <div className="text-muted">Se incarca...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;
  if (!data || data.length === 0) return <div className="text-muted">Nu s-au gasit clienti similari.</div>;

  return (
    <ul className="list-group">
      {data.map((c) => (
        <li key={c.id} className="list-group-item d-flex justify-content-between align-items-center">
          <span>{c.numeComplet}</span>
          <span className="badge bg-success rounded-pill">{(c.scorSimilaritate * 100).toFixed(1)}%</span>
        </li>
      ))}
    </ul>
  );
}

function StatsPage() {
  const user = auth.get();
  const [sezonData, setSezonData] = useState(null);
  const [sezonLoading, setSezonLoading] = useState(true);
  const [sezonError, setSezonError] = useState('');
  const [activeTab, setActiveTab] = useState('sezon');

  useEffect(() => {
    let ignore = false;
    api
      .getStatsSezon()
      .then((res) => {
        if (!ignore) setSezonData(res || []);
      })
      .catch((err) => {
        if (!ignore) setSezonError(err.message);
      })
      .finally(() => {
        if (!ignore) setSezonLoading(false);
      });
    return () => {
      ignore = true;
    };
  }, []);

  const tabs = [
    { id: 'sezon', label: 'Analiza sezoniera' },
    { id: 'predictii', label: 'Predictii lunare' },
    { id: 'grupare', label: 'Grupare clienti' },
    { id: 'similari', label: 'Clienti similari' },
  ];

  return (
    <div>
      <h3 className="mb-4">Statistici avansate</h3>

      <ul className="nav nav-tabs mb-4">
        {tabs.map((t) => (
          <li key={t.id} className="nav-item">
            <button
              className={`nav-link ${activeTab === t.id ? 'active' : ''}`}
              onClick={() => setActiveTab(t.id)}
            >
              {t.label}
            </button>
          </li>
        ))}
      </ul>

      {activeTab === 'sezon' && (
        <div>
          <h5>Vizionari pe categorie si luna (top 5 categorii/luna)</h5>
          <p className="text-muted small">
            Verde = rank 1, Cyan = rank 2, Galben = rank 3, Gri = rank 4-5
          </p>
          {sezonLoading && <div className="text-muted">Se incarca...</div>}
          {sezonError && <div className="alert alert-danger">{sezonError}</div>}
          {!sezonLoading && !sezonError && <SezonHeatmap data={sezonData} />}
        </div>
      )}

      {activeTab === 'predictii' && (
        <div>
          <h5>Predictii filme populare pentru luna selectata</h5>
          <p className="text-muted small">
            Scorul combina indexul sezonier al categoriei cu rating-ul filmului.
          </p>
          <PredictiiSection />
        </div>
      )}

      {activeTab === 'grupare' && (
        <div>
          <h5>Grupare clienti dupa similaritate</h5>
          <p className="text-muted small">
            Un threshold mai mic formeaza grupe mai mari (similaritate minima mai mica).
          </p>
          <GrupareSection />
        </div>
      )}

      {activeTab === 'similari' && (
        <div>
          <h5>Clienti cu gusturi similare tie</h5>
          <p className="text-muted small">
            Scorul combina 70% Jaccard pe filme comune si 30% proximitate rating.
          </p>
          {user && <SimilariSection idClient={user.id} />}
        </div>
      )}
    </div>
  );
}

export default StatsPage;

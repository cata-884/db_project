import { useEffect, useState } from 'react';
import { api } from '../api.js';
import EticheteSelector from './EticheteSelector.jsx';

function ReviewForm({ idFilm, idClient, distributie = [], onSubmitted }) {
  const [nota, setNota] = useState(8);
  const [textComentariu, setTextComentariu] = useState('');
  const [etichete, setEtichete] = useState([]);
  const [selected, setSelected] = useState(new Set());
  const [actoriComentarii, setActoriComentarii] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;
    api
      .getEtichete()
      .then((data) => {
        if (!ignore) setEtichete(data || []);
      })
      .catch((err) => {
        if (!ignore) setError(err.message);
      });
    return () => {
      ignore = true;
    };
  }, []);

  const toggle = (id) => {
    const copy = new Set(selected);
    if (copy.has(id)) copy.delete(id);
    else copy.add(id);
    setSelected(copy);
  };

  const handleActorComment = (idActor, value) => {
    setActoriComentarii((prev) => ({ ...prev, [idActor]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const actoriIds = Object.keys(actoriComentarii)
        .filter((k) => actoriComentarii[k].trim() !== '')
        .map(Number);
      const actoriCom = actoriIds.map((id) => actoriComentarii[id]);

      await api.postRecenzie({
        idClient,
        idFilm,
        nota: Number(nota),
        textComentariu,
        etichetaIds: [...selected],
        actoriIds,
        actoriComentarii: actoriCom,
      });

      setTextComentariu('');
      setSelected(new Set());
      setActoriComentarii({});
      if (onSubmitted) onSubmitted();
    } catch (err) {
      const message = err.message || 'Eroare la trimiterea recenziei.';
      const lower = message.toLowerCase();
      if (err.status === 409 || lower.includes('ora-00001') || lower.includes('duplicat')) {
        setError('Ai recenzat deja acest film.');
      } else {
        setError(message);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card card-body">
      <h5 className="mb-3">Adauga o recenzie</h5>
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="mb-3">
        <label className="form-label">Nota (1-10)</label>
        <input
          type="number"
          min="1"
          max="10"
          className="form-control"
          value={nota}
          onChange={(e) => setNota(e.target.value)}
        />
      </div>

      <div className="mb-3">
        <label className="form-label">Comentariu</label>
        <textarea
          className="form-control"
          rows="3"
          value={textComentariu}
          onChange={(e) => setTextComentariu(e.target.value)}
        />
      </div>

      <div className="mb-3">
        <label className="form-label">Etichete</label>
        <EticheteSelector etichete={etichete} selected={selected} onToggle={toggle} />
      </div>

      {distributie.length > 0 && (
        <div className="mb-3">
          <label className="form-label">Comentarii despre actori (optional)</label>
          <div className="list-group">
            {distributie.map((actor) => (
              <div key={actor.idActor || actor.id_actor} className="list-group-item py-2">
                <label className="form-label mb-1 small fw-bold">
                  {actor.numeScena || actor.nume_scena}{' '}
                  <span className="text-muted fw-normal">
                    {actor.nume} {actor.prenume} — {actor.rol}
                  </span>
                </label>
                <input
                  type="text"
                  className="form-control form-control-sm"
                  placeholder="Comentariu optional..."
                  value={actoriComentarii[actor.idActor || actor.id_actor] || ''}
                  onChange={(e) =>
                    handleActorComment(actor.idActor || actor.id_actor, e.target.value)
                  }
                />
              </div>
            ))}
          </div>
        </div>
      )}

      <button className="btn btn-primary" type="submit" disabled={loading}>
        {loading ? 'Se salveaza...' : 'Posteaza'}
      </button>
    </form>
  );
}

export default ReviewForm;

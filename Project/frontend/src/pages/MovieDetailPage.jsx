import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api.js';
import { auth } from '../auth.js';
import ReviewList from '../components/ReviewList.jsx';
import ReviewForm from '../components/ReviewForm.jsx';
import VideoPlayerModal from '../components/VideoPlayerModal.jsx';

function MovieDetailPage() {
  const { id } = useParams();
  const user = auth.get();
  const [movie, setMovie] = useState(null);
  const [actors, setActors] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [categorii, setCategorii] = useState([]);
  const [versiuni, setVersiuni] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showPlayer, setShowPlayer] = useState(false);
  const [selectedVersionId, setSelectedVersionId] = useState(null);

  const loadAll = async () => {
    setLoading(true);
    setError('');
    try {
      const [movieData, actorData, reviewData, categoriiData, versiuniData] = await Promise.all([
        api.getFilm(id),
        api.getActoriFilm(id),
        api.getRecenziiFilm(id),
        api.getCategorii(),
        api.getVersiuniFilm(id)
      ]);
      setMovie(movieData);
      setActors(actorData || []);
      setReviews(reviewData || []);
      setCategorii(categoriiData || []);
      setVersiuni(versiuniData || []);
      if (versiuniData && versiuniData.length > 0) setSelectedVersionId(versiuniData[0].id);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAll();
  }, [id]);

  const handleVizionare = () => {
    if (!user || !selectedVersionId) return;
    setShowPlayer(true);
  };

  const handleDeleteRecenzie = async (idRecenzie) => {
    try {
      await api.deleteRecenzie(idRecenzie);
      loadAll();
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading) return <div>Se incarca...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;
  if (!movie) return <div className="text-muted">Film inexistent.</div>;

  const categorieName = categorii.find((c) => c.id === (movie.idCategorie || movie.id_categorie))?.nume;

  return (
    <div className="row g-4">
      <div className="col-lg-8">
        <div className="card card-body mb-3">
          <h3 className="mb-2">{movie.titlu}</h3>
          <p className="text-muted">{movie.descriere || 'Fara descriere'}</p>
          <div className="d-flex gap-3 flex-wrap">
            <span>⭐ {movie.rating?.toFixed?.(1) || movie.rating || 'N/A'}</span>
            <span>Data lansare: {movie.dataLansare || movie.data_lansare || '-'}</span>
            <span>Categorie: {categorieName || '-'}</span>
          </div>
          {user && versiuni.length > 0 && (
            <div className="d-flex align-items-center gap-2 mt-3">
              <select
                className="form-select w-auto"
                value={selectedVersionId || ''}
                onChange={(e) => setSelectedVersionId(Number(e.target.value))}
              >
                {versiuni.map((v) => (
                  <option key={v.id} value={v.id}>
                    {v.rezolutie} &bull; {v.limbi} &bull; {v.format}
                  </option>
                ))}
              </select>
              <button className="btn btn-success" onClick={handleVizionare}>
                Vizioneaza
              </button>
            </div>
          )}
          {user && versiuni.length === 0 && (
            <div className="text-muted small mt-2">Nu exista versiuni pentru acest film.</div>
          )}
        </div>

        <div className="card card-body mb-3">
          <h5>Actori</h5>
          {actors.length === 0 ? (
            <div className="text-muted">Nu exista actori pentru acest film.</div>
          ) : (
            <ul className="list-group list-group-flush">
              {actors.map((a) => (
                <li key={a.idActor || a.id_actor} className="list-group-item">
                  {(a.numeScena || a.nume_scena || '').trim() && (
                    <strong>{a.numeScena || a.nume_scena}</strong>
                  )}{' '}
                  {a.nume} {a.prenume} — <span className="text-muted">{a.rol}</span>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="card card-body mb-3">
          <h5>Recenzii</h5>
          <ReviewList reviews={reviews} />
        </div>

        {user && (() => {
          const existingReview = reviews.find((r) => r.idClient === user.id || r.id_client === user.id);
          if (existingReview) {
            return (
              <div className="card card-body">
                <h5>Recenzia ta</h5>
                <p className="text-muted small mb-2">
                  Ai deja o recenzie pentru acest film. Poti sterge recenzia si posta una noua.
                </p>
                <button
                  className="btn btn-outline-danger btn-sm"
                  onClick={() => handleDeleteRecenzie(existingReview.id)}
                >
                  Sterge recenzia
                </button>
              </div>
            );
          }
          return (
            <ReviewForm
              idFilm={movie.id}
              idClient={user.id}
              distributie={actors}
              onSubmitted={loadAll}
            />
          );
        })()}
      </div>

      <div className="col-lg-4">
        <div className="card card-body">
          <h6>Detalii rapide</h6>
          <div className="small text-muted">
            Autentificarea foloseste localStorage.
          </div>
        </div>
      </div>

      {showPlayer && user && (
        <VideoPlayerModal
          movie={movie}
          idClient={user.id}
          idVersiune={selectedVersionId}
          onClose={() => setShowPlayer(false)}
        />
      )}
    </div>
  );
}

export default MovieDetailPage;


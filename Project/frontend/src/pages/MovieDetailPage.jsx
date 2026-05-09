import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api.js';
import { auth } from '../auth.js';
import ReviewList from '../components/ReviewList.jsx';
import ReviewForm from '../components/ReviewForm.jsx';
import VideoPlayerModal from '../components/VideoPlayerModal.jsx';
import MoviePoster from '../components/MoviePoster.jsx';

function MovieDetailPage() {
  const { id } = useParams();
  const user = auth.get();
  const [movie, setMovie] = useState(null);
  const [actors, setActors] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showPlayer, setShowPlayer] = useState(false);

  const loadAll = async () => {
    setLoading(true);
    setError('');
    try {
      const [movieData, actorData, reviewData] = await Promise.all([
        api.getFilm(id),
        api.getActoriFilm(id),
        api.getRecenziiFilm(id)
      ]);
      setMovie(movieData);
      setActors(actorData || []);
      setReviews(reviewData || []);
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
    if (!user) return;
    setShowPlayer(true);
  };

  if (loading) return <div>Se incarca...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;
  if (!movie) return <div className="text-muted">Film inexistent.</div>;

  return (
    <div className="row g-4">
      <div className="col-lg-8">
        <div className="card card-body mb-3">
          <h3 className="mb-2">{movie.titlu}</h3>
          <p className="text-muted">{movie.descriere || 'Fara descriere'}</p>
          <div className="d-flex gap-3 flex-wrap">
            <span>⭐ {movie.rating?.toFixed?.(1) || movie.rating || 'N/A'}</span>
            <span>Data lansare: {movie.dataLansare || movie.data_lansare || '-'}</span>
            <span>Categorie: {movie.idCategorie || movie.id_categorie || '-'}</span>
          </div>
          {user && (
            <button className="btn btn-success mt-3" onClick={handleVizionare}>
              Vizioneaza
            </button>
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

        {user && (
          <ReviewForm
            idFilm={movie.id}
            idClient={user.id}
            onSubmitted={loadAll}
          />
        )}
      </div>

      <div className="col-lg-4">
        <div className="card card-body mb-3">
          <MoviePoster movie={movie} height={260} />
          <button
            className="btn btn-primary w-100 mt-3"
            onClick={handleVizionare}
            disabled={!user}
          >
            {user ? 'Vizioneaza (simulat)' : 'Autentifica-te pentru a viziona'}
          </button>
        </div>
        <div className="card card-body">
          <h6>Detalii rapide</h6>
          <div className="small text-muted">
            Autentificarea foloseste localStorage. Vizionarea este simulata.
          </div>
        </div>
      </div>

      {showPlayer && user && (
        <VideoPlayerModal
          movie={movie}
          idClient={user.id}
          onClose={() => setShowPlayer(false)}
        />
      )}
    </div>
  );
}

export default MovieDetailPage;


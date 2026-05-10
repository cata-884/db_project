import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import MovieCard from '../components/MovieCard.jsx';

function ProfilePage() {
  const navigate = useNavigate();
  const [profil, setProfil] = useState(null);
  const [recomandari, setRecomandari] = useState([]);
  const [istoric, setIstoric] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;
    const load = async () => {
      setLoading(true);
      setError('');
      try {
        const [profilData, recData, istoricData] = await Promise.all([
          api.getProfil(),
          api.getRecomandari(),
          api.getIstoric()
        ]);
        if (!ignore) {
          setProfil(profilData);
          setRecomandari(recData || []);
          setIstoric(istoricData || []);
        }
      } catch (err) {
        if (!ignore) setError(err.message);
      } finally {
        if (!ignore) setLoading(false);
      }
    };

    load();
    return () => {
      ignore = true;
    };
  }, []);

  if (loading) return <div>Se incarca...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <div className="row g-4">
      <div className="col-12">
        <h3>Profil client</h3>
      </div>

      <div className="col-md-4">
        <div className="card card-body">
          <h5>Profil cinematografic</h5>
          <div className="text-muted small mb-2">
            Date generate de procedura p_profil_cinematografic
          </div>
          {profil ? (
            <ul className="list-group list-group-flush">
              <li className="list-group-item">Categorie preferata: {profil.categoriePreferata || profil.categorie_preferata || '-'}</li>
              <li className="list-group-item">Actor preferat: {profil.actorPreferat || profil.actor_preferat || '-'}</li>
              <li className="list-group-item">Rating mediu: {profil.ratingMediu || profil.rating_mediu || '-'}</li>
              <li className="list-group-item">Total filme vazute: {profil.totalFilmeVazute || profil.total_filme_vazute || '-'}</li>
              <li className="list-group-item">Total recenzii: {profil.totalRecenzii || profil.total_recenzii || '-'}</li>
              <li className="list-group-item">Sentiment dominant: {profil.sentimentDominant || profil.sentiment_dominant || '-'}</li>
            </ul>
          ) : (
            <div className="text-muted">Nu exista date.</div>
          )}
        </div>
      </div>

      <div className="col-md-8">
        <div className="card card-body mb-4">
          <h5>Recomandari pentru tine</h5>
          <div className="text-muted small mb-2">
            Recomandari generate de procedura p_recomandari_pentru_client
          </div>
          {recomandari.length === 0 ? (
            <div className="text-muted">Nu exista recomandari.</div>
          ) : (
            <div className="row g-3">
              {recomandari.slice(0, 10).map((movie) => (
                <div key={movie.id || movie.id_film} className="col-md-6">
                  <MovieCard
                    movie={movie}
                    onClick={() => navigate(`/movies/${movie.id || movie.id_film}`)}
                  />
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="card card-body">
          <h5>Istoric vizionari</h5>
          {istoric.length === 0 ? (
            <div className="text-muted">Nu exista vizionari.</div>
          ) : (
            <div className="table-responsive">
              <table className="table table-sm">
                <thead>
                  <tr>
                    <th>Titlu</th>
                    <th>Versiune</th>
                    <th>Data</th>
                  </tr>
                </thead>
                <tbody>
                  {istoric.map((v, idx) => (
                    <tr key={v.idVizualizare || idx}>
                      <td>{v.titluFilm || '-'}</td>
                      <td>{v.denumireVersiune || '-'}</td>
                      <td>{v.dataVizualizare || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProfilePage;

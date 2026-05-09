import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import MovieCard from '../components/MovieCard.jsx';

function MoviesPage() {
  const [filme, setFilme] = useState([]);
  const [categorii, setCategorii] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const loadData = async (category) => {
    setLoading(true);
    setError('');
    try {
      const [cats, movies] = await Promise.all([
        api.getCategorii(),
        category === 'all' ? api.getFilme() : api.getFilmeByCategorie(category)
      ]);
      setCategorii(cats || []);
      setFilme(movies || []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData(selectedCategory);
  }, [selectedCategory]);

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="mb-0">Filme</h3>
        <select
          className="form-select w-auto"
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
        >
          <option value="all">Toate categoriile</option>
          {categorii.map((c) => (
            <option key={c.id} value={c.id}>
              {c.nume}
            </option>
          ))}
        </select>
      </div>

      {loading && <div>Se incarca...</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      {!loading && !error && filme.length === 0 && (
        <div className="text-muted">Nu exista filme.</div>
      )}

      <div className="row g-3">
        {filme.map((movie) => (
          <div key={movie.id} className="col-md-4">
            <MovieCard movie={movie} onClick={() => navigate(`/movies/${movie.id}`)} />
          </div>
        ))}
      </div>
    </div>
  );
}

export default MoviesPage;


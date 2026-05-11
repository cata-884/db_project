function MovieCard({movie, onClick}) {
  return (
      <div className="card h-100" style={{cursor: 'pointer'}} onClick={onClick}>
        <div className="card-body">
          <h5 className="card-title">{movie.titlu}</h5>
          <p className="card-text small text-muted">
            {movie.descriere ? `${movie.descriere.substring(0, 100)}...` : 'Fara descriere'}
          </p>
          <div className="d-flex justify-content-between">
            <small>⭐ {movie.rating != null ? movie.rating.toFixed(1) : 'N/A'}</small>
            <small>{movie.dataLansare || movie.data_lansare || ''}</small>
          </div>
        </div>
      </div>
  );
}

export default MovieCard;

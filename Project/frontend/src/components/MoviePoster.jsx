function MoviePoster({ movie, height = 220 }) {
  const id = Number(movie?.id || 1);
  const hue = (id * 137) % 360;
  const gradient = `linear-gradient(135deg, hsl(${hue}, 60%, 30%), hsl(${(hue + 40) % 360}, 60%, 50%))`;

  return (
    <div
      className="movie-poster"
      style={{ height, background: gradient }}
      aria-label={`Poster ${movie?.titlu || ''}`}
    >
      <div className="movie-poster-title">🎬 {movie?.titlu}</div>
    </div>
  );
}

export default MoviePoster;


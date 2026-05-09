function ReviewList({ reviews }) {
  if (!reviews || reviews.length === 0) {
    return <div className="text-muted">Nu exista recenzii inca.</div>;
  }

  return (
    <div className="list-group">
      {reviews.map((r) => (
        <div key={r.id || `${r.idClient}-${r.idFilm}`} className="list-group-item">
          <div className="d-flex justify-content-between align-items-center">
            <strong>{r.numeClient || r.clientNume || r.username || 'Client'}</strong>
            <span className="badge bg-primary">{r.nota}</span>
          </div>
          {r.textComentariu && <p className="mb-1">{r.textComentariu}</p>}
          {r.dataPostare && <small className="text-muted">{r.dataPostare}</small>}
        </div>
      ))}
    </div>
  );
}

export default ReviewList;


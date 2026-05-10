const SENTIMENT_BADGE = {
  POZITIV: 'success',
  NEGATIV: 'danger',
  NEUTRU: 'secondary',
};

function formatEtichetaLabel(denumire = '') {
  return denumire.toLowerCase().replace(/_/g, ' ').replace(/^./, (c) => c.toUpperCase());
}

function ReviewList({ reviews }) {
  if (!reviews || reviews.length === 0) {
    return <div className="text-muted">Nu exista recenzii inca.</div>;
  }

  return (
    <div className="list-group">
      {reviews.map((r) => (
        <div key={r.id || `${r.idClient}-${r.idFilm}`} className="list-group-item">
          <div className="d-flex justify-content-between align-items-center mb-1">
            <strong>{r.numeClient || r.clientNume || r.username || 'Client'}</strong>
            <div className="d-flex gap-2 align-items-center">
              {r.sentiment && (
                <span className={`badge bg-${SENTIMENT_BADGE[r.sentiment] || 'secondary'}`}>
                  {r.sentiment}
                </span>
              )}
              <span className="badge bg-primary">{r.nota}</span>
            </div>
          </div>
          {r.textComentariu && <p className="mb-1">{r.textComentariu}</p>}
          {r.etichete && r.etichete.length > 0 && (
            <div className="d-flex flex-wrap gap-1 mb-1">
              {r.etichete.map((et) => (
                <span key={et} className="badge bg-light text-dark border">
                  {formatEtichetaLabel(et)}
                </span>
              ))}
            </div>
          )}
          {r.actoriComentarii && r.actoriComentarii.length > 0 && (
            <ul className="list-unstyled mb-1 ps-2 border-start border-2">
              {r.actoriComentarii.map((c, i) => (
                <li key={i} className="small text-muted">
                  <span className="fw-semibold">{c.numeScena}:</span>{' '}
                  <span className="fst-italic">{c.comentariu}</span>
                </li>
              ))}
            </ul>
          )}
          {r.dataPostare && <small className="text-muted">{r.dataPostare}</small>}
        </div>
      ))}
    </div>
  );
}

export default ReviewList;

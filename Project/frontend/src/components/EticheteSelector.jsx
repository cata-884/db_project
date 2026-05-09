function formatLabel(denumire = '') {
  return denumire
    .toLowerCase()
    .replace(/_/g, ' ')
    .replace(/^./, (c) => c.toUpperCase());
}

function EticheteSelector({ etichete, selected, onToggle }) {
  const grupate = {
    POZITIV: etichete.filter((e) => e.sentiment === 'POZITIV'),
    NEGATIV: etichete.filter((e) => e.sentiment === 'NEGATIV'),
    NEUTRU: etichete.filter((e) => e.sentiment === 'NEUTRU')
  };

  const culori = {
    POZITIV: 'success',
    NEGATIV: 'danger',
    NEUTRU: 'secondary'
  };

  return (
    <div>
      {Object.entries(grupate).map(([sentiment, list]) => (
        <div key={sentiment} className="mb-3">
          <div className="text-uppercase small text-muted mb-2">{sentiment}</div>
          <div className="d-flex flex-wrap gap-2">
            {list.length === 0 && <span className="text-muted small">-</span>}
            {list.map((et) => {
              const isSelected = selected.has(et.id);
              const variant = culori[sentiment];
              return (
                <button
                  key={et.id}
                  type="button"
                  className={`btn btn-sm ${
                    isSelected ? `btn-${variant}` : `btn-outline-${variant}`
                  }`}
                  onClick={() => onToggle(et.id)}
                >
                  {formatLabel(et.denumire)}
                </button>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}

export default EticheteSelector;


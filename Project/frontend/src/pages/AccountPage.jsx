import { useEffect, useState } from 'react';
import { api } from '../api.js';
import { auth } from '../auth.js';

function AccountPage() {
  const user = auth.get();
  const [client, setClient] = useState(null);
  const [form, setForm] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;
    api
      .getMe()
      .then((res) => {
        if (!ignore) {
          setClient(res);
          setForm({
            telefonFixCod: res.telefonFixCod || '+40',
            telefonFixNr: res.telefonFixNr || '',
            telefonMobilCod: res.telefonMobilCod || '+40',
            telefonMobilNr: res.telefonMobilNr || '',
            adresa: res.adresa || '',
            oras: res.oras || '',
            dataNastere: res.dataNastere || ''
          });
        }
      })
      .catch((err) => {
        if (!ignore) setError(err.message);
      })
      .finally(() => {
        if (!ignore) setLoading(false);
      });
    return () => {
      ignore = true;
    };
  }, [user?.id]);

  const validateForm = () => {
    if (!form) return 'Date invalide.';
    const digitsOnly = (value) => value === '' || /^[0-9]+$/.test(value);
    if (!digitsOnly(form.telefonFixNr.trim()) || !digitsOnly(form.telefonMobilNr.trim())) {
      return 'Numerele de telefon trebuie sa contina doar cifre.';
    }
    const trimmedAddress = form.adresa.trim();
    const trimmedCity = form.oras.trim();
    if (form.adresa && trimmedAddress.length === 0) {
      return 'Adresa nu poate fi doar spatii.';
    }
    if (form.oras && trimmedCity.length === 0) {
      return 'Orasul nu poate fi doar spatii.';
    }
    if (form.dataNastere) {
      const date = new Date(form.dataNastere);
      const year = date.getFullYear();
      const today = new Date();
      if (Number.isNaN(date.getTime())) return 'Data nasterii invalida.';
      if (year < 1900) return 'Data nasterii trebuie sa fie dupa 1900.';
      if (date > today) return 'Data nasterii nu poate fi in viitor.';
    }
    return '';
  };

  const handleSave = async () => {
    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }
    setError('');
    setSaving(true);
    try {
      const updated = await api.updateMe(form);
      setClient(updated);
      setEditMode(false);
    } catch (err) {
      setError(err.message || 'Eroare la salvare.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div>Se incarca...</div>;
  if (error && !client) return <div className="alert alert-danger">{error}</div>;
  if (!client) return null;

  const rows = [
    { label: 'Nume complet', value: `${client.nume} ${client.prenume}` },
    { label: 'Username', value: client.username },
    { label: 'Email', value: client.email },
    { label: 'Telefon fix', value: client.telefonFixFormatted || '-' },
    { label: 'Telefon mobil', value: client.telefonMobilFormatted || '-' },
    { label: 'Adresa', value: client.adresa || '-' },
    { label: 'Oras', value: client.oras || '-' },
    { label: 'Data nastere', value: client.dataNastere || '-' },
  ];

  return (
    <div className="row justify-content-center">
      <div className="col-md-6">
        <div className="card">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h5 className="mb-0">Contul meu</h5>
            {!editMode && (
              <button className="btn btn-sm btn-outline-primary" onClick={() => setEditMode(true)}>
                Editeaza
              </button>
            )}
          </div>
          <div className="card-body">
            {error && <div className="alert alert-danger">{error}</div>}
            {editMode ? (
              <div className="d-grid gap-3">
                <div>
                  <label className="form-label">Telefon fix</label>
                  <div className="d-flex gap-2">
                    <select
                      className="form-select"
                      value={form.telefonFixCod}
                      onChange={(e) => setForm({ ...form, telefonFixCod: e.target.value })}
                    >
                      <option>+40</option>
                      <option>+1</option>
                      <option>+44</option>
                      <option>+49</option>
                    </select>
                    <input
                      className="form-control"
                      value={form.telefonFixNr}
                      onChange={(e) => setForm({ ...form, telefonFixNr: e.target.value })}
                      placeholder="0212345678"
                    />
                  </div>
                </div>
                <div>
                  <label className="form-label">Telefon mobil</label>
                  <div className="d-flex gap-2">
                    <select
                      className="form-select"
                      value={form.telefonMobilCod}
                      onChange={(e) => setForm({ ...form, telefonMobilCod: e.target.value })}
                    >
                      <option>+40</option>
                      <option>+1</option>
                      <option>+44</option>
                      <option>+49</option>
                    </select>
                    <input
                      className="form-control"
                      value={form.telefonMobilNr}
                      onChange={(e) => setForm({ ...form, telefonMobilNr: e.target.value })}
                      placeholder="0712345678"
                    />
                  </div>
                </div>
                <div>
                  <label className="form-label">Adresa</label>
                  <input
                    className="form-control"
                    value={form.adresa}
                    onChange={(e) => setForm({ ...form, adresa: e.target.value })}
                  />
                </div>
                <div>
                  <label className="form-label">Oras</label>
                  <input
                    className="form-control"
                    value={form.oras}
                    onChange={(e) => setForm({ ...form, oras: e.target.value })}
                  />
                </div>
                <div>
                  <label className="form-label">Data nasterii</label>
                  <input
                    type="date"
                    className="form-control"
                    value={form.dataNastere}
                    onChange={(e) => setForm({ ...form, dataNastere: e.target.value })}
                  />
                </div>
                <div className="d-flex gap-2">
                  <button className="btn btn-primary" onClick={handleSave} disabled={saving}>
                    {saving ? 'Se salveaza...' : 'Salveaza'}
                  </button>
                  <button
                    className="btn btn-outline-secondary"
                    onClick={() => {
                      setEditMode(false);
                      setError('');
                    }}
                    disabled={saving}
                  >
                    Anuleaza
                  </button>
                </div>
              </div>
            ) : (
              <ul className="list-group list-group-flush">
                {rows.map((r) => (
                  <li key={r.label} className="list-group-item d-flex justify-content-between">
                    <span className="text-muted">{r.label}</span>
                    <span className="fw-bold">{r.value}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default AccountPage;

import { useEffect, useMemo, useRef, useState } from 'react';
import { api } from '../api.js';
import { createPortal } from 'react-dom';

const SIM_DURATION_MINUTES = 90;
const SIM_DURATION_SECONDS = SIM_DURATION_MINUTES * 60;
const SPEED_SECONDS_PER_TICK = 30;
const TICK_MS = 1000;
const SKIP_PERCENT = 10;
const MIN_UPDATE_INTERVAL_MS = 5000;

function formatTimeFromPercent(percent) {
  const total = SIM_DURATION_SECONDS;
  const current = Math.floor((percent / 100) * total);
  const min = Math.floor(current / 60);
  const sec = Math.floor(current % 60);
  return `${min}:${sec.toString().padStart(2, '0')}`;
}

function VideoPlayerModal({ movie, idClient, idVersiune, onClose }) {
  const [progress, setProgress] = useState(0);
  const [isPlaying, setIsPlaying] = useState(true);
  const [isDragging, setIsDragging] = useState(false);
  const [wasPlayingBeforeDrag, setWasPlayingBeforeDrag] = useState(false);
  const [stare, setStare] = useState('IN_PROGRESS');
  const [idVizualizare, setIdVizualizare] = useState(null);
  const [lastSyncAt, setLastSyncAt] = useState(null);
  const [error, setError] = useState('');
  const [hoverPercent, setHoverPercent] = useState(null);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [nowTs, setNowTs] = useState(Date.now());
  const intervalRef = useRef(null);
  const barRef = useRef(null);
  const initRef = useRef(false);
  const lastUpdateRef = useRef(0);
  const latestStateRef = useRef({
    stare: 'IN_PROGRESS',
    durataMinute: 0,
    idVizualizare: null
  });

  const durataMinute = useMemo(() => Math.round((progress / 100) * SIM_DURATION_MINUTES), [progress]);

  const lastSyncLabel = useMemo(() => {
    if (!lastSyncAt) return 'n/a';
    const seconds = Math.max(0, Math.floor((nowTs - lastSyncAt) / 1000));
    return `${seconds}s`;
  }, [lastSyncAt, nowTs]);

  const updateNow = () => setNowTs(Date.now());

  useEffect(() => {
    const t = setInterval(updateNow, 1000);
    return () => clearInterval(t);
  }, []);

  const saveInitial = async () => {
    if (initRef.current) return;
    initRef.current = true;
    if (!idVersiune) return;
    try {
      const res = await api.postVizualizare({
        idClient,
        idVersiune,
        durata: 0,
        stare: 'IN_PROGRESS'
      });
      const vizId = res?.id || res?.idVizualizare || res?.id_vizualizare;
      setIdVizualizare(vizId || null);
      latestStateRef.current.idVizualizare = vizId || null;
      setLastSyncAt(Date.now());
    } catch (err) {
      setError(err.message || 'Eroare la inregistrare.');
    }
  };

  const updateVizualizare = async (status, durataValue) => {
    if (!idVizualizare) return;
    try {
      await api.updateVizualizare(idVizualizare, {
        id: idVizualizare,
        idClient,
        idVersiune,
        durata: durataValue,
        stare: status
      });
      lastUpdateRef.current = Date.now();
      setLastSyncAt(Date.now());
    } catch (err) {
      setError(err.message || 'Eroare la actualizare.');
    }
  };

  const updateVizualizareThrottled = async (status, durataValue, force = false) => {
    const now = Date.now();
    if (!force && now - lastUpdateRef.current < MIN_UPDATE_INTERVAL_MS) return;
    await updateVizualizare(status, durataValue);
  };

  useEffect(() => {
    saveInitial();
  }, [idVersiune]);

  useEffect(() => {
    latestStateRef.current = {
      stare,
      durataMinute,
      idVizualizare
    };
  }, [stare, durataMinute, idVizualizare]);

  useEffect(() => {
    if (!isPlaying || isDragging || stare === 'COMPLETED') {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
      return undefined;
    }

    intervalRef.current = setInterval(() => {
      setProgress((prev) => Math.min(100, prev + (SPEED_SECONDS_PER_TICK / SIM_DURATION_SECONDS) * 100));
    }, TICK_MS);

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
    };
  }, [isPlaying, isDragging, stare]);

  useEffect(() => {
    if (progress >= 100 && stare !== 'COMPLETED') {
      setStare('COMPLETED');
      setIsPlaying(false);
      updateVizualizare('COMPLETED', SIM_DURATION_MINUTES);
    }
  }, [progress, stare]);

  useEffect(() => {
    return () => {
      const latest = latestStateRef.current;
      if (latest.stare !== 'COMPLETED' && latest.idVizualizare) {
        updateVizualizare('ABANDONED', latest.durataMinute);
      }
    };
  }, []);

  const togglePlayPause = async () => {
    if (stare === 'COMPLETED') return;
    if (isPlaying) {
      setIsPlaying(false);
      setStare('PAUSED');
      await updateVizualizare('PAUSED', durataMinute);
    } else {
      setIsPlaying(true);
      setStare('IN_PROGRESS');
    }
  };

  const skipByPercent = (delta) => {
    if (stare === 'COMPLETED') return;
    setProgress((prev) => Math.min(100, Math.max(0, prev + delta)));
  };

  const handleStop = async () => {
    setIsPlaying(false);
    setStare('ABANDONED');
    await updateVizualizare('ABANDONED', durataMinute);
    onClose();
  };

  const handleSkipEnd = async () => {
    setProgress(100);
    setStare('COMPLETED');
    setIsPlaying(false);
    await updateVizualizare('COMPLETED', SIM_DURATION_MINUTES);
  };

  const calcPercentFromClientX = (clientX) => {
    const rect = barRef.current?.getBoundingClientRect();
    if (!rect) return 0;
    const raw = ((clientX - rect.left) / rect.width) * 100;
    return Math.min(100, Math.max(0, raw));
  };

  const handleBarClick = (e) => {
    if (!idVizualizare) return;
    const next = calcPercentFromClientX(e.clientX);
    setProgress(next);
    updateVizualizareThrottled(stare, Math.round((next / 100) * SIM_DURATION_MINUTES));
  };

  const handleMouseMove = (e) => {
    const next = calcPercentFromClientX(e.clientX);
    setHoverPercent(next);
    if (!isDragging) return;
    setProgress(next);
  };

  const handleMouseLeave = () => setHoverPercent(null);

  const handleDragStart = (e) => {
    if (!idVizualizare) return;
    setWasPlayingBeforeDrag(isPlaying);
    setIsDragging(true);
    setIsPlaying(false);
    setStare('PAUSED');
    const next = calcPercentFromClientX(e.clientX);
    setProgress(next);
    updateVizualizare('PAUSED', Math.round((next / 100) * SIM_DURATION_MINUTES));
  };

  const handleDragEnd = async () => {
    setIsDragging(false);
    const shouldResume = wasPlayingBeforeDrag && progress < 100;
    setIsPlaying(shouldResume);
    setStare(progress >= 100 ? 'COMPLETED' : shouldResume ? 'IN_PROGRESS' : 'PAUSED');
    await updateVizualizareThrottled(shouldResume ? 'IN_PROGRESS' : 'PAUSED', durataMinute);
  };

  useEffect(() => {
    if (!isDragging) return undefined;
    const onMove = (e) => handleMouseMove(e);
    const onUp = () => handleDragEnd();
    window.addEventListener('mousemove', onMove);
    window.addEventListener('mouseup', onUp);
    return () => {
      window.removeEventListener('mousemove', onMove);
      window.removeEventListener('mouseup', onUp);
    };
  }, [isDragging, progress, wasPlayingBeforeDrag]);

  const displayTime = `${formatTimeFromPercent(progress)} / ${formatTimeFromPercent(100)}`;
  const controlsDisabled = !idVizualizare || !idVersiune;

  return createPortal(
    <div className="modal fade show d-block" tabIndex="-1" role="dialog">
      <div className={`modal-dialog modal-xl ${isFullscreen ? 'modal-fullscreen' : ''}`} role="document">
        <div className="modal-content" onClick={(e) => e.stopPropagation()}>
          <div className="modal-header">
            <h5 className="modal-title">{movie.titlu}</h5>
            <button type="button" className="btn-close" onClick={handleStop}></button>
          </div>
          <div className="modal-body">
            <div className="fake-player">
              <div className="fake-poster">
                <div className="fake-poster-title">{movie.titlu}</div>
                <div className="play-pulse">PLAY</div>
              </div>
              <div className="player-controls">
                <button
                  className="btn btn-light btn-sm"
                  onClick={() => skipByPercent(-SKIP_PERCENT)}
                  disabled={controlsDisabled}
                >
                  Back
                </button>
                <button className="btn btn-light btn-sm" onClick={togglePlayPause} disabled={controlsDisabled}>
                  {isPlaying ? 'Pause' : 'Play'}
                </button>
                <button
                  className="btn btn-light btn-sm"
                  onClick={() => skipByPercent(SKIP_PERCENT)}
                  disabled={controlsDisabled}
                >
                  Forward
                </button>
                <div className="player-time">{displayTime}</div>
                <button
                  className="btn btn-light btn-sm"
                  onClick={() => setIsFullscreen((v) => !v)}
                  disabled={controlsDisabled}
                >
                  Full
                </button>
              </div>
              <div
                className="progress progress-interactive"
                ref={barRef}
                onClick={handleBarClick}
                onMouseMove={handleMouseMove}
                onMouseLeave={handleMouseLeave}
                onMouseDown={handleDragStart}
              >
                <div
                  className="progress-bar"
                  role="progressbar"
                  style={{ width: `${progress}%` }}
                  aria-valuenow={progress}
                  aria-valuemin="0"
                  aria-valuemax="100"
                ></div>
                {hoverPercent !== null && (
                  <div
                    className="progress-hover"
                    style={{ left: `${hoverPercent}%` }}
                  >
                    {formatTimeFromPercent(hoverPercent)}
                  </div>
                )}
              </div>
              <div className="player-debug">
                <span>Stare: {stare}</span>
                <span>• Inregistrare: live BD</span>
              </div>
              {controlsDisabled && (
                <div className="text-muted small mt-2">Se initializeaza vizionarea...</div>
              )}
            </div>
            <div className="player-debug-panel">
              <div>ID Vizualizare: {idVizualizare || '-'}</div>
              <div>Stare curenta: {stare}</div>
              <div>Durata salvata: {durataMinute} min</div>
              <div>Ultima sincronizare BD: {lastSyncLabel}</div>
            </div>
            {error && <div className="alert alert-danger mt-3">{error}</div>}
          </div>
          <div className="modal-footer">
            <button className="btn btn-outline-danger" onClick={handleStop}>
              Stop
            </button>
            <button className="btn btn-primary" onClick={handleSkipEnd}>
              Skip to end
            </button>
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade show"></div>
    </div>,
    document.body
  );
}

export default VideoPlayerModal;


import { useEffect, useMemo, useRef, useState } from 'react';
import { api } from '../api.js';
import { createPortal } from 'react-dom';

const SIM_DURATION_MINUTES = 90;
const SIM_DURATION_SECONDS = SIM_DURATION_MINUTES * 60;
const SPEED_SECONDS_PER_TICK = 30;
const TICK_MS = 1000;
const SKIP_PERCENT = 10;

function formatTimeFromPercent(percent) {
  const total = SIM_DURATION_SECONDS;
  const current = Math.floor((percent / 100) * total);
  const min = Math.floor(current / 60);
  const sec = Math.floor(current % 60);
  return `${min}:${sec.toString().padStart(2, '0')}`;
}

function VideoPlayerModal({ movie, idClient, onClose }) {
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
    try {
      const res = await api.postVizualizare({
        idClient,
        idVersiune: 1,
        durata: 0,
        stare: 'IN_PROGRESS'
      });
      const vizId = res?.id || res?.idVizualizare || res?.id_vizualizare;
      setIdVizualizare(vizId || null);
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
        idVersiune: 1,
        durata: durataValue,
        stare: status
      });
      setLastSyncAt(Date.now());
    } catch (err) {
      setError(err.message || 'Eroare la actualizare.');
    }
  };

  useEffect(() => {
    saveInitial();
  }, []);

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
      if (stare !== 'COMPLETED') {
        updateVizualizare('ABANDONED', durataMinute);
      }
    };
  }, [stare, durataMinute, idVizualizare]);

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
    const next = calcPercentFromClientX(e.clientX);
    setProgress(next);
    updateVizualizare(stare, Math.round((next / 100) * SIM_DURATION_MINUTES));
  };

  const handleMouseMove = (e) => {
    const next = calcPercentFromClientX(e.clientX);
    setHoverPercent(next);
    if (!isDragging) return;
    setProgress(next);
  };

  const handleMouseLeave = () => setHoverPercent(null);

  const handleDragStart = (e) => {
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
    await updateVizualizare(shouldResume ? 'IN_PROGRESS' : 'PAUSED', durataMinute);
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
                <div className="play-pulse">▶</div>
              </div>
              <div className="player-controls">
                <button className="btn btn-light btn-sm" onClick={() => skipByPercent(-SKIP_PERCENT)}>
                  ⏮
                </button>
                <button className="btn btn-light btn-sm" onClick={togglePlayPause}>
                  {isPlaying ? '⏸' : '▶'}
                </button>
                <button className="btn btn-light btn-sm" onClick={() => skipByPercent(SKIP_PERCENT)}>
                  ⏭
                </button>
                <div className="player-time">{displayTime}</div>
                <button className="btn btn-light btn-sm" onClick={() => setIsFullscreen((v) => !v)}>
                  ⛶
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


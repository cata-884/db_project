import { useEffect, useRef, useState } from 'react';
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

function VideoPlayerModal({ movie, idClient, idVersiune, onClose }) {
  const [progress, setProgress] = useState(0);
  const [isPlaying, setIsPlaying] = useState(true);
  const [isDragging, setIsDragging] = useState(false);
  const [wasPlayingBeforeDrag, setWasPlayingBeforeDrag] = useState(false);
  const [idVizualizare, setIdVizualizare] = useState(null);
  const [error, setError] = useState('');
  const [hoverPercent, setHoverPercent] = useState(null);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const intervalRef = useRef(null);
  const barRef = useRef(null);
  const initRef = useRef(false);

  const saveInitial = async () => {
    if (initRef.current) return;
    initRef.current = true;
    if (!idVersiune) return;
    try {
      const res = await api.postVizualizare({ idClient, idVersiune });
      const vizId = res?.id || res?.idVizualizare || res?.id_vizualizare;
      setIdVizualizare(vizId || null);
    } catch (err) {
      setError(err.message || 'Eroare la inregistrare.');
    }
  };

  useEffect(() => {
    saveInitial();
  }, [idVersiune]);

  useEffect(() => {
    if (!isPlaying || isDragging || progress >= 100) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
      return undefined;
    }
    intervalRef.current = setInterval(() => {
      setProgress((prev) => Math.min(100, prev + (SPEED_SECONDS_PER_TICK / SIM_DURATION_SECONDS) * 100));
    }, TICK_MS);
    return () => clearInterval(intervalRef.current);
  }, [isPlaying, isDragging, progress]);

  useEffect(() => {
    if (progress >= 100) setIsPlaying(false);
  }, [progress]);

  const togglePlayPause = () => {
    if (progress >= 100) return;
    setIsPlaying((prev) => !prev);
  };

  const skipByPercent = (delta) => {
    if (progress >= 100) return;
    setProgress((prev) => Math.min(100, Math.max(0, prev + delta)));
  };

  const calcPercentFromClientX = (clientX) => {
    const rect = barRef.current?.getBoundingClientRect();
    if (!rect) return 0;
    return Math.min(100, Math.max(0, ((clientX - rect.left) / rect.width) * 100));
  };

  const handleBarClick = (e) => {
    if (!idVizualizare) return;
    setProgress(calcPercentFromClientX(e.clientX));
  };

  const handleMouseMove = (e) => {
    const next = calcPercentFromClientX(e.clientX);
    setHoverPercent(next);
    if (isDragging) setProgress(next);
  };

  const handleMouseLeave = () => setHoverPercent(null);

  const handleDragStart = (e) => {
    if (!idVizualizare) return;
    setWasPlayingBeforeDrag(isPlaying);
    setIsDragging(true);
    setIsPlaying(false);
    setProgress(calcPercentFromClientX(e.clientX));
  };

  const handleDragEnd = () => {
    setIsDragging(false);
    setIsPlaying(wasPlayingBeforeDrag && progress < 100);
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
            <button type="button" className="btn-close" onClick={onClose}></button>
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
                  <div className="progress-hover" style={{ left: `${hoverPercent}%` }}>
                    {formatTimeFromPercent(hoverPercent)}
                  </div>
                )}
              </div>
              <div className="player-debug">
                <span>• Vizionare inregistrata in BD</span>
              </div>
              {controlsDisabled && (
                <div className="text-muted small mt-2">Se initializeaza vizionarea...</div>
              )}
            </div>
            <div className="player-debug-panel">
              <div>ID Vizualizare: {idVizualizare || '-'}</div>
            </div>
            {error && <div className="alert alert-danger mt-3">{error}</div>}
          </div>
          <div className="modal-footer">
            <button className="btn btn-outline-danger" onClick={onClose}>
              Stop
            </button>
            <button className="btn btn-primary" onClick={() => { setProgress(100); setIsPlaying(false); }}>
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

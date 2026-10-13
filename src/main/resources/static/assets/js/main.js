// MEDIAHUB MAIN JS (thème, zone de dépôt, lecteur audio, J'aime et favoris)
document.addEventListener('DOMContentLoaded', () => {
    // 1. THEME MANAGEMENT
    const themeBtn = document.querySelector('.theme-btn');
    if (themeBtn) {
        themeBtn.addEventListener('click', () => {
            document.body.classList.toggle('light-theme');
            const isLight = document.body.classList.contains('light-theme');
            localStorage.setItem('theme', isLight ? 'light' : 'dark');
        });
    }

    // Restore saved theme on page load
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'light') {
        document.body.classList.add('light-theme');
    } else {
        document.body.classList.remove('light-theme');
    }

    // 2. DRAG AND DROP ZONE (page Publier)
    const dropzone = document.getElementById('dropzone');
    const fileInput = document.getElementById('file-input');
    
    if (dropzone && fileInput) {
        // Trigger click on file input when dropzone is clicked
        dropzone.addEventListener('click', () => fileInput.click());

        // Highlight drop zone when item is dragged over
        ['dragenter', 'dragover'].forEach(eventName => {
            dropzone.addEventListener(eventName, (e) => {
                e.preventDefault();
                e.stopPropagation();
                dropzone.classList.add('dragover');
            }, false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            dropzone.addEventListener(eventName, (e) => {
                e.preventDefault();
                e.stopPropagation();
                dropzone.classList.remove('dragover');
            }, false);
        });

        // Handle dropped files
        dropzone.addEventListener('drop', (e) => {
            const dt = e.dataTransfer;
            const files = dt.files;
            if (files.length > 0) {
                fileInput.files = files;
                updateDropzoneLabel(files[0].name);
            }
        });

        // Handle selected file via dialogue
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                updateDropzoneLabel(fileInput.files[0].name);
            }
        });

        function updateDropzoneLabel(fileName) {
            const label = dropzone.querySelector('p');
            const subLabel = dropzone.querySelector('span');
            if (label) {
                label.innerHTML = `Fichier sélectionné : <strong style="color: var(--accent);">${fileName}</strong>`;
            }
            if (subLabel) {
                subLabel.textContent = "Cliquez ou glissez un autre fichier pour le remplacer";
            }
            // Display icon change
            const icon = dropzone.querySelector('i');
            if (icon) {
                icon.className = 'fas fa-file-check';
                icon.style.color = 'var(--success)';
            }
        }
    }

    // 3. CUSTOM AUDIO PLAYER (page Média)
    const audioEl = document.getElementById('native-audio');
    const playBtn = document.getElementById('audio-play-btn');
    const progressContainer = document.getElementById('audio-progress-container');
    const progressBarFill = document.getElementById('audio-progress-fill');
    const currentTimeText = document.getElementById('audio-current-time');
    const durationTimeText = document.getElementById('audio-duration');
    const volumeSlider = document.getElementById('audio-volume-slider');
    const volumeFill = document.getElementById('audio-volume-fill');
    const volumeIcon = document.getElementById('audio-volume-icon');

    if (audioEl) {
        // Toggle play and pause
        if (playBtn) {
            playBtn.addEventListener('click', () => {
                if (audioEl.paused) {
                    audioEl.play();
                } else {
                    audioEl.pause();
                }
            });
        }

        // Listen for play/pause state triggers to update UI icons
        audioEl.addEventListener('play', () => {
            if (playBtn) {
                playBtn.innerHTML = '<i class="fas fa-pause"></i>';
            }
        });

        audioEl.addEventListener('pause', () => {
            if (playBtn) {
                playBtn.innerHTML = '<i class="fas fa-play"></i>';
            }
        });

        // Time updates
        audioEl.addEventListener('timeupdate', () => {
            const current = audioEl.currentTime;
            const duration = audioEl.duration || 0;
            
            // Update time labels
            if (currentTimeText) {
                currentTimeText.textContent = formatTime(current);
            }
            if (durationTimeText && duration > 0) {
                durationTimeText.textContent = formatTime(duration);
            }

            // Update seek bar percentage
            if (progressBarFill && duration > 0) {
                const percent = (current / duration) * 100;
                progressBarFill.style.width = `${percent}%`;
            }
        });

        // Set duration initially when metadata loaded
        audioEl.addEventListener('loadedmetadata', () => {
            if (durationTimeText) {
                durationTimeText.textContent = formatTime(audioEl.duration);
            }
        });

        // Clicking progress bar to seek
        if (progressContainer) {
            progressContainer.addEventListener('click', (e) => {
                const rect = progressContainer.getBoundingClientRect();
                const width = rect.width;
                const clickX = e.clientX - rect.left;
                const duration = audioEl.duration || 0;
                if (duration > 0) {
                    audioEl.currentTime = (clickX / width) * duration;
                }
            });
        }

        // Volume adjustment slider
        if (volumeSlider) {
            volumeSlider.addEventListener('click', (e) => {
                const rect = volumeSlider.getBoundingClientRect();
                const width = rect.width;
                const clickX = e.clientX - rect.left;
                let volume = clickX / width;
                if (volume < 0) volume = 0;
                if (volume > 1) volume = 1;

                audioEl.volume = volume;
                if (volumeFill) {
                    volumeFill.style.width = `${volume * 100}%`;
                }

                // Update volume icons
                if (volumeIcon) {
                    if (volume === 0) {
                        volumeIcon.className = 'fas fa-volume-mute';
                    } else if (volume < 0.5) {
                        volumeIcon.className = 'fas fa-volume-down';
                    } else {
                        volumeIcon.className = 'fas fa-volume-up';
                    }
                }
            });
        }

        // Helper: format duration seconds into MM:SS format
        function formatTime(secs) {
            const minutes = Math.floor(secs / 60);
            const seconds = Math.floor(secs % 60);
            return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
        }
    }

    // 4. AJAX INTERACTIONS: LIKES & FAVORITES
    const likeBtn = document.getElementById('like-action-btn');
    const favBtn = document.getElementById('fav-action-btn');

    if (likeBtn) {
        likeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const mediaId = likeBtn.getAttribute('data-media-id');
            if (!mediaId) return;

            fetch(`/media/${encodeURIComponent(mediaId)}/like`, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    // Update state classes
                    if (data.state) {
                        likeBtn.classList.add('active');
                    } else {
                        likeBtn.classList.remove('active');
                    }
                    
                    // Update counts text
                    const countSpan = likeBtn.querySelector('.likes-count');
                    if (countSpan) {
                        countSpan.textContent = data.count;
                    }

                    // Native SweetAlert notice (subtle)
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: data.state ? 'Ajouté aux J\'aime !' : 'Retiré des J\'aime',
                        showConfirmButton: false,
                        timer: 1500
                    });
                } else if (data.error === 'unauthorized') {
                    Swal.fire({
                        icon: 'error',
                        title: 'Connexion requise',
                        text: 'Vous devez vous connecter pour aimer cette publication.',
                        confirmButtonText: 'Se connecter',
                        showCancelButton: true,
                        cancelButtonText: 'Fermer'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.location.href = '/login';
                        }
                    });
                }
            })
            .catch(err => console.error(err));
        });
    }

    if (favBtn) {
        favBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const mediaId = favBtn.getAttribute('data-media-id');
            if (!mediaId) return;

            fetch(`/media/${encodeURIComponent(mediaId)}/favori`, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    if (data.state) {
                        favBtn.classList.add('active');
                        favBtn.querySelector('span').textContent = 'Favoris (Ajouté)';
                    } else {
                        favBtn.classList.remove('active');
                        favBtn.querySelector('span').textContent = 'Ajouter aux favoris';
                    }

                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: data.state ? 'Ajouté aux favoris !' : 'Retiré des favoris',
                        showConfirmButton: false,
                        timer: 1500
                    });
                } else if (data.error === 'unauthorized') {
                    Swal.fire({
                        icon: 'error',
                        title: 'Connexion requise',
                        text: 'Vous devez vous connecter pour ajouter des favoris.',
                        confirmButtonText: 'Se connecter',
                        showCancelButton: true,
                        cancelButtonText: 'Fermer'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.location.href = '/login';
                        }
                    });
                }
            })
            .catch(err => console.error(err));
        });
    }
});

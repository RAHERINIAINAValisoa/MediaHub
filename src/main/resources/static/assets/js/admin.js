// MEDIAHUB - TABLEAU DE BORD ADMINISTRATEUR
document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('admin-root');
    if (!root) return;

    // 1. ONGLETS (Utilisateurs / Médias)
    function switchTab(name) {
        ['users', 'medias'].forEach((tab) => {
            const active = tab === name;
            document.getElementById('tab-btn-' + tab).classList.toggle('active', active);
            document.getElementById('tab-panel-' + tab).style.display = active ? 'block' : 'none';
        });
    }
    document.querySelectorAll('[data-tab]').forEach((btn) => {
        if (btn.tagName === 'BUTTON') {
            btn.addEventListener('click', () => switchTab(btn.dataset.tab));
        }
    });
    switchTab(root.dataset.tab === 'medias' ? 'medias' : 'users');

    // 2. CONFIRMATION AVANT SUPPRESSION (formulaires portant data-confirm)
    document.querySelectorAll('form[data-confirm]').forEach((form) => {
        form.addEventListener('submit', (e) => {
            if (!window.confirm(form.dataset.confirm)) {
                e.preventDefault();
            }
        });
    });

    // 3. MODIFICATION D'UN MÉDIA (fenêtre SweetAlert2)
    function escapeHtml(value) {
        const div = document.createElement('div');
        div.textContent = value == null ? '' : String(value);
        return div.innerHTML.replace(/"/g, '&quot;');
    }

    document.querySelectorAll('.edit-media-btn').forEach((btn) => {
        btn.addEventListener('click', () => openEditMediaModal(btn.dataset));
    });

    function openEditMediaModal(data) {
        Swal.fire({
            title: 'Modifier le Média',
            html: `
                <div style="text-align: left; font-family: var(--font-sans);">
                    <div style="margin-bottom: 15px;">
                        <label for="swal-title" class="form-label" style="display:block; font-weight:600; margin-bottom:5px;">Titre *</label>
                        <input id="swal-title" class="swal2-input" style="width: 100%; margin:0; box-sizing:border-box; background:var(--bg-input); border:1px solid var(--border-color); color:var(--text-main); border-radius:var(--radius-md);" value="${escapeHtml(data.titre)}">
                    </div>
                    <div>
                        <label for="swal-desc" class="form-label" style="display:block; font-weight:600; margin-bottom:5px;">Description</label>
                        <textarea id="swal-desc" class="swal2-textarea" style="width: 100%; margin:0; box-sizing:border-box; height:100px; background:var(--bg-input); border:1px solid var(--border-color); color:var(--text-main); border-radius:var(--radius-md);">${escapeHtml(data.description)}</textarea>
                    </div>
                    <div style="margin-top: 15px; display:flex; align-items:center; gap:8px;">
                        <input type="checkbox" id="swal-valid" ${data.valide === 'true' ? 'checked' : ''} style="width:18px; height:18px;">
                        <label for="swal-valid" style="font-weight:600; cursor:pointer;">Approuver et publier (visible de tous)</label>
                    </div>
                </div>
            `,
            focusConfirm: false,
            showCancelButton: true,
            confirmButtonText: 'Enregistrer',
            cancelButtonText: 'Annuler',
            confirmButtonColor: 'var(--accent)',
            preConfirm: () => {
                const titre = document.getElementById('swal-title').value.trim();
                const description = document.getElementById('swal-desc').value;
                const valide = document.getElementById('swal-valid').checked;
                if (!titre) {
                    Swal.showValidationMessage('Le titre est obligatoire');
                    return false;
                }
                return { titre, description, valide };
            }
        }).then((result) => {
            if (!result.isConfirmed) return;

            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/admin/medias/' + encodeURIComponent(data.id) + '/modifier';

            const fields = { titre: result.value.titre, description: result.value.description };
            if (result.value.valide) fields.valide = '1';

            Object.keys(fields).forEach((key) => {
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = key;
                input.value = fields[key];
                form.appendChild(input);
            });
            document.body.appendChild(form);
            form.submit();
        });
    }
});

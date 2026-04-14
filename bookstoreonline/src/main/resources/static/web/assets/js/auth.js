/**
 * Booksaw Auth & Navbar Sync Logic
 */

function syncNavbarUI() {
    try {
        const username = localStorage.getItem('username');
        const role = localStorage.getItem('role');
        const authSection = document.getElementById('auth-section');
        
        if (!authSection) return;

        if (username) {
            const root = typeof getRelativePath === "function" ? getRelativePath() : '';
            const isAdmin = role && (
                role.toUpperCase().includes('ADMIN') || 
                role.toUpperCase().includes('STAFF') ||
                role.toUpperCase().includes('QUANLY')
            );

            let dropdownHtml = `
                <div class="dropdown d-inline-block">
                    <a href="#" class="text-white dropdown-toggle text-decoration-none fw-bold" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="icon icon-user me-1"></i> Hi, ${username}
                    </a>
                    <ul class="dropdown-menu shadow-lg border-0" aria-labelledby="userDropdown" style="z-index: 10000 !important;">
                        <li><a class="dropdown-item py-2 text-dark" href="${root}Users/Profile.html"><i class="icon icon-clipboard me-2"></i>Hồ sơ cá nhân</a></li>
                        ${isAdmin ? `<li><a class="dropdown-item py-2 text-dark" href="${root}Dashboard/Admin/Index.html"><i class="icon icon-settings me-2"></i>Quản trị cửa hàng</a></li>` : ''}
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item py-2 text-danger fw-bold" href="#" onclick="handleLogout(event)"><i class="icon icon-back-in me-2"></i>Đăng xuất</a></li>
                    </ul>
                </div>
            `;
            authSection.innerHTML = dropdownHtml;
        }
    } catch (e) {
        console.error('Navbar Sync Error:', e);
    }
}

function handleLogout(e) {
    if(e) e.preventDefault();
    localStorage.clear();
    alert('Hẹn gặp lại bạn tại Booksaw!');
    const root = typeof getRelativePath === "function" ? getRelativePath() : '';
    window.location.href = root + 'index.html';
}

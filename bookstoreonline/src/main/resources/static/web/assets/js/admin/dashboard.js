/**
    Booksaw Admin Dashboard Logic
    Fetches real stats from the Backend API and updates the UI.
*/

document.addEventListener('DOMContentLoaded', () => {
    // Only run if we are on the dashboard index page
    if (document.querySelector('.card h3')) {
        loadDashboardStats();
    }
});

async function loadDashboardStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/admin/dashboard/stats`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) throw new Error('Failed to fetch dashboard stats');

        const result = await response.json();
        
        if (result.status === 'success') {
            const stats = result.data;
            updateStatsUI(stats);
        }
    } catch (error) {
        console.error('Dashboard Stats Error:', error);
    }
}

function updateStatsUI(stats) {
    const cards = document.querySelectorAll('.card');

    cards.forEach(card => {
        const titleElement = card.querySelector('span');
        if (!titleElement) return;

        const title = titleElement.innerText.toUpperCase();
        const valueElement = card.querySelector('h3');

        if (title.includes('DOANH THU')) {
            valueElement.innerText = formatCurrency(stats.totalRevenue);
        } else if (title.includes('ĐƠN HÀNG')) {
            valueElement.innerText = stats.totalOrders;
        } else if (title.includes('SÁCH')) {
            valueElement.innerText = stats.totalBooks;
        } else if (title.includes('KHÁCH HÀNG')) {
            valueElement.innerText = stats.totalCustomers;
        }
    });
}

function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value);
}

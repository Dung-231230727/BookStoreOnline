/**
 * common.js - Shared utilities for Bookstore Online
 */

const common = {
    getBaseURL() {
        const path = window.location.pathname;
        if (path.includes('/Books/') || path.includes('/Auth/') || path.includes('/Orders/')) {
            return '../';
        }
        return '';
    },
    getQueryParam(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name);
    }
};

const api = {
    baseUrl: 'http://localhost:8080/api', 
    
    getToken: () => localStorage.getItem('access_token'),
    setToken: (token) => localStorage.setItem('access_token', token),
    clearToken: () => localStorage.removeItem('access_token'),
    
    getUser: () => JSON.parse(localStorage.getItem('user_info')),
    setUser: (user) => localStorage.setItem('user_info', JSON.stringify(user)),
    clearUser: () => localStorage.removeItem('user_info'),

    request: async (endpoint, options = {}) => {
        const token = api.getToken();
        const headers = { 'Content-Type': 'application/json', ...options.headers };
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const startTime = Date.now();
        const method = options.method || 'GET';
        
        console.groupCollapsed(`%c API ${method} %c ${endpoint}`, "color:white; background:#C5A992; padding:2px 5px; border-radius:3px;", "color:#2F2F2F; font-weight:bold;");
        if (options.body) console.log("Payload:", JSON.parse(options.body));

        try {
            const response = await fetch(`${api.baseUrl}${endpoint}`, { ...options, headers });
            const result = await response.json();
            const duration = Date.now() - startTime;

            console.log(`Status: ${response.status} (${duration}ms)`);
            console.log("Response Data:", result);

            if (!response.ok) {
                if (response.status === 401) { api.clearToken(); api.clearUser(); }
                console.error("API Error Status:", response.status);
                throw new Error(result.message || 'Error from server');
            }
            console.groupEnd();
            return result;
        } catch (error) {
            console.error("API Call Failed:", error);
            console.groupEnd();
            throw error;
        }
    },

    get: (endpoint) => api.request(endpoint, { method: 'GET' }),
    post: (endpoint, data) => api.request(endpoint, { method: 'POST', body: JSON.stringify(data) }),
    put: (endpoint, data) => api.request(endpoint, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (endpoint) => api.request(endpoint, { method: 'DELETE' }),

    showToast: (message, type = 'success') => {
        const toastEl = document.getElementById('appToast');
        if (!toastEl) return;
        const toastBody = document.getElementById('toastMessage');
        const toastHeader = toastEl.querySelector('.toast-header');
        toastBody.innerText = message;
        if (type === 'error') toastHeader.classList.replace('bg-accent', 'bg-danger');
        else toastHeader.classList.replace('bg-danger', 'bg-accent');
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    },

    formatCurrency: (amount) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    }
};

const formatPrice = (price) => api.formatCurrency(price);

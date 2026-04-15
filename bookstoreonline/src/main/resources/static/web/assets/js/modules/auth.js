/**
 * auth.js - Authentication Logic
 * Standardized for Full English Backend synchronization
 */
const auth = {
    // Perform Login
    login: async (username, password) => {
        try {
            const result = await api.post('/auth/login', { username, password });
            
            if (result.status === 200) {
                const loginData = result.data;
                api.setToken(loginData.token);
                
                // Save user info for use across the site
                api.setUser({
                    username: loginData.username,
                    role:     loginData.role,
                    fullName: loginData.fullName || loginData.name || loginData.username,
                    email:    loginData.email    || loginData.emailAddress || '',
                    phone:    loginData.phone    || loginData.phoneNumber || loginData.mobilePhone || '',
                    address:  loginData.address  || loginData.shippingAddress || loginData.street || ''
                });
                
                api.showToast("Login Successful!", "success");

                // Update UI layout context
                if (window.layout && layout.updateUserHeader) layout.updateUserHeader();

                // Professional Admin Dashboard redirect
                if (loginData.role === 'ADMIN' || loginData.role === 'STAFF') {
                    setTimeout(() => layout.render('Dashboard', 'Admin/Index'), 500);
                } else {
                    setTimeout(() => layout.render('Home', 'Index'), 500);
                }
            }
        } catch (error) {
            api.showToast(error.message, "error");
        }
    },

    // Perform Registration
    register: async (userData) => {
        try {
            const result = await api.post('/auth/register', userData);
            if (result.status === 200) {
                api.showToast("Registration successful! Please login.", "success");
                layout.render('Auth', 'Login');
            }
        } catch (error) {
            api.showToast(error.message, "error");
        }
    },

    // Logout
    logout: () => {
        api.clearToken();
        api.clearUser();
        sessionStorage.removeItem('applied_voucher');
        sessionStorage.removeItem('applied_discount');

        if (window.layout && layout.updateUserHeader) layout.updateUserHeader();
        api.showToast("Logged out successfully");
        layout.render('Home', 'Index');
    }
};

/**
 * Form Binding Logic (to be called after view render)
 */
function initAuthForms() {
    $("#loginForm").on('submit', function(e) {
        e.preventDefault();
        const user = $("#username").val();
        const pass = $("#password").val();
        auth.login(user, pass);
    });

    $("#registerForm").on('submit', function(e) {
        e.preventDefault();
        const pass = $("#reg-password").val();
        const confirm = $("#reg-confirm-password").val();
        
        if (pass !== confirm) {
            api.showToast("Password confirmation does not match", "error");
            return;
        }

        const data = {
            username: $("#reg-username").val(),
            email: $("#reg-email").val(),
            password: pass
        };
        auth.register(data);
    });
}
